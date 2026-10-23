package com.hbm.client.reactor.icf.pellet;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.icf.pellet.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x179 PNG/slot layout,52-pixel muon and fluid strips; no invented processing delay. */
public final class IcfPelletPressScreen extends LegacyContainerScreen<IcfPelletPressMenu> {
 public IcfPelletPressScreen(IcfPelletPressMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_icf_press.png"),176,179);}
 private IcfPelletPressEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position)instanceof IcfPelletPressEntity press?press:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var press=machine();if(press==null)return;int muon=press.muon*52/IcfPelletPressEntity.MAX_MUON;drawLegacyRegion(g,28,70-muon,176,52-muon,4,muon);tank(g,press.tanks[0],44);tank(g,press.tanks[1],152);}
 private void tank(GuiGraphics g,FluidTank tank,int x){int fill=tank.getFill()*52/tank.getMaxFill();if(fill<=0)return;int color=tank.getTankType().getTint();g.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-fill,0,16-fill,16,fill,16,16);g.flush();g.setColor(1,1,1,1);RenderSystem.disableBlend();}
 private void info(GuiGraphics g,FluidTank tank,int px,int x,int y){if(!legacyInfoHovered(x,y,px,18,16,52))return;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var press=machine();if(press==null)return;info(g,press.tanks[0],44,x,y);info(g,press.tanks[1],152,x,y);if(legacyInfoHovered(x,y,62,54,16,16)&&!menu.getSlot(4).hasItem())g.renderTooltip(font,Component.translatable("gui.hbm.icf.input_vertical").withStyle(ChatFormatting.YELLOW),x,y);if(legacyInfoHovered(x,y,134,54,16,16)&&!menu.getSlot(5).hasItem())g.renderTooltip(font,Component.translatable("gui.hbm.icf.input_sides").withStyle(ChatFormatting.YELLOW),x,y);}
}
