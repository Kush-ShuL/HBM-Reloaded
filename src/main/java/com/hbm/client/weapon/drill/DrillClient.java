package com.hbm.client.weapon.drill;
import com.hbm.client.weapon.legendary.GunVisuals;
import com.hbm.client.weapon.pepperbox.GunHandPoses;
import com.hbm.weapon.sedna.gun.drill.*;
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
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DrillClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var renderer=new ItemRenderDrill();GunVisuals.register(DrillContent.GUN.get(),renderer);event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}@Override public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},DrillContent.GUN.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->DrillOrchestra.CLIENT=DrillSound::orchestra);/* Original GunFactoryClient assigns no ammo/durability panel to this gun; the physical source gauge remains. */}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(DrillContent.GUN.getId())?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel original){super(original);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}}
 private DrillClient(){}
}
