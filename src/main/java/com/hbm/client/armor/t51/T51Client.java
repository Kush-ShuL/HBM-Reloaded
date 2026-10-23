package com.hbm.client.armor.t51;

import com.hbm.armor.t51.T51Content;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class T51Client {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private final T51ItemRenderer renderer=new T51ItemRenderer();@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}},T51Content.HELMET.get(),T51Content.PLATE.get(),T51Content.LEGS.get(),T51Content.BOOTS.get());
        event.registerItem(new IClientItemExtensions(){
            private final com.hbm.client.armor.LegacyHazmatArmorModel inner=new com.hbm.client.armor.LegacyHazmatArmorModel(.5F),outer=new com.hbm.client.armor.LegacyHazmatArmorModel(1);
            @Override public net.minecraft.client.model.Model getGenericArmorModel(net.minecraft.world.entity.LivingEntity living,net.minecraft.world.item.ItemStack stack,net.minecraft.world.entity.EquipmentSlot slot,net.minecraft.client.model.HumanoidModel<?> parent){var model=slot==net.minecraft.world.entity.EquipmentSlot.LEGS?inner:outer;model.configure(living,slot,parent);return model;}
            @Override public void setupModelAnimations(net.minecraft.world.entity.LivingEntity living,net.minecraft.world.item.ItemStack stack,net.minecraft.world.entity.EquipmentSlot slot,net.minecraft.client.model.Model model,float swing,float amount,float partial,float age,float yaw,float pitch){((com.hbm.client.armor.LegacyHazmatArmorModel)model).animate(swing,amount,age,yaw,pitch);}
        },T51Content.TI_HELMET.get(),T51Content.TI_PLATE.get(),T51Content.TI_LEGS.get(),T51Content.TI_BOOTS.get());
    }
    @SubscribeEvent public static void keys(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event){event.register(T51ClientEvents.HUD);}
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->com.hbm.armor.t51.T51Network.CLIENT_HUD=value->T51ClientEvents.hudEnabled=value);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){for(String id:new String[]{"t51_helmet","t51_plate","t51_legs","t51_boots"}){var key=ModelResourceLocation.inventory(T51Content.id(id));var model=event.getModels().get(key);if(model!=null)event.getModels().put(key,new BakedModelWrapper<BakedModel>(model){@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}});}}
    private T51Client(){}
}
