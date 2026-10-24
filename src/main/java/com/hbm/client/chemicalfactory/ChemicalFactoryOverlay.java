package com.hbm.client.chemicalfactory;
import com.hbm.fluid.FluidTank;
import com.hbm.machine.chemicalfactory.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
/** Original coolant/recipe-field mouth distinction; actual coolant amounts use user-requested capacity bars. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ChemicalFactoryOverlay {
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.screen!=null||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockState(hit.getBlockPos()).getBlock() instanceof ChemicalFactoryBlock block))return;var core=block.findCore(mc.level,hit.getBlockPos());if(core==null||!(mc.level.getBlockEntity(core) instanceof ChemicalFactoryEntity factory))return;boolean coolant=factory.coolantPosition(hit.getBlockPos());int field=factory.fieldAtPort(hit.getBlockPos());if(!coolant&&field<0)return;var graphics=event.getGuiGraphics();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2-22;graphics.drawString(mc.font,Component.translatable("block.hbm.machine_chemical_factory"),x,y,0xffff00,true);if(coolant){bar(graphics,mc,factory.water,x,y+13,true);bar(graphics,mc,factory.lps,x,y+26,false);}else graphics.drawString(mc.font,Component.literal("\u00a7e-> \u00a7r").append(Component.translatable("gui.hbm.chemical_factory.field",field+1)),x,y+13,0xffffff,true);}
 private static void bar(GuiGraphics graphics,Minecraft mc,FluidTank tank,int x,int y,boolean input){graphics.fill(x+1,y+1,x+1+(int)(178D*tank.getFill()/tank.getMaxFill()),y+11,0xa0000000|tank.getTankType().getColor()&0xffffff);graphics.drawString(mc.font,Component.literal(input?"\u00a7a-> \u00a7r":"\u00a7c<- \u00a7r").append(tank.getTankType().displayName()).append(" "+tank.getFill()+"/"+tank.getMaxFill()+" mB"),x+2,y+1,0xffffff,true);}
}
