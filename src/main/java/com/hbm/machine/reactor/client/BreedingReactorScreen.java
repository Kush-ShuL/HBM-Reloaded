package com.hbm.machine.reactor.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.reactor.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x166 breeder screen and original70x20 progress fill. */
public final class BreedingReactorScreen extends LegacyContainerScreen<BreedingReactorMenu> {
    private static final ResourceLocation UTILITY=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/gui_utility.png");
    public BreedingReactorScreen(BreedingReactorMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_breeder.png"),176,166);}
    private BreedingReactorBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof BreedingReactorBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,72,4210752,false);var machine=machine();String flux=Integer.toString(machine==null?0:machine.flux);graphics.drawString(font,flux,88-font.width(flux)/2,21,0x08ff00,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var machine=machine();int width=machine==null?0:(int)(machine.progress*70);if(width>0)drawLegacyRegion(graphics,53,32,176,0,width,20);graphics.blit(UTILITY,leftPos-16,topPos+16,24,0,16,16);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,-16,16,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.reactor.breeder.1"),Component.translatable("gui.hbm.reactor.breeder.2"),Component.translatable("gui.hbm.reactor.breeder.3")),x,y);}
}
