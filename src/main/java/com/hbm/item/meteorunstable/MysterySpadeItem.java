package com.hbm.item.meteorunstable;

import com.hbm.world.structure.materials.MeteorMaterialContent;
import java.util.List;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Original ItemMS; a hidden single-stack special-use item, not a vanilla shovel or durability tool. */
public final class MysterySpadeItem extends Item {
    private static final Random ITEM_RAND = new Random();
    public MysterySpadeItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.mysteryshovel.name"); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) { tooltip.add(Component.translatable("tooltip.hbm.mysteryshovel.lost")); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var pos = context.getClickedPos();
        if (!level.isClientSide && level.getBlockState(pos).is(MeteorMaterialContent.DIRT.get())) {
            level.destroyBlock(pos, false);
            for (int metadata = 1; metadata <= 3; metadata++) {
                var fragment = MeteorUnstableContent.metastable(metadata);
                float x = ITEM_RAND.nextFloat() * .8F + .1F, y = ITEM_RAND.nextFloat() * .8F + .1F, z = ITEM_RAND.nextFloat() * .8F + .1F;
                var entity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, fragment);
                entity.setDeltaMovement((float)ITEM_RAND.nextGaussian() * .05F, (float)ITEM_RAND.nextGaussian() * .05F + .2F, (float)ITEM_RAND.nextGaussian() * .05F);
                level.addFreshEntity(entity);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
