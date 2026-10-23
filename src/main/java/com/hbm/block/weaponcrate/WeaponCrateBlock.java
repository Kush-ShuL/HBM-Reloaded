package com.hbm.block.weaponcrate;

import com.hbm.block.crate.CrateContent;
import com.hbm.block.falling.FallingSpec;
import com.hbm.block.falling.LegacyFallingBlock;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.loot.LegacyCrateLoot;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Source BlockCrate: exact shared1.7 falling engine, crowbar-only opening and the existing sole weaponList sampler. */
public final class WeaponCrateBlock extends LegacyFallingBlock {
    public WeaponCrateBlock() { super(new FallingSpec("crate_weapon", "BlockCrate", "wood", "wood", "crate_weapon", 5F, 6F, false, false)); }
    @Override protected MapCodec<? extends LegacyFallingBlock> codec() { return MapCodec.unit(this); }
    @Override protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!held.is(CrateContent.CROWBAR.get())) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (world instanceof ServerLevel server) {
            dropItems(server, pos);
            world.removeBlock(pos, false);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HbmSounds.get("block.crateBreak"), SoundSource.BLOCKS, .5F, 1F);
        }
        // Original crowbar is neither consumed nor damaged, even in creative; modern clicked hand is respected.
        return ItemInteractionResult.sidedSuccess(world.isClientSide);
    }
    public void dropItems(ServerLevel world, BlockPos pos) {
        var random = new Random(); // Exactly one new source-local java.util.Random per opening, independent of world.random.
        // The shared sampler performs ALL source weighted choices before the following spawn-offset/Gaussian loop.
        for (var stack : LegacyCrateLoot.contents(world, "weaponList", random)) {
            float x = random.nextFloat() * .8F + .1F, y = random.nextFloat() * .8F + .1F, z = random.nextFloat() * .8F + .1F;
            var drop = new ItemEntity(world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, stack);
            drop.setDeltaMovement((float)random.nextGaussian() * .05F, (float)random.nextGaussian() * .05F + .2F, (float)random.nextGaussian() * .05F);
            world.addFreshEntity(drop);
        }
    }
    @Override public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) { return new ItemStack(this); }
}
