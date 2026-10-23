package com.hbm.client.weapon.folly;

import com.hbm.client.weapon.legendary.GunVisuals;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.folly.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Actual original Folly model, ammo HUD and sole shared source MK4CL renderer. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FollyClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        for(var name:new String[]{"gun_folly"}){
            var renderer=new ItemRenderFolly();
            var item=FollyContent.GUN.get();GunVisuals.register(item,renderer);
            event.registerItem(new IClientItemExtensions(){
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
                @Override public HumanoidModel.ArmPose getArmPose(LivingEntity living,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(living,hand,stack);}
            },item);
        }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{FollyContent.GUN.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_AMMO);FollyFactory.folly_sm.setRendererBeam(FollyVisuals::beam);FollyFactory.folly_nuke.setRenderer(FollyVisuals::nuke);});}
    @SubscribeEvent public static void projectiles(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(FollyContent.BULLET_CL.get(),com.hbm.client.weapon.SednaProjectileClient.ConfiguredBulletRenderer::new);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&FollyContent.GUN.getId().equals(id.id())?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{
        Model(BakedModel original){super(original);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return false;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}
    }
    private FollyClient(){}
}
