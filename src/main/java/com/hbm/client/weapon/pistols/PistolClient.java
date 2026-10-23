package com.hbm.client.weapon.pistols;
import com.hbm.client.weapon.legendary.*;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.pistols.*;
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
public final class PistolClient {
 private static void add(Item item,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}},item);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){ add(PistolGuns.gun_light_revolver,new ItemRenderAtlas(PistolModels.bio_revolver_tex),event);
 add(PistolGuns.gun_light_revolver_atlas,new ItemRenderAtlas(PistolModels.bio_revolver_atlas_tex),event);
 add(PistolGuns.gun_light_revolver_dani,new ItemRenderDANI(),event);
 add(PistolGuns.gun_am180,new ItemRenderAm180(),event);
 add(PistolGuns.gun_star_f,new ItemRenderStarF(),event);
 add(PistolGuns.gun_star_f_akimbo,new ItemRenderStarFAkimbo(),event);
 add(PistolGuns.gun_greasegun,new ItemRenderGreasegun(),event);
 add(PistolGuns.gun_lag,new ItemRenderLAG(),event);
 add(PistolGuns.gun_uzi,new ItemRenderUzi(),event);
 add(PistolGuns.gun_uzi_akimbo,new ItemRenderUziAkimbo(),event);
 add(PistolGuns.gun_henry,new ItemRenderHenry(PistolModels.henry_tex),event);
 add(PistolGuns.gun_henry_lincoln,new ItemRenderHenry(PistolModels.henry_lincoln_tex),event);
 add(PistolGuns.gun_heavy_revolver,new ItemRenderHeavyRevolver(PistolModels.heavy_revolver_tex),event);
 add(PistolGuns.gun_heavy_revolver_protege,new ItemRenderHeavyRevolver(PistolModels.heavy_revolver_protege_tex),event);
 add(PistolGuns.gun_hangman,new ItemRenderHangman(),event);
 add(PistolGuns.gun_debug,new ItemRenderDebug(),event);
}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
  PistolGuns.gun_debug.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO, SourceGunHudComponents.HUD_COMPONENT_AMMO_SECOND);
PistolGuns.gun_light_revolver.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_light_revolver_atlas.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_henry.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_henry_lincoln.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_greasegun.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_heavy_revolver.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_heavy_revolver_protege.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_am180.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_uzi.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_star_f.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_lag.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_hangman.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_light_revolver_dani.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY_MIRROR, SourceGunHudComponents.HUD_COMPONENT_AMMO_MIRROR);
PistolGuns.gun_light_revolver_dani.getConfig(null,1).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_uzi_akimbo.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY_MIRROR, SourceGunHudComponents.HUD_COMPONENT_AMMO_MIRROR);
PistolGuns.gun_uzi_akimbo.getConfig(null,1).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
PistolGuns.gun_star_f_akimbo.getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY_MIRROR, SourceGunHudComponents.HUD_COMPONENT_AMMO_MIRROR);
PistolGuns.gun_star_f_akimbo.getConfig(null,1).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
  XFactory357.m357_express.setRenderer(SourceBulletStyles.RENDER_EXPRESS_BULLET);
XFactory357.m357_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
XFactory357.m357_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory357.m357_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory357.m357_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory357.m357_bp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory22lr.p22_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
XFactory22lr.p22_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory22lr.p22_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory22lr.p22_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory45.p45_du.setRenderer(SourceBulletStyles.RENDER_DU_BULLET);
XFactory45.p45_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
XFactory45.p45_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory45.p45_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory45.p45_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory9mm.p9_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
XFactory9mm.p9_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory9mm.p9_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory9mm.p9_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory44.m44_equestrian_mn7.setRenderer(SourceBulletStyles.RENDER_LEGENDARY_BULLET);
XFactory44.m44_equestrian_pip.setRenderer(SourceBulletStyles.RENDER_LEGENDARY_BULLET);
XFactory44.m44_express.setRenderer(SourceBulletStyles.RENDER_EXPRESS_BULLET);
XFactory44.m44_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
XFactory44.m44_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory44.m44_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory44.m44_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
XFactory44.m44_bp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
PistolDebugFactory.ammo_debug.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
PistolDebugFactory.ammo_debug_shot.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
    });}
 @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener)resources->PistolAnimationAssets.clear());}
 @SubscribeEvent public static void entities(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(PistolSpecialEntities.TORPEDO.get(),TorpedoRenderer::new);}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&PistolContent.allStacks().stream().anyMatch(stack->net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id.id()))?new Model(model):model);}
 private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}public boolean isCustomRenderer(){return true;}public boolean isGui3d(){return false;}public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
