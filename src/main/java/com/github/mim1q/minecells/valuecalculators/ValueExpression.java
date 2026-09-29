package com.github.mim1q.minecells.valuecalculators;

import java.util.ArrayList;
import java.util.function.ToDoubleFunction;
import java.util.List;
import java.util.Locale;

@FunctionalInterface
public interface ValueExpression {
    double evaluate(ToDoubleFunction<String> variables);

    static ValueExpression constant(double value) {
        return variables -> value;
    }

    static ValueExpression parse(String source) {
        return new Parser(source).parseAll();
    }

    final class ParseException extends RuntimeException {
        ParseException(String message, String source, int position) {
            super(message + " at position " + position + " in \"" + source + "\"");
        }
    }

    final class Parser {
        private final String source;
        private int pos;

        private Parser(String source) {
            this.source = source;
        }

        private ValueExpression parseAll() {
            ValueExpression expression = parseSum();
            skipWhitespace();
            if (pos < source.length()) {
                throw error("Unexpected '" + source.charAt(pos) + "'");
            }
            return expression;
        }

        private ValueExpression parseSum() {
            ValueExpression left = parseProduct();
            while (true) {
                if (accept('+')) {
                    ValueExpression a = left, b = parseProduct();
                    left = v -> a.evaluate(v) + b.evaluate(v);
                } else if (accept('-')) {
                    ValueExpression a = left, b = parseProduct();
                    left = v -> a.evaluate(v) - b.evaluate(v);
                } else {
                    return left;
                }
            }
        }

        private ValueExpression parseProduct() {
            ValueExpression left = parseUnary();
            while (true) {
                if (accept('*')) {
                    ValueExpression a = left, b = parseUnary();
                    left = v -> a.evaluate(v) * b.evaluate(v);
                } else if (accept('/')) {
                    ValueExpression a = left, b = parseUnary();
                    left = v -> {
                        double divisor = b.evaluate(v);
                        return divisor == 0.0D ? 0.0D : a.evaluate(v) / divisor;
                    };
                } else if (accept('%')) {
                    ValueExpression a = left, b = parseUnary();
                    left = v -> {
                        double divisor = b.evaluate(v);
                        return divisor == 0.0D ? 0.0D : a.evaluate(v) % divisor;
                    };
                } else {
                    return left;
                }
            }
        }

        private ValueExpression parseUnary() {
            if (accept('-')) {
                ValueExpression inner = parseUnary();
                return v -> -inner.evaluate(v);
            }
            if (accept('+')) {
                return parseUnary();
            }
            return parsePower();
        }

        private ValueExpression parsePower() {
            ValueExpression base = parsePrimary();
            if (accept('^')) {
                ValueExpression exponent = parseUnary();
                return v -> Math.pow(base.evaluate(v), exponent.evaluate(v));
            }
            return base;
        }

        private ValueExpression parsePrimary() {
            skipWhitespace();
            if (pos >= source.length()) {
                throw error("Unexpected end of expression");
            }
            char c = source.charAt(pos);
            if (accept('(')) {
                ValueExpression inner = parseSum();
                expect(')');
                return inner;
            }
            if (Character.isDigit(c) || c == '.') {
                return parseNumber();
            }
            if (Character.isLetter(c) || c == '_') {
                String name = parseIdentifier();
                if (accept('(')) {
                    List<ValueExpression> args = new ArrayList<>();
                    if (!accept(')')) {
                        do {
                            args.add(parseSum());
                        } while (accept(','));
                        expect(')');
                    }
                    return function(name, args);
                }
                return switch (name.toLowerCase(Locale.ROOT)) {
                    case "pi" -> constant(Math.PI);
                    case "e" -> constant(Math.E);
                    case "true" -> constant(1.0D);
                    case "false" -> constant(0.0D);
                    default -> v -> v.applyAsDouble(name);
                };
            }
            throw error("Unexpected '" + c + "'");
        }

