package com.hbm.client.compressor;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.compressor.*;
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
public final class CompressorScreen extends LegacyContainerScreen<CompressorMenu> {
 public CompressorScreen(CompressorMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_compressor.png"),176,204);}
 CompressorBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof CompressorBlockEntity be?be:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,0xc7c1a3,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var machine=machine();if(machine==null)return;if(menu.power()>=machine.powerRequirement)drawLegacyRegion(g,156,4,176,52,9,12);drawLegacyRegion(g,43+machine.tanks[0].getPressure()*11,46,193,18,8,124);int progress=machine.progress*55/Math.max(1,machine.processTime);drawLegacyRegion(g,42,26,192,0,progress,17);int power=(int)(menu.power()*52/100000);drawLegacyRegion(g,152,70-power,176,52-power,16,power);tank(g,machine.tanks[0],17);tank(g,machine.tanks[1],107);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-height,0,16-height,16,height,16,16);g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,i==0?17:107,18,16,52)){var t=m.tanks[i];var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));lines.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(t.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,152,18,16,52))g.renderTooltip(font,Component.literal(menu.power()+"/100000HE"),x,y);for(int i=0;i<5;i++)if(legacyInfoHovered(x,y,43+i*11,46,8,14))g.renderTooltip(font,Component.literal(i+" PU -> "+(i+1)+" PU"),x,y);}
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);for(int i=0;i<5;i++)if(legacyInfoHovered(x,y,43+i*11,46,8,14)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new CompressorControl(menu.containerId,i));return true;}return handled;}
}
