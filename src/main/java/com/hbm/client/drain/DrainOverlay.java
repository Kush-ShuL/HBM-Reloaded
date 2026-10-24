package com.hbm.client.drain;
import com.hbm.machine.drain.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original source look overlay remains visible next to the crosshair, independently of Jade. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class DrainOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.screen!=null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof DrainBlock block))return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof DrainEntity drain))return;var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-10;g.drawString(mc.font,Component.translatable("block.hbm.machine_drain"),x,y,0xffff00,true);y+=12;var tank=drain.tank;g.fill(x+1,y+1,x+1+(int)(178D*tank.getFill()/tank.getMaxFill()),y+11,0xa0000000|tank.getTankType().getColor()&0xffffff);g.drawString(mc.font,Component.literal("\u00a7a-> \u00a7r").append(tank.getTankType().displayName()).append(" "+tank.getFill()+"/"+tank.getMaxFill()+" mB"),x+2,y+1,0xffffff,true);}
}
