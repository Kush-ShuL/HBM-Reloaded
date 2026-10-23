package com.hbm.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Original BlockBase / BlockGeneric semantics; gameplay-specialized blocks use their own classes. */
public final class BasicBlock extends Block {
    public BasicBlock(Properties properties, boolean canSpawn) {
        super(canSpawn ? properties : properties.isValidSpawn((state, level, pos, type) -> false));
    }

    public void dismantle(Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        float x = level.random.nextFloat() * .6F + .2F;
        float y = level.random.nextFloat() * .2F;
        float z = level.random.nextFloat() * .6F + .2F;
        ItemEntity item = new ItemEntity(level, pos.getX() + x, pos.getY() + y + 1, pos.getZ() + z, new ItemStack(this));
        item.setDeltaMovement((float) level.random.nextGaussian() * .05F,
                (float) level.random.nextGaussian() * .05F + .2F, (float) level.random.nextGaussian() * .05F);
        if (!level.isClientSide) level.addFreshEntity(item);
    }
}
