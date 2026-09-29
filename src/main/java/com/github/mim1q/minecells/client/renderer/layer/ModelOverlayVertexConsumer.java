package com.github.mim1q.minecells.client.renderer.layer;

import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Vector3f;

/**
 * Re-emits entity model quads as an inflated shell with UVs tiled once per 16 model pixels.
 * Equivalent of gimm1q's cuboid overlay, working purely on the vertex stream: each quad is pushed out
 * along its normal and grown in-plane by the same offset, so neighbouring faces meet like an inflated cuboid.
 * Zero-area quads (the thin sides of flat model planes) are dropped.
 */
public final class ModelOverlayVertexConsumer implements VertexConsumer {
    private static final float EPSILON = 1.0E-5F;

    private final VertexConsumer wrapped;
    private final float offset;
    private final float vScroll;

    private final float[] xs = new float[4];
    private final float[] ys = new float[4];
    private final float[] zs = new float[4];
    private final float[] us = new float[4];
    private final float[] vs = new float[4];
    private final int[] overlays = new int[4];
    private final int[] lights = new int[4];
    private final float[] nxs = new float[4];
    private final float[] nys = new float[4];
    private final float[] nzs = new float[4];
    private int count;

    private float curX;
    private float curY;
    private float curZ;
    private float curU;
    private float curV;
    private int curOverlay;
    private int curLight;
    private float curNx;
    private float curNy;
    private float curNz;

    /**
     * @param offset  distance from the original surface, in model pixels
     * @param vScroll texture V offset applied to every vertex (animation)
     */
    public ModelOverlayVertexConsumer(VertexConsumer wrapped, float offset, float vScroll) {
        this.wrapped = wrapped;
        this.offset = offset / 16.0F;
        this.vScroll = vScroll;
    }

    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        curX = x;
        curY = y;
        curZ = z;
        curU = u;
        curV = v;
        curOverlay = overlay;
        curLight = light;
        curNx = nx;
        curNy = ny;
        curNz = nz;
        endVertex();
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        curX = (float) x;
        curY = (float) y;
        curZ = (float) z;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        curU = u;
        curV = v;
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        curOverlay = u & 0xFFFF | v << 16;
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        curLight = u & 0xFFFF | v << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        curNx = x;
        curNy = y;
        curNz = z;
        return this;
    }

    @Override
    public void endVertex() {
        xs[count] = curX;
        ys[count] = curY;
        zs[count] = curZ;
        us[count] = curU;
        vs[count] = curV;
        overlays[count] = curOverlay;
        lights[count] = curLight;
        nxs[count] = curNx;
        nys[count] = curNy;
        nzs[count] = curNz;
        if (++count == 4) {
            count = 0;
            emitQuad();
        }
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
    }

    @Override
    public void unsetDefaultColor() {
    }

    private void emitQuad() {
        Vector3f p0 = new Vector3f(xs[0], ys[0], zs[0]);
        Vector3f edgeA = new Vector3f(xs[1], ys[1], zs[1]).sub(p0);
        Vector3f edgeB = new Vector3f(xs[3], ys[3], zs[3]).sub(p0);
        float lengthA = edgeA.length();
        float lengthB = edgeB.length();
        if (lengthA < EPSILON || lengthB < EPSILON) {
            return;
        }
        edgeA.div(lengthA);
        edgeB.div(lengthB);

        Vector3f normal = new Vector3f(nxs[0], nys[0], nzs[0]);
        if (normal.lengthSquared() < EPSILON) {
            normal.set(edgeA).cross(edgeB);
        }
        normal.normalize();

        boolean aIsU = Math.abs(us[1] - us[0]) >= Math.abs(vs[1] - vs[0]);
        float uSpan = aIsU ? lengthA : lengthB;
        float vSpan = aIsU ? lengthB : lengthA;
        float uMin = Math.min(Math.min(us[0], us[1]), Math.min(us[2], us[3]));
        float uMax = Math.max(Math.max(us[0], us[1]), Math.max(us[2], us[3]));
        float vMin = Math.min(Math.min(vs[0], vs[1]), Math.min(vs[2], vs[3]));
        float vMax = Math.max(Math.max(vs[0], vs[1]), Math.max(vs[2], vs[3]));

        float cx = (xs[0] + xs[1] + xs[2] + xs[3]) * 0.25F;
        float cy = (ys[0] + ys[1] + ys[2] + ys[3]) * 0.25F;
        float cz = (zs[0] + zs[1] + zs[2] + zs[3]) * 0.25F;

        for (int i = 0; i < 4; i++) {
            float dx = xs[i] - cx;
            float dy = ys[i] - cy;
            float dz = zs[i] - cz;
            float signA = Math.signum(dx * edgeA.x + dy * edgeA.y + dz * edgeA.z);
            float signB = Math.signum(dx * edgeB.x + dy * edgeB.y + dz * edgeB.z);
            float x = xs[i] + (normal.x + edgeA.x * signA + edgeB.x * signB) * offset;
            float y = ys[i] + (normal.y + edgeA.y * signA + edgeB.y * signB) * offset;
            float z = zs[i] + (normal.z + edgeA.z * signA + edgeB.z * signB) * offset;

            float u = uMax - uMin > EPSILON ? (us[i] - uMin) / (uMax - uMin) * uSpan : 0.0F;
            float v = vMax - vMin > EPSILON ? (vs[i] - vMin) / (vMax - vMin) * vSpan : 0.0F;

            wrapped.vertex(x, y, z, 1.0F, 1.0F, 1.0F, 1.0F, u, v + vScroll, overlays[i], lights[i], normal.x, normal.y, normal.z);
        }
    }
}
