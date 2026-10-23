package com.hbm.client.weapon.energy;

import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.energy.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
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

/** Source GunFactoryClient's actual Energy renderer/HUD callbacks, through the shared per-hand core. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class EnergyClient {
    private static void add(String name,LegacyGunRenderer renderer,RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
        @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}
    },EnergyContent.GUNS.get(name).get());}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        add("gun_tesla_cannon",new ItemRenderTeslaCannon(),event);
        add("gun_laser_pistol",new ItemRenderLaserPistol(EnergyModels.laser_pistol_tex),event);
        add("gun_laser_pistol_pew_pew",new ItemRenderLaserPistol(EnergyModels.laser_pistol_pew_pew_tex),event);
        add("gun_laser_pistol_morning_glory",new ItemRenderLaserPistol(EnergyModels.laser_pistol_morning_glory_tex),event);
        add("gun_lasrifle",new ItemRenderLasrifle(),event);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        for(var holder:EnergyContent.GUNS.values())holder.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO);
        for(var bullet:new com.hbm.weapon.sedna.BulletConfig[]{EnergyBullets.energy_tesla,EnergyBullets.energy_tesla_overcharge,EnergyBullets.energy_tesla_ir})bullet.setRendererBeam((beam,partial)->EnergyBeams.lightning(beam,partial,false));
        EnergyBullets.energy_tesla_ir_sub.setRendererBeam((beam,partial)->EnergyBeams.lightning(beam,partial,true));
        for(var bullet:new com.hbm.weapon.sedna.BulletConfig[]{EnergyBullets.energy_las,EnergyBullets.energy_las_overcharge,EnergyBullets.energy_las_ir})bullet.setRendererBeam((beam,partial)->EnergyBeams.laser(beam,partial,false));
        for(var bullet:new com.hbm.weapon.sedna.BulletConfig[]{EnergyBullets.energy_emerald,EnergyBullets.energy_emerald_overcharge,EnergyBullets.energy_emerald_ir})bullet.setRendererBeam((beam,partial)->EnergyBeams.laser(beam,partial,true));
    });}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&EnergyContent.GUNS.values().stream().anyMatch(holder->holder.getId().equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{
        Model(BakedModel model){super(model);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return false;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}
    }
    private EnergyClient(){}
}
