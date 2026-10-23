package com.hbm.client.oilliquefactor;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.oilliquefactor.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
/** True176x204 background and all original4slots/gauges,without blurred rescale or idle task bars. */
public final class LiquefactorScreen extends LegacyContainerScreen<LiquefactorMenu> {
 public LiquefactorScreen(LiquefactorMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_liquefactor.png"),176,204);}
 private LiquefactorEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof LiquefactorEntity m?m:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,0xc7c1a3,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var m=machine();if(m==null)return;int energy=(int)(m.getPower()*52/100000);drawLegacyRegion(g,134,70-energy,176,52-energy,16,energy);if(m.processing&&m.progress>0)drawLegacyRegion(g,42,17,192,0,m.progress*42/Math.max(1,m.processTime),35);if(energy>0)drawLegacyRegion(g,138,4,176,52,9,12);int height=m.tank.getFill()*52/m.tank.getMaxFill();if(height<=0)return;int color=m.tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int done=0;done<height;done+=16){int h=Math.min(16,height-done);g.blit(m.tank.getTankType().getTexture(),leftPos+71,topPos+88-done-h,0,16-h,16,h,16,16);}g.setColor(1,1,1,1);}
 @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack){var lines=new ArrayList<>(super.getTooltipFromContainerItem(stack));if(stack.getItem() instanceof FurnaceUpgradeItem u){int level=u.tier();lines.add(Component.translatable("block.hbm.machine_liquefactor").withStyle(ChatFormatting.YELLOW));if(u.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.delay","-"+level*25+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+level*100+"%").withStyle(ChatFormatting.RED));}else lines.add(Component.translatable("upgrade.consumption","-"+(100-100/(level+1))+"%").withStyle(ChatFormatting.GREEN));}return lines;}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;if(legacyInfoHovered(x,y,134,18,16,52))g.renderComponentTooltip(font,List.of(Component.literal(m.getPower()+"/100000HE"),Component.translatable("gui.hbm.liquefactor.consumption",m.usage)),x,y);if(legacyInfoHovered(x,y,71,36,16,52)){var t=m.tank;var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));if(t.getPressure()!=0)lines.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(t.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}}
}
