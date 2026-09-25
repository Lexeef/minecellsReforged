package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EliteObeliskEntity extends ObeliskEntity {
    private static final ResourceLocation ELITE_ADVANCEMENT = MineCells.id("elite");

    private ResourceLocation spawnerRuneDataId = MineCells.id("elite/vine_rune");

    public EliteObeliskEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Item getActivationItem() {
        return null;
    }

    @Override
    public ResourceLocation getSpawnerRuneDataId() {
        return spawnerRuneDataId;
    }

    public void setSpawnerRuneDataId(ResourceLocation spawnerRuneDataId) {
        this.spawnerRuneDataId = spawnerRuneDataId;
    }

    @Override
    public AABB getBox() {
        return AABB.ofSize(position(), 32.0D, 32.0D, 32.0D);
    }

    @Override
    protected void postProcessEntity(Entity entity) {
        Vec3 pos = position().add(getLookAngle().scale(2.0D));
        entity.setPos(pos.x, pos.y, pos.z);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        MinecraftServer server = getServer();
        if (!level().isClientSide && server != null && player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = server.getAdvancements().getAdvancement(ELITE_ADVANCEMENT);
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, "obelisk_activated");
            }
        }
        return super.interact(player, hand);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("spawnerRuneDataId")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("spawnerRuneDataId"));
            if (id != null) {
                spawnerRuneDataId = id;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("spawnerRuneDataId", spawnerRuneDataId.toString());
    }
}
