package com.hbm.client.exposurechamber;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.exposurechamber.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x186 screen and all original256-atlas coordinates; use chosen Minecraft font. */
public final class ExposureChamberScreen extends LegacyContainerScreen<ExposureChamberMenu> {
 public ExposureChamberScreen(ExposureChamberMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_exposure_chamber.png"),176,186);}
 private ExposureChamberEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof ExposureChamberEntity chamber?chamber:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,92,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var chamber=machine();if(chamber==null)return;int p=chamber.progress*42/(chamber.processTime+1),c=chamber.savedParticles*16/8,e=chamber.power.multiply(java.math.BigInteger.valueOf(34)).divide(ExposureChamberEntity.MAX_POWER).intValue();drawLegacyRegion(g,36,39,192,0,p,10);drawLegacyRegion(g,26,52-c,192,26-c,9,c);drawLegacyRegion(g,152,52-e,176,34-e,16,e);if(chamber.power.compareTo(java.math.BigInteger.valueOf(chamber.consumption))>=0)drawLegacyRegion(g,156,4,176,34,9,12);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var chamber=machine();if(chamber==null)return;if(legacyInfoHovered(x,y,152,18,16,34))g.renderTooltip(font,Component.literal(chamber.power+" / "+ExposureChamberEntity.MAX_POWER+" HE"),x,y);if(legacyInfoHovered(x,y,26,36,9,16))g.renderTooltip(font,Component.literal(chamber.savedParticles+" / 8"),x,y);}
}
