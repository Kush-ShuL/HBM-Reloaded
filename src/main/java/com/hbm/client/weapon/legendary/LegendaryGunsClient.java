package com.hbm.client.weapon.legendary;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.legendary.*;
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
import java.util.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegendaryGunsClient {
    private static final Map<Item,LegacyGunRenderer> RENDERERS=new IdentityHashMap<>();
    private static void add(Item item,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){RENDERERS.put(item,renderer);GunVisuals.register(item,renderer);event.registerItem(new IClientItemExtensions(){@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}@Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}},item);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){add(LegendaryGunsContent.LILMAC.get(),new ItemRenderHeavyRevolver(LegendaryModels.texture("lilmac")),event);add(LegendaryGunsContent.BROKEN.get(),new ItemRenderMaresleg(LegendaryModels.texture("maresleg_broken")),event);add(LegendaryGunsContent.SEXY.get(),new ItemRenderSexy(LegendaryModels.texture("sexy_real_no_fake")),event);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        CasingEffects.client=SpentCasingParticle::receive;
        for(var config:List.of(LegendaryGunFactory.m44_bp,LegendaryGunFactory.m44_sp,LegendaryGunFactory.m44_fmj,LegendaryGunFactory.m44_jhp,LegendaryGunFactory.g12_bp,LegendaryGunFactory.g12_bp_magnum,LegendaryGunFactory.g12_bp_slug,LegendaryGunFactory.g12,LegendaryGunFactory.g12_slug,LegendaryGunFactory.g12_magnum))config.setRenderer(GunBulletRenderer.STANDARD);
        LegendaryGunFactory.m44_ap.setRenderer(GunBulletRenderer.AP);LegendaryGunFactory.g12_phosphorus.setRenderer(GunBulletRenderer.AP);LegendaryGunFactory.m44_express.setRenderer(GunBulletRenderer.EXPRESS);LegendaryGunFactory.g12_explosive.setRenderer(GunBulletRenderer.EXPRESS);
        LegendaryGunFactory.g12_flechette.setRenderer((bullet,partial)->colored(bullet,partial,0x8C8C8C,0xCACACA,false));
        for(var config:List.of(LegendaryGunFactory.m44_equestrian_pip,LegendaryGunFactory.g12_equestrian_bj,LegendaryGunFactory.g12_equestrian_tkr))config.setRenderer((bullet,partial)->colored(bullet,partial,0x7F006E,0xFF7FED,true));
        LegendaryGunsContent.LILMAC.get().getConfig(null,0).hud(GunHudComponents.DURABILITY,GunHudComponents.AMMO);LegendaryGunsContent.SEXY.get().getConfig(null,0).hud(GunHudComponents.DURABILITY,GunHudComponents.AMMO);LegendaryGunsContent.BROKEN.get().getConfig(null,0).hud(GunHudComponents.AMMO);
    });}
    private static void colored(com.hbm.entity.projectile.EntityBulletBaseMK4 bullet,float partial,int dark,int light,boolean fullbright){double length=bullet.prevVelocity+(bullet.velocity-bullet.prevVelocity)*partial;if(length>0)GunBulletRenderer.renderBulletStandard(dark,light,length,.03125D,.03125D*.25D,fullbright);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var ids=Set.of("hbm:gun_heavy_revolver_lilmac","hbm:gun_maresleg_broken","hbm:gun_autoshotgun_sexy");event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&ids.contains(id.id().toString())?new GunModel(model):model);}
    private static final class GunModel extends BakedModelWrapper<BakedModel>{GunModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
