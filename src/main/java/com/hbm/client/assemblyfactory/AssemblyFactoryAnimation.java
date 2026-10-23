package com.hbm.client.assemblyfactory;
import com.hbm.machine.assemblyfactory.AssemblyFactoryEntity;
import com.hbm.registry.HbmSounds;
import java.lang.ref.WeakReference;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
/** Exact source TragicYuri/sliding-carriage/striker/saw state machines; no server animation persistence. */
public final class AssemblyFactoryAnimation {
 private static final Map<AssemblyFactoryEntity,AssemblyFactoryAnimation> STATES=new WeakHashMap<>();
 private final WeakReference<AssemblyFactoryEntity> owner;private final boolean[] didProcess;private boolean muffled;private Loop sound;
 public final TragicYuri[] animations=new TragicYuri[2];public boolean frame;
 private AssemblyFactoryAnimation(AssemblyFactoryEntity machine){owner=new WeakReference<>(machine);didProcess=machine.didProcess;for(int i=0;i<animations.length;i++)animations[i]=new TragicYuri(i);}
 private AssemblyFactoryEntity machine(){return owner.get();}
 public static AssemblyFactoryAnimation get(AssemblyFactoryEntity machine){return STATES.computeIfAbsent(machine,AssemblyFactoryAnimation::new);}
 public static void tick(AssemblyFactoryEntity machine){get(machine).update(machine);}
 public static void removed(AssemblyFactoryEntity machine){var animation=STATES.remove(machine);if(animation!=null)animation.stop();}
 private static double interp(double from,double to,float partial){return from+(to-from)*partial;}
 private float getVolume(float volume){return muffled?volume*.1F:volume;}
 private void oneShot(String id,float volume,float pitch){var machine=machine();if(machine==null||machine.getLevel()==null)return;var p=machine.getBlockPos();machine.getLevel().playLocalSound(p.getX(),p.getY(),p.getZ(),HbmSounds.get(id),SoundSource.BLOCKS,volume,pitch,false);}
 private void stop(){if(sound!=null){sound.finish();Minecraft.getInstance().getSoundManager().stop(sound);sound=null;}}
 private void update(AssemblyFactoryEntity machine){var level=machine.getLevel();if(level==null)return;var client=Minecraft.getInstance();var pos=machine.getBlockPos();muffled=machine.isMuffled();
  if(machine.processing()&&client.player!=null&&client.player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500){if(sound==null||!client.getSoundManager().isActive(sound)){sound=new Loop(machine);client.getSoundManager().play(sound);}sound.keepAlive=0;sound.update();}else stop();
  for(var animation:animations)animation.update(machine.processing());if(level.getGameTime()%20==0)frame=!level.getBlockState(pos.above(3)).isAir();
 }
 private static final class Loop extends AbstractTickableSoundInstance {
  final AssemblyFactoryEntity machine;int keepAlive;
  Loop(AssemblyFactoryEntity machine){super(HbmSounds.get("block.motor"),SoundSource.BLOCKS,RandomSource.create());this.machine=machine;looping=true;attenuation=Attenuation.NONE;var p=machine.getBlockPos();x=p.getX();y=p.getY();z=p.getZ();update();}
  void update(){pitch=.75F;var player=Minecraft.getInstance().player;volume=(machine.isMuffled()?.05F:.5F)*(player==null?1:(float)(1-Math.sqrt(player.distanceToSqr(x,y,z))/15));}
  @Override public void tick(){if(machine.isRemoved()||++keepAlive>20)finish();}void finish(){stop();}
 }
	public class TragicYuri {
		
		public AssemblerArm striker;
		public AssemblerArm saw;

		Random rand = new Random();
		YuriState state = YuriState.WORKING;
		double slider = 0;
		double prevSlider = 0;
		boolean direction = false;
		int timeUntilReposition;
		
