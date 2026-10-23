package com.hbm.client.weapon.legendary;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
/** Explicit per-draw pose/material context for retained source renderer methods. No fixed-function GL is invoked. */
public final class LegacyGunGL {
    public static final int GL_LIGHTING=2896,GL_ALPHA_TEST=3008,GL_GREATER=516,GL_SMOOTH=7425,GL_FLAT=7424;
    private static final ThreadLocal<Frame> CURRENT=new ThreadLocal<>();
    public record Frame(PoseStack pose,MultiBufferSource buffers,int light,int overlay,ResourceLocation[] texture){}
    public static AutoCloseable push(PoseStack pose,MultiBufferSource buffers,int light,int overlay){var old=CURRENT.get();CURRENT.set(new Frame(pose,buffers,light,overlay,new ResourceLocation[1]));return ()->{if(old==null)CURRENT.remove();else CURRENT.set(old);};}
    public static Frame frame(){return java.util.Objects.requireNonNull(CURRENT.get(),"Gun renderer must be inside its explicit pose/material context");}
    public static PoseStack currentPose(){var context=CURRENT.get();return context==null?null:context.pose();}
    public static void glPushMatrix(){frame().pose.pushPose();}public static void glPopMatrix(){frame().pose.popPose();}
    public static void glTranslated(double x,double y,double z){frame().pose.translate(x,y,z);}public static void glTranslatef(float x,float y,float z){glTranslated(x,y,z);}
    public static void glScaled(double x,double y,double z){frame().pose.scale((float)x,(float)y,(float)z);}
    public static void glRotated(double angle,double x,double y,double z){frame().pose.mulPose(new Quaternionf().rotationAxis((float)Math.toRadians(angle),(float)x,(float)y,(float)z));}
    public static void glRotatef(float angle,float x,float y,float z){glRotated(angle,x,y,z);}
    // These source declarations select the same native lit/cutout render type and authored HFR normals below.
    public static void glEnable(int flag){if(flag!=GL_LIGHTING&&flag!=GL_ALPHA_TEST)throw new IllegalArgumentException("Unknown retained gun state "+flag);}
    public static void glAlphaFunc(int compare,float threshold){if(compare!=GL_GREATER||threshold!=0)throw new IllegalArgumentException("Unknown retained gun alpha state");}
    public static void glShadeModel(int mode){if(mode!=GL_SMOOTH&&mode!=GL_FLAT)throw new IllegalArgumentException("Unknown retained gun shade state");}
    public static void texture(ResourceLocation texture){frame().texture[0]=texture;}
    public static void mesh(ResourceLocation model,String part){var context=frame();var texture=java.util.Objects.requireNonNull(context.texture[0],"Source gun model texture");var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);var vertices=context.buffers.getBuffer(RenderType.entityCutout(texture));if(part==null)LegacyObjRenderer.renderAll(mesh,context.pose,vertices,context.light,context.overlay,-1,LegacyObjRenderer.Convention.HFR);else LegacyObjRenderer.renderOnly(mesh,context.pose,vertices,context.light,context.overlay,-1,LegacyObjRenderer.Convention.HFR,part);}
    private LegacyGunGL(){}
}
