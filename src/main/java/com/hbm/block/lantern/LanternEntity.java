package com.hbm.block.lantern;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Actual stateless TileEntityLantern: no invented inventory, fuel, switch, repair state or computer. */
public final class LanternEntity extends BlockEntity {
    public LanternEntity(BlockPos pos, BlockState state) { super(LanternContent.ENTITY.get(), pos, state); }
    public static void tick(Level level, BlockPos pos, BlockState state, LanternEntity lantern) {
        if (level.isClientSide || level.getGameTime() % 20 != 0) return;
        double x = pos.getX() + .5, y = pos.getY() + 5.5, z = pos.getZ() + .5;
        for (EntityGlyphid glyphid : level.getEntitiesOfClass(EntityGlyphid.class, new AABB(x, y, z, x, y, z).inflate(7.5)))
            glyphid.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
    }
}
