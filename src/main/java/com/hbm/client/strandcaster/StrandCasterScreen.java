package com.hbm.client.strandcaster;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.strandcaster.*;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x214, 34x molten fill and24px two source tanks, no automatic background blur. */
public final class StrandCasterScreen extends LegacyContainerScreen<StrandCasterMenu> {
 public StrandCasterScreen(StrandCasterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_strand_caster.png"),176,214);}
 private StrandCasterEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof StrandCasterEntity caster?caster:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,4,0xffffff,false);g.drawString(font,playerInventoryTitle,8,120,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var caster=machine();if(caster==null)return;if(caster.amount!=0&&caster.type!=null){int height=Math.min(caster.amount*79/caster.capacity(),92),color=caster.type.moltenColor();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);drawLegacyRegion(g,17,93-height,176,89-height,34,height);g.setColor(1,1,1,1);g.flush();var vc=g.bufferSource().getBuffer(StrandCasterGuiTypes.GLOW);float px=leftPos+17,py=topPos+93-height,u=176/256F,v=(89-height)/256F;var pose=g.pose().last();vc.addVertex(pose,px,py,0).setUv(u,v).setColor(1F,1F,1F,.3F);vc.addVertex(pose,px,py+height,0).setUv(u,89/256F).setColor(1F,1F,1F,.3F);vc.addVertex(pose,px+34,py+height,0).setUv(210/256F,89/256F).setColor(1F,1F,1F,.3F);vc.addVertex(pose,px+34,py,0).setUv(210/256F,v).setColor(1F,1F,1F,.3F);g.flush();}tank(g,caster.water,38);tank(g,caster.steam,89);}
 private void tank(GuiGraphics g,FluidTank tank,int bottom){int height=tank.getFill()*24/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int drawn=0;drawn<height;){int piece=Math.min(16,height-drawn);g.blit(tank.getTankType().getTexture(),leftPos+82,topPos+bottom-drawn-piece,0,16-piece,16,piece,16,16);drawn+=piece;}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var caster=machine();if(caster==null)return;if(legacyInfoHovered(x,y,16,17,36,81))g.renderTooltip(font,caster.type==null?Component.translatable("gui.hbm.foundry.empty"):Component.translatable(caster.type.descriptionId()).append(": ").append(com.hbm.material.HbmMaterials.formatAmount(caster.amount,hasShiftDown())),x,y);if(legacyInfoHovered(x,y,82,14,16,24))tankInfo(g,caster.water,x,y);if(legacyInfoHovered(x,y,82,65,16,24))tankInfo(g,caster.steam,x,y);}
 private void tankInfo(GuiGraphics g,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
}
