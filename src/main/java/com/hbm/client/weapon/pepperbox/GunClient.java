package com.hbm.client.weapon.pepperbox;

import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.weapon.pepperbox.mixin.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.factory.PepperboxFactory;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
public final class GunClient implements GunClientBridge {
    private enum GunKeys implements net.neoforged.neoforge.client.settings.IKeyConflictContext {
        SINGLE,DUAL,HELD;
        @Override public boolean isActive(){var client=Minecraft.getInstance();var player=client.player;if(player==null||client.screen!=null)return false;boolean held=player.getMainHandItem().getItem() instanceof ItemGunBaseNT||GunHands.operable(player,InteractionHand.OFF_HAND);boolean dual=GunHands.dual(player);return held&&(this==HELD||this==DUAL==dual);}
        @Override public boolean conflicts(net.neoforged.neoforge.client.settings.IKeyConflictContext other){return other==this||other==HELD||this==HELD&&other instanceof GunKeys||other==net.neoforged.neoforge.client.settings.KeyConflictContext.IN_GAME;}
    }
    public static final KeyMapping PRIMARY=new KeyMapping("key.hbm.sedna_primary",GunKeys.SINGLE,InputConstants.Type.MOUSE,0,"key.categories.hbm"),SECONDARY=new KeyMapping("key.hbm.sedna_secondary",GunKeys.SINGLE,InputConstants.Type.MOUSE,1,"key.categories.hbm"),TERTIARY=new KeyMapping("key.hbm.sedna_tertiary",GunKeys.SINGLE,InputConstants.Type.MOUSE,2,"key.categories.hbm");
    public static final KeyMapping DUAL_MAIN=new KeyMapping("key.hbm.sedna_dual_main",GunKeys.DUAL,InputConstants.Type.MOUSE,1,"key.categories.hbm"),DUAL_OFF=new KeyMapping("key.hbm.sedna_dual_off",GunKeys.DUAL,InputConstants.Type.MOUSE,0,"key.categories.hbm");
    public static final KeyMapping RELOAD=new KeyMapping("key.hbm.sedna_reload",GunKeys.HELD,InputConstants.Type.KEYSYM,org.lwjgl.glfw.GLFW.GLFW_KEY_R,"key.categories.hbm");
    public static List<KeyMapping> bindings(){return List.of(PRIMARY,SECONDARY,TERTIARY,RELOAD,DUAL_MAIN,DUAL_OFF);}
    private static final Map<LivingEntity,Long> MAIN_FLASHES=new WeakHashMap<>(),OFF_FLASHES=new WeakHashMap<>();
    /** Source renderers keep their owner-map API; a bound actual stack selects the appropriate hand. */
    public static final Map<LivingEntity,Long> FLASHES=new AbstractMap<>(){
        @Override public Long get(Object entity){if(!(entity instanceof LivingEntity living))return null;return (GunHands.hand(living,GunVisualState.currentStack())==InteractionHand.OFF_HAND?OFF_FLASHES:MAIN_FLASHES).get(living);}
        @Override public Set<Entry<LivingEntity,Long>> entrySet(){return MAIN_FLASHES.entrySet();}
    };
    private static PepperboxItemRenderer renderer;
    public static PepperboxItemRenderer renderer(){if(renderer==null)renderer=new PepperboxItemRenderer();return renderer;}
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){for(var mapping:bindings())event.register(mapping);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{
        ItemGunBaseNT.CLIENT=new GunClient();
        for(var config:List.of(PepperboxFactory.stone,PepperboxFactory.flint,PepperboxFactory.iron,PepperboxFactory.shot))config.setRenderer(GunBulletRenderer.STANDARD);
        PepperboxContent.PEPPERBOX.get().getConfig(null,0).hud(GunHudComponents.DURABILITY,GunHudComponents.AMMO);
    });}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){
        @Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer();}
        @Override public HumanoidModel.ArmPose getArmPose(LivingEntity entity,InteractionHand hand,ItemStack stack){return GunHandPoses.pose(entity,hand,stack);}
    },PepperboxContent.PEPPERBOX.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().toString().equals("hbm:gun_pepperbox")?new GunModel(model):model);}
    private static final class GunModel extends BakedModelWrapper<BakedModel>{
        GunModel(BakedModel model){super(model);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return false;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack pose,boolean left){return this;}
    }
    @Override public Player localPlayer(){return Minecraft.getInstance().player;}
    @Override public boolean isWeaponTable(){return Minecraft.getInstance().screen instanceof GunWeaponTableScreen;}
    @Override public void animation(ItemStack stack,int slot,InteractionHand hand,GunAnimation type,int receiverIndex,int configIndex){
        var client=Minecraft.getInstance();var player=client.player;if(player==null)return;
        try(var visual=GunVisualState.bind(stack)){
        var gun=(ItemGunBaseNT)stack.getItem();var config=gun.getConfig(stack,configIndex);
        if(type==GunAnimation.CYCLE){
            gun.lastShot[configIndex]=System.currentTimeMillis();gun.shotRand=player.getRandom().nextDouble();
            var receivers=config.getReceivers(stack);if(receiverIndex>=0&&receiverIndex<receivers.length){var recoil=receivers[receiverIndex].getRecoil(stack);if(recoil!=null)recoil.accept(stack,new ItemGunBaseNT.LambdaContext(config,player,new GunInventory(player),configIndex));}
        }
        var animations=config.getAnims(stack);if(animations==null)return;var animation=animations.apply(stack,type);
        if(animation==null&&(type==GunAnimation.ALT_CYCLE||type==GunAnimation.CYCLE_EMPTY))animation=animations.apply(stack,GunAnimation.CYCLE);
        if(animation==null)return;
        if(hand==InteractionHand.MAIN_HAND){var hands=(GunHandAccess)client.gameRenderer.itemInHandRenderer;hands.hbm$cachedGun(stack);hands.hbm$gunHeight(0);hands.hbm$oldGunHeight(0);}
        boolean isReloadAnimation=type==GunAnimation.RELOAD||type==GunAnimation.RELOAD_CYCLE;
        if(isReloadAnimation&&GunArmorPerks.isTrenchMaster(player))animation.setTimeMult(.5);
        var rail=new HbmAnimations.Animation(stack.getItem().getDescriptionId(),System.currentTimeMillis(),animation,isReloadAnimation&&config.getReloadAnimSequential(stack));rail.instance=GunHands.identity(stack);HbmAnimations.put(hand,slot,configIndex,rail);
        }
    }
    @Override public void muzzleFlash(int entityId,InteractionHand hand){var level=Minecraft.getInstance().level;if(level!=null&&level.getEntity(entityId) instanceof LivingEntity entity)(hand==InteractionHand.OFF_HAND?OFF_FLASHES:MAIN_FLASHES).put(entity,System.currentTimeMillis());}
    @Override public void blackPowder(GunNetwork.BlackPowder packet){BlackPowderParticles.receive(packet);}
    private GunClient(){}
}
