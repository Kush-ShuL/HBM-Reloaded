package com.hbm.armor.liquidator;

import com.hbm.armor.GasMask;
import com.hbm.armor.GasMaskFilters;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Source full hood: no blacklist, accepts every real filter, using the unique GasMaskFilters store/table. */
public final class ArmorLiquidatorMask extends ArmorLiquidator implements GasMask {
    public ArmorLiquidatorMask(){super(Type.HELMET,"liquidator_helmet");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var held=player.getItemInHand(hand);
        if(player.isShiftKeyDown()){
            var filter=filter(held,player);
            if(!filter.isEmpty()){GasMaskFilters.remove(held);if(!player.getInventory().add(filter))player.drop(filter,true);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);}
        }
        return super.use(level,player,hand);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){GasMaskFilters.tooltip(stack,context,lines,flags);}
}
