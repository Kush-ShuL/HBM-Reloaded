package com.hbm.client.rotaryfurnace;
import com.hbm.fluid.FluidTank;
import com.hbm.machine.rotaryfurnace.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Source exact position-sensitive steam/process/fuel mouths; no empty black crosshair backing. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RotaryFurnaceOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.screen!=null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof RotaryFurnaceBlock block))return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof RotaryFurnaceEntity furnace))return;var facing=DummyableBlock.facing(furnace.getBlockState());boolean steam=at(core,facing,-1,-1,hit.getBlockPos())||at(core,facing,-1,-2,hit.getBlockPos()),process=at(core,facing,1,2,hit.getBlockPos())||at(core,facing,-1,2,hit.getBlockPos()),fuel=at(core,facing,1,1,hit.getBlockPos());if(!steam&&!process&&!fuel)return;var g=event.getGuiGraphics();int x=g.guiWidth()/2+8,y=g.guiHeight()/2-22;g.drawString(mc.font,Component.translatable("block.hbm.machine_rotary_furnace"),x,y,0xffff00,true);int line=y+13;if(steam){bar(g,mc,furnace.tanks[1],x,line,true);line+=13;bar(g,mc,furnace.tanks[2],x,line,false);line+=13;}if(process){bar(g,mc,furnace.tanks[0],x,line,true);line+=13;}if(fuel)g.drawString(mc.font,Component.literal("\u00a7e-> \u00a7r").append(Component.translatable("gui.hbm.rotary_furnace.fuel")),x,line,0xffffff,true);}
 private static boolean at(BlockPos core,Direction facing,int forward,int side,BlockPos hit){return core.relative(facing,forward).relative(RotaryFurnaceEntity.turnDown(facing),side).equals(hit);}
 private static void bar(GuiGraphics g,Minecraft mc,FluidTank tank,int x,int y,boolean input){if(tank.getFill()>0)g.fill(x+1,y+1,x+1+(int)(178D*tank.getFill()/tank.getMaxFill()),y+11,0xa0000000|tank.getTankType().getColor()&0xffffff);g.drawString(mc.font,Component.literal(input?"\u00a7a-> \u00a7r":"\u00a7c<- \u00a7r").append(tank.getTankType().displayName()).append(" "+tank.getFill()+"/"+tank.getMaxFill()+" mB"),x+2,y+1,0xffffff,true);}
}
