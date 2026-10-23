package com.hbm.bomb.nuclear.programmable;

import com.hbm.bomb.nuclear.*;
import com.hbm.registry.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityNukeBalefire's original timer, source sounds, inventory predicates and 250-block state distribution. */
public final class TimedBalefireBlockEntity extends ProgrammableBombInventory {
 public boolean loaded,started;public int timer=18000;private int lastTimer=Integer.MIN_VALUE;private boolean lastStarted,lastLoaded;
 public final ContainerData data=new ContainerData(){public int get(int i){return switch(i){case 0->timer;case 1->started?1:0;case 2->loaded?1:0;default->0;};}public void set(int i,int value){switch(i){case 0->timer=value;case 1->started=value!=0;case 2->loaded=value!=0;}}public int getCount(){return 3;}};
 public TimedBalefireBlockEntity(BlockPos pos,BlockState state){super(ProgrammableNuclearContent.TIMED_ENTITY.get(),pos,state,2,"container.nukeFstbmb",12);}
 public static boolean hasEgg(net.minecraft.world.Container slots){return slots.getItem(0).is(HbmContent.item("egg_balefire"));}
 public static int battery(net.minecraft.world.Container slots){var item=slots.getItem(1);return item.is(HbmContent.item("battery_spark"))?1:item.is(HbmContent.item("battery_trixite"))?2:0;}
 public boolean isLoaded(){return hasEgg(this)&&battery(this)>0;}
 public static String clock(int timer){String minutes=""+(timer/1200),seconds=""+((timer/20)%60);if(minutes.length()==1)minutes="0"+minutes;if(seconds.length()==1)seconds="0"+seconds;return minutes+":"+seconds;}
 public static void tick(Level level,BlockPos pos,BlockState state,TimedBalefireBlockEntity bomb){bomb.loaded=bomb.isLoaded();if(!bomb.loaded)bomb.started=false;if(bomb.started){bomb.timer--;bomb.setChanged();if(bomb.timer%20==0)level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("weapon.fstbmbPing"),SoundSource.BLOCKS,5,1);}if(bomb.timer<=0){bomb.explode();return;}if(bomb.timer!=bomb.lastTimer||bomb.started!=bomb.lastStarted||bomb.loaded!=bomb.lastLoaded||level.getGameTime()%20==0){TimedBalefireState.send(bomb);bomb.lastTimer=bomb.timer;bomb.lastStarted=bomb.started;bomb.lastLoaded=bomb.loaded;}}
 public boolean button(Player player,int value){if(!stillValid(player))return false;if(value==0&&isLoaded()){level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get("weapon.fstbmbStart"),SoundSource.BLOCKS,5,1);started=true;setChanged();return true;}if(value>=1&&value<=999){timer=value*20;setChanged();return true;}return false;}
 public void explode(){if(level==null||level.isClientSide||isRemoved())return;clearContent();level.removeBlock(worldPosition,false);var explosion=new NuclearBalefireExplosion(NuclearSpecialContent.BALEFIRE_EXPLOSION.get(),level);explosion.setPos(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5);explosion.destructionRange=250;level.addFreshEntity(explosion);NuclearTorex.statFacBale(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,250);}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new TimedBalefireMenu(id,inventory,this,data);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("started",started);tag.putInt("timer",timer);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);started=tag.getBoolean("started");timer=tag.getInt("timer");if(tag.contains("hbmLoaded"))loaded=tag.getBoolean("hbmLoaded");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){CompoundTag tag=saveWithoutMetadata(lookup);tag.putBoolean("hbmLoaded",loaded);return tag;}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
