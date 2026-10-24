package com.hbm.client.generator.steamengine;
import com.hbm.machine.generator.steamengine.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original no-GUI look overlay, using the user's requested live resource bars. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SteamEngineOverlay {
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof SteamEngineBlock block))return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof SteamEngineBlockEntity engine))return;var graphics=event.getGuiGraphics();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-10;graphics.drawString(mc.font,Component.translatable("block.hbm.machine_steam_engine"),x,y,0xffff00,true);y+=12;for(var tank:engine.tanks){bar(graphics,x,y,180,(double)tank.getFill()/tank.getMaxFill(),tank.getTankType().getColor());graphics.drawString(mc.font,tank.getTankType().displayName().getString()+" "+tank.getFill()+"/"+tank.getMaxFill()+"mB",x+2,y+1,0xffffff,true);y+=14;}}
    private static void bar(GuiGraphics graphics,int x,int y,int width,double percent,int color){graphics.fill(x+1,y+1,x+1+(int)((width-2)*Math.max(0,Math.min(1,percent))),y+11,0xa0000000|color&0xffffff);}
}