        private ValueExpression parseNumber() {
            int start = pos;
            while (pos < source.length() && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
                pos++;
            }
            if (pos < source.length() && (source.charAt(pos) == 'e' || source.charAt(pos) == 'E')) {
                int save = pos;
                pos++;
                if (pos < source.length() && (source.charAt(pos) == '+' || source.charAt(pos) == '-')) {
                    pos++;
                }
                if (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                    while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                        pos++;
                    }
                } else {
                    pos = save;
                }
            }
            try {
                return constant(Double.parseDouble(source.substring(start, pos)));
            } catch (NumberFormatException e) {
                throw error("Invalid number");
            }
        }

        private String parseIdentifier() {
            int start = pos;
            while (pos < source.length() && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_' || source.charAt(pos) == '.' || source.charAt(pos) == ':')) {
                pos++;
            }
            return source.substring(start, pos);
        }

        private ValueExpression function(String name, List<ValueExpression> args) {
            String lower = name.toLowerCase(Locale.ROOT);
            return switch (lower) {
                case "abs" -> unary(lower, args, Math::abs);
                case "floor" -> unary(lower, args, Math::floor);
                case "ceil" -> unary(lower, args, Math::ceil);
                case "round" -> unary(lower, args, x -> (double) Math.round(x));
                case "sqrt" -> unary(lower, args, x -> x < 0.0D ? 0.0D : Math.sqrt(x));
                case "sin" -> unary(lower, args, Math::sin);
                case "cos" -> unary(lower, args, Math::cos);
                case "tan" -> unary(lower, args, Math::tan);
                case "log", "ln" -> unary(lower, args, x -> x <= 0.0D ? 0.0D : Math.log(x));
                case "log10" -> unary(lower, args, x -> x <= 0.0D ? 0.0D : Math.log10(x));
                case "exp" -> unary(lower, args, Math::exp);
                case "sign", "signum" -> unary(lower, args, Math::signum);
                case "pow" -> {
                    requireArgs(lower, args, 2);
                    ValueExpression a = args.get(0), b = args.get(1);
                    yield v -> Math.pow(a.evaluate(v), b.evaluate(v));
                }
                case "min" -> {
                    requireAtLeast(lower, args, 1);
                    yield v -> {
                        double result = Double.POSITIVE_INFINITY;
                        for (ValueExpression arg : args) {
                            result = Math.min(result, arg.evaluate(v));
                        }
                        return result;
                    };
                }
                case "max" -> {
                    requireAtLeast(lower, args, 1);
                    yield v -> {
                        double result = Double.NEGATIVE_INFINITY;
                        for (ValueExpression arg : args) {
                            result = Math.max(result, arg.evaluate(v));
                        }
                        return result;
                    };
                }
                case "clamp" -> {
                    requireArgs(lower, args, 3);
                    ValueExpression x = args.get(0), lo = args.get(1), hi = args.get(2);
                    yield v -> Math.max(lo.evaluate(v), Math.min(hi.evaluate(v), x.evaluate(v)));
                }
                case "if" -> {
                    requireArgs(lower, args, 3);
                    ValueExpression c = args.get(0), a = args.get(1), b = args.get(2);
                    yield v -> c.evaluate(v) != 0.0D ? a.evaluate(v) : b.evaluate(v);
                }
                default -> throw error("Unknown function '" + name + "'");
            };
        }

        private ValueExpression unary(String name, List<ValueExpression> args, java.util.function.DoubleUnaryOperator op) {
            requireArgs(name, args, 1);
            ValueExpression arg = args.get(0);
            return v -> op.applyAsDouble(arg.evaluate(v));
        }

        private void requireArgs(String name, List<ValueExpression> args, int count) {
            if (args.size() != count) {
                throw error("Function '" + name + "' expects " + count + " argument(s)");
            }
        }

        private void requireAtLeast(String name, List<ValueExpression> args, int count) {
            if (args.size() < count) {
                throw error("Function '" + name + "' expects at least " + count + " argument(s)");
            }
        }

        private boolean accept(char c) {
            skipWhitespace();
            if (pos < source.length() && source.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        private void expect(char c) {
            if (!accept(c)) {
                throw error("Expected '" + c + "'");
            }
        }

        private void skipWhitespace() {
            while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
                pos++;
            }
        }

        private ParseException error(String message) {
            return new ParseException(message, source, pos);
        }
    }
}
