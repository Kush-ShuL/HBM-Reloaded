package com.hbm.client.bobble;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

/** Narrow PoseStack/buffer backend for the unchanged RenderBobble source algorithm. No OpenGL calls. */
final class BobbleDraw {
    final PoseStack poses;final MultiBufferSource buffers;final int overlay;
    int light;int argb=-1;boolean lighting=true,texture2d=true,cull=true,blend=false,additive=false,smooth=false,zeroAlpha=false;
    private ResourceLocation texture=ResourceLocation.parse("hbm:textures/models/trinkets/socket.png");
    private final Deque<Lighting> lights=new ArrayDeque<>();
    private static final Map<WavefrontMesh,WavefrontMesh> FLAT=new IdentityHashMap<>();
    /** HFR VBOs contain authored corner normals; legacy GL_FLAT uses the provoking (last) vertex normal. */
    private static WavefrontMesh flat(WavefrontMesh mesh){return FLAT.computeIfAbsent(mesh,source->{List<WavefrontMesh.Face> faces=new ArrayList<>();for(var face:source.faces()){var last=face.corners().getLast();var normal=last.normal()<0?face.normal():source.normals().get(last.normal());faces.add(new WavefrontMesh.Face(face.corners(),face.object(),face.groups(),face.legacyPart(),face.material(),face.smoothingGroup(),normal,face.sourceLine()));}return new WavefrontMesh(source.positions(),source.textureCoordinates(),source.normals(),faces,source.triangles(),source.materialLibraries(),source.nonFaceElements(),source.otherDirectives());});}
    private record Lighting(int light,boolean lighting){}
    BobbleDraw(PoseStack poses,MultiBufferSource buffers,int light,int overlay){this.poses=poses;this.buffers=buffers;this.light=light;this.overlay=overlay;}
    void bind(ResourceLocation texture){this.texture=texture;}
    void glPushMatrix(){poses.pushPose();}void glPopMatrix(){poses.popPose();}
    void glTranslated(double x,double y,double z){poses.translate(x,y,z);}void glTranslatef(float x,float y,float z){glTranslated(x,y,z);}
    void glScaled(double x,double y,double z){poses.scale((float)x,(float)y,(float)z);}void glScalef(float x,float y,float z){glScaled(x,y,z);}
    void glRotated(double angle,double x,double y,double z){poses.mulPose(new Quaternionf().rotationAxis((float)Math.toRadians(angle),(float)x,(float)y,(float)z));}
    void glRotatef(float angle,float x,float y,float z){glRotated(angle,x,y,z);}
    void glEnable(int cap){set(cap,true);}void glDisable(int cap){set(cap,false);}
    private void set(int cap,boolean enabled){switch(cap){case GL11.GL_LIGHTING->lighting=enabled;case GL11.GL_TEXTURE_2D->texture2d=enabled;case GL11.GL_CULL_FACE->cull=enabled;case GL11.GL_BLEND->blend=enabled;default->{/* GL_RESCALE_NORMAL is performed by PoseStack normals. */}}}
    void glAlphaFunc(int function,float reference){zeroAlpha=reference==0;}
    void glShadeModel(int mode){smooth=mode==GL11.GL_SMOOTH;}
    void glBlendFunc(int source,int destination){additive=destination==GL11.GL_ONE;}
    void blendSeparate(int source,int destination,int sourceAlpha,int destinationAlpha){additive=destination==GL11.GL_ONE;}
    void glColor4f(float r,float g,float b,float a){argb=((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(b*255);}
    void glPushAttrib(int ignored){lights.push(new Lighting(light,lighting));}
    void glPopAttrib(){var old=lights.pop();light=old.light;lighting=old.lighting;}
    void fullbright(){light=LightTexture.FULL_BRIGHT;}
    Model model(String path,boolean forge){return new Model(ResourceLocation.parse("hbm:"+path),forge);}
    final class Model {
        final ResourceLocation id;final boolean forge;
        Model(ResourceLocation id,boolean forge){this.id=id;this.forge=forge;}
        void renderPart(String part){draw(part);}void renderAll(){draw(null);}
        private void draw(String part){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);if(!forge&&!smooth)mesh=flat(mesh);RenderType type=renderType(!("Glow".equals(part)&&blend&&additive));var consumer=buffers.getBuffer(type);var convention=forge?LegacyObjRenderer.Convention.FORGE:smooth?LegacyObjRenderer.Convention.HFR:LegacyObjRenderer.Convention.HFR_FLAT;
            if(part==null)LegacyObjRenderer.renderAll(mesh,poses,consumer,light,overlay,argb,convention);else LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,argb,convention,part);flush(type);
        }
    }
    private RenderType renderType(){return renderType(true);}
    private RenderType renderType(boolean depthWrite){return BobbleRenderTypes.get(texture,cull,blend,additive,!lighting,texture2d,zeroAlpha,depthWrite);}
    private void flush(RenderType type){if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(type);}
    void label(String label,int color){
        Font font=Minecraft.getInstance().font;glTranslated(.63,.175F,0);glScalef(.01F,-.01F,.01F);glTranslated(0,0,font.width(label)*.5D);glRotatef(90,0,1,0);glTranslatef(0,1,0);
        Set<RenderType> used=new LinkedHashSet<>();MultiBufferSource labels=type->{RenderType wrapped=BobbleRenderTypes.label(type);used.add(wrapped);return buffers.getBuffer(wrapped);};
        font.drawInBatch(label,0,0,color,false,poses.last().pose(),labels,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);used.forEach(this::flush);
    }
    /** Original ItemRenderer.renderItemIn2D, using the unaltered 16px item PNG rather than a substituted item model. */
    void icon(ResourceLocation icon) {
        bind(icon);RenderType type=renderType();
        LegacyItemIn2D.render(poses,buffers.getBuffer(type),overlay,light,argb,16,16,.0625F);
        flush(type);
    }
}
