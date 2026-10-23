package com.hbm.client.reactor.rbmk.console;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.rbmk.console.RbmkConsoleEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR console,225 live panel quads/fuel-control markers and six real physical font displays. */
public final class RbmkConsoleRenderer implements BlockEntityRenderer<RbmkConsoleEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/rbmk/rbmk_console.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/rbmk_control.png");
    public RbmkConsoleRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(RbmkConsoleEntity console,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);float angle=switch(DummyableBlock.facing(console.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)).translate(.5F,0,0);LegacyContactShadows.render(console,mesh,transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));poses.translate(.5,0,0);LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);
        var out=buffers.getBuffer(RbmkConsoleRenderTypes.PANELS);for(int i=0;i<225;i++){var column=console.columns[i];if(column==null)continue;var tag=column.data();float x=-.3725F,y=3.625F-i/15*.125F,z=.875F-i%15*.125F;float r,g,b;if(tag.contains("color")&&tag.getByte("color")>=0){int color=switch(tag.getByte("color")){case 0->0xff0000;case 1->0xffff00;case 2->0x008000;case 3->0x0000ff;case 4->0x8000ff;default->0xffffff;};r=(color>>16&255)/255F;g=(color>>8&255)/255F;b=(color&255)/255F;}else{double heat=tag.getDouble("heat")/tag.getDouble("maxHeat"),color=.65+i%2*.05;r=(float)(color+(1-color)*heat);g=b=(float)color;}if(tag.getByte("indicator")>0){r=g=1;b=0;}float w=.0625F*.75F;quad(out,poses,new float[][]{{x,y+w,z-w},{x,y+w,z+w},{x,y-w,z+w},{x,y-w,z-w}},r,g,b);switch(column.type()){case FUEL,FUEL_SIM->dot(out,poses,x+.01F,y,z,0,.25F+(float)(tag.getDouble("enrichment")*.75),0);case CONTROL->{float e=(float)tag.getDouble("level");dot(out,poses,x+.01F,y,z,e,e,0);}case CONTROL_AUTO->{float e=(float)tag.getDouble("level");dot(out,poses,x+.01F,y,z,e,0,e);}default->{}}}
        poses.translate(-.42F,3.5F,1.75F);var font=LegacyFonts.get();MultiBufferSource textBuffers=type->buffers.getBuffer(RbmkConsoleRenderTypes.font(type));for(int i=0;i<6;i++){String text=console.screens[i].display;if(text==null||text.isEmpty())continue;var parts=text.split("=");if(parts.length==2)text=Component.translatable(parts[0],parts[1]).getString();poses.pushPose();if(i%2==1)poses.translate(0,0,-3.5F);poses.translate(0,-.75F*(i/2),0);int width=font.width(text);float scale=Math.min(.03F,.8F/Math.max(width,1));poses.scale(scale,-scale,scale);poses.mulPose(Axis.YP.rotationDegrees(90));font.drawInBatch(text,-width/2,-font.lineHeight/2,0xff00ff00,false,poses.last().pose(),textBuffers,Font.DisplayMode.NORMAL,0,LightTexture.FULL_BRIGHT);poses.popPose();}poses.popPose();
    }
    private static void quad(VertexConsumer out,PoseStack poses,float[][] vertices,float r,float g,float b){for(var v:vertices)out.addVertex(poses.last().pose(),v[0],v[1],v[2]).setColor(r,g,b,1);}
    private static void dot(VertexConsumer out,PoseStack poses,float x,float y,float z,float r,float g,float b){float w=.03125F,e=.022097F;quad(out,poses,new float[][]{{x,y+w,z},{x,y+e,z+e},{x,y,z+w},{x,y-e,z+e}},r,g,b);quad(out,poses,new float[][]{{x,y+e,z-e},{x,y+w,z},{x,y-e,z-e},{x,y,z-w}},r,g,b);quad(out,poses,new float[][]{{x,y+w,z},{x,y-e,z+e},{x,y-w,z},{x,y-e,z-e}},r,g,b);}
    @Override public boolean shouldRenderOffScreen(RbmkConsoleEntity console){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(RbmkConsoleEntity console){var p=console.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+4,p.getZ()+3);}
}
