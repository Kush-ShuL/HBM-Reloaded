package com.hbm.client.effect;

import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.entity.effect.EntityBlackHole;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** The opaque core stays in the entity pass; source additive/alpha effects sort against native clouds. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class BlackHolePasses {
    private record Entry(EntityBlackHole entity,float partial,Consumer<PoseStack> draw){}
    private static final ArrayList<Entry> PENDING=new ArrayList<>();
    private static boolean worldFrame,depthOnly;
    public static boolean depthOnly(){return depthOnly;}
    static boolean defer(EntityBlackHole entity,float partial,Consumer<PoseStack> draw){
        if(!worldFrame)return false;
        PENDING.add(new Entry(entity,partial,draw));NuclearTransparencyLayers.request();return true;
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void frame(RenderLevelStageEvent event){
        if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY){PENDING.clear();worldFrame=true;depthOnly=false;}
        else if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_LEVEL){worldFrame=false;PENDING.clear();depthOnly=false;}
    }
    @SubscribeEvent public static void effects(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||PENDING.isEmpty())return;
        if(NuclearTransparencyLayers.active()){
            NuclearTransparencyLayers.bindNuclearLayer();
            try{draw(event,false);}catch(RuntimeException|Error failure){PENDING.clear();throw failure;}
            finally{NuclearTransparencyLayers.restoreAfterNuclear();}
            NuclearTransparencyLayers.deferBehind(()->{
                NuclearTransparencyLayers.bindNuclearLayer();
                try{draw(event,false);}finally{PENDING.clear();NuclearTransparencyLayers.restoreAfterNuclear();}
            });
        }else{
            boolean fabulous=Minecraft.useShaderTransparency();
            if(fabulous)RenderStateShard.PARTICLES_TARGET.setupRenderState();
            try{draw(event,false);if(fabulous)draw(event,true);}
            finally{
                PENDING.clear();
                if(fabulous){RenderStateShard.PARTICLES_TARGET.clearRenderState();RenderStateShard.WEATHER_TARGET.setupRenderState();}
            }
        }
    }
    /** Deferred world effects own a pose/normal snapshot, never a mutable caller stack. */
    static PoseStack copyPose(PoseStack source){
        var copy=new PoseStack();copy.last().pose().set(source.last().pose());copy.last().normal().set(source.last().normal());return copy;
    }
    static void drawOrDefer(boolean depth,PoseStack pose,Consumer<PoseStack> draw){
        var copy=copyPose(pose);
        if(WorldEffectsForward.defer(()->{
            boolean previous=depthOnly;depthOnly=depth;
            try{draw.accept(copy);}finally{depthOnly=previous;}
        }))return;
        draw.accept(pose);
    }
    private static void draw(RenderLevelStageEvent event,boolean depth){
        var eye=event.getCamera().getPosition();var pose=event.getPoseStack();depthOnly=depth;
        try{
            for(var entry:PENDING){
                var entity=entry.entity();if(entity.isRemoved())continue;
                pose.pushPose();
                try{
                    pose.translate(Mth.lerp(entry.partial(),entity.xOld,entity.getX())-eye.x,Mth.lerp(entry.partial(),entity.yOld,entity.getY())-eye.y,Mth.lerp(entry.partial(),entity.zOld,entity.getZ())-eye.z);
                    float size=entity.size();pose.scale(size,size,size);drawOrDefer(depth,pose,entry.draw());
                }finally{pose.popPose();}
            }
        }finally{
            depthOnly=false;RenderSystem.colorMask(true,true,true,true);RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();RenderSystem.enableCull();RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();
        }
    }
    private BlackHolePasses(){}
}
