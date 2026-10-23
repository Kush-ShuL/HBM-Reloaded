package com.hbm.client.weapon.flaregun;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.flaregun.*;
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
public final class FlaregunClient {
    private FlaregunClient(){}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        var renderer=new ItemRenderFlaregun();
        event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
            public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},FlaregunContent.FLAREGUN.get());
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        var stack=new ItemStack(FlaregunContent.FLAREGUN.get());
        FlaregunContent.FLAREGUN.get().getConfig(stack,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        FlaregunBullets.FLARE.setRenderer((bullet,partial)->FlareSignalRenderer.render(bullet,partial,1F,.5F,.5F));
        FlaregunBullets.SUPPLY.setRenderer((bullet,partial)->FlareSignalRenderer.render(bullet,partial,.5F,.5F,1F));
        FlaregunBullets.WEAPON.setRenderer((bullet,partial)->FlareSignalRenderer.render(bullet,partial,.5F,1F,.5F));
    });}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(ResourceLocation.parse("hbm:gun_flaregun"))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
