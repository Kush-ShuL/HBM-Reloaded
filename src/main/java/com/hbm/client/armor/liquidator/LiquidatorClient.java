package com.hbm.client.armor.liquidator;

import com.hbm.armor.liquidator.LiquidatorContent;
import com.hbm.client.armor.LegacyHazmatArmorModel;
import com.hbm.client.armor.LegacyM65Model;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Same original ModelM65/ModelBiped implementation, with real Liquidator skins and original fullscreen dark overlay. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LiquidatorClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        private final LegacyHazmatArmorModel mask=new LegacyHazmatArmorModel(new LegacyM65Model()),outer=new LegacyHazmatArmorModel(1),inner=new LegacyHazmatArmorModel(.5F);
        @Override public Model getGenericArmorModel(LivingEntity living,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){var model=slot==EquipmentSlot.HEAD?mask:slot==EquipmentSlot.LEGS?inner:outer;model.configure(living,slot,parent);return model;}
        @Override public void setupModelAnimations(LivingEntity living,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
        @Override public void renderHelmetOverlay(ItemStack stack,Player player,GuiGraphics graphics,DeltaTracker delta){
            if(!stack.is(LiquidatorContent.HELMET.get()))return;
            RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableDepthTest();RenderSystem.depthMask(false);graphics.setColor(1,1,1,1);
            graphics.pose().pushPose();graphics.pose().translate(0,0,-90);
            graphics.blit(LiquidatorContent.id("textures/misc/overlay_dark.png"),0,0,0,0,graphics.guiWidth(),graphics.guiHeight(),graphics.guiWidth(),graphics.guiHeight());
            graphics.flush();graphics.pose().popPose();RenderSystem.depthMask(true);RenderSystem.enableDepthTest();graphics.setColor(1,1,1,1);
        }
    },LiquidatorContent.HELMET.get(),LiquidatorContent.PLATE.get(),LiquidatorContent.LEGS.get(),LiquidatorContent.BOOTS.get());}
    private LiquidatorClient(){}
}
