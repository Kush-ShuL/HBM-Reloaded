package com.hbm.world.structure.templates.funnel.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.world.structure.templates.funnel.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original176x168 GUIFunnel background, labels, mode region and asymmetric hover bounds. */
public final class FunnelScreen extends LegacyContainerScreen<FunnelMenu> {
    public FunnelScreen(FunnelMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_funnel.png"),176,168);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);drawLegacyRegion(graphics,159,73,176,menu.mode()*10,10,10);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,74,4210752,false);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,159,73,10,10))graphics.renderTooltip(font,Component.translatable("gui.hbm.funnel.mode",Component.translatable(menu.mode()==1?"gui.hbm.funnel.3x3":menu.mode()==2?"gui.hbm.funnel.2x2":"gui.hbm.funnel.all")),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,159,73,10,10)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1F));PacketDistributor.sendToServer(new FunnelControl(menu.position));return true;}return handled;}
}
