package com.hbm.client.turbofan;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilextraction.AfterburnUpgradeItem;
import com.hbm.machine.turbofan.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
/** Exact GUIMachineTurbofan176x203, original13-frame round blood gauge and AFTERBURN information. */
public final class TurbofanScreen extends LegacyContainerScreen<TurbofanMenu> {
 public TurbofanScreen(TurbofanMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/generators/gui_turbofan.png"),176,203);}private TurbofanEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof TurbofanEntity t?t:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,43-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,109,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var t=machine();if(t==null)return;int power=(int)(t.power*52/TurbofanEntity.MAX_POWER);drawLegacyRegion(g,143,69-power,192,52-power,16,power);int after=menu.afterburner;if(after>0)drawLegacyRegion(g,98,44,176,(Math.min(after,6)-1)*16,16,16);if(t.showBlood){int frame=(int)Math.round(12D*t.blood.getFill()/t.blood.getMaxFill());g.blit(ResourceLocation.parse("hbm:textures/gui/gauges/small_round.png"),leftPos+97,topPos+16,0,18*frame,18,18,18,234);}tank(g,t.tank);}
 private void tank(GuiGraphics g,FluidTank tank){int fill=(int)((long)tank.getFill()*52/tank.getMaxFill());if(fill<=0)return;int c=tank.getTankType().getTint();g.setColor((c>>16&255)/255F,(c>>8&255)/255F,(c&255)/255F,1);for(int x=0;x<34;x+=16)for(int done=0;done<fill;done+=16){int h=Math.min(16,fill-done);g.blit(tank.getTankType().getTexture(),leftPos+35+x,topPos+69-done-h,0,16-h,Math.min(16,34-x),h,16,16);}g.setColor(1,1,1,1);}
 private List<Component> info(FluidTank t){var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));if(t.getPressure()!=0)lines.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(t.getTankType(),lines,hasShiftDown());return lines;}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var t=machine();if(t==null)return;if(legacyInfoHovered(x,y,35,17,34,52))g.renderComponentTooltip(font,info(t.tank),x,y);if(t.showBlood&&legacyInfoHovered(x,y,98,17,16,16))g.renderComponentTooltip(font,info(t.blood),x,y);if(legacyInfoHovered(x,y,143,17,16,52))g.renderComponentTooltip(font,List.of(Component.translatable("tooltip.hbm.battery.energy_stored",BatteryItem.shortNumber(t.power),BatteryItem.shortNumber(TurbofanEntity.MAX_POWER))),x,y);}
 @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack){var lines=new ArrayList<>(super.getTooltipFromContainerItem(stack));if(stack.getItem() instanceof AfterburnUpgradeItem upgrade){int level=upgrade.tier();lines.add(Component.literal(">>> ").append(Component.translatable("block.hbm.machine_turbofan")).append(" <<<").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("upgrade.efficiency","+"+(int)(level*100*(1+Math.min(level/3D,4D)))+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(level*100)+"%").withStyle(ChatFormatting.RED));}return lines;}
}
