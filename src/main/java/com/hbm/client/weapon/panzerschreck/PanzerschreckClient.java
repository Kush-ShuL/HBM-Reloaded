package com.hbm.client.weapon.panzerschreck;
import com.hbm.client.model.*;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.panzerschreck.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PanzerschreckClient {
    private PanzerschreckClient(){}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        var renderer=new ItemRenderPanzerschreck();
        event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
            public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},PanzerschreckContent.PANZERSCHRECK.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        var stack=new ItemStack(PanzerschreckContent.PANZERSCHRECK.get());
        PanzerschreckContent.PANZERSCHRECK.get().getConfig(stack,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        for(var config:PanzerschreckBullets.ROCKETS)config.setRenderer(PanzerschreckClient::rocket);
    });}
    /** Original LegoClient.RENDER_RPZB model and full-bright exhaust taper. */
    public static void rocket(EntityBulletBaseMK4 bullet,float partial){
        var frame=GunBulletRenderContext.current();var pose=frame.poses();pose.pushPose();
        pose.scale(.125F,.125F,.125F);pose.mulPose(Axis.YP.rotationDegrees(-90));pose.translate(0,0,3.5);
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/weapons/panzerschreck.obj"));
        LegacyObjRenderer.renderOnly(mesh,pose,frame.buffers().getBuffer(RenderType.entityCutout(PanzerschreckModels.panzerschreck_tex)),frame.light(),net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Rocket");
        pose.popPose();pose.translate(.375,0,0);
        double length=bullet.prevVelocity+(bullet.velocity-bullet.prevVelocity)*partial;
        if(length>0)GunBulletRenderer.renderBulletStandard(0x808080,0xFFF2A7,length*2,.03125D,.03125D*.25D,true);
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(ResourceLocation.parse("hbm:gun_panzerschreck"))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
