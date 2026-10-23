package com.hbm.block.ore;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteTicker;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/** Original local-player light palette, rendered continuously between client samples. */
public final class AlexandriteSprites implements SpriteSource {
    public static final AlexandriteSprites INSTANCE=new AlexandriteSprites();
    public static final MapCodec<AlexandriteSprites> CODEC=MapCodec.unit(INSTANCE);
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","item/gem_alexandrite");
    private static final Set<Contents> ACTIVE=ConcurrentHashMap.newKeySet();
    static SpriteSourceType TYPE;
    private static int previousLight,currentLight;
    private static boolean sampled;
    private AlexandriteSprites(){}
    @Override public SpriteSourceType type(){return TYPE;}
    @Override public void run(ResourceManager resources,Output output){
        resources.getResource(TEXTURE.withPath(path->"textures/"+path+".png")).ifPresent(resource->output.add(TEXTURE,loader->{
            SpriteContents loaded=loader.loadSprite(TEXTURE,resource);
            if(loaded==null)return null;
            NativeImage image=loaded.getOriginalImage();
            if(image.getWidth()!=loaded.width()||image.getHeight()!=loaded.height()*16){loaded.close();throw new IllegalArgumentException("Alexandrite requires the original 16 vertically stacked light frames");}
            NativeImage copy=new NativeImage(image.getWidth(),image.getHeight(),false);copy.copyFrom(image);
            int width=loaded.width(),height=loaded.height();ResourceMetadata metadata=loaded.metadata();loaded.close();
            return new Contents(new FrameSize(width,height),copy,metadata);
        }));
    }
    public static void tick(ClientTickEvent.Post event){
        Minecraft client=Minecraft.getInstance();
        if(client.level==null||client.player==null)return; // Source retains its last frame outside a world.
        int light=client.player.level().getBrightness(LightLayer.BLOCK,client.player.blockPosition());
        previousLight=sampled?currentLight:light;currentLight=light;sampled=true;
    }
    public static void frame(RenderFrameEvent.Pre event){
        if(ACTIVE.isEmpty())return;
        float light=Mth.lerp(event.getPartialTick().getGameTimeDeltaPartialTick(true),previousLight,currentLight);
        if(ACTIVE.stream().noneMatch(sprite->sprite.needsUpload(light)))return;
        RenderSystem.assertOnRenderThread();
        int bound=GlStateManager._getInteger(3553); // Current unit's GL_TEXTURE_BINDING_2D.
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).bind();
        try{for(Contents sprite:ACTIVE)if(sprite.needsUpload(light))sprite.uploadLight(light);}
        finally{RenderSystem.bindTexture(bound);}
    }
    private static final class Contents extends SpriteContents {
        private int originX,originY;
        private NativeImage[] blended;
        private float uploadedLight=Float.NaN;
        Contents(FrameSize size,NativeImage image,ResourceMetadata metadata){super(TEXTURE,size,image,metadata);}
        @Override public SpriteTicker createTicker(){return null;} // No independent color cycle.
        @Override public void uploadFirstFrame(int x,int y){
            super.uploadFirstFrame(x,y);originX=x;originY=y;
            blended=new NativeImage[byMipLevel.length];
            for(int level=0;level<blended.length;level++)blended[level]=new NativeImage(Math.max(1,width()>>level),Math.max(1,height()>>level),false);
            ACTIVE.add(this);
        }
        boolean needsUpload(float light){return blended!=null&&Float.compare(uploadedLight,light)!=0;}
        void uploadLight(float value){
            float light=Mth.clamp(value,0,15);int from=Mth.floor(light),to=Math.min(15,from+1);float fraction=light-from;
            for(int level=0;level<blended.length;level++){
                NativeImage image=blended[level],frames=byMipLevel[level];int width=image.getWidth(),height=image.getHeight();
                for(int y=0;y<height;y++)for(int x=0;x<width;x++)image.setPixelRGBA(x,y,blend(frames.getPixelRGBA(x,y+from*height),frames.getPixelRGBA(x,y+to*height),fraction));
                image.upload(level,originX>>level,originY>>level,0,0,width,height,blended.length>1,false);
            }
            uploadedLight=value;
        }
        private static int blend(int from,int to,float fraction){
            int result=0;for(int shift=0;shift<32;shift+=8)result|=((int)Mth.lerp(fraction,(from>>>shift)&255,(to>>>shift)&255))<<shift;
            return result;
        }
        @Override public void close(){ACTIVE.remove(this);if(blended!=null){for(NativeImage image:blended)image.close();blended=null;}super.close();}
    }
}
