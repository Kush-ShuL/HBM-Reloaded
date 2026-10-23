package com.hbm.client.armor;

import com.hbm.armor.HazmatVariants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HazmatVariantClient {
    private HazmatVariantClient(){}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        private final LegacyHazmatArmorModel inner=new LegacyHazmatArmorModel(.5F),outer=new LegacyHazmatArmorModel(1),mask=new LegacyHazmatArmorModel(new LegacyM65Model());
        @Override public Model getGenericArmorModel(LivingEntity living,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){var model=slot==EquipmentSlot.HEAD?mask:slot==EquipmentSlot.LEGS?inner:outer;model.configure(living,slot,parent);return model;}
        @Override public void setupModelAnimations(LivingEntity living,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
    },HazmatVariants.RED_HELMET.get(),HazmatVariants.RED_PLATE.get(),HazmatVariants.RED_LEGS.get(),HazmatVariants.RED_BOOTS.get(),HazmatVariants.GREY_HELMET.get(),HazmatVariants.GREY_PLATE.get(),HazmatVariants.GREY_LEGS.get(),HazmatVariants.GREY_BOOTS.get());}
}
