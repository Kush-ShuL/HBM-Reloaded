package com.hbm.client.armor.rpa;
import com.hbm.armor.rpa.RpaContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.weapon.pepperbox.GunHandPoses;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RpaClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final RpaItemRenderer renderer=new RpaItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},RpaContent.HELMET.get(),RpaContent.PLATE.get(),RpaContent.LEGS.get(),RpaContent.BOOTS.get());event.registerItem(new IClientItemExtensions(){private final RpaMeleeRenderer renderer=new RpaMeleeRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}@Override public net.minecraft.client.model.HumanoidModel.ArmPose getArmPose(net.minecraft.world.entity.LivingEntity entity,net.minecraft.world.InteractionHand hand,net.minecraft.world.item.ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}},RpaContent.MELEE.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var item:java.util.List.of(RpaContent.HELMET,RpaContent.PLATE,RpaContent.LEGS,RpaContent.BOOTS))T51ArmorRenderer.register(item.get(),RpaArmorRenderer::render);RpaMeleeRenderer.registerRpa();});}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String id:new String[]{"rpa_helmet","rpa_plate","rpa_legs","rpa_boots","gun_pa_melee"}){var key=ModelResourceLocation.inventory(RpaContent.id(id));var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new BakedModelWrapper<>(model){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
 private RpaClient(){}
}
