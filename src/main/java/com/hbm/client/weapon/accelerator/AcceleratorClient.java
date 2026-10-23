package com.hbm.client.weapon.accelerator;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.accelerator.*;
import com.mojang.blaze3d.vertex.*;
import java.io.IOException;
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
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AcceleratorClient {
    private static void add(String name,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
        @Override public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}
    },AcceleratorContent.GUNS.get(name).get());}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){add("gun_tau",new ItemRenderTau(),event);add("gun_coilgun",new ItemRenderCoilgun(),event);add("gun_n_i_4_n_i",new ItemRenderNI4NI(),event);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        AcceleratorGuns.gun_tau.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        AcceleratorGuns.gun_coilgun.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        // Source NI4NI deliberately has no durability/ammo HUD assignment;
        // its four actual model lamps display the regenerating coin count.
        AcceleratorFactory.coil_tungsten.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);AcceleratorFactory.coil_ferrouranium.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
        AcceleratorFactory.tau_uranium.setRendererBeam((beam,partial)->AcceleratorBeams.tau(beam,partial,false));
        AcceleratorFactory.tau_uranium_charge.setRendererBeam((beam,partial)->AcceleratorBeams.tau(beam,partial,true));
        AcceleratorFactory.ni4ni_arc.setRendererBeam(AcceleratorBeams::ni4ni);
    });}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:accelerator_model"),DefaultVertexFormat.NEW_ENTITY),shader->AcceleratorModelRenderTypes.shader=shader);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&AcceleratorContent.GUNS.values().stream().anyMatch(holder->holder.getId().equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}}
    private AcceleratorClient(){}
}
