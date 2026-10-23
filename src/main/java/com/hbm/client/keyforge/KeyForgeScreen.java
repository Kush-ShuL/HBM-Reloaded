package com.hbm.client.keyforge;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.keyforge.KeyForgeMenu;
import java.util.Arrays;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Source176x186 GUI, exact three slots, white title and fixed info-panel tooltip anchors. */
public final class KeyForgeScreen extends LegacyContainerScreen<KeyForgeMenu> {
    private static final ResourceLocation UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
    public KeyForgeScreen(KeyForgeMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/machine/gui_keyforge.png"),176,186);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,92,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);drawTexturedModalRect(graphics,UTILITY,leftPos+12,topPos+28,8,0,16,16,256,256);drawTexturedModalRect(graphics,UTILITY,leftPos+12,topPos+44,24,0,16,16,256,256);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);info(graphics,x,y,28,"desc.gui.keyforge.key");info(graphics,x,y,44,"desc.gui.keyforge.random");}
    private void info(GuiGraphics graphics,int x,int y,int row,String key){if(legacyInfoHovered(x,y,12,row,16,16))graphics.renderComponentTooltip(font,Arrays.stream(I18n.get(key).split("\\$",-1)).map(Component::literal).map(c->(Component)c).toList(),leftPos+20,topPos+row+16);}
}
