package com.hbm.client.plasmaforge;

import com.hbm.machine.plasmaforge.PlasmaForgeBlockEntity;
import java.util.*;
import java.util.function.Consumer;

/** Original client-only ring and ForgeArm state machines; no animation state enters machine NBT. */
public final class PlasmaForgeAnimation {
    private static final Map<PlasmaForgeBlockEntity,PlasmaForgeAnimation> STATES = new WeakHashMap<>();
    final PlasmaForgeBlockEntity machine;
    public int timeOffset=-1;
    public double prevRing,ring,ringSpeed,ringTarget;
    public int ringDelay;
    public final ForgeArm armStriker=new ForgeArm(ForgeArmType.STRIKER),armJet=new ForgeArm(ForgeArmType.JET);
    private PlasmaForgeAnimation(PlasmaForgeBlockEntity machine){this.machine=machine;}
    public static PlasmaForgeAnimation get(PlasmaForgeBlockEntity machine){return STATES.computeIfAbsent(machine,PlasmaForgeAnimation::new);}
    public static void removed(PlasmaForgeBlockEntity machine){STATES.remove(machine);}
    public static void tick(PlasmaForgeBlockEntity machine){get(machine).update();}
    private void update(){
        var random=machine.getLevel().random;
        if(timeOffset==-1)timeOffset=random.nextInt(30_000);
        armStriker.updateArm();armJet.updateArm();prevRing=ring;
        if(machine.didProcess){
            if(ring!=ringTarget){
                double ringDelta=Math.abs(ringTarget-ring);
                if(ringDelta<=ringSpeed)ring=ringTarget;
                if(ringTarget>ring)ring+=ringSpeed;
                if(ringTarget<ring)ring-=ringSpeed;
                if(ringTarget==ring){double sub=ringTarget>=360?-360D:360D;ringTarget+=sub;ring+=sub;prevRing+=sub;ringDelay=100+random.nextInt(41);}
            }else{
                if(ringDelay>0)ringDelay--;
                if(ringDelay<=0){ringTarget+=(random.nextDouble()+1)*60*(random.nextBoolean()?-1:1);ringSpeed=2.5D;}
            }
        }
    }
	public class ForgeArm {

		public ForgeArmType type;
		public ForgeArmState state = ForgeArmState.RETIRE;
		public double[] angles;
		public double[] prevAngles;
		public double[] targetAngles;
		public double[] speed;
		public int actionDelay = 0;
		
		public ForgeArm(ForgeArmType type) {
			this.type = type;
			this.angles =		new double[type.angleCount];
			this.prevAngles =	new double[type.angleCount];
			this.targetAngles =	new double[type.angleCount];
			this.speed =		new double[type.angleCount];
			
			for(int i = 0; i < speed.length; i++) {
				if(i < 3 || i == 4) speed[i] = 15;
				if(i == 3) speed[i] = 15;
				if(i > 4) speed[i] = 0.5;
			}
		}
		
		public void updateArm() {
			for(int i = 0; i < angles.length; i++) prevAngles[i] = angles[i];

			if(!didProcess()) this.state = ForgeArmState.RETIRE;
			if(this.state == ForgeArmState.RETIRE) this.actionDelay = 0;
			
			if(this.actionDelay > 0) {
				this.actionDelay--;
				return;
			}
			
			this.type.stateMachine.accept(this);
		}
		
		public boolean didProcess() { return machine.didProcess; }
		
		public boolean move() {
			boolean didMove = false;

			for(int i = 0; i < angles.length; i++) {
				if(angles[i] == targetAngles[i]) continue;
				didMove = true;

				double angle = angles[i];
				double target = targetAngles[i];
				double turn = speed[i];
				double delta = Math.abs(angle - target);

				if(delta <= turn) {
					angles[i] = targetAngles[i];
					continue;
				}
				if(angle < target) angles[i] += turn;
				else angles[i] -= turn;
			}

			return !didMove;
		}
		
		public void playStrikerSound() {
			var pos = machine.getBlockPos();
            machine.getLevel().playLocalSound(pos.getX(), pos.getY(), pos.getZ(), net.minecraft.sounds.SoundEvent.createVariableRangeEvent(net.minecraft.resources.ResourceLocation.parse("hbm:item.boltgun")), net.minecraft.sounds.SoundSource.BLOCKS, machine.volume(0.25F), 1.25F, false);
		}
		
		public double[] getPositions(float interp) {
			double[] pos = new double[this.angles.length];
			for(int i = 0; i < pos.length; i++) {
				pos[i] = this.prevAngles[i] + (this.angles[i] - this.prevAngles[i]) * interp;
			}
			return pos;
		}
	}
	
