package com.hbm.client.electrolyser;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.*;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.electrolyser.*;
import com.hbm.material.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Actual two210x204 source GUI layouts, native font and original tank/molten/power/progress UV. */
public final class ElectrolyserScreen extends LegacyContainerScreen<ElectrolyserMenu> {
 public ElectrolyserScreen(ElectrolyserMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_electrolyser_"+(menu.metal?"metal":"fluid")+".png"),210,204);}
 private ElectrolyserBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ElectrolyserBlockEntity m?m:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,105-font.width(title)/2-16,7,0xffffff,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int power=(int)(menu.power()*89/20000000);if(power>0)drawLegacyRegion(g,186,107-power,210,89-power,16,power);var m=machine();if(m==null)return;
  if(menu.power()>=(menu.metal?m.usageOre:m.usageFluid))drawLegacyRegion(g,190,4,226,menu.metal?25:40,9,12);
  if(menu.metal){molten(g,m.leftStack,58,m.maxMaterial);molten(g,m.rightStack,96,m.maxMaterial);int p=m.processOreTime<=0?0:m.progressOre*26/m.processOreTime;if(p>0)drawLegacyRegion(g,7,71-p,226,25-p,22,p);tank(g,m.tanks[3],36);}
  else{int p=m.processFluidTime<=0?0:m.progressFluid*41/m.processFluidTime;if(p>0)drawLegacyRegion(g,62,26,226,0,12,p);tank(g,m.tanks[0],42);tank(g,m.tanks[1],96);tank(g,m.tanks[2],116);}
 }
 private void molten(GuiGraphics g,MaterialStack material,int x,int maximum){if(material==null)return;int height=material.amount*42/maximum,color=material.material.moltenColor();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);if(height>0)drawLegacyRegion(g,x,60-height,210,131-height,34,height);g.setColor(1,1,1,1);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()<=0||tank.getTankType()==HbmFluids.NONE)return;int height=tank.getFill()*52/tank.getMaxFill();int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int y=0;y<height;y+=16){int h=Math.min(16,height-y);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-y-h,0,16-h,16,h,16,16);}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m!=null){if(menu.metal){if(legacyInfoHovered(x,y,36,18,16,52))tankInfo(g,m.tanks[3],x,y);if(legacyInfoHovered(x,y,58,18,34,42))materialInfo(g,m.leftStack,x,y);if(legacyInfoHovered(x,y,96,18,34,42))materialInfo(g,m.rightStack,x,y);}else{int[] xs={42,96,116};for(int i=0;i<3;i++)if(legacyInfoHovered(x,y,xs[i],18,16,52))tankInfo(g,m.tanks[i],x,y);}}if(legacyInfoHovered(x,y,186,18,16,89))g.renderTooltip(font,Component.literal(menu.power()+"/20000000HE"),x,y);}
 private void materialInfo(GuiGraphics g,MaterialStack stack,int x,int y){g.renderTooltip(font,stack==null?Component.translatable("gui.hbm.electrolyser.empty").withStyle(ChatFormatting.RED):stack.material.displayName().copy().append(": ").append(HbmMaterials.formatAmount(stack.amount,hasShiftDown())).withStyle(ChatFormatting.YELLOW),x,y);}
 private void tankInfo(GuiGraphics g,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
 @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(leftPos+8<=x&&x<leftPos+62&&topPos+82<y&&y<=topPos+94){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new ElectrolyserControl(menu.containerId,!menu.metal));return true;}return result;}
}
