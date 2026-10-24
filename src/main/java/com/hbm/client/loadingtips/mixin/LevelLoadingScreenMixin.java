package com.hbm.client.loadingtips.mixin;

import com.hbm.client.loadingtips.LoadingTipsConfig;
import com.hbm.client.loadingtips.LoadingTipsRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Replaces the native canvas while leaving native progress, narration and removal lifecycle running. */
@Mixin(value=LevelLoadingScreen.class,remap=false)
public abstract class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(){super(Component.empty());}

    @WrapOperation(method="render",at=@At(value="INVOKE",
        target="Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void hbm$background(Screen screen,GuiGraphics graphics,int mouseX,int mouseY,float partialTick,
            Operation<Void> original) {
        if(LoadingTipsConfig.enabled())LoadingTipsRenderer.background(graphics,width,height);
        else original.call(screen,graphics,mouseX,mouseY,partialTick);
    }

    @WrapOperation(method="render",at=@At(value="INVOKE",
        target="Lnet/minecraft/client/gui/screens/LevelLoadingScreen;renderChunks(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/server/level/progress/StoringChunkProgressListener;IIII)V"))
    private void hbm$realChunkProgress(GuiGraphics graphics,StoringChunkProgressListener listener,
            int x,int y,int scale,int padding,Operation<Void> original) {
        if(!LoadingTipsConfig.enabled()) {
            original.call(graphics,listener,x,y,scale,padding);
            return;
        }
        int progress=Mth.clamp(listener.getProgress(),0,100);
        LoadingTipsRenderer.content(graphics,font,width,height,Component.translatable("menu.loadingLevel"),
            Component.translatable("loading.progress",progress),progress);
    }

    @WrapOperation(method="render",at=@At(value="INVOKE",
        target="Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    private void hbm$replaceNativePercentage(GuiGraphics graphics,Font font,Component text,
            int x,int y,int color,Operation<Void> original) {
        if(!LoadingTipsConfig.enabled())original.call(graphics,font,text,x,y,color);
    }
}