		public TragicYuri(int group) {
			striker = new AssemblerArm(	group == 0 ? 0 : 3);
			saw = new AssemblerArm(		group == 0 ? 1 : 2).yepThatsASaw();
			timeUntilReposition = 140 + rand.nextInt(161);
		}
		
		public void update(boolean working) {
			this.prevSlider = this.slider;
			
			// one of the arms must do something. doesn't matter which or what position the carriage is in
			if(didProcess[striker.recipeIndex] || didProcess[saw.recipeIndex]) switch(state) {
			case WORKING: {
				timeUntilReposition--;
				if(timeUntilReposition <= 0) {
					state = YuriState.RETIRING;
				}
			} break;
			case RETIRING: {
				if(striker.state == ArmState.WAIT && saw.state == ArmState.WAIT) { // only progress as soon as both arms are done moving
					state = YuriState.SLIDING;
					direction = !direction;
					if(!muffled) oneShot("block.assemblerStart", getVolume(0.25F), 1.25F + machine().getLevel().random.nextFloat() * 0.25F);
				}
			} break;
			case SLIDING: {
				double sliderSpeed = 1D / 10D; // 10 ticks for transit
				if(direction) {
					slider += sliderSpeed;
					if(slider >= 1) {
						slider = 1;
						state = YuriState.WORKING;
					}
				} else {
					slider -= sliderSpeed;
					if(slider <= 0) {
						slider = 0;
						state = YuriState.WORKING;
					}
				}
				if(state == YuriState.WORKING) timeUntilReposition = 140 + rand.nextInt(161); // 7 to 15 seconds
			} break;
			}
			
			striker.updateArm();
			saw.updateArm();
		}
		
		public double getSlider(float interp) {
			return this.prevSlider + (this.slider - this.prevSlider) * interp;
		}
		
		// there's a ton of way to make this more optimized/readable/professional/scrungular but i don't care i am happy this crap works at all
		public class AssemblerArm { // more fucking nesting!!!11
			
			public double[] angles = new double[4];
			public double[] prevAngles = new double[4];
			public double[] targetAngles = new double[4];
			public double[] speed = new double[4];
			public double sawAngle;
			public double prevSawAngle;
			public int recipeIndex; // the index of which pedestal is serviced, assuming the carriage is at default position

			ArmState state = ArmState.REPOSITION;
			int actionDelay = 0;
			boolean saw = false;
			
			public AssemblerArm(int index) {
				this.recipeIndex = index;
				this.resetSpeed();
				this.chooseNewArmPoistion();
			}
			
			public AssemblerArm yepThatsASaw() { this.saw = true; this.chooseNewArmPoistion(); return this; }
			
			private void resetSpeed() {
				speed[0] = 15;	//Pivot
				speed[1] = 15;	//Arm
				speed[2] = 15;	//Piston
				speed[3] = saw ? 0.125 : 0.5;	//Striker
			}

