package com.hbm.client.equipment.vaultclothing;
import com.hbm.client.armor.*;
import com.hbm.equipment.vaultclothing.VaultClothingContent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.*;
/** Original mono ModelM65/filter visibility and actual ModelGoggles/damage-overlay. No mono overlay existed. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class VaultClothingClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        event.registerItem(new IClientItemExtensions(){
            private final LegacyHazmatArmorModel model=new LegacyHazmatArmorModel(new LegacyM65Model());
            @Override public Model getGenericArmorModel(LivingEntity entity,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){model.configure(entity,slot,parent);return model;}
            @Override public void setupModelAnimations(LivingEntity entity,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
        },VaultClothingContent.MONO.get());
        event.registerItem(new IClientItemExtensions(){
            private final LegacyHazmatArmorModel model=new LegacyHazmatArmorModel(new LegacyGogglesModel());
            @Override public Model getGenericArmorModel(LivingEntity entity,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){model.configure(entity,slot,parent);return model;}
            @Override public void setupModelAnimations(LivingEntity entity,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
            @Override public void renderHelmetOverlay(ItemStack stack,Player player,GuiGraphics graphics,DeltaTracker delta){
                int index=(int)((double)stack.getDamageValue()/stack.getMaxDamage()*6D);if(index<0||index>5)index=5;
                var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/misc/overlay_goggles_"+index+".png");graphics.flush();
                RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableDepthTest();RenderSystem.depthMask(false);graphics.setColor(1,1,1,1);graphics.pose().pushPose();
                try {graphics.pose().translate(0,0,-90);graphics.blit(texture,0,0,0,0,graphics.guiWidth(),graphics.guiHeight(),graphics.guiWidth(),graphics.guiHeight());graphics.flush();}
                finally {graphics.pose().popPose();RenderSystem.depthMask(true);RenderSystem.enableDepthTest();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
            }
        },VaultClothingContent.GOGGLES.get());
    }
    private VaultClothingClient(){}
}
