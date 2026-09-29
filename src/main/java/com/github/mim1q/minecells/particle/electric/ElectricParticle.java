package com.github.mim1q.minecells.particle.electric;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ElectricParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final Vec3 direction;
    private final float pitch;
    private final float yaw;
    private float particleRoll;
    private final int length;
    private final int color;
    private final float size;
    private final boolean isMainBranch;

    private ElectricParticle(
        SpriteSet spriteSet,
        ClientLevel level,
        double x,
        double y,
        double z,
        double velocityX,
        double velocityY,
        double velocityZ,
        Vec3 direction,
        int length,
        int color,
        float size,
        boolean isMainBranch
    ) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.direction = direction.normalize();
        this.length = length;
        this.color = color;
        this.lifetime = 6;
        this.size = size;
        this.isMainBranch = isMainBranch;
        this.yaw = (float) Math.asin(-this.direction.y);
        this.pitch = (float) Math.atan2(this.direction.x, this.direction.z);
        this.particleRoll = level.random.nextFloat() * 2 * Mth.PI;
        this.setParticleSpeed(velocityX, velocityY, velocityZ);
        this.setSprite(spriteSet.get(level.random));
    }

    @Override
    public void tick() {
        this.setPos(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.setSprite(this.spriteSet.get(level.random));
        if (this.length == 0) {
            return;
        }
        if (this.age == 1) {
            for (int i = 0; i < 3; i++) {
                if (level.random.nextFloat() < 0.5f) {
                    this.addSideBranch();
                }
            }
            this.addNextMainBranch();
        }
    }

    private void addSideBranch() {
        float delta = level.random.nextFloat();
        Vec3 newPos = new Vec3(this.x, this.y, this.z).add(this.direction.scale(delta * size));
        Vec3 randomDirection = this.direction.offsetRandom(level.random, 2.0f);
        int randomLength = Math.min(this.length - 1, level.random.nextInt(3));
        level.addParticle(
            new ElectricParticleEffect(randomDirection, randomLength, this.color, this.size * 0.5f, false),
            newPos.x,
            newPos.y,
            newPos.z,
            xd,
            yd,
            zd
        );
    }

    private void addNextMainBranch() {
        Vec3 newDirection = this.direction.offsetRandom(level.getRandom(), isMainBranch ? 0.1f : 2.0f);
        Vec3 newPos = new Vec3(this.x, this.y, this.z).add(this.direction.scale(size));
        level.addParticle(
            new ElectricParticleEffect(newDirection, this.length - 1, this.color, this.size, this.isMainBranch),
            newPos.x,
            newPos.y,
            newPos.z,
            xd,
            yd,
            zd
        );
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float tickDelta) {
        float lifetimeDelta = (this.age + tickDelta) / this.lifetime;
        int alpha = 255;
        if (lifetimeDelta < 0.2) {
            alpha = (int) (lifetimeDelta / 0.2 * 255);
        } else if (lifetimeDelta > 0.8) {
            alpha = (int) ((1 - lifetimeDelta) / 0.2 * 255);
        }
        alpha = Mth.clamp(alpha, 0, 255);

        Vec3 cameraPos = camera.getPosition();
        float px = (float) (Mth.lerp(tickDelta, xo, x) - cameraPos.x);
        float py = (float) (Mth.lerp(tickDelta, yo, y) - cameraPos.y);
        float pz = (float) (Mth.lerp(tickDelta, zo, z) - cameraPos.z);

        Quaternionf rotation = new Quaternionf();
        rotation.rotateY(pitch);
        rotation.rotateX(yaw);
        rotation.rotateZ(particleRoll);

        Vector3f[] vectors = new Vector3f[]{
            new Vector3f(-0.5f, 0f, 0.0f),
            new Vector3f(-0.5f, 0f, 1.0f),
            new Vector3f(0.5f, 0f, 1.0f),
            new Vector3f(0.5f, 0f, 0.0f)
        };
        for (Vector3f vector : vectors) {
            vector.rotate(rotation);
            vector.mul(size);
            vector.add(px, py, pz);
        }

        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();
        float[] u = new float[]{maxU, maxU, minU, minU};
        float[] v = new float[]{maxV, minV, minV, maxV};
        int light = 0xF000F0;
        int r = this.color >> 16 & 0xFF;
        int g = this.color >> 8 & 0xFF;
        int b = this.color & 0xFF;

        for (int i = 0; i <= 3; ++i) {
            consumer.vertex(vectors[i].x(), vectors[i].y(), vectors[i].z()).uv(u[i], v[i]).color(r, g, b, alpha).uv2(light).endVertex();
        }
        for (int i = 3; i >= 0; --i) {
            consumer.vertex(vectors[i].x(), vectors[i].y(), vectors[i].z()).uv(u[i], v[i]).color(r, g, b, alpha).uv2(light).endVertex();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Factory(SpriteSet spriteSet) implements ParticleProvider<ElectricParticleEffect> {
        @Override
        public Particle createParticle(ElectricParticleEffect parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ElectricParticle(
                spriteSet,
                level,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                parameters.direction(),
                parameters.length(),
                parameters.color(),
                parameters.size(),
                parameters.isMainBranch()
            );
        }
    }
}
