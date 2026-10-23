package com.hbm.client.door.transition;

import com.hbm.client.animation.collada.*;
import com.hbm.client.animation.collada.AnimationWrapper.EndResult;
import com.hbm.client.animation.collada.AnimationWrapper.EndType;
import com.hbm.door.generic.GenericDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.*;
import java.util.concurrent.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;

/** Original seal.dae flipV/24040ms and RenderDoorGeneric AnimatedModel branch; no replacement OBJ or second skeletal engine. */
public final class TransitionSealModel implements PreparableReloadListener {
    public static final TransitionSealModel INSTANCE=new TransitionSealModel();
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/doors/seal.dae"),TEXTURE=ResourceLocation.parse("hbm:textures/models/doors/transition_seal.png");
    private record Prepared(AnimatedModel model,Animation animation){}
    private volatile Prepared current;
    private Prepared load(ResourceManager resources){try{return new Prepared(ColladaLoader.load(resources,MODEL,true),ColladaLoader.loadAnim(resources,24040,MODEL));}catch(IOException exception){throw new UncheckedIOException("Cannot load actual transition seal Collada",exception);}}
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier,ResourceManager resources,ProfilerFiller preparationProfiler,ProfilerFiller reloadProfiler,Executor preparationExecutor,Executor reloadExecutor){return CompletableFuture.supplyAsync(()->load(resources),preparationExecutor).thenCompose(barrier::wait).thenAcceptAsync(prepared->current=prepared,reloadExecutor);}
    public static void world(GenericDoorBlockEntity door,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        Prepared data=INSTANCE.current;if(data==null){data=INSTANCE.load(Minecraft.getInstance().getResourceManager());INSTANCE.current=data;}
        // Source non-SEDNA doOffsetTransform precedes the hierarchy. getTranslation is not applied again in its animated-model branch.
        pose.translate(0,0,.5);long now=System.currentTimeMillis();long start=door.state>1?door.animStartTime:now;boolean reverse=door.state==GenericDoorBlockEntity.OPEN||door.state==GenericDoorBlockEntity.CLOSING;
        var wrapper=new AnimationWrapper(start,data.animation).onEnd(new EndResult(EndType.STAY));if(reverse)wrapper.reverse();data.model.controller.setAnim(wrapper);
        data.model.renderAnimated(pose,buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)),light,overlay,-1,System.currentTimeMillis());
    }
    private TransitionSealModel(){}
}
