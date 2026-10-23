package com.hbm.client.armor;

import com.hbm.armor.HazmatContent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HazmatClient {
    private static final ResourceLocation OVERLAY=ResourceLocation.fromNamespaceAndPath("hbm","textures/misc/overlay_hazmat.png");
    private HazmatClient(){}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(HazmatContent.MENU.get(),ArmorTableScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        private final LegacyHazmatArmorModel inner=new LegacyHazmatArmorModel(.5F),outer=new LegacyHazmatArmorModel(1);
        @Override public Model getGenericArmorModel(LivingEntity living,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){var model=slot==EquipmentSlot.LEGS?inner:outer;model.configure(living,slot,parent);return model;}
        @Override public void setupModelAnimations(LivingEntity living,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
        @Override public void renderHelmetOverlay(ItemStack stack,Player player,GuiGraphics graphics,DeltaTracker deltaTracker){
            if(!stack.is(HazmatContent.HELMET.get()))return;
            RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableDepthTest();RenderSystem.depthMask(false);
            graphics.setColor(1,1,1,1);graphics.pose().pushPose();graphics.pose().translate(0,0,-90);
            graphics.blit(OVERLAY,0,0,0,0,graphics.guiWidth(),graphics.guiHeight(),graphics.guiWidth(),graphics.guiHeight());
            graphics.flush();graphics.pose().popPose();RenderSystem.depthMask(true);RenderSystem.enableDepthTest();graphics.setColor(1,1,1,1);
        }
    },HazmatContent.HELMET.get(),HazmatContent.PLATE.get(),HazmatContent.LEGS.get(),HazmatContent.BOOTS.get());}
}
