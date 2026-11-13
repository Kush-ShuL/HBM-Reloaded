package com.hbm.client.polarizedglass;

import com.hbm.satellite.SatelliteInterceptEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

/** Camera-relative orbital flash and burning debris, using the existing shader-compatible sky path. */
@EventBusSubscriber(modid="hbm", value=Dist.CLIENT)
public final class SatelliteSkyEffects {
    private static final RenderType FLASH = SourceSkyMarkerTypes.marker("asat_flash", "hbm:textures/particle/flare.png", true, false);
    private static final RenderType WAVE = SourceSkyMarkerTypes.marker("asat_wave", "hbm:textures/particle/shockwave.png", true, false);
    private static final RenderType DEBRIS = SourceSkyMarkerTypes.marker("asat_debris", "hbm:textures/particle/asat_white.png", false, false);
    private static final List<Effect> EFFECTS = new ArrayList<>();
    private static ClientLevel activeLevel;
    private static final class Effect {
        final Vec3 origin, motion;
        final float yaw, pitch;
        final int lifetime, burnDelay;
        int age;
        Effect(Vec3 origin, Vec3 motion, float yaw, float pitch, int lifetime, int burnDelay) {
            this.origin=origin; this.motion=motion; this.yaw=yaw; this.pitch=pitch; this.lifetime=lifetime; this.burnDelay=burnDelay;
        }
    }
    private static void useLevel(ClientLevel level) { if (activeLevel != level) { EFFECTS.clear(); activeLevel=level; } }
    public static void spawn() {
        var level=Minecraft.getInstance().level;
        if (level==null) return;
        useLevel(level);
        var random=level.random;
        var origin=new Vec3(random.nextGaussian()*100, 250, random.nextGaussian()*100);
        int count=5+random.nextInt(10); float angle=random.nextFloat()*360;
        for (int i=0; i<count; i++) {
            int lifetime=900+random.nextInt(300), delay=200+random.nextInt(200);
            float yaw=angle+random.nextFloat()*180, pitch=15+random.nextFloat()*15;
            var motion=new Vec3(0, -.1, 0).zRot((float)Math.toRadians(pitch)).yRot((float)Math.toRadians(yaw));
            EFFECTS.add(new Effect(origin,motion,yaw,pitch,lifetime,delay));
        }
        EFFECTS.add(new Effect(origin,Vec3.ZERO,0,0,200,0));
    }
    @SubscribeEvent public static void command(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmclientdbg").then(Commands.literal("sat").executes(context -> { spawn(); return 1; })));
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Pre event) {
        var mc=Minecraft.getInstance(); useLevel(mc.level);
        if (mc.level==null || mc.isPaused()) return;
        EFFECTS.removeIf(effect -> ++effect.age>=effect.lifetime);
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { useLevel(null); }
    @SubscribeEvent public static void render(RenderLevelStageEvent event) {
        if (event.getStage()!=RenderLevelStageEvent.Stage.AFTER_SKY) return;
        var mc=Minecraft.getInstance(); useLevel(mc.level);
        if (mc.level==null || EFFECTS.isEmpty()) return;
        var pose=new PoseStack(); pose.mulPose(event.getModelViewMatrix());
        float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);
        try (var state=new SourceSkyMarkers.MarkerState(); var memory=new ByteBufferBuilder(16384)) {
            RenderSystem.setShaderColor(1,1,1,1);
            for (var effect:EFFECTS) {
                float age=effect.age+partial;
                if (effect.burnDelay==0) {
                    float wave=age/200F*15, flash=15-age/100F, arc=age/2.5F+10;
                    quad(memory,pose,WAVE,effect.origin,wave,(1-age/200F)*.5F);
                    var below=effect.origin.add(0,-1,0);
                    quad(memory,pose,FLASH,below,flash,1-age/200F);
                    if (age<40) quad(memory,pose,FLASH,below,arc,1-age/40F);
                } else {
                    float alpha=age<effect.burnDelay?0:age>effect.lifetime-100?1-(age-effect.lifetime+100)/100F:(age-effect.burnDelay)/effect.burnDelay;
                    if (alpha<=0) continue;
                    pose.pushPose(); var p=effect.origin.add(effect.motion.scale(age)); pose.translate(p.x,p.y,p.z);
                    pose.mulPose(Axis.YP.rotationDegrees(effect.yaw)); pose.mulPose(Axis.ZP.rotationDegrees(effect.pitch+45));
                    debris(memory,pose,Math.min(alpha,1)); pose.popPose();
                }
            }
        }
    }
    private static void quad(ByteBufferBuilder memory,PoseStack pose,RenderType type,Vec3 p,float size,float alpha) {
        var out=new BufferBuilder(memory,VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        vertex(out,pose,(float)p.x-size,(float)p.y,(float)p.z-size,0,0,255,alpha);
        vertex(out,pose,(float)p.x+size,(float)p.y,(float)p.z-size,1,0,255,alpha);
        vertex(out,pose,(float)p.x+size,(float)p.y,(float)p.z+size,1,1,255,alpha);
        vertex(out,pose,(float)p.x-size,(float)p.y,(float)p.z+size,0,1,255,alpha);
        type.draw(out.buildOrThrow());
    }
    private static void debris(ByteBufferBuilder memory,PoseStack pose,float alpha) {
        var out=new BufferBuilder(memory,VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        float[][] corners={{1,-1},{1,1},{-1,1},{-1,-1}};
        for(int i=0;i<4;i++) {
            var a=corners[i];var b=corners[(i+1)%4];
            vertex(out,pose,10,a[0]*.02F,a[1]*.02F,0,0,64,alpha);
            vertex(out,pose,10,b[0]*.02F,b[1]*.02F,0,0,64,alpha);
            vertex(out,pose,0,b[0]*.25F,b[1]*.25F,0,0,255,alpha);
            vertex(out,pose,0,a[0]*.25F,a[1]*.25F,0,0,255,alpha);
        }
        for(int end=0;end<2;end++)for(var c:corners)vertex(out,pose,end==0?10:0,c[0]*(end==0?.02F:.25F),c[1]*(end==0?.02F:.25F),0,0,end==0?64:255,alpha);
        DEBRIS.draw(out.buildOrThrow());
    }
    private static void vertex(BufferBuilder out,PoseStack pose,float x,float y,float z,float u,float v,int color,float alpha) {
        out.addVertex(pose.last(),x,y,z).setUv(u,v).setColor(color,color,color,(int)(alpha*255));
    }
    @EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
    public static final class Setup {
        @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> SatelliteInterceptEffect.CLIENT=SatelliteSkyEffects::spawn); }
    }
    private SatelliteSkyEffects() { }
}
