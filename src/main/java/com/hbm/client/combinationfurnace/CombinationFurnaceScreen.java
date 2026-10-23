package com.hbm.client.combinationfurnace;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.combinationfurnace.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Exact source176x186/256atlas and bottom-anchored single-quad repeating tank UV. */
public final class CombinationFurnaceScreen extends LegacyContainerScreen<CombinationFurnaceMenu> {
    public CombinationFurnaceScreen(CombinationFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_furnace_combination.png"),176,186);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,0x404040,false);g.drawString(font,playerInventoryTitle,8,92,0x404040,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);drawLegacyRegion(g,45,37,176,0,menu.progress()*38/CombinationFurnaceEntity.PROCESS_TIME,5);drawLegacyRegion(g,45,46,176,5,menu.heat()*37/CombinationFurnaceEntity.MAX_HEAT,5);var tank=menu.tank();int height=tank.getFill()*52/tank.getMaxFill();if(height>0){int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+118,topPos+70-height,0,16-height,16,height,16,16);g.setColor(1,1,1,1);}}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);if(legacyInfoHovered(x,y,118,18,16,52)){var tank=menu.tank();var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,44,36,39,7))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",menu.progress(),CombinationFurnaceEntity.PROCESS_TIME)),x,y);if(legacyInfoHovered(x,y,44,45,39,7))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",menu.heat(),CombinationFurnaceEntity.MAX_HEAT)),x,y);}
}
