package com.hbm.client.wiki;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Source GUIScreenPreview: a centered inventory model, slot frame, wheel zoom and native identity. No image writes. */
public final class ItemPreviewScreen extends Screen {
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png");
    private final ItemStack preview;
    private int zoom=1;

    public ItemPreviewScreen(ItemStack stack){
        super(Component.translatable("screen.hbm.item_preview"));
        preview=stack.copyWithCount(1);
    }

    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick){
        float[] color=RenderSystem.getShaderColor().clone();
        try{
            RenderSystem.setShaderColor(1,1,1,1);
            if(minecraft.level!=null)graphics.fill(0,0,width,height,0xffc6c6c6);
            else renderBackground(graphics,mouseX,mouseY,partialTick);
            graphics.flush();
            double scale=minecraft.getWindow().getGuiScale();
            double centerX=minecraft.getWindow().getWidth()/scale/2D;
            double centerY=minecraft.getWindow().getHeight()/scale/2D;
            graphics.pose().pushPose();
            try{
                graphics.pose().translate(centerX,centerY,0);
                graphics.pose().scale(zoom,zoom,zoom);
                graphics.blit(TEXTURE,-9,-9,5,87,18,18);
                graphics.flush();
            }finally{graphics.pose().popPose();}
            graphics.pose().pushPose();
            try{
                // Preserve the source uniform model zoom while keeping native GUI item's Z150 at its own layer.
                graphics.pose().translate(centerX,centerY,150F*(1F-zoom));
                graphics.pose().scale(zoom,zoom,zoom);
                graphics.renderItem(preview,-8,-8);
                graphics.renderItemDecorations(font,preview,-8,-8);
                graphics.flush();
            }finally{graphics.pose().popPose();}
            graphics.pose().pushPose();
            try{
                graphics.pose().scale(.5F,.5F,1);
                var zoomText=Component.translatable("gui.hbm.item_preview.zoom",zoom);
                var scaleText=Component.translatable("gui.hbm.item_preview.window_scale",scale);
                var identity=Component.literal(BuiltInRegistries.ITEM.getKey(preview.getItem()).toString());
                line(graphics,zoomText,height*2-35);
                line(graphics,scaleText,height*2-25);
                line(graphics,identity,height*2-15);
                graphics.flush();
            }finally{graphics.pose().popPose();}
        }finally{RenderSystem.setShaderColor(color[0],color[1],color[2],color[3]);}
    }

    private void line(GuiGraphics graphics,Component text,int y){
        graphics.drawString(font,text,width*2-font.width(text)-2,y,0xff0000,false);
    }

    @Override public boolean mouseScrolled(double mouseX,double mouseY,double horizontal,double vertical){
        long window=minecraft.getWindow().getWindow();
        if(GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_LEFT)==GLFW.GLFW_PRESS
                ||GLFW.glfwGetMouseButton(window,GLFW.GLFW_MOUSE_BUTTON_RIGHT)==GLFW.GLFW_PRESS)return false;
        zoom=Mth.clamp(zoom+(vertical>0?1:vertical<0?-1:0),1,15);
        return true;
    }

    @Override public boolean keyPressed(int key,int scanCode,int modifiers){
        if(key==GLFW.GLFW_KEY_ESCAPE||minecraft.options.keyInventory.matches(key,scanCode)){onClose();return true;}
        return super.keyPressed(key,scanCode,modifiers);
    }

    @Override public void onClose(){
        if(minecraft.player!=null)minecraft.player.closeContainer();else minecraft.setScreen(null);
    }
}
