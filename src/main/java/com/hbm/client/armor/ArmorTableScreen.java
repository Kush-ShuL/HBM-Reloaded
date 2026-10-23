package com.hbm.client.armor;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.ArmorTableMenu;
import com.hbm.client.gui.LegacyContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/** Original 198x222 GUI and 256px source atlas, without modern replacement controls. */
public final class ArmorTableScreen extends LegacyContainerScreen<ArmorTableMenu> {
    private static final String[] HINTS={"helmet","chestplate","leggings","boots","servo","cladding","insert","special","battery"};
    public ArmorTableScreen(ArmorTableMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_armor_modifier.png"),198,222);}
    @Override protected void renderBg(GuiGraphics graphics,float partialTick,int mouseX,int mouseY){
        graphics.setColor(1,1,1,1);
        drawLegacyRegion(graphics,22,0,0,0,176,222);
        drawLegacyRegion(graphics,0,31,176,96,22,100);
        ItemStack piece=menu.armor.getItem(0);
        if(!piece.isEmpty())drawLegacyRegion(graphics,63,60,176,piece.getItem() instanceof ArmorItem?74:52,22,22);
        else if(System.currentTimeMillis()%1000<500)drawLegacyRegion(graphics,63,60,176,52,22,22);
        for(int index=0;index<9;index++){var slot=menu.getSlot(index);if(slot.hasItem())drawLegacyRegion(graphics,slot.x-1,slot.y-1,176,ArmorModStorage.isApplicable(piece,slot.getItem())?34:16,18,18);}
    }
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){
        Component name=Component.translatable("container.armorTable");
        graphics.drawString(font,name,110-font.width(name)/2,6,4210752,false);
        graphics.drawString(font,Component.translatable("container.inventory"),30,128,4210752,false);
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick){
        super.render(graphics,mouseX,mouseY,partialTick);
        if(menu.getCarried().isEmpty())for(int index=0;index<10;index++){
            var slot=menu.getSlot(index);
            if(!slot.hasItem()&&isHovering(slot.x,slot.y,16,16,mouseX,mouseY))graphics.renderTooltip(font,Component.translatable(index<9?"armorMod.type."+HINTS[index]:"armorMod.insertHere").withStyle(index<9?ChatFormatting.LIGHT_PURPLE:ChatFormatting.YELLOW),mouseX,mouseY);
        }
    }
}
