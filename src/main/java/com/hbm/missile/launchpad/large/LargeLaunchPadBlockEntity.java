package com.hbm.missile.launchpad.large;
import com.hbm.missile.launchpad.standard.StandardLaunchPadBlockEntity;import com.hbm.item.missile.MissileItem;import com.hbm.item.missile.MissileItem.MissileFormFactor;import java.util.*;import java.util.function.Consumer;import net.minecraft.core.*;import net.minecraft.nbt.CompoundTag;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.state.BlockState;
/** Original independent erector/lift state machine; exact source timing and heavy missile speed reduction. */
public final class LargeLaunchPadBlockEntity extends StandardLaunchPadBlockEntity{public int formFactor=-1;public boolean erected,readyToLoad,scheduleErect,liftMoving,erectorMoving;public float lift=1,erector=90,prevLift=1,prevErector=90,syncLift,syncErector;private int sync;private boolean applyingSync;public static Consumer<LargeLaunchPadBlockEntity> CLIENT_TICK=m->{},CLIENT_REMOVED=m->{};public LargeLaunchPadBlockEntity(BlockPos pos,BlockState state){super(LargeLaunchPadContent.ENTITY.get(),pos,state);delay=20;}
 @Override protected boolean isReadyForLaunch(){return erected&&readyToLoad;}@Override protected double getLaunchOffset(){return 2;}@Override protected void onLaunch(){erected=false;}
 public static List<BlockPos> portPositionsLarge(BlockPos p){return List.of(p.offset(4,0,-2),p.offset(4,0,2),p.offset(-4,0,-2),p.offset(-4,0,2),p.offset(-2,0,4),p.offset(2,0,4),p.offset(-2,0,-4),p.offset(2,0,-4));}@Override protected List<BlockPos> actualPortPositions(){return portPositionsLarge(worldPosition);}@Override protected Direction[] actualPortSides(BlockPos p){return new Direction[]{p.getX()-worldPosition.getX()==4?Direction.EAST:p.getX()-worldPosition.getX()==-4?Direction.WEST:p.getZ()>worldPosition.getZ()?Direction.SOUTH:Direction.NORTH};}
 public static void tick(Level level,BlockPos pos,BlockState state,LargeLaunchPadBlockEntity machine){if(level instanceof ServerLevel server){machine.serverMechanism(server);machine.serverBaseTick(server);}else{machine.prevLift=machine.lift;machine.prevErector=machine.erector;if(machine.sync>0){machine.lift+=(machine.syncLift-machine.lift)/machine.sync;machine.erector+=(machine.syncErector-machine.erector)/machine.sync;machine.sync--;}else{machine.lift=machine.syncLift;machine.erector=machine.syncErector;}CLIENT_TICK.accept(machine);}}
 private void serverMechanism(ServerLevel level){
			this.prevLift = this.lift;
			this.prevErector = this.erector;

			float erectorSpeed = 1.5F;
			float liftSpeed = 0.025F;

			if(this.isMissileValid()) {
				if(getItem(0).getItem() instanceof MissileItem) {
					MissileItem missile = (MissileItem) getItem(0).getItem();
					this.formFactor = missile.formFactor.ordinal();

					if(missile.formFactor == MissileFormFactor.ATLAS || missile.formFactor == MissileFormFactor.HUGE) {
						erectorSpeed /= 2F;
						liftSpeed /= 2F;
					}
				}

				if(this.erector == 90F && this.lift == 1F) {
					this.readyToLoad = true;
				}
			} else {
				readyToLoad = false;
				erected = false;
				delay = 20;
			}

			if(this.power >= 75_000) {
				if(delay > 0) {
					delay--;

					if(delay < 10 && scheduleErect) {
						this.erected = true;
						this.scheduleErect = false;
					}

					// if there is no missile or the missile isn't ready (i.e. the erector hasn't returned to zero position yet), retract
					if(getItem(0).isEmpty() || !readyToLoad) {
						//fold back erector
						if(erector < 90F) {
							erector = Math.min(erector + erectorSpeed, 90F);
							if(erector == 90F) delay = 20;
						//extend lift
						} else if(lift < 1F) {
							lift = Math.min(lift + liftSpeed, 1F);
							if(erector == 1F) {
								//if the lift is fully extended, the loading can begin
								readyToLoad = true;
								delay = 20;
							}
						}
					}

				} else {

					//only extend if the erector isn't up yet and the missile can be loaded
					if(!erected && readyToLoad) {
						this.state = this.LOADING;

						//first, rotate the erector
						if(erector != 0F) {
							erector = Math.max(erector - erectorSpeed, 0F);
							if(erector == 0F) delay = 20;
						//then retract the lift
						} else if(lift > 0) {
							lift = Math.max(lift - liftSpeed, 0F);
							if(lift == 0F) {
								//once the lift is at the bottom, the missile is deployed
								scheduleErect = true;
								delay = 20;
							}
						}
					} else {
						//first, fold back the erector
						if(erector < 90F) {
							erector = Math.min(erector + erectorSpeed, 90F);
							if(erector == 90F) delay = 20;
						//then extend the lift again
						} else if(lift < 1F) {
							lift = Math.min(lift + liftSpeed, 1F);
							if(erector == 1F) {
								//if the lift is fully extended, the loading can begin
								readyToLoad = true;
								delay = 20;
							}
						}
					}
				}
			}

			if(!this.hasFuel() || !this.isMissileValid()) this.state = this.MISSING;
			if(this.erected && this.canLaunch()) this.state = this.READY;

			boolean prevLiftMoving = this.liftMoving;
			boolean prevErectorMoving = this.erectorMoving;
			this.liftMoving = false;
			this.erectorMoving = false;
			if(this.prevLift != this.lift) this.liftMoving = true;
			if(this.prevErector != this.erector) this.erectorMoving = true;

			if(prevLiftMoving && !this.liftMoving) level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),com.hbm.registry.HbmSounds.get("door.wgh_stop"),net.minecraft.sounds.SoundSource.BLOCKS,2F,1F);
			if(prevErectorMoving && !this.erectorMoving) level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),com.hbm.registry.HbmSounds.get("door.garage_stop"),net.minecraft.sounds.SoundSource.BLOCKS,2F,1F);

 }
 @Override protected void saveMachine(CompoundTag tag){super.saveMachine(tag);tag.remove("delay");tag.putBoolean("erected",erected);tag.putBoolean("readyToLoad",readyToLoad);tag.putFloat("lift",lift);tag.putFloat("erector",erector);tag.putInt("formFactor",formFactor);}@Override protected void loadMachine(CompoundTag tag){super.loadMachine(tag);erected=tag.getBoolean("erected");readyToLoad=tag.getBoolean("readyToLoad");formFactor=tag.getInt("formFactor");if(applyingSync){liftMoving=tag.getBoolean("liftMoving");erectorMoving=tag.getBoolean("erectorMoving");syncLift=tag.getFloat("lift");syncErector=tag.getFloat("erector");if(lift!=syncLift||erector!=syncErector)sync=3;}else{delay=20;lift=tag.getFloat("lift");erector=tag.getFloat("erector");}}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("liftMoving",liftMoving);tag.putBoolean("erectorMoving",erectorMoving);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){applyingSync=true;try{super.handleUpdateTag(tag,lookup);}finally{applyingSync=false;}}@Override public void setRemoved(){if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}}
