package com.hbm.client.weapon.aberrator;
import com.hbm.client.model.*;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.aberrator.*;
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
public final class AberratorClient {
    private AberratorClient(){}
    private static void add(Item item,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},item);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){add(AberratorGuns.gun_aberrator,new ItemRenderAberrator(),event);add(AberratorGuns.gun_aberrator_eott,new ItemRenderEOTT(),event);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{AberratorGuns.gun_aberrator.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_AMMO);AberratorGuns.gun_aberrator_eott.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_AMMO_MIRROR);AberratorGuns.gun_aberrator_eott.getConfig(null,1).hud(SourceGunHudComponents.HUD_COMPONENT_AMMO);XFactory35800.p35800.setRendererBeam((beam,partial)->AberratorBeams.draw(beam,partial,false));XFactory35800.p35800_bl.setRendererBeam((beam,partial)->AberratorBeams.draw(beam,partial,true));});}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&AberratorContent.allStacks().stream().anyMatch(stack->net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
