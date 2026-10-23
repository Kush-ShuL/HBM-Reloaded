package com.hbm.satellite.attack;
import com.hbm.satellite.SatelliteBase;
import com.hbm.satellite.link.SatelliteLinkBlockEntity;
import api.hbm.redstoneoverradio.IRORInteractive;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
/** Source class9; one-shot entity targeting retains fallback, random-offset and cooldown ordering. */
public final class SatellitePrecisionLaser extends SatelliteBase {
 public static final String CMD_FIRE="fire",CMD_CANFIRE="canfire",CMD_SETENTITYTARGET="setentitytarget";
 public static final int MAX_TARGET_RANGE=1000,CHARGE_TIME=5*20;
 public long lastShot;public int targetedEntity=-1;
 @Override public String getType(){return "ORBITAL_TATOO_REMOVER";}
 @Override public Component[] getInfo(ServerLevel world){boolean ready=lastShot+CHARGE_TIME<world.getGameTime();int cooldown=(int)(lastShot+CHARGE_TIME-world.getGameTime());return new Component[]{Component.translatable("item.satellite.precision_laser.name"),ready?Component.translatable("satellite.ready"):Component.translatable("satellite.cooldown",cooldown/20+"s")};}
 @Override public void writeToNBT(CompoundTag tag,HolderLookup.Provider lookup){super.writeToNBT(tag,lookup);tag.putLong("lastShot",lastShot);tag.putInt("targetedEntity",targetedEntity);}
 @Override public void readFromNBT(CompoundTag tag,HolderLookup.Provider lookup){super.readFromNBT(tag,lookup);lastShot=tag.getLong("lastShot");targetedEntity=tag.getInt("targetedEntity");}
 @Override public void onCommandImpl(ServerLevel world,String...cmd){
  if(cmd.length==0)return;
  if(cmd[0].equals(CMD_FIRE)){
   if(targetedEntity!=-1){var entity=world.getEntity(targetedEntity);targetedEntity=-1;if(entity==null||entity.isRemoved())return;
    int x=(int)Math.floor(entity.getX()),z=(int)Math.floor(entity.getZ());double dx=x-targetX,dz=z-targetZ;
    if(dx*dx+dz*dz<=MAX_TARGET_RANGE*MAX_TARGET_RANGE){double offX=world.random.nextDouble()*.05-.025,offY=world.random.nextDouble()*.05-.025,offZ=world.random.nextDouble()*.05-.025;deathBlast(world,entity.getX()+offX,entity.getY()+offY,entity.getZ()+offZ);return;}
   }
   deathBlast(world,targetX,targetZ);return;
  }
  if(cmd[0].equals(CMD_CANFIRE)){tx=Boolean.toString(lastShot+CHARGE_TIME<world.getGameTime()).toUpperCase(Locale.US);return;}
  if(cmd[0].equals(CMD_SETENTITYTARGET)&&cmd.length==2){targetedEntity=IRORInteractive.parseInt(cmd[1]);return;}
 }
 @Override public void onCoordAction(ServerLevel world,Player player,int x,int y,int z){setTarget(x,z);deathBlast(world,targetX,targetZ);}
 public void deathBlast(ServerLevel world,int x,int z){int y=SatelliteLinkBlockEntity.sourceHeight(world,x,z);deathBlast(world,x+.5,y,z+.5);}
 public void deathBlast(ServerLevel world,double x,double y,double z){if(lastShot+CHARGE_TIME<world.getGameTime()){lastShot=world.getGameTime();markDirty();var blast=new OrbitalLaserEntity(AttackSatelliteContent.ORBITAL_LASER.get(),world);blast.setPos(x,y,z);blast.explode();world.addFreshEntity(blast);}}
}
