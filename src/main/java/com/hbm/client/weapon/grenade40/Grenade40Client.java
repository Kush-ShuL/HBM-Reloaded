package com.hbm.client.weapon.grenade40;
import com.hbm.client.model.*;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.grenade40.*;
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
public final class Grenade40Client {
    private Grenade40Client(){}
    private static void add(Item item,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}public HumanoidModel.ArmPose getArmPose(LivingEntity owner,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(owner,hand,stack);}},item);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){add(Grenade40Guns.gun_congolake,new ItemRenderCongoLake(),event);add(Grenade40Guns.gun_mk108,new ItemRenderMK108(),event);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        for(var stack:Grenade40Content.allStacks())((com.hbm.weapon.sedna.gun.ItemGunBaseNT)stack.getItem()).getConfig(stack,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        for(BulletConfig config:new BulletConfig[]{XFactory40mm.g40_he,XFactory40mm.g40_heat,XFactory40mm.g40_demo,XFactory40mm.g40_inc,XFactory40mm.g40_phosphorus})config.setRenderer(Grenade40Client::grenade);
    });}
    /** Actual source RENDER_GRENADE: scale .25, 90Z and projectiles/Grenade with original HFR UV/normals. */
    private static void grenade(EntityBulletBaseMK4 bullet,float partial){var frame=GunBulletRenderContext.current();var pose=frame.poses();pose.pushPose();pose.scale(.25F,.25F,.25F);pose.mulPose(Axis.ZP.rotationDegrees(90));var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/projectiles/projectiles.obj"));LegacyObjRenderer.renderOnly(mesh,pose,frame.buffers().getBuffer(RenderType.entityCutout(Grenade40Models.grenade_tex)),frame.light(),net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,-1,LegacyObjRenderer.Convention.HFR,"Grenade");pose.popPose();}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&Grenade40Content.allStacks().stream().anyMatch(stack->net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{Model(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public boolean isGui3d(){return false;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}}
}
