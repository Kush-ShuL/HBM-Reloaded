package com.hbm.client.weapon.chemthrower;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.chemthrower.ItemGunChemthrower;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
/** Actual ORCHESTRA_CHEMTHROWER: client-side loop, 15-block linear range, 10tick keepalive; animation timer5 timeout. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ChemthrowerSound {
 private static final Map<LivingEntity,Loop> LOOPS=new IdentityHashMap<>();
 public static void orchestra(ItemStack stack,LambdaContext context){update(stack,context.entity,context.configIndex);}
 private static void update(ItemStack stack,LivingEntity owner,int index){var manager=Minecraft.getInstance().getSoundManager();var loop=LOOPS.get(owner);if(ItemGunBaseNT.getLastAnim(stack,index)==GunAnimation.CYCLE&&ItemGunBaseNT.getAnimTimer(stack,index)<5){if(loop==null||!manager.isActive(loop)){if(loop!=null)loop.finish();loop=new Loop(owner);LOOPS.put(owner,loop);manager.play(loop);}loop.keepAlive();}else if(loop!=null){loop.finish();manager.stop(loop);LOOPS.remove(owner);}}
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){var mc=Minecraft.getInstance();if(mc.level==null){LOOPS.values().forEach(Loop::finish);LOOPS.clear();return;}for(var player:mc.level.players())if(player!=mc.player&&(player.getMainHandItem().getItem()instanceof ItemGunChemthrower||com.hbm.client.weapon.flamer.FlamerClient.isLoopGun(player.getMainHandItem())))update(player.getMainHandItem(),player,0);LOOPS.entrySet().removeIf(entry->entry.getValue().isStopped());}
 private static final class Loop extends AbstractTickableSoundInstance {private final LivingEntity owner;private int sinceKeepAlive;Loop(LivingEntity owner){super(HbmSounds.get("weapon.fire.flameLoop"),SoundSource.PLAYERS,RandomSource.create());this.owner=owner;looping=true;attenuation=Attenuation.NONE;pitch=1;keepAlive();}void keepAlive(){sinceKeepAlive=0;x=owner.getX();y=owner.getY();z=owner.getZ();updateVolume();}private void updateVolume(){var player=Minecraft.getInstance().player;float distance=player==null?0:(float)Math.sqrt(player.distanceToSqr(x,y,z));volume=distance/15F*-1F+1F;}@Override public void tick(){if(owner.isRemoved()||!owner.isAlive()||sinceKeepAlive>10)finish();sinceKeepAlive++;x=owner.getX();y=owner.getY();z=owner.getZ();updateVolume();}void finish(){stop();}}
 private ChemthrowerSound(){}
}
