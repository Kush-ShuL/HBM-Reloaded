package com.hbm.client.batterysocket;

import com.hbm.HbmReloaded;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.item.battery.BatteryPackItem;
import com.hbm.item.battery.CreativeBatteryItem;
import com.hbm.item.battery.SelfChargingBatteryItem;
import com.hbm.machine.batterysocket.BatterySocketBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Original RenderBatterySocket groups, HFR normals, supports, horse and independent four-corner arcs. */
public final class BatterySocketRenderer implements BlockEntityRenderer<BatterySocketBlockEntity> {
    static final ResourceLocation MODEL=HbmReloaded.id("models/machines/battery.obj"),TEXTURE=HbmReloaded.id("textures/models/machines/battery_socket.png"),SC_TEXTURE=HbmReloaded.id("textures/models/machines/battery_sc.png");
    private static final ResourceLocation HORSE=HbmReloaded.id("models/mobs/horse.obj"),HORSE_TEXTURE=HbmReloaded.id("textures/models/horse/sunburst.png");
    public BatterySocketRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(BatterySocketBlockEntity socket,float partialTick,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();poses.translate(.5,0,.5);
        float angle=switch(DummyableBlock.facing(socket.getBlockState())) {case NORTH->90;case WEST->180;case SOUTH->270;default->0;};
        poses.mulPose(Axis.YP.rotationDegrees(angle));poses.translate(-.5,0,.5);
        part(poses,buffers,light,overlay,TEXTURE,"Socket");if(socket.frame())part(poses,buffers,light,overlay,TEXTURE,"Supports");
        var stack=socket.displayedStack();
        if(!stack.isEmpty()) {
            if(stack.getItem() instanceof BatteryPackItem pack) {var variant=pack.variant(stack);part(poses,buffers,light,overlay,variant.texture,variant.isCapacitor()?"Capacitor":"Battery");}
            else if(stack.getItem() instanceof SelfChargingBatteryItem)part(poses,buffers,light,overlay,SC_TEXTURE,"Battery");
            else {
                double time=socket.getLevel().getGameTime()%360+partialTick;
                if(stack.getItem() instanceof CreativeBatteryItem) {
                    poses.pushPose();poses.scale(.75F,.75F,.75F);poses.mulPose(Axis.YP.rotationDegrees((float)(-time*25)));
                    var horse=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),HORSE);
                    // HorsePronter.reset(), enableHorn(): every pose is zero and wings are disabled.
                    LegacyObjRenderer.renderOnly(horse,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(HORSE_TEXTURE)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR,"Body","Head","Mane","NoseFemale","HornPointy","LeftFrontLeg","RightFrontLeg","LeftBackLeg","RightBackLeg","Tail");
                    poses.popPose();
                } else {
                    poses.pushPose();poses.translate(0,.5,0);poses.scale(1.5F,1.5F,1.5F);poses.mulPose(Axis.YP.rotationDegrees((float)(-time*2.5)));
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack,ItemDisplayContext.FIXED,light,overlay,poses,buffers,socket.getLevel(),0);poses.popPose();
                }
                var random=new Random(socket.getLevel().getGameTime()/5);random.nextBoolean();
                for(int i=-1;i<=1;i+=2)for(int j=-1;j<=1;j+=2)if(random.nextInt(4)==0) {
                    poses.pushPose();poses.translate(0,.75,0);Vec3 target=new Vec3(.4375*i,1.1875,.4375*j);
                    SocketArcRenderer.beam(target,poses,buffers,(int)(System.currentTimeMillis()%1000)/50,15,.0625F);
                    SocketArcRenderer.beam(target,poses,buffers,(int)(System.currentTimeMillis()%1000)/50,1,0);poses.popPose();
                }
            }
        }
        poses.popPose();
    }
    static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,String...parts) {
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR,parts);
    }
    @Override public int getViewDistance(){return 256;}
    @Override public boolean shouldRenderOffScreen(BatterySocketBlockEntity socket){return true;}
    @Override public AABB getRenderBoundingBox(BatterySocketBlockEntity socket){var pos=socket.getBlockPos();return new AABB(pos.getX()-1,pos.getY(),pos.getZ()-1,pos.getX()+2,pos.getY()+2,pos.getZ()+2);}
}
