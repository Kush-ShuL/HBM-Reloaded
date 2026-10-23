package com.hbm.client.network.separator;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.network.separator.NetworkSeparatorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original ILookOverlay remains available independently of Jade. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NetworkSeparatorOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof NetworkSeparatorBlock block))return;var tile=block.core(mc.level,hit.getBlockPos());if(tile==null)return;var g=event.getGuiGraphics();var f=LegacyFonts.get();int x=g.guiWidth()/2+8,y=g.guiHeight()/2;var name=block.getName();g.drawString(f,name,x+1,y-9,0x404000,false);g.drawString(f,name,x,y-10,0xffff00,false);for(var line:NetworkSeparatorReadout.lines(NetworkSeparatorReadout.data(tile))){g.drawString(f,line,x,y,0xffffff,true);y+=10;}}
 private NetworkSeparatorOverlay(){}
}
