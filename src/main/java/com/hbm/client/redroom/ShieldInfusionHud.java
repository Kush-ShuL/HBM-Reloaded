package com.hbm.client.redroom;
import com.hbm.item.redroom.ShieldInfusion;
import com.hbm.client.gui.font.LegacyFonts;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Exact RenderScreenOverlay shield81x9,source UV146/147 and five-pass number; reserves original10leftHeight. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ShieldInfusionHud {
 @SubscribeEvent(receiveCanceled=true) public static void render(RenderGuiLayerEvent.Pre event){if(!event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL))return;var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.options.hideGui)return;float maximum=ShieldInfusion.maximum(mc.player);if(maximum<=0)return;float shield=ShieldInfusion.data(mc.player).shield;var g=event.getGuiGraphics();int left=g.guiWidth()/2-91,top=g.guiHeight()-mc.gui.leftHeight;var texture=ResourceLocation.parse("hbm:textures/misc/overlay_misc.png");g.blit(texture,left,top,146,0,81,9,256,256);g.blit(texture,left+1,top,147,9,(int)Math.ceil(shield*79/maximum),9,256,256);String label=Double.toString(((int)(shield*10))/10D);var font=LegacyFonts.get();int x=left+40-font.width(label)/2,y=top+1;g.drawString(font,label,x+1,y,0,false);g.drawString(font,label,x-1,y,0,false);g.drawString(font,label,x,y-1,0,false);g.drawString(font,label,x,y+1,0,false);g.drawString(font,label,x,y,0xffff80,false);mc.gui.leftHeight+=10;}
 private ShieldInfusionHud(){}
}
