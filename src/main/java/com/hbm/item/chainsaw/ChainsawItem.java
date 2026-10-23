package com.hbm.item.chainsaw;

import com.hbm.fluid.HbmFluids;
import com.hbm.item.toolability.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;

/** Actual ItemChainsaw, not a gun, battery or replacement axe/harvest engine. */
public final class ChainsawItem extends ItemToolAbilityFueled {
    public ChainsawItem() {
        super("chainsaw",25F,-.05D,ChainsawTier.INSTANCE,Type.AXE,
                new AvailableAbilities().add(AvailableAbilities.Ability.SILK,0).add(AvailableAbilities.Ability.RECURSION,2)
                        .add(AvailableAbilities.Ability.CHAINSAW,1).add(AvailableAbilities.Ability.BEHEADER,0),
                5000,1,250,HbmFluids.fromName("DIESEL"),HbmFluids.fromName("DIESEL_CRACK"),
                HbmFluids.fromName("KEROSENE"),HbmFluids.fromName("BIOFUEL"),HbmFluids.fromName("GASOLINE"),
                HbmFluids.fromName("GASOLINE_LEADED"),HbmFluids.fromName("PETROIL"),HbmFluids.fromName("PETROIL_LEADED"),
                HbmFluids.fromName("COALGAS"),HbmFluids.fromName("COALGAS_LEADED"));
    }
    @Override public boolean sourceCanShear(Player player,ItemStack stack,Level level,BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof IShearable target&&target.isShearable(player,stack,level,pos);
    }
    @Override public boolean onEntitySwing(ItemStack stack,LivingEntity entity,InteractionHand hand) {
        // Source checks RAW DAMAGE, not fuel. An empty saw can still play this harmless swing animation.
        if(hand==InteractionHand.MAIN_HAND&&entity instanceof ServerPlayer player&&stack.getDamageValue()<stack.getMaxDamage())ChainsawAnimationPacket.send(player);
        return false;
    }
    @Override public boolean shouldCauseReequipAnimation(ItemStack previous,ItemStack current,boolean slotChanged) {
        // Exact1.7 equipped item/damage comparison. Fuel/preset NBT updates must not repeatedly lower the held model.
        return previous.getItem()!=current.getItem()||previous.getDamageValue()!=current.getDamageValue();
    }
}
