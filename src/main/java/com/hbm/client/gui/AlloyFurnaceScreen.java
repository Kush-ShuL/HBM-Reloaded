package com.hbm.client.gui;
import com.hbm.machine.alloy.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AlloyFurnaceScreen extends LegacyContainerScreen<AlloyMenu> {
    public AlloyFurnaceScreen(AlloyMenu menu,Inventory player,Component title) { super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/guidifurnace.png"),176,166); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(AlloyContent.MENU.get(),AlloyFurnaceScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y) { graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false); graphics.drawString(font,playerInventoryTitle,8,72,4210752,false); }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y) {
        super.renderBg(graphics,partial,x,y);
        if(menu.fuel()>0) { int height=menu.fuel()*52/AlloyBlockEntity.MAX_FUEL; drawLegacyRegion(graphics,44,70-height,201,53-height,16,height); }
        int progress=menu.progress()*24/AlloyBlockEntity.PROCESS_TIME; drawLegacyRegion(graphics,101,35,176,14,progress+1,17);
        if(menu.fuel()>0&&(menu.canProcess()||progress>0)) drawLegacyRegion(graphics,63,37,176,0,14,14);
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial) {
        super.render(graphics,mouseX,mouseY,partial);
        if(menu.getCarried().isEmpty()&&hoveredSlot!=null&&hoveredSlot.index>=0&&hoveredSlot.index<3) {
            String[] names={"down","up","north","south","west","east"}; int side=menu.side(hoveredSlot.index);
            graphics.renderTooltip(font,Component.translatable("gui.hbm.alloy.accepts_from",Component.translatable("gui.hbm.side."+names[Math.floorMod(side,6)])).withStyle(net.minecraft.ChatFormatting.YELLOW),mouseX,mouseY-(hoveredSlot.hasItem()?15:0));
        }
    }
}
