package com.hbm.machine.reactor.zirnox.client;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.zirnox.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
/** Source HFR meshes, source tilted foundation transform and actual mesh contact shadows. */
public final class ZirnoxRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final boolean destroyed;
    public ZirnoxRenderer(BlockEntityRendererProvider.Context context,boolean destroyed){this.destroyed=destroyed;}
    static ResourceLocation id(String path){return ResourceLocation.parse("hbm:"+path);}
    static WavefrontMesh mesh(boolean destroyed){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id("models/zirnox"+(destroyed?"_destroyed":"")+".obj"));}
    static void model(boolean destroyed,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(destroyed),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(id("textures/models/zirnox"+(destroyed?"_destroyed":"")+".png"))),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    private int angle(T reactor){return switch(DummyableBlock.facing(reactor.getBlockState())){case NORTH->90;case SOUTH->270;case WEST->180;default->0;};}
    @Override public void render(T reactor,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var matrix=new Matrix4f().translation(.5F,0,.5F);if(reactor instanceof ZirnoxBlockEntity active&&active.tilted)matrix.translate(0,-.5F,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));matrix.rotateY((float)Math.toRadians(angle(reactor)));LegacyContactShadows.render(reactor,mesh(destroyed),matrix,poses,buffers);poses.pushPose();poses.mulPose(matrix);model(destroyed,poses,buffers,light,overlay);poses.popPose();}
    @Override public boolean shouldRenderOffScreen(T reactor){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(T reactor){var pos=reactor.getBlockPos();return destroyed?new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+3,pos.getZ()+4):new AABB(pos.getX()-2,pos.getY(),pos.getZ()-2,pos.getX()+3,pos.getY()+5,pos.getZ()+3);}
}
