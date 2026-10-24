package com.hbm.client.pneumatic;
import com.hbm.pneumatic.anchor.PneumaticAnchorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original BasePipeAnchor ILookOverlay empty-body yellow title. Existing PipeOverlay alone owns wrench-distance HUD. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PneumaticOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof PneumaticAnchorBlockEntity anchor))return;var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2;var title=anchor.getBlockState().getBlock().getName();g.drawString(mc.font,title,x+1,y-9,0x404000,false);g.drawString(mc.font,title,x,y-10,0xFFFF00,false);}
 private PneumaticOverlay(){}
}
