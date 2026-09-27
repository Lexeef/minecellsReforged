package com.github.mim1q.minecells.dimension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public final class MineCellsDimensionGraph {
    private final Map<MineCellsDimension, Node> graph = new HashMap<>();

    public MineCellsDimensionGraph() {
        Node overworld = add(MineCellsDimension.OVERWORLD);
        Node prison = add(MineCellsDimension.PRISONERS_QUARTERS, overworld);
        Node promenade = add(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED, prison);
        add(MineCellsDimension.INSUFFERABLE_CRYPT, prison);
        Node ramparts = add(MineCellsDimension.RAMPARTS, promenade);
        add(MineCellsDimension.BLACK_BRIDGE, ramparts);
    }

    private Node add(MineCellsDimension dimension, Node... upstream) {
        Node node = new Node(dimension, upstream);
        graph.put(dimension, node);
        return node;
    }

    public boolean canTraverseToOverworld(MineCellsDimension dimension, BiPredicate<MineCellsDimension, MineCellsDimension> edgePredicate) {
        Node node = graph.get(dimension);
        return node != null && node.canTraverseToOverworld(edgePredicate);
    }

    public boolean areAdjacent(MineCellsDimension first, MineCellsDimension second) {
        Node firstNode = graph.get(first);
        Node secondNode = graph.get(second);
        return firstNode != null && firstNode.hasUpstream(second)
            || secondNode != null && secondNode.hasUpstream(first);
    }

    private record Node(MineCellsDimension dimension, Node... upstream) {
        private boolean canTraverseToOverworld(BiPredicate<MineCellsDimension, MineCellsDimension> edgePredicate) {
            if (dimension == MineCellsDimension.OVERWORLD) {
                return true;
            }
            for (Node next : upstream) {
                if (edgePredicate.test(dimension, next.dimension) && next.canTraverseToOverworld(edgePredicate)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasUpstream(MineCellsDimension target) {
            for (Node next : upstream) {
                if (next.dimension == target) {
                    return true;
                }
            }
            return false;
        }
    }
}
