package com.hbm.client.cyclotron;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.crystallizer.EffectUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.cyclotron.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
/** Literal source190x215 GUI,three actual tank fields and contextual real-upgrade formulas. */
public final class CyclotronScreen extends LegacyContainerScreen<CyclotronMenu> {
 public CyclotronScreen(CyclotronMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_cyclotron.png"),190,215);}
 private CyclotronEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof CyclotronEntity c?c:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,79-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,15,imageHeight-96+2,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var c=machine();if(c==null)return;int power=(int)(c.getPower()*63/CyclotronEntity.MAX_POWER);drawLegacyRegion(g,168,80-power,190,62-power,16,power);int progress=c.progress*34/CyclotronEntity.DURATION;drawLegacyRegion(g,48,27,206,0,progress,34);if(progress>0)drawLegacyRegion(g,172,4,190,63,9,12);g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos+49,topPos+85,0,32,8,8,256,256);tank(g,c.tanks[0],11,88,34,7);tank(g,c.tanks[1],11,97,34,7);tank(g,c.tanks[2],107,97,34,16);}
 /** All three source gauges use orientation1: horizontal fill and U=1→1-fill/16, not vertical tanks. */
 private void tank(GuiGraphics g,FluidTank tank,int x,int bottom,int width,int height){int fill=(int)((long)tank.getFill()*width/tank.getMaxFill());if(fill<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int dx=0;dx<fill;dx+=16){int w=Math.min(16,fill-dx);g.pose().pushPose();g.pose().translate(leftPos+x+dx+w,topPos+bottom-height,0);g.pose().scale(-1,1,1);g.blit(tank.getTankType().getTexture(),0,0,16-w,0,w,height,16,16);g.pose().popPose();}g.setColor(1,1,1,1);}
 @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack){var lines=new ArrayList<>(super.getTooltipFromContainerItem(stack));if(stack.getItem() instanceof FurnaceUpgradeItem u){int level=u.tier();lines.add(Component.translatable("block.hbm.machine_cyclotron").withStyle(ChatFormatting.YELLOW));if(u.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.delay","-"+(100-100/(level+1))+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.coolantConsumption","+"+level*100+"%").withStyle(ChatFormatting.RED));}else lines.add(Component.translatable("upgrade.consumption","-"+level*10+"%").withStyle(ChatFormatting.GREEN));}else if(stack.getItem() instanceof EffectUpgradeItem u){lines.add(Component.translatable("block.hbm.machine_cyclotron").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("upgrade.coolantConsumption","-"+(100-100/(u.tier()+1))+"%").withStyle(ChatFormatting.GREEN));}return lines;}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var c=machine();if(c==null)return;if(legacyInfoHovered(x,y,168,18,16,63))g.renderComponentTooltip(font,List.of(Component.translatable("tooltip.hbm.battery.energy_stored",BatteryItem.shortNumber(c.getPower()),BatteryItem.shortNumber(CyclotronEntity.MAX_POWER))),x,y);for(int i=0;i<3;i++){int tx=i<2?11:107,ty=i==0?81:i==1?90:81,w=34,h=i<2?7:16;if(legacyInfoHovered(x,y,tx,ty,w,h)){var t=c.tanks[i];var info=new ArrayList<Component>();info.add(t.getTankType().displayName());info.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));if(t.getPressure()!=0)info.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()));FluidInfo.append(t.getTankType(),info,hasShiftDown());g.renderComponentTooltip(font,info,x,y);}}if(legacyInfoHovered(x,y,49,85,8,8))g.renderComponentTooltip(font,List.of(Component.translatable("desc.gui.upgrade"),Component.translatable("desc.gui.upgrade.speed"),Component.translatable("desc.gui.upgrade.effectiveness"),Component.translatable("desc.gui.upgrade.power")),x,y);}
}