			public void updateArm() {
				resetSpeed();
				
				for(int i = 0; i < angles.length; i++) {
					prevAngles[i] = angles[i];
				}
				
				prevSawAngle = sawAngle;
				
				int serviceIndex = recipeIndex;
				if(slider > 0.5) serviceIndex += (serviceIndex % 2 == 0 ? 1 : -1); // if the carriage has moved, swap the indices so they match up with the serviced pedestal
				if(!didProcess[serviceIndex]) state = ArmState.RETIRE;
				
				if(state == ArmState.CUT || state == ArmState.EXTEND) {
					this.sawAngle += 45D;
				}

				if(actionDelay > 0) {
					actionDelay--;
					return;
				}

				switch(state) {
				// Move. If done moving, set a delay and progress to EXTEND
				case REPOSITION: {
					if(move()) {
						actionDelay = 2;
						state = ArmState.EXTEND;
						targetAngles[3] = saw ? -0.375D : -0.75D;
					}
				} break;
				case EXTEND:
					if(move()) {
						
						if(saw) {
							state = ArmState.CUT;
							targetAngles[2] = -targetAngles[2];
							if(!muffled) oneShot("block.assemblerCut", getVolume(0.5F), 1F + rand.nextFloat() * 0.25F);
						} else {
							state = ArmState.RETRACT;
							targetAngles[3] = 0D;
							if(!muffled) oneShot("block.assemblerStrike", getVolume(0.5F), 1F);
						}
					}
					break;
				case CUT: {
					speed[2] = Math.abs(targetAngles[2] / 20D);
					if(move()) {
						state = ArmState.RETRACT;
						targetAngles[3] = 0D;
					}
				} break;
				case RETRACT:
					if(move()) {
						actionDelay = 2 + rand.nextInt(5);
						chooseNewArmPoistion();
						state = TragicYuri.this.state == YuriState.RETIRING ? ArmState.RETIRE : ArmState.REPOSITION;
					}
					break;
				case RETIRE: {
					this.targetAngles[0] = 0;
					this.targetAngles[1] = 0;
					this.targetAngles[2] = 0;
					this.targetAngles[3] = 0;
					
					if(move()) {
						actionDelay = 2 + rand.nextInt(5);
						chooseNewArmPoistion();
						state = ArmState.WAIT;
					}
				} break;
				case WAIT: {
					if(TragicYuri.this.state == YuriState.WORKING) this.state = ArmState.REPOSITION;
				} break;
				}
			}
			
			public void chooseNewArmPoistion() {
				
				double[][] pos = !saw ? new double[][] {
					// striker
					{10, 10, -10},
					{15, 15, -15},
					{25, 10, -15},
					{30, 0, -10},
					{-10, 10, 0},
					{-20, 30, -15}
				} : new double[][] {
					// saw
					{-15, 15, -10},
					{-15, 15, -15},
					{-15, 15, 10},
					{-15, 15, 15},
					{-15, 15, 2},
					{-15, 15, -2}
				};
				
				int chosen = rand.nextInt(pos.length);
				this.targetAngles[0] = pos[chosen][0];
				this.targetAngles[1] = pos[chosen][1];
				this.targetAngles[2] = pos[chosen][2];
			}
			
			private boolean move() {
				boolean didMove = false;

				for(int i = 0; i < angles.length; i++) {
					if(angles[i] == targetAngles[i])
						continue;

					didMove = true;

					double angle = angles[i];
					double target = targetAngles[i];
					double turn = speed[i];
					double delta = Math.abs(angle - target);

					if(delta <= turn) {
						angles[i] = targetAngles[i];
						continue;
					}

					if(angle < target) {
						angles[i] += turn;
					} else {
						angles[i] -= turn;
					}
				}

				return !didMove;
			}
			
			public double[] getPositions(float interp) {
				return new double[] {
						interp(this.prevAngles[0], this.angles[0], interp),
						interp(this.prevAngles[1], this.angles[1], interp),
						interp(this.prevAngles[2], this.angles[2], interp),
						interp(this.prevAngles[3], this.angles[3], interp),
						interp(this.prevSawAngle, this.sawAngle, interp)
				};
			}
		}
	}
	
	/*
	 * Arms cycle through REPOSITION -> EXTEND -> CUT (if saw) -> RETRACT
	 * If transit is planned, the carriage's state will change to RETIRING
	 * If the carriage is RETIRING, each arm will enter RETIRE state after RETRACT
	 * Once the arm has returned to null position, it changes to WAIT
	 * If both arms WAIT, the carriage switches to SLIDING
	 * Once transit is done, carriage returns to WORKING
	 * If the carriage is WORKING, any arm that is in the WAIT state will return to REPOSITION
	 */
	
	public static enum YuriState {
		WORKING,
		RETIRING, // waiting for arms to enter WAITING state
		SLIDING // transit to next position
	}

	public static enum ArmState {
		REPOSITION,
		EXTEND,
		CUT,
		RETRACT,
		RETIRE, // return to null position for carriage transit
		WAIT // either waiting for or in the middle of carriage transit
	}

}
