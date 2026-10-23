package com.hbm.client.fluidtank;

import com.hbm.fluid.HbmFluidType;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

/** A single lit surface decal: compose the original atlas instead of floating digit planes. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TankDangerDiamondDecal extends RenderType {
    private static final ResourceLocation ATLAS=ResourceLocation.parse("hbm:textures/models/misc/danger_diamond.png");
    private record Mark(int poison,int fire,int reactive,HbmFluidType.Symbol symbol) {}
    private static final Map<Mark,ResourceLocation> TEXTURES=new HashMap<>();
    private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
    private static NativeImage atlas;
    private TankDangerDiamondDecal(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}

    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){
        event.registerReloadListener((ResourceManagerReloadListener)resources->{
            var manager=Minecraft.getInstance().getTextureManager();
            TEXTURES.values().forEach(manager::release);
            TEXTURES.clear();TYPES.clear();
            if(atlas!=null){atlas.close();atlas=null;}
        });
    }

    private static ResourceLocation texture(HbmFluidType fluid){
        return TEXTURES.computeIfAbsent(new Mark(fluid.poison,fluid.flammability,fluid.reactivity,fluid.symbol),mark->{
            if(atlas==null)try(var stream=Minecraft.getInstance().getResourceManager().open(ATLAS)){
                atlas=NativeImage.read(stream);
            }catch(IOException error){throw new UncheckedIOException(error);}
            // Two pixels per original atlas pixel retain the half-pixel source digit placement exactly.
            var image=new NativeImage(278,278,true);
            copy(image,5,45,139,139,0,0,278,278);
            digit(image,mark.poison(),53,111);
            digit(image,mark.fire(),119,45);
            digit(image,mark.reactive(),185,111);
            if(mark.symbol()!=HbmFluidType.Symbol.NONE)
                copy(image,mark.symbol().x,mark.symbol().y,59,59,80,146,118,118);
            var id=ResourceLocation.parse("hbm:runtime/tank_danger_"+mark.poison()+"_"+mark.fire()+"_"+mark.reactive()+"_"+mark.symbol().ordinal());
            Minecraft.getInstance().getTextureManager().register(id,new DynamicTexture(image));
            return id;
        });
    }

    private static void digit(NativeImage image,int value,int x,int y){
        if(value>=0&&value<6)copy(image,value==0?125:5+(value-1)*24,5,20,28,x,y,40,56);
    }
    private static void copy(NativeImage image,int sx,int sy,int sw,int sh,int dx,int dy,int width,int height){
        for(int y=0;y<height;y++)for(int x=0;x<width;x++){
            int source=atlas.getPixelRGBA(sx+x*sw/width,sy+y*sh/height),alpha=source>>>24;
            if(alpha==0)continue;
            int tx=dx+x,ty=dy+y;
            if(alpha==255){image.setPixelRGBA(tx,ty,source);continue;}
            int destination=image.getPixelRGBA(tx,ty),da=destination>>>24;
            int outAlpha=alpha+(da*(255-alpha)+127)/255;
            int result=outAlpha<<24;
            for(int shift=0;shift<24;shift+=8){
                int sc=(source>>>shift)&255,dc=(destination>>>shift)&255;
                int numerator=sc*alpha*255+dc*da*(255-alpha);
                result|=((numerator+outAlpha*127)/(outAlpha*255))<<shift;
            }
            image.setPixelRGBA(tx,ty,result);
        }
    }

    private static RenderType type(ResourceLocation texture){
        return TYPES.computeIfAbsent(texture,key->create("hbm_tank_surface_danger",DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new TextureStateShard(key,false,false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLayeringState(POLYGON_OFFSET_LAYERING).setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false)));
    }
    public static void render(PoseStack poses,MultiBufferSource buffers,int light,HbmFluidType fluid){
        var out=buffers.getBuffer(type(texture(fluid)));var pose=poses.last();
        vertex(out,pose,light,.5F,-.5F,1,0);
        vertex(out,pose,light,.5F,.5F,0,0);
        vertex(out,pose,light,-.5F,.5F,0,1);
        vertex(out,pose,light,-.5F,-.5F,1,1);
    }
    private static void vertex(VertexConsumer out,PoseStack.Pose pose,int light,float y,float z,float u,float v){
        out.addVertex(pose.pose(),0,y,z).setColor(255,255,255,255).setUv(u,v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose,1,0,0);
    }
}
