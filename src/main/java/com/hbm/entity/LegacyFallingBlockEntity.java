package com.hbm.entity;

import com.hbm.block.falling.LegacyFallingBlock;
import com.hbm.block.falling.LegacyFallingRules;
import com.hbm.registry.HbmFallingBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Exact 1.7 falling tick order; modern Entity posY is the old logical center minus half the box height. */
public final class LegacyFallingBlockEntity extends FallingBlockEntity {
    public static final double GRAVITY = 0.03999999910593033D, DRAG = 0.9800000190734863D, GROUND_DRAG = 0.699999988079071D;
    private int metadata, fallHurtMax = 40;
    private float fallHurtAmount = 2F;
    private boolean hurtEntities, cancelDrop;
    public LegacyFallingBlockEntity(EntityType<? extends FallingBlockEntity> type, Level level) { super(type, level); }
    public LegacyFallingBlockEntity(Level level, BlockPos source, BlockState state) {
        this(HbmFallingBlocks.ENTITY.get(), level);
        setCarriedState(state);
        metadata = state.hasProperty(LegacyFallingBlock.LEGACY_META) ? state.getValue(LegacyFallingBlock.LEGACY_META) : 0;
        blocksBuilding = true;
        setPos(source.getX() + .5D, source.getY() + .5D - getBbHeight() / 2F, source.getZ() + .5D);
        setDeltaMovement(Vec3.ZERO); xo = getX(); yo = getY(); zo = getZ(); setStartPos(source);
    }
    /** Uses the public supported NBT load path to assign the parent's private carried state. */
    private void setCarriedState(BlockState state) {
        CompoundTag tag = new CompoundTag(); tag.put("BlockState", NbtUtils.writeBlockState(state));
        tag.putInt("Time", time); tag.putBoolean("DropItem", dropItem);
        tag.putBoolean("HurtEntities", false); tag.putFloat("FallHurtAmount", 2); tag.putInt("FallHurtMax", 40);
        if (blockData != null) tag.put("TileEntityData", blockData);
        super.readAdditionalSaveData(tag);
    }
    public double legacyCenterY() { return getY() + getBbHeight() / 2F; }
    public BlockPos legacyPosition() { return BlockPos.containing(getX(), legacyCenterY(), getZ()); }
    public int legacyMetadata() { return metadata; }
    @Override public void tick() {
        BlockState state = getBlockState();
        if (LegacyFallingRules.material(state).name().equals("air")) { discard(); return; }
        xo = getX(); yo = getY(); zo = getZ();
        time++;
        setDeltaMovement(getDeltaMovement().add(0, -GRAVITY, 0));
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(DRAG));
        if (level().isClientSide) return;
        BlockPos pos = legacyPosition();
        if (time == 1) {
            if (!level().getBlockState(pos).is(state.getBlock())) { discard(); return; }
            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        if (onGround()) {
            setDeltaMovement(getDeltaMovement().multiply(GROUND_DRAG, -.5D, GROUND_DRAG));
            if (level().getBlockState(pos).is(Blocks.MOVING_PISTON)) return;
            discard();
            if (!cancelDrop && LegacyFallingRules.canPlace(level(), pos, state)
                    && !LegacyFallingRules.isFree(level().getBlockState(pos.below())) && level().setBlock(pos, state, 3)) {
                if (state.getBlock() instanceof LegacyFallingBlock block) block.legacyLanded(level(), pos, metadata);
                if (blockData != null && state.hasBlockEntity() && level().getBlockEntity(pos) != null) {
                    var blockEntity = level().getBlockEntity(pos);
                    var data = blockEntity.saveWithoutMetadata(level().registryAccess());
                    for (String key : blockData.getAllKeys()) if (!key.equals("x") && !key.equals("y") && !key.equals("z")) data.put(key, blockData.get(key).copy());
                    blockEntity.loadWithComponents(data, level().registryAccess()); blockEntity.setChanged();
                }
            } else if (dropItem && !cancelDrop) dropLegacyItem();
        } else if (time > 100 && (pos.getY() < level().getMinBuildHeight() + 1 || pos.getY() > level().getMaxBuildHeight()) || time > 600) {
            if (dropItem) dropLegacyItem();
            discard();
        }
    }
    private void dropLegacyItem() {
        // The old entity ignores doEntityDrops, and all these source blocks have damageDropped=0.
        ItemStack stack = new ItemStack(getBlockState().getBlock());
        var item = new net.minecraft.world.entity.item.ItemEntity(level(), getX(), legacyCenterY(), getZ(), stack);
        item.setDefaultPickUpDelay(); level().addFreshEntity(item);
    }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource ignored) {
        if (!hurtEntities) return false;
        int blocks = Mth.ceil(distance - 1F);
        if (blocks <= 0) return false;
        boolean anvil = getBlockState().is(net.minecraft.tags.BlockTags.ANVIL);
        DamageSource damage = anvil ? damageSources().anvil(this) : damageSources().fallingBlock(this);
        float amount = Math.min(Mth.floor(blocks * fallHurtAmount), fallHurtMax);
        for (Entity entity : level().getEntities(this, getBoundingBox(), entity -> true)) entity.hurt(damage, amount);
        if (anvil && random.nextFloat() < .05000000074505806D + blocks * .05D) {
            int stage = metadata >> 2, orientation = metadata & 3;
            if (++stage > 2) cancelDrop = true;
            else { metadata = orientation | stage << 2; BlockState damaged = AnvilBlock.damage(getBlockState()); if (damaged != null) setCarriedState(damaged); }
        }
        return false;
    }
    public void setHurtEntities(boolean hurt) { hurtEntities = hurt; }
    @Override public void setHurtsEntities(float amount, int maximum) { hurtEntities = true; fallHurtAmount = amount; fallHurtMax = maximum; }
    @Override public void disableDrop() { cancelDrop = true; }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Data", (byte) metadata); tag.putByte("Time", (byte) time);
        tag.putBoolean("HurtEntities", hurtEntities); tag.putFloat("FallHurtAmount", fallHurtAmount); tag.putInt("FallHurtMax", fallHurtMax);
        // Source's broken-anvil cancellation flag does not persist.
        tag.remove("CancelDrop");
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        metadata = tag.getByte("Data") & 255; time = tag.getByte("Time") & 255;
        if (tag.contains("HurtEntities", 99)) { hurtEntities = tag.getBoolean("HurtEntities"); fallHurtAmount = tag.getFloat("FallHurtAmount"); fallHurtMax = tag.getInt("FallHurtMax"); }
        else if (getBlockState().is(net.minecraft.tags.BlockTags.ANVIL)) hurtEntities = true;
        cancelDrop = false;
        if (getBlockState().hasProperty(LegacyFallingBlock.LEGACY_META)) setCarriedState(getBlockState().setValue(LegacyFallingBlock.LEGACY_META, metadata & 15));
    }
}
