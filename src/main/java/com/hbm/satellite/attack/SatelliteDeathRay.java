package com.hbm.satellite.attack;
import com.hbm.satellite.SatelliteBase;
import com.hbm.satellite.link.SatelliteLinkBlockEntity;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
/** Actual source orbital death-ray class3, with its real delayed MK5 visual/attack entity. */
public final class SatelliteDeathRay extends SatelliteBase {
 public static final String CMD_FIRE="fire",CMD_CANFIRE="canfire";
 public static final int CHARGE_TIME=5*60*20;
 public long lastShot;
 @Override public String getType(){return "ORBITAL_FUN_PLATFORM_:)";}
 @Override public Component[] getInfo(ServerLevel world){boolean ready=lastShot+CHARGE_TIME<world.getGameTime();int cooldown=(int)(lastShot+CHARGE_TIME-world.getGameTime());return new Component[]{Component.translatable("item.satellite.death_ray.name"),ready?Component.translatable("satellite.ready"):Component.translatable("satellite.cooldown",cooldown/20+"s")};}
 @Override public void writeToNBT(CompoundTag tag,HolderLookup.Provider lookup){super.writeToNBT(tag,lookup);tag.putLong("lastShot",lastShot);}
 @Override public void readFromNBT(CompoundTag tag,HolderLookup.Provider lookup){super.readFromNBT(tag,lookup);lastShot=tag.getLong("lastShot");}
 @Override public void onCommandImpl(ServerLevel world,String...cmd){if(cmd.length==0)return;if(cmd[0].equals(CMD_FIRE)){deathBlast(world,targetX,targetZ);return;}if(cmd[0].equals(CMD_CANFIRE)){tx=Boolean.toString(lastShot+CHARGE_TIME<world.getGameTime()).toUpperCase(Locale.US);return;}}
 @Override public void onCoordAction(ServerLevel world,Player player,int x,int y,int z){setTarget(x,z);deathBlast(world,targetX,targetZ);}
 public void deathBlast(ServerLevel world,int x,int z){if(lastShot+CHARGE_TIME<world.getGameTime()){lastShot=world.getGameTime();markDirty();int y=SatelliteLinkBlockEntity.sourceHeight(world,x,z);var blast=new DeathBlastEntity(AttackSatelliteContent.DEATH_BLAST.get(),world);blast.setPos(x,y,z);world.addFreshEntity(blast);}}
}
