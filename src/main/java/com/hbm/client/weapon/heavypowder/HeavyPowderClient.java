package com.hbm.client.weapon.heavypowder;
import com.hbm.client.model.*;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.heavypowder.*;
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
public final class HeavyPowderClient {
    private HeavyPowderClient(){}
    private static void add(Item item,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},item);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){add(HeavyPowderGuns.gun_bolter,new ItemRenderBolter(),event);add(HeavyPowderGuns.gun_double_barrel,new ItemRenderDoubleBarrel(HeavyPowderModels.double_barrel_tex),event);add(HeavyPowderGuns.gun_double_barrel_sacred_dragon,new ItemRenderDoubleBarrel(HeavyPowderModels.double_barrel_sacred_dragon_tex),event);add(HeavyPowderGuns.gun_autoshotgun_heretic,new com.hbm.client.weapon.legendary.ItemRenderSexy(HeavyPowderModels.heretic_tex),event);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{for(var stack:HeavyPowderContent.allStacks())((com.hbm.weapon.sedna.gun.ItemGunBaseNT)stack.getItem()).getConfig(stack,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        HeavyPowderGuns.gun_autoshotgun_heretic.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_AMMO);
        XFactory75Bolt.b75.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);XFactory75Bolt.b75_inc.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);XFactory75Bolt.b75_exp.setRenderer(SourceBulletStyles.RENDER_EXPRESS_BULLET);
        XFactory10ga.g10.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);XFactory10ga.g10_shrapnel.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);XFactory10ga.g10_du.setRenderer(SourceBulletStyles.RENDER_DU_BULLET);XFactory10ga.g10_slug.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);XFactory10ga.g10_explosive.setRenderer(SourceBulletStyles.RENDER_HE_BULLET);
    });}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&HeavyPowderContent.allStacks().stream().anyMatch(stack->net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
