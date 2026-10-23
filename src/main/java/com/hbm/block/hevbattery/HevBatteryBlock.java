package com.hbm.block.hevbattery;

import com.hbm.armor.t51.T51Runtime;
import com.hbm.damage.PoweredFullSuitArmor;
import com.hbm.item.battery.HbmBattery;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Actual HEVBattery world pickup: source full-set gate, per-piece150000HE and consumed block. */
public final class HevBatteryBlock extends Block {
    private static final VoxelShape SHAPE = box(6, 0, 6, 10, 6, 10);
    public HevBatteryBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(.5F, .15F).noOcclusion().requiresCorrectToolForDrops().lightLevel(state -> 10));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (T51Runtime.fullSet(player, false) && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof PoweredFullSuitArmor) {
            for (var slot : T51Runtime.ORDER) {
                var armor = player.getItemBySlot(slot);
                if (armor.getItem() instanceof HbmBattery battery)
                    battery.setCharge(armor, Math.min(battery.getCharge(armor) + 150000L, battery.getMaxCharge(armor)));
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("item.battery"), SoundSource.PLAYERS, 1F, 1F);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return InteractionResult.CONSUME;
    }
}
