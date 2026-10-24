package com.hbm.client.loadingtips.mixin;

import com.hbm.client.loadingtips.LoadingTipsConfig;
import com.hbm.client.loadingtips.LoadingTipsRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps the native ProgressListener fields and its stop/clear-screen branch authoritative. */
@Mixin(value=ProgressScreen.class,remap=false)
public abstract class ProgressScreenMixin extends Screen {
    @Shadow private Component header;
    @Shadow private Component stage;
    @Shadow private int progress;
    @Shadow private boolean stop;
    @Unique private boolean hbm$specifiedProgress;

    protected ProgressScreenMixin(){super(Component.empty());}

    @Inject(method="progressStagePercentage",at=@At("TAIL"))
    private void hbm$actualProgress(int percentage,CallbackInfo callback) {
        hbm$specifiedProgress=percentage>=0;
    }

    @Inject(method="progressStage",at=@At("TAIL"))
    private void hbm$newStage(Component message,CallbackInfo callback) {
        hbm$specifiedProgress=false;
    }

    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void hbm$renderLoading(GuiGraphics graphics,int mouseX,int mouseY,float partialTick,CallbackInfo callback) {
        if(!LoadingTipsConfig.enabled()||stop)return;
        LoadingTipsRenderer.background(graphics,width,height);
        LoadingTipsRenderer.content(graphics,font,width,height,header,stage,hbm$specifiedProgress?progress:-1);
        callback.cancel();
    }
}
