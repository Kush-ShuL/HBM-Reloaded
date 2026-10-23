package com.hbm.item.survivalloot;

import com.hbm.armor.*;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual ArmorGasMask's M65 branch: real iron armor, source filter blacklist, removal and original model texture. */
public final class M65GasMaskItem extends ArmorItem implements GasMask {
    public M65GasMaskItem(){super(ArmorMaterials.IRON,Type.HELMET,new Properties().stacksTo(1).durability(165));}
    @Override public List<Hazard> blacklist(ItemStack mask,LivingEntity wearer){return List.of(Hazard.GAS_BLISTERING);}
    @Override public boolean isValidRepairItem(ItemStack armor,ItemStack ingredient){return ingredient.is(Items.IRON_INGOT);}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return ResourceLocation.parse("hbm:textures/models/modelm65.png");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var held=player.getItemInHand(hand);
        if(player.isShiftKeyDown()){var installed=filter(held,player);if(!installed.isEmpty()){GasMaskFilters.remove(held);if(!player.getInventory().add(installed))player.drop(installed,true);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);}}
        // ItemArmor.onItemRightClick1.7 equips only into an empty slot; modern automatic swapping would discard that condition.
        if(player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()){player.setItemSlot(EquipmentSlot.HEAD,held.copy());held.setCount(0);return InteractionResultHolder.sidedSuccess(held,level.isClientSide);}
        return InteractionResultHolder.pass(held);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){GasMaskFilters.tooltip(stack,context,lines,flags);lines.add(Component.translatable("hazard.neverProtects").withStyle(ChatFormatting.RED));lines.add(Component.literal(" -").append(Component.translatable("hazard.corrosive")).withStyle(ChatFormatting.DARK_RED));}
}
