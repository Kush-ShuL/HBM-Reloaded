package com.hbm.weapon.sedna.gun;

import java.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Actual held-stack identities; all magazine/state/timer data stays on that real stack. */
@EventBusSubscriber(modid="hbm")
public final class GunHands {
    public static final String INSTANCE="hbm_gun_instance";
    private static final Map<Player,Held> HELD=new WeakHashMap<>();
    private static final ThreadLocal<ItemStack> ACTIVE=new ThreadLocal<>();
    private record Held(ItemStack main,ItemStack off,boolean dual){}
    public static boolean forbiddenOffhand(ItemStack stack){return stack.getItem() instanceof com.hbm.weapon.MainHandWeapon||stack.getItem() instanceof ItemGunBaseNT gun&&!gun.supportsPistolDualWield();}
    public static boolean dual(Player player){return player.getMainHandItem().getItem() instanceof ItemGunBaseNT main&&main.supportsPistolDualWield()&&player.getOffhandItem().getItem() instanceof ItemGunBaseNT off&&off.supportsPistolDualWield();}
    public static ItemStack viewStack(Player player){var main=player.getMainHandItem();return main.getItem() instanceof ItemGunBaseNT?main:operable(player,InteractionHand.OFF_HAND)?player.getOffhandItem():ItemStack.EMPTY;}
    public static boolean operable(Player player,InteractionHand hand){var stack=player.getItemInHand(hand);return stack.getItem() instanceof ItemGunBaseNT&&(hand==InteractionHand.MAIN_HAND||!forbiddenOffhand(stack)&&!(player.getMainHandItem().getItem() instanceof com.hbm.weapon.MainHandWeapon)&&!(player.getMainHandItem().getItem() instanceof ItemGunBaseNT main&&!main.supportsPistolDualWield()));}
    public static InteractionHand hand(LivingEntity owner,ItemStack stack){var off=owner.getOffhandItem();if(off==stack)return InteractionHand.OFF_HAND;var token=stack==null?null:identity(stack);return token!=null&&token.equals(identity(off))&&!token.equals(identity(owner.getMainHandItem()))?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND;}
    public static ItemStack activeStack(){return ACTIVE.get();}
    public static Scope active(ItemStack stack){return new Scope(stack);}
    public static final class Scope implements AutoCloseable{private final ItemStack previous;private Scope(ItemStack stack){previous=ACTIVE.get();ACTIVE.set(stack);}@Override public void close(){if(previous==null)ACTIVE.remove();else ACTIVE.set(previous);}}
    public static UUID identity(ItemStack stack){var data=GunStackData.data(stack);return data.hasUUID(INSTANCE)?data.getUUID(INSTANCE):null;}
    public static UUID ensureIdentity(ItemStack stack){var id=identity(stack);if(id==null){id=UUID.randomUUID();UUID assigned=id;GunStackData.update(stack,data->data.putUUID(INSTANCE,assigned));}return id;}
    public static void clearControls(ItemStack stack){if(!(stack.getItem() instanceof ItemGunBaseNT gun))return;var data=GunStackData.data(stack);boolean changed=data.getBoolean(ItemGunBaseNT.KEY_AIMING);data.putBoolean(ItemGunBaseNT.KEY_AIMING,false);for(int i=0;i<gun.getConfigCount();i++)for(var key:List.of(ItemGunBaseNT.KEY_PRIMARY,ItemGunBaseNT.KEY_SECONDARY,ItemGunBaseNT.KEY_TERTIARY,ItemGunBaseNT.KEY_RELOAD)){changed|=data.getBoolean(key+i);data.putBoolean(key+i,false);}if(changed)stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(data));}
    public static void prepare(Player player){
        if(player.level().isClientSide)return;
        var off=player.getOffhandItem();if(forbiddenOffhand(off)){player.setItemSlot(EquipmentSlot.OFFHAND,ItemStack.EMPTY);clearControls(off);if(!player.getInventory().add(off))player.drop(off,false);player.inventoryMenu.broadcastChanges();}
        var main=player.getMainHandItem();off=player.getOffhandItem();boolean dual=dual(player);var previous=HELD.get(player);
        if(previous==null||previous.main!=main||previous.off!=off||previous.dual!=dual){if(previous!=null){clearControls(previous.main);clearControls(previous.off);}clearControls(main);clearControls(off);HELD.put(player,new Held(main,off,dual));}
        if(!player.isAlive()||player.isSpectator()||!GunSettings.enabled()){clearControls(main);clearControls(off);}
        boolean fresh=false;if(main.getItem() instanceof ItemGunBaseNT&&off.getItem() instanceof ItemGunBaseNT&&identity(main)!=null&&identity(main).equals(identity(off))){GunStackData.update(off,data->data.putUUID(INSTANCE,UUID.randomUUID()));fresh=true;}for(var stack:List.of(main,off))if(stack.getItem() instanceof ItemGunBaseNT&&identity(stack)==null){ensureIdentity(stack);fresh=true;}if(fresh)player.inventoryMenu.broadcastChanges();
        if(dual){if(ItemGunBaseNT.getIsAiming(main))ItemGunBaseNT.setIsAiming(main,false);if(ItemGunBaseNT.getIsAiming(off))ItemGunBaseNT.setIsAiming(off,false);}
    }
    @SubscribeEvent public static void tick(PlayerTickEvent.Pre event){prepare(event.getEntity());}
    @SubscribeEvent public static void swap(LivingSwapItemsEvent.Hands event){if(forbiddenOffhand(event.getItemSwappedToOffHand())){event.setCanceled(true);if(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)player.inventoryMenu.sendAllDataToRemote();}}
    @SubscribeEvent public static void death(LivingDeathEvent event){if(event.getEntity() instanceof Player player){for(var stack:player.getInventory().items)clearControls(stack);clearControls(player.getOffhandItem());HELD.remove(player);}}
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event){var player=event.getEntity();var state=HELD.remove(player);if(state!=null){clearControls(state.main);clearControls(state.off);}}
    private GunHands(){}
}
