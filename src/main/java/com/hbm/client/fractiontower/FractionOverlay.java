package com.hbm.client.fractiontower;
import com.hbm.machine.fractiontower.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original crosshair fluid information, with the user's native progress-bar presentation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class FractionOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.screen!=null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof FractionBlock block)||block.spacer)return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof FractionEntity tower))return;var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-22;g.drawString(mc.font,Component.translatable("block.hbm.machine_fraction_tower"),x,y,0xffff00,true);for(int i=0;i<3;i++){y+=13;var t=tower.tanks[i];g.fill(x+1,y+1,x+1+(int)(178D*t.getFill()/t.getMaxFill()),y+11,0xa0000000|t.getTankType().getColor()&0xffffff);g.drawString(mc.font,Component.literal(i==0?"\u00a7a-> \u00a7r":"\u00a7c<- \u00a7r").append(t.getTankType().displayName()).append(" "+t.getFill()+"/"+t.getMaxFill()+" mB"),x+2,y+1,0xffffff,true);}}
}
