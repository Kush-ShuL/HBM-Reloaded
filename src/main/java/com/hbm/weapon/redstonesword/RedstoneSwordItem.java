package com.hbm.weapon.redstonesword;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemSwordAbility;
import com.hbm.weapon.legacymelee.LegacySwordWear;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Source RedstoneSword extends ordinary stone ItemSword, with its exact fourteen-wear placement quirk. */
public final class RedstoneSwordItem extends ItemSwordAbility {
    private final RandomSource itemRandom = RandomSource.create();
    private enum OriginalStoneTier implements Tier {
        INSTANCE;
        @Override public int getUses() { return 131; }
        @Override public float getSpeed() { return 4F; }
        @Override public float getAttackDamageBonus() { return 1F; }
        @Override public int getEnchantmentValue() { return 5; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_STONE_TOOL; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Blocks.COBBLESTONE); }
    }
    public RedstoneSwordItem() { super("redstone_sword", 5F, 0, OriginalStoneTier.INSTANCE, new AvailableAbilities()); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        var level = context.getLevel(); var stack = context.getItemInHand();
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());
        if (!player.mayUseItemAt(target, context.getClickedFace(), stack)) return InteractionResult.PASS;
        if (level.isEmptyBlock(target)) {
            level.playSound(player, target.getX() + .5, target.getY() + .5, target.getZ() + .5,
                SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1F, itemRandom.nextFloat() * .4F + .8F);
            // Source calls setBlock directly, without checking wire survival or consuming redstone dust.
            level.setBlock(target, Blocks.REDSTONE_WIRE.defaultBlockState(), 3);
        }
        // Even occupied targets consume14wear after successful permission; no guessed success-only discount.
        LegacySwordWear.damage(stack, 14, player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
