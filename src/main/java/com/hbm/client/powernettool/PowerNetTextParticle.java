package com.hbm.client.powernettool;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
/** Source type=debug is ParticleText (not ParticleDebug):100ticks,source scale*.5,Y.01/noClip and native shadowed world font. */
public final class PowerNetTextParticle extends Particle {
    private final String text;
    private final float sourceScale;
    public PowerNetTextParticle(ClientLevel level,double x,double y,double z,String text) {
        super(level,x,y,z);this.text=text;hasPhysics=false;lifetime=100;xd=zd=0;yd=.01D;gravity=0;friction=.98F;
        sourceScale=(random.nextFloat()*.5F+.5F)*2*.5F;setSize(.1F,.1F);
    }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.CUSTOM; }
    @Override public void render(VertexConsumer ignored,Camera camera,float partial) {
        var mc=Minecraft.getInstance();if(mc.player==null)return;
        var eye=camera.getPosition();var poses=new PoseStack();
        poses.translate((float)(Mth.lerp(partial,xo,x)-eye.x),(float)(Mth.lerp(partial,yo,y)-eye.y),(float)(Mth.lerp(partial,zo,z)-eye.z));
        // Source faces the actual local player's yaw/pitch, deliberately not a guessed third-person camera transform.
        poses.mulPose(Axis.YP.rotationDegrees(-mc.player.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(mc.player.getXRot()));
        poses.scale(-sourceScale*.01F,-sourceScale*.01F,sourceScale*.01F);
        var buffers=mc.renderBuffers().bufferSource();
        mc.font.drawInBatch(text,-(int)(mc.font.width(text)*.5F),-(int)(mc.font.lineHeight*.5F),0xffffff00,true,poses.last().pose(),buffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);
        buffers.endBatch();
    }
}
