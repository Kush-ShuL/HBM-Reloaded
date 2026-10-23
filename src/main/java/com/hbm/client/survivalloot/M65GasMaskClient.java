package com.hbm.client.survivalloot;
import com.hbm.client.armor.*;
import com.hbm.item.survivalloot.SurvivalLootContent;
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
/** Original M65 helmet mesh and six progressive damage overlays, independent from installed filter wear. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class M65GasMaskClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        private final LegacyHazmatArmorModel model=new LegacyHazmatArmorModel(new LegacyM65Model());
        @Override public Model getGenericArmorModel(LivingEntity living,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> parent){model.configure(living,slot,parent);return model;}
        @Override public void setupModelAnimations(LivingEntity living,ItemStack stack,EquipmentSlot slot,Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
        @Override public void renderHelmetOverlay(ItemStack stack,Player player,GuiGraphics graphics,DeltaTracker delta){
            int index=Math.min((int)((double)stack.getDamageValue()/stack.getMaxDamage()*6),5);var texture=ResourceLocation.parse("hbm:textures/misc/overlay_goggles_"+index+".png");
            RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.disableDepthTest();RenderSystem.depthMask(false);graphics.setColor(1,1,1,1);graphics.pose().pushPose();graphics.pose().translate(0,0,-90);graphics.blit(texture,0,0,0,0,graphics.guiWidth(),graphics.guiHeight(),graphics.guiWidth(),graphics.guiHeight());graphics.flush();graphics.pose().popPose();RenderSystem.depthMask(true);RenderSystem.enableDepthTest();graphics.setColor(1,1,1,1);
        }
    },SurvivalLootContent.M65.get());}
    private M65GasMaskClient(){}
}
