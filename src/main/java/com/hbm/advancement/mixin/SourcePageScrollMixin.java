package com.hbm.advancement.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The original page also has nodes left/above zero; native tabs assume an origin at zero. */
@Mixin(AdvancementTab.class)
public abstract class SourcePageScrollMixin {
    @Shadow @Final private AdvancementNode rootNode;
    @Shadow private int minX;
    @Shadow private int minY;
    @Shadow private int maxX;
    @Shadow private int maxY;
    @Shadow private double scrollX;
    @Shadow private double scrollY;

    @Inject(method="scroll",at=@At("HEAD"),cancellable=true)
    private void hbm$includeOriginalPageEdges(double deltaX,double deltaY,CallbackInfo result) {
        var id=rootNode.holder().id();
        if(!id.getNamespace().equals("hbm")||!id.getPath().equals("nuclear_tech"))return;
        if(maxX-minX>234)scrollX=Mth.clamp(scrollX+deltaX,234D-maxX,-(double)minX);
        if(maxY-minY>113)scrollY=Mth.clamp(scrollY+deltaY,113D-maxY,-(double)minY);
        result.cancel();
    }
}
