package com.hbm.world.meteor;

import com.hbm.armor.ArmorModStorage;
import com.hbm.machine.pedestal.PedestalEntries;
import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.minecraft.server.level.ServerLevel;

/** Original global shower counter and per-world START tick random sequence. */
public final class MeteorScheduler {
 private static final Random RANDOM=new Random();
 private static final EquipmentSlot[] ARMOR={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
 private static int shower;
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event)->shower=0);}
 public static void update(ServerLevel level){
  if(!MeteorConfig.enabled())return;
  int chance=shower>0?MeteorConfig.SHOWER_CHANCE.get():MeteorConfig.STRIKE_CHANCE.get();
  if(chance>0&&RANDOM.nextInt(chance)==0&&!level.players().isEmpty()){
   var player=level.players().get(RANDOM.nextInt(level.players().size()));
   if(player.level().dimension()==Level.OVERWORLD){
    boolean repel=false,strike=true;
    for(var slot:ARMOR){var armor=player.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor))continue;var mods=ArmorModStorage.pryMods(armor,player.registryAccess());
     for(int i=0;i<8;i++){if(mods[i].is(MeteorContent.PROTECTION.get()))repel=true;if(mods[i].is(MeteorContent.CHARM.get()))strike=false;}
    }
    if(!repel||strike)for(var entry:PedestalEntries.entries(level.dimension()))if(Math.abs(entry.pos().getX()-player.getBlockX())<=100&&Math.abs(entry.pos().getZ()-player.getBlockZ())<=100){
     if(entry.type()==PedestalEntries.Kind.CHARM_OF_PROTECTION)repel=true;if(entry.type()==PedestalEntries.Kind.METEORITE_CHARM)strike=false;
    }
    if(strike)spawnAt(player,repel);
   }
  }
  if(shower>0)shower--;
  int strikeChance=MeteorConfig.STRIKE_CHANCE.get();
  if(strikeChance>0&&RANDOM.nextInt(strikeChance*100)==0&&MeteorConfig.SHOWERS.get())shower=(int)(MeteorConfig.SHOWER_DURATION.get()*.75+MeteorConfig.SHOWER_DURATION.get()*.25*RANDOM.nextFloat());
 }
 public static void spawnAt(Player player,boolean repel){
  var meteor=new MeteorEntity(player.level());int radius=MeteorConfig.SPAWN_RADIUS.get();
  meteor.moveTo(player.getX()+RANDOM.nextInt(radius*2+1)-radius,player.level().getMaxBuildHeight()+MeteorConfig.SPAWN_HEIGHT_OFFSET.get(),player.getZ()+RANDOM.nextInt(radius*2+1)-radius,0,0);
  Vec3 velocity;
  if(repel){velocity=new Vec3(meteor.getX()-player.getX(),0,meteor.getZ()-player.getZ()).normalize().scale(RANDOM.nextDouble());meteor.safe=true;}
  else velocity=new Vec3(RANDOM.nextDouble()-.5,0,0).yRot((float)(Math.PI*RANDOM.nextDouble()));
  meteor.setDeltaMovement(velocity.x,-2.5,velocity.z);player.level().addFreshEntity(meteor);
 }
 private MeteorScheduler(){}
}
