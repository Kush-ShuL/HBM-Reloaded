package com.hbm.client.crystallizer;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.crystallizer.*;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Source176x204, original pixel positions and pressure selection overlay8x124 preserved. */
public final class CrystallizerScreen extends LegacyContainerScreen<CrystallizerMenu> {
 public CrystallizerScreen(CrystallizerMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_crystallizer_alt.png"),176,204);}
 CrystallizerBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof CrystallizerBlockEntity be?be:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int power=(int)(menu.power()*52/1000000);drawLegacyRegion(g,152,70-power,176,64-power,16,power);var machine=machine();if(machine==null)return;drawLegacyRegion(g,80,47,176,0,machine.progress*28/Math.max(1,machine.duration),12);g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos+117,topPos+22,0,32,8,8,256,256);tank(g,machine.tank,35);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-height,0,16-height,16,height,16,16);g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;if(legacyInfoHovered(x,y,35,18,16,52)){var t=m.tank;var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));FluidInfo.append(t.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,152,18,16,52))g.renderTooltip(font,Component.literal(menu.power()+"/1000000HE"),x,y);if(legacyInfoHovered(x,y,117,22,8,8))g.renderComponentTooltip(font,java.util.List.of(Component.translatable("desc.gui.upgrade"),Component.translatable("desc.gui.upgrade.speed"),Component.translatable("desc.gui.upgrade.effectiveness"),Component.translatable("desc.gui.upgrade.overdrive")),leftPos+200,topPos+45);}
}
