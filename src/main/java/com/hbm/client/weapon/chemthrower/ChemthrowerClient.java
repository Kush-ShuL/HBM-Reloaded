package com.hbm.client.weapon.chemthrower;
import com.hbm.client.weapon.legendary.GunVisuals;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.client.bomb.nuclear.ChemicalCloudParticle;
import com.hbm.bomb.conventional.grenade.FlameEffects;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.hbm.fluid.HbmFluids;
import com.hbm.weapon.sedna.gun.chemthrower.ChemthrowerContent;
import java.util.*;
import net.minecraft.client.Minecraft;
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
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChemthrowerClient {
 private static final Set<EntityChemical> VISUALS=Collections.newSetFromMap(new WeakHashMap<>());
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){var renderer=new ItemRenderChemthrower();GunVisuals.register(ChemthrowerContent.GUN.get(),renderer);event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}@Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}},ChemthrowerContent.GUN.get());}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(ChemthrowerContent.CHEMICAL.get(),ChemicalRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ChemthrowerContent.GUN.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);ChemthrowerContent.CLIENT_ORCHESTRA=ChemthrowerSound::orchestra;EntityChemical.CLIENT_TICK=ChemthrowerClient::effects;});}
 private static void effects(EntityChemical chemical){var minecraft=Minecraft.getInstance();if(minecraft.level==null)return;if(VISUALS.add(chemical))minecraft.particleEngine.add(new ChemicalCloudParticle(minecraft.level,chemical));var type=chemical.getFluidType();var style=chemical.getStyle();boolean near=minecraft.player!=null&&minecraft.player.distanceTo(chemical)<100;if(type==HbmFluids.BALEFIRE){if(near)FlameEffects.client(chemical.level(),chemical.getX(),chemical.getY()-.125,chemical.getZ(),1);}else if(style==ChemicalStyle.LIQUID)ChemicalLiquidDust.spawn(chemical);else if(style==ChemicalStyle.BURNING&&near)FlameEffects.client(chemical.level(),chemical.getX(),chemical.getY()-.125,chemical.getZ(),0);}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().equals(ChemthrowerContent.GUN.getId())?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel original){super(original);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}}
 private ChemthrowerClient(){}
}
