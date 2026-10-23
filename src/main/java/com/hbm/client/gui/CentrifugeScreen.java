package com.hbm.client.gui;

import com.hbm.machine.centrifuge.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Source182x189 GUI, unchanged texture regions, four36px columns and upgrade help hitbox. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CentrifugeScreen extends LegacyContainerScreen<CentrifugeMenu> {
    public CentrifugeScreen(CentrifugeMenu menu,Inventory inventory,Component title) { super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_centrifuge.png"),182,189); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(CentrifugeContent.MENU.get(),CentrifugeScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y) { graphics.drawString(font,title,109-font.width(title)/2,6,0xffffff,false); graphics.drawString(font,playerInventoryTitle,11,95,4210752,false); }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY) {
        super.renderBg(graphics,partial,mouseX,mouseY);
        if(menu.power()>0&&menu.capacity()!=0) { int height=(int)(menu.power()*37/menu.capacity()); drawLegacyRegion(graphics,8,55-height,182,37-height,16,height); }
        if(menu.progress()>0&&menu.processTime()!=0) {
            int progress=menu.progress()*145/menu.processTime();
            for(int i=0;i<4;i++) { int height=Math.min(progress,36); if(height>0) drawLegacyRegion(graphics,72+i*20,57-height,182,73-height,12,height); progress-=height; if(progress<=0) break; }
        }
        // Original GuiInfoContainer's 8x8 info panel is copied as the same source sprite.
        graphics.blit(ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/gui_utility.png"),leftPos+160,topPos+16,0,32,8,8,256,256);
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial) {
        super.render(graphics,mouseX,mouseY,partial);
        if(legacyInfoHovered(mouseX,mouseY,8,18,16,37)) graphics.renderTooltip(font,Component.literal(shortNumber(menu.power())+"/"+shortNumber(menu.capacity())+"HE"),mouseX,mouseY);
        if(legacyInfoHovered(mouseX,mouseY,160,16,8,8)) graphics.renderComponentTooltip(font,List.of(Component.translatable("desc.gui.upgrade"),Component.translatable("desc.gui.upgrade.speed"),Component.translatable("desc.gui.upgrade.power"),Component.translatable("desc.gui.upgrade.overdrive")),mouseX,mouseY);
    }
    private static String shortNumber(long value) { if(Math.abs(value)<1000) return Long.toString(value); String[] suffix={"k","M","G","T","P","E"}; double number=value; int i=-1; do { number/=1000; i++; } while(Math.abs(number)>=1000&&i<5); return (number<=-100?Math.round(number*10)/10D:Math.round(number*100)/100D)+suffix[i]; }
}
