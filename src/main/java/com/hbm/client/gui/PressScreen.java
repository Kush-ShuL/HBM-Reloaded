package com.hbm.client.gui;

import com.hbm.machine.press.PressBlockEntity;
import com.hbm.machine.press.PressContent;
import com.hbm.machine.press.PressMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PressScreen extends LegacyContainerScreen<PressMenu> {
    public PressScreen(PressMenu menu,Inventory inventory,Component title) { super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_press.png"),176,214); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(PressContent.MENU.get(),PressScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY) { graphics.drawString(font,title,88-font.width(title)/2,5,0xffffff,false); graphics.drawString(font,playerInventoryTitle,8,120,4210752,false); }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY) {
        super.renderBg(graphics,partial,mouseX,mouseY);
        if(menu.burnTime()>=20) drawLegacyRegion(graphics,26,36,0,214,14,14);
        double press= minecraft.level.getBlockEntity(menu.position()) instanceof PressBlockEntity machine ? machine.renderPress(1) : menu.press();
        int height=(int)(press*16/200);
        if(height>0) drawLegacyRegion(graphics,79,35,15,214,18,height);
        gauge(graphics,leftPos+34,topPos+25,menu.speed()/400D);
    }
    public static void gauge(GuiGraphics graphics,int x,int y,double progress) {
        graphics.flush(); RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);
        double angle=Math.toRadians(-Mth.clamp(progress,0,1)*270-45),s=Math.sin(angle),c=Math.cos(angle);
        for(int layer=0;layer<2;layer++) {
            float scale=layer==0?1.5F:1;
            int color=layer==0?0xff000000:0xff7f0000;
            // Vec3NT.rotateAroundZ uses x'=x*cos+y*sin, y'=y*cos-x*sin.
            for(double[] point:new double[][]{{0,5},{1,-2},{-1,-2}}) buffer.addVertex(graphics.pose().last().pose(),(float)(x+(point[0]*c+point[1]*s)*scale),(float)(y+(point[1]*c-point[0]*s)*scale),0).setColor(color);
        }
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial) {
        super.render(graphics,mouseX,mouseY,partial);
        if(legacyInfoHovered(mouseX,mouseY,25,16,18,18)) graphics.renderTooltip(font,Component.literal((menu.speed()*100/400)+"%"),mouseX,mouseY);
        if(legacyInfoHovered(mouseX,mouseY,25,34,18,18)) graphics.renderTooltip(font,Component.literal((menu.burnTime()/200)+" operations left"),mouseX,mouseY);
    }
}
