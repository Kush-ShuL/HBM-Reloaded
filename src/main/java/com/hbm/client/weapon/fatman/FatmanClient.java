package com.hbm.client.weapon.fatman;

import com.hbm.client.weapon.legendary.GunVisuals;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.fatman.*;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Actual original FatMan model, durability/ammo HUD and complete shared native projectile callbacks. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FatmanClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        for(var name:new String[]{"gun_fatman"}){
            var renderer=new ItemRenderFatMan();
            var item=AntimatterM42Content.FATMAN.get();GunVisuals.register(item,renderer);
            event.registerItem(new IClientItemExtensions(){
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
                @Override public HumanoidModel.ArmPose getArmPose(LivingEntity living,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(living,hand,stack);}
            },item);
        }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{AntimatterM42Content.FATMAN.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);for(var bullet:new com.hbm.weapon.sedna.BulletConfig[]{FatmanBullets.nuke_standard,FatmanBullets.nuke_demo,FatmanBullets.nuke_high})bullet.setRenderer(FatmanVisuals::nuke);FatmanBullets.nuke_tots.setRenderer(FatmanVisuals::tiny);FatmanBullets.nuke_hive.setRenderer(FatmanVisuals::hive);FatmanBullets.nuke_balefire.setRenderer(FatmanVisuals::balefire);com.hbm.weapon.sedna.CatapultBulletConfigs.CLUSTER_SUBMUNITION.setRenderer(FatmanVisuals::bomb);});}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&AntimatterM42Content.FATMAN.getId().equals(id.id())?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{
        Model(BakedModel original){super(original);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return false;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}
    }
    private FatmanClient(){}
}
