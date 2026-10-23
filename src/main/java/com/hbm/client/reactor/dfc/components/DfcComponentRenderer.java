package com.hbm.client.reactor.dfc.components;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.dfc.components.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** RenderCoreComponent's original extra90Y, all six orientation matrices, Forge meshes and beam calls. */
public final class DfcComponentRenderer implements BlockEntityRenderer<DfcComponentEntity> {
 public DfcComponentRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(DfcComponentKind kind){String model=kind==DfcComponentKind.STABILIZER?"injector":kind.id.substring(4);return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/core_"+model+".obj"));}
 static void model(DfcComponentKind kind,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var texture=ResourceLocation.parse("hbm:textures/models/core_"+kind.id.substring(4)+".png");LegacyObjRenderer.renderAll(mesh(kind),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
 private static org.joml.Matrix4f matrix(int meta){var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI/2);switch(meta){case 0->transform.translate(0,.5F,-.5F).rotateX((float)Math.PI/2);case 1->transform.translate(0,.5F,.5F).rotateX(-(float)Math.PI/2);case 2->transform.rotateY((float)Math.PI/2);case 4->transform.rotateY((float)Math.PI);case 3->transform.rotateY((float)Math.PI*1.5F);}return transform;}
 @Override public void render(DfcComponentEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var local=matrix(tile.getBlockState().getValue(DfcComponentBlock.META));LegacyContactShadows.render(tile,mesh(tile.kind),local,pose,buffers);pose.pushPose();pose.mulPose(local);model(tile.kind,pose,buffers,light,overlay);pose.translate(0,.5,0);int range=tile.beam;int ticks=(int)tile.getLevel().getGameTime();if(range>0){if(tile instanceof DfcEmitterEntity){DfcComponentBeams.draw(pose,buffers,range,false,true,0x404000,0x404000,0,1,0,2,.0625F);DfcComponentBeams.draw(pose,buffers,range,true,true,0x401500,0x401500,ticks%1000,range*2,.125F,4,.0625F);DfcComponentBeams.draw(pose,buffers,range,true,true,0x401500,0x401500,ticks%1000+1,range*2,.125F,4,.0625F);}if(tile instanceof DfcInjectorEntity injector){if(injector.tanks[0].getFill()>0)DfcComponentBeams.draw(pose,buffers,range,true,false,injector.tanks[0].getTankType().getColor(),0x808080,ticks%1000,range,.0625F,0,0);if(injector.tanks[1].getFill()>0)DfcComponentBeams.draw(pose,buffers,range,true,false,injector.tanks[1].getTankType().getColor(),0x808080,ticks%1000+1,range,.0625F,0,0);}if(tile instanceof DfcStabilizerEntity){DfcComponentBeams.draw(pose,buffers,range,false,false,0xffa200,0xffd000,ticks*-25%360,range*3,.125F,0,0);DfcComponentBeams.draw(pose,buffers,range,false,false,0xffa200,0xffd000,ticks*-15%360+180,range*3,.125F,0,0);DfcComponentBeams.draw(pose,buffers,range,false,false,0xffa200,0xffd000,ticks*-5%360+180,range*3,.125F,0,0);}}pose.popPose();}
 @Override public boolean shouldRenderOffScreen(DfcComponentEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(DfcComponentEntity tile){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
