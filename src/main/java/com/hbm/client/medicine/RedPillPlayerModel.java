package com.hbm.client.medicine;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.item.medicine.MedicineEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

/** ModelMan: byte-identical OBJ, flat normals, original pivots and actual player part rotations. */
public final class RedPillPlayerModel extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/armor/player_fem.obj");
    private static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/entity/player_fem.png");
    private RedPillPlayerModel(RenderLayerParent<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> parent) {super(parent);}
    @Override public void render(PoseStack pose,MultiBufferSource buffers,int light,AbstractClientPlayer player,float swing,float amount,float partial,float age,float yaw,float pitch) {
        if(!player.hasEffect(MedicineEffects.DEATH))return;
        var model=getParentModel();var vertices=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        part(pose,vertices,light,model.head,"Head",0,0,0);part(pose,vertices,light,model.body,"Body",0,0,0);
        part(pose,vertices,light,model.leftArm,"LeftArm",5,2,0);part(pose,vertices,light,model.rightArm,"RightArm",-5,2,0);
        part(pose,vertices,light,model.leftLeg,"LeftLeg",1.9F,12,0);part(pose,vertices,light,model.rightLeg,"RightLeg",-1.9F,12,0);
    }
    private static void part(PoseStack pose,VertexConsumer vertices,int light,ModelPart part,String name,float ox,float oy,float oz) {
        pose.pushPose();pose.translate(part.x/16F,part.y/16F,part.z/16F);
        if(part.zRot!=0)pose.mulPose(Axis.ZP.rotation(part.zRot));
        if(part.yRot!=0)pose.mulPose(Axis.YP.rotation(part.yRot));
        if(part.xRot!=0)pose.mulPose(Axis.XP.rotation(part.xRot));
        pose.translate(-ox/16F,-oy/16F,-oz/16F);pose.scale(1F/16F,1F/16F,1F/16F);
        LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),pose,vertices,light,OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR_FLAT,name);
        pose.popPose();
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @SubscribeEvent public static void add(EntityRenderersEvent.AddLayers event) {
            for(var skin:event.getSkins()) {
                net.minecraft.client.renderer.entity.player.PlayerRenderer renderer=event.getSkin(skin);
                if(renderer!=null)renderer.addLayer(new RedPillPlayerModel(renderer));
            }
        }
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Visibility {
        private static final Map<AbstractClientPlayer,Map<ModelPart,Boolean>> HIDDEN=new IdentityHashMap<>();
        @SubscribeEvent public static void pre(RenderPlayerEvent.Pre event) {
            if(!(event.getEntity() instanceof AbstractClientPlayer player)||!player.hasEffect(MedicineEffects.DEATH))return;
            var model=event.getRenderer().getModel();var old=new IdentityHashMap<ModelPart,Boolean>();
            for(ModelPart part:new ModelPart[]{model.head,model.hat,model.body,model.leftArm,model.rightArm,model.leftLeg,model.rightLeg,model.jacket,model.leftSleeve,model.rightSleeve,model.leftPants,model.rightPants}){old.put(part,part.visible);part.visible=false;}
            HIDDEN.put(player,old);
        }
        @SubscribeEvent public static void post(RenderPlayerEvent.Post event) {
            var old=HIDDEN.remove(event.getEntity());if(old!=null)old.forEach((part,visible)->part.visible=visible);
        }
    }
}
