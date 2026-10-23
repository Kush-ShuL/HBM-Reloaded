package com.hbm.item.tool;

import com.hbm.block.pipe.NativePipeBlockEntity;
import com.hbm.pneumatic.anchor.AnchorInteractions;
import com.hbm.multiblock.MultiblockTool;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/** X5846 ItemWrench: two-click native pipeline links and the original hit knockback. */
public final class WrenchItem extends SwordItem implements MultiblockTool,com.hbm.item.legacysword.LegacySwordBlocking {
    private static final Tier STEEL = new Tier() {
        @Override public int getUses() { return 750; }
        @Override public float getSpeed() { return 8F; }
        @Override public float getAttackDamageBonus() { return 2F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(HbmContent.item("ingot_steel")); }
    };
    public WrenchItem() { super(STEEL, new Properties().attributes(SwordItem.createAttributes(STEEL, 4, -2.4F))); }
    @Override public boolean isHandDrill(ItemStack stack, Player player) { return false; }
    @Override public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) { return net.minecraft.world.item.UseAnim.BLOCK; }
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity) { return 72000; }
    @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level,Player player,InteractionHand hand) { player.startUsingItem(hand);return net.minecraft.world.InteractionResultHolder.consume(player.getItemInHand(hand)); }
    @Override public boolean canPerformAction(ItemStack stack,net.neoforged.neoforge.common.ItemAbility ability) { return !ability.equals(net.neoforged.neoforge.common.ItemAbilities.SWORD_SWEEP)&&!ability.equals(net.neoforged.neoforge.common.ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,ability); }
    @Override public float getDestroySpeed(ItemStack stack,net.minecraft.world.level.block.state.BlockState state) { if(state.is(net.minecraft.world.level.block.Blocks.COBWEB))return 15;return switch(com.hbm.block.fluid.LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;}; }
    @Override public boolean isCorrectToolForDrops(ItemStack stack,net.minecraft.world.level.block.state.BlockState state) { return state.is(net.minecraft.world.level.block.Blocks.COBWEB); }

    public static BlockPos start(ItemStack stack) {
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return data.contains("x") ? new BlockPos(data.getInt("x"), data.getInt("y"), data.getInt("z")) : null;
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer(); var level = context.getLevel(); var pos = context.getClickedPos(); var stack = context.getItemInHand();
        if (player == null || player.isShiftKeyDown() || !AnchorInteractions.isAnchor(level.getBlockEntity(pos))) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        var second=level.getBlockEntity(pos);
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        BlockPos previous = start(stack);
        if (previous == null) {
            data.putInt("x", pos.getX()); data.putInt("y", pos.getY()); data.putInt("z", pos.getZ());
            player.displayClientMessage(Component.translatable("message.hbm.pneumatic.pipe.start"), false);
        } else {
            if (AnchorInteractions.isAnchor(level.getBlockEntity(previous))) {
                var first=level.getBlockEntity(previous);
                int status = AnchorInteractions.canLink(first, second);
                if (status == 0) { AnchorInteractions.addConnection(first,pos); AnchorInteractions.addConnection(second,previous); }
                player.displayClientMessage(Component.translatable("message.hbm.pneumatic.pipe.status."+status), false);
            } else player.displayClientMessage(Component.translatable("message.hbm.pneumatic.pipe.error"), false);
            data.remove("x"); data.remove("y"); data.remove("z");
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data)); player.swing(context.getHand(), true);
        return InteractionResult.CONSUME;
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity attacker) {
        var look = attacker.getLookAngle().scale(0.5);
        entity.setDeltaMovement(entity.getDeltaMovement().add(look)); entity.hasImpulse = true;
        entity.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), net.minecraft.sounds.SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.PLAYERS, 3F, 0.75F);
        return false;
    }
    @Override public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) { /* Original hitEntity returns false without charging sword durability. */ }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BlockPos pos = start(stack);
        if (pos == null) tooltip.add(Component.translatable("tooltip.hbm.pneumatic.wrench.connect"));
        else { tooltip.add(Component.translatable("tooltip.hbm.pneumatic.wrench.x",pos.getX())); tooltip.add(Component.translatable("tooltip.hbm.pneumatic.wrench.y",pos.getY())); tooltip.add(Component.translatable("tooltip.hbm.pneumatic.wrench.z",pos.getZ())); }
    }
}
