package com.hbm.client.weapon.utility;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.utility.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChargeThrowerClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var renderer=new ItemRenderChargeThrower();event.registerItem(new IClientItemExtensions(){
  @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
  @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}
 },ChargeThrowerContent.GUN.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
  ChargeThrowerContent.GUN.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
  ChargeThrowerBullets.HOOK.setRenderer(ChargeThrowerProjectiles::hook);
  ChargeThrowerBullets.MORTAR.setRenderer((bullet,partial)->ChargeThrowerProjectiles.mortar(bullet,partial,false));
  ChargeThrowerBullets.HEAVY_MORTAR.setRenderer((bullet,partial)->ChargeThrowerProjectiles.mortar(bullet,partial,true));
 });}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(ChargeThrowerContent.GUN.getId())?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel> {
  Model(BakedModel model){super(model);}
  @Override public boolean isCustomRenderer(){return true;}
  @Override public boolean isGui3d(){return false;}
  @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
  @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}
 }
 private ChargeThrowerClient(){}
}
