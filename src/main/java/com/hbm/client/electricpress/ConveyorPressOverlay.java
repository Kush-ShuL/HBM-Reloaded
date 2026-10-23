package com.hbm.client.electricpress;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.electricpress.ConveyorPressBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Source installed-stamp/HE look information with the user's real bar and no black background. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ConveyorPressOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof ConveyorPressBlock block))return;var machine=block.core(mc.level,hit.getBlockPos());if(machine==null)return;var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-10;graphics.drawString(font,Component.translatable("block.hbm.machine_conveyor_press"),x,y,0xffff00,true);y+=12;int width=(int)(178*Math.clamp(machine.getPower()/50000D,0,1));if(width>0)graphics.fill(x+1,y+1,x+1+width,y+11,0xa0f2c94c);graphics.drawString(font,Component.translatable("jade.hbm.energy",machine.getPower(),50000),x+2,y+1,0xffffff,true);graphics.drawString(font,ElectricPressClient.stamp(machine.renderedStamp),x,y+14,0xffffff,true);}
}
