package com.hbm.client.weapon.flamer;

import com.hbm.bomb.conventional.grenade.FlameEffects;
import com.hbm.client.animation.bus.AnimationLoader;
import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.client.weapon.chemthrower.ChemthrowerSound;
import com.hbm.client.weapon.legendary.GunVisuals;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.weapon.sedna.gun.flamer.*;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Source GunFactoryClient Flamer models, true animation JSON, original no-counter HUD and FlameCreator. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FlamerClient {
    private static volatile Map<String,BusAnimation> animations=Map.of();
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){
        event.registerReloadListener((ResourceManagerReloadListener) resources->{
            try { animations=AnimationLoader.load(resources,ResourceLocation.parse("hbm:models/weapons/animations/flamethrower.json")); }
            catch(IOException error){throw new IllegalStateException("Cannot load original Flamer animation",error);}
        });
    }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        for(var name:new String[]{"gun_flamer","gun_flamer_topaz","gun_flamer_daybreaker"}){
            var texture=name.equals("gun_flamer")?"flamethrower":name.replace("gun_flamer","flamethrower");
            var renderer=new ItemRenderFlamer(ResourceLocation.parse("hbm:textures/models/weapons/"+texture+".png"));
            var item=FlamerContent.GUNS.get(name).get();GunVisuals.register(item,renderer);
            event.registerItem(new IClientItemExtensions(){
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer;}
                @Override public HumanoidModel.ArmPose getArmPose(LivingEntity living,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(living,hand,stack);}
            },item);
        }
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        for(var holder:FlamerContent.GUNS.values())holder.get().getConfig(null,0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY,SourceGunHudComponents.HUD_COMPONENT_AMMO_NOCOUNTER);
        FlamerAnimations.RELOAD=()->animations.get("Reload");
        FlamerContent.CLIENT_ORCHESTRA=ChemthrowerSound::orchestra;
        FlamerBullets.CLIENT_FIRE=bullet->flame(bullet,0);
        FlamerBullets.CLIENT_BALEFIRE=bullet->flame(bullet,1);
    });}
    private static void flame(Entity bullet,int palette){var mc=Minecraft.getInstance();if(bullet.level().isClientSide&&mc.player!=null&&mc.player.distanceTo(bullet)<100)FlameEffects.client(bullet.level(),bullet.getX(),bullet.getY()-.125,bullet.getZ(),palette);}
    public static boolean isLoopGun(ItemStack stack){return stack.is(FlamerContent.GUNS.get("gun_flamer").get())||stack.is(FlamerContent.GUNS.get("gun_flamer_topaz").get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&FlamerContent.GUNS.values().stream().anyMatch(holder->holder.getId().equals(id.id()))?new Model(model):model);}
    private static final class Model extends BakedModelWrapper<BakedModel>{
        Model(BakedModel original){super(original);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return false;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){return this;}
    }
    private FlamerClient(){}
}
