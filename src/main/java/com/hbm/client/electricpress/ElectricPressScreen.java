package com.hbm.client.electricpress;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.electricpress.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Original176x186 GUI,34px energy gauge,16px head indicator and actual source speed help. */
public final class ElectricPressScreen extends LegacyContainerScreen<ElectricPressMenu> {
    public ElectricPressScreen(ElectricPressMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_electric_press.png"),176,186);}
    private ElectricPressEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof ElectricPressEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,89-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,92,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;int energy=(int)(machine.getPower()*34/50000);drawLegacyRegion(graphics,152,52-energy,176,34-energy,16,energy);int height=(int)(machine.renderPress*16/200);if(height>0)drawLegacyRegion(graphics,18,33,192,0,18,height);}
    @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack){var lines=new ArrayList<>(super.getTooltipFromContainerItem(stack));if(stack.getItem() instanceof FurnaceUpgradeItem upgrade&&upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("block.hbm.machine_epress").withStyle(ChatFormatting.YELLOW));lines.add(Component.translatable("upgrade.delay","-"+(50*upgrade.tier()/3)+"%").withStyle(ChatFormatting.GREEN));}return lines;}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine!=null&&legacyInfoHovered(x,y,152,18,16,34))graphics.renderComponentTooltip(font,List.of(Component.translatable("jade.hbm.energy",machine.getPower(),50000)),x,y);}
}
