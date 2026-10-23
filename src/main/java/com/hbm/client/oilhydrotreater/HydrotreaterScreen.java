package com.hbm.client.oilhydrotreater;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.client.wiaj.actors.JarStackText;
import com.hbm.fluid.*;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilhydrotreater.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
/** Original176x238 layout,54-pixel electricity gauge, four52-pixel tanks and catalyst stack tooltip. */
public final class HydrotreaterScreen extends LegacyContainerScreen<HydrotreaterMenu> {
 public HydrotreaterScreen(HydrotreaterMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_hydrotreater.png"),176,238);}
 private HydrotreaterBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof HydrotreaterBlockEntity m?m:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,5,0xffffff,false);g.drawString(font,playerInventoryTitle,8,144,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int power=(int)(menu.power()*54/1000000);if(power>0)drawLegacyRegion(g,17,70-power,176,52-power,16,power);var m=machine();if(m==null)return;int[] xs={35,53,125,143};for(int i=0;i<4;i++)tank(g,m.tanks[i],xs[i]);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()<=0||tank.getTankType()==HbmFluids.NONE)return;int height=tank.getFill()*52/tank.getMaxFill();int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int y=0;y<height;y+=16){int h=Math.min(16,height-y);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-y-h,0,16-h,16,h,16,16);}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m!=null){int[] xs={35,53,125,143};for(int i=0;i<4;i++)if(legacyInfoHovered(x,y,xs[i],18,16,52))tankInfo(g,m.tanks[i],x,y);}if(legacyInfoHovered(x,y,17,18,16,52))g.renderTooltip(font,Component.literal(menu.power()+"/1000000HE"),x,y);
  if(menu.getCarried().isEmpty()&&!menu.getSlot(10).hasItem()&&legacyInfoHovered(x,y,89,36,16,16)){var converter=new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:catalytic_converter")));JarStackText.draw(g,List.<Object[]>of(new Object[]{converter},new Object[]{converter.getHoverName().getString()}),x,y,width,height);}
 }
 private void tankInfo(GuiGraphics g,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
}
