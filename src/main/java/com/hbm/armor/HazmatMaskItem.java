package com.hbm.armor;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

/** Original yellow hood: unrestricted installed filter, independent radiation armor exponent. */
public class HazmatMaskItem extends HazmatArmorItem implements GasMask {
    public HazmatMaskItem(Holder<ArmorMaterial> material){super(material,Type.HELMET);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack held=player.getItemInHand(hand);
        if(player.isShiftKeyDown()){
            ItemStack filter=filter(held,player);
            if(!filter.isEmpty()){GasMaskFilters.remove(held);if(!player.getInventory().add(filter))player.drop(filter,true);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);}
        }
        return super.use(level,player,hand);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){GasMaskFilters.tooltip(stack,context,lines,flags);super.appendHoverText(stack,context,lines,flags);}
}
