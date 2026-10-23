package com.hbm.entity.mob.fbi;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
/** Actual unpowered75dur ArmorFSB three-piece suit, not RPA/powered AJR. */
public final class PaaArmorItem extends SourceFullSetArmorItem {
    private final String name;
    public PaaArmorItem(Type type,String name){super(PaaContent.MATERIAL,type,75);this.name=name;}
    @Override public boolean noHelmet(){return true;}
    @Override public List<MobEffectInstance> fsbEffects(){return List.of(new MobEffectInstance(MobEffects.DIG_SPEED,20,0));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return ResourceLocation.parse("hbm:textures/armor/paa_"+(slot==EquipmentSlot.LEGS?2:1)+".png");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flags){text.add(Component.translatable("armor.fullSetBonus").withStyle(net.minecraft.ChatFormatting.GOLD));text.add(Component.translatable("effect.minecraft.haste").withStyle(net.minecraft.ChatFormatting.AQUA));}
}