	public static enum ForgeArmType {
		STRIKER(STRIKER_STATE_MACHINE, 6), // pivot to lower arm, lower arm to upper arm, upper arm to mount, mount to strikers, striker 1, striker 2
		JET(JET_STATE_MACHINE, 4); // pivot to lower arm, lower arm to upper arm, upper arm to jet, jet (unused, actual jet is handled via other conditions)
		
		protected final int angleCount;
		protected final Consumer<ForgeArm> stateMachine;
		
		private ForgeArmType(Consumer<ForgeArm> stateMachine, int angleCount) {
			this.stateMachine = stateMachine;
			this.angleCount = angleCount;
		}
	}
	
	public static enum ForgeArmState {
		REPOSITION,
		EXTEND1,
		EXTEND2,
		RETRACT1,
		RETRACT2,
		RETIRE
	}
	
	public static Random rand = new Random();
	
	public static double[][] strikerPositions = new double[][] {
			{20, -30, -20, 30},
			{45, -80, 15, 30},
			{30, -45, -10, 30},
			{15, -20, -30, 30},
			{0, 10, -55, 30}
	};
	
	public static double[][] jetPositions = new double[][] {
			{10, 45, -120},
			{20, 45, -140},
			{0, 30, -80},
			{0, 40, -100},
			{30, 50, -160}
	};
	
	public static Consumer<ForgeArm> STRIKER_STATE_MACHINE = (arm) -> {
		
		switch(arm.state) {
		case REPOSITION: {
			if(arm.move()) {
				arm.actionDelay = 5;
				arm.state = ForgeArmState.EXTEND1;
				arm.targetAngles[4] = 0.5D;
			}
		} break;
		case EXTEND1: {
			if(arm.move()) {
				arm.actionDelay = 0;
				arm.state = ForgeArmState.RETRACT1;
				arm.targetAngles[4] = 0D;
				arm.playStrikerSound();
			}
		} break;
		case RETRACT1: {
			if(arm.move()) {
				arm.actionDelay = 0;
				arm.state = ForgeArmState.EXTEND2;
				arm.targetAngles[5] = 0.5D;
			}
		} break;
		case EXTEND2: {
			if(arm.move()) {
				arm.actionDelay = 0;
				arm.state = ForgeArmState.RETRACT2;
				arm.targetAngles[5] = 0D;
				arm.playStrikerSound();
			}
		} break;
		case RETRACT2: {
			if(arm.move()) {
				if(rand.nextInt(3) == 0) {
					arm.actionDelay = 10;
					arm.state = ForgeArmState.REPOSITION;
					choosePosition(arm, strikerPositions);
				} else {
					arm.actionDelay = 5;
					arm.state = ForgeArmState.EXTEND1;
					arm.targetAngles[4] = 0.5D;
				}
			}
		} break;
		case RETIRE: {
			for(int i = 0; i < arm.targetAngles.length; i++) arm.targetAngles[i] = 0;
			if(arm.move()) {
				arm.actionDelay = 10;
				arm.state = ForgeArmState.REPOSITION;
				choosePosition(arm, strikerPositions);
			}
		} break;
		}
		
		// experimental bit to make the dual pivots reset between repositions for more movement, looked way too janky
		/*if(arm.state == ForgeArmState.REPOSITION || arm.state == ForgeArmState.RETIRE) {
			arm.targetAngles[3] = 0;
		} else {
			arm.targetAngles[3] = 30;
		}*/
	};

	@SuppressWarnings("incomplete-switch") // shut up
	public static Consumer<ForgeArm> JET_STATE_MACHINE = (arm) -> {
		
		switch(arm.state) {
		case REPOSITION: {
			if(arm.move()) {
				arm.actionDelay = 20 + rand.nextInt(3) * 10;
				arm.state = ForgeArmState.REPOSITION;
				choosePosition(arm, jetPositions);
			}
		} break;
		case RETIRE: {
			for(int i = 0; i < arm.targetAngles.length; i++) arm.targetAngles[i] = 0;
			if(arm.move()) {
				arm.actionDelay = 10;
				arm.state = ForgeArmState.REPOSITION;
				choosePosition(arm, jetPositions);
			}
		} break;
		}
	};
	
	public static void choosePosition(ForgeArm arm, double[][] positions) {
		double[] newPos = positions[rand.nextInt(positions.length)];
		for(int i = 0; i < newPos.length; i++) arm.targetAngles[i] = newPos[i];
	}

}
