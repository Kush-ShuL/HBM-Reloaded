package com.hbm.client.autocrafter;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.autocrafter.*;
import com.hbm.pneumatic.PatternMatcher;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x240 GUI/256atlas, exact template/input/battery geometry and two-line shifted tooltips. */
public final class AutocrafterScreen extends LegacyContainerScreen<AutocrafterMenu> {
    public AutocrafterScreen(AutocrafterMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_autocrafter.png"),176,240);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);int energy=menu.machine.getPowerExact().multiply(BigInteger.valueOf(52)).divide(BigInteger.valueOf(AutocrafterBlockEntity.MAX_POWER)).intValue();drawLegacyRegion(graphics,17,97-energy,176,52-energy,16,energy);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,146,4210752,false);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);
        if(legacyInfoHovered(x,y,17,45,16,52))graphics.renderTooltip(font,Component.literal(menu.machine.getPowerExact()+" / "+AutocrafterBlockEntity.MAX_POWER+" HE"),x,y);
        if(!menu.getCarried().isEmpty())return;
        for(int i=0;i<9;i++){var slot=menu.getSlot(i);String mode=menu.machine.matcher.modes[i];if(isHovering(slot.x,slot.y,16,16,x,y)&&mode!=null)graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.autocrafter.right_click").withStyle(ChatFormatting.RED),PatternMatcher.label(mode)),x,y-30);}
        var output=menu.getSlot(9);if(output.hasItem()&&isHovering(output.x,output.y,16,16,x,y))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.autocrafter.right_click").withStyle(ChatFormatting.RED),Component.literal((menu.machine.recipeIndex+1)+" / "+menu.machine.recipeCount).withStyle(ChatFormatting.YELLOW)),x,y-30);
    }
}
