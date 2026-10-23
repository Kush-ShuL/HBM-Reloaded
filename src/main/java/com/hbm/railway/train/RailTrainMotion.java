package com.hbm.railway.train;
import java.util.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.railway.track.*;
import com.hbm.railway.track.IRailNTM.*;
import com.hbm.railway.train.RailCarEntity.TrainCoupling;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import static com.hbm.railway.train.RailTrainMath.*;
/** Actual active source world-END train-unit scheduling, smart suspension, route and collision-push math. */
public final class RailTrainMotion {
public static RailVector getRelPosAlongRail(BlockPos anchor,double distanceToCover,TrackGauge gauge,Level worldObj,RailVector next,float yaw,MoveContext context){

		
		if(distanceToCover < 0) {
			distanceToCover *= -1;
			yaw += 180;
		}
		
		int it = 0;
		
		do {
			
			it++;
			
			if(it > 30) {
				return null;
			}
			
			int x = anchor.getX();
			int y = anchor.getY();
			int z = anchor.getZ();
			Block block = readRail(worldObj,anchor);
			
			RailVector rot = RailVector.createVectorHelper(0, 0, 1);
			rot.rotateAroundY((float) (-yaw * Math.PI / 180D));
			
			if(block instanceof IRailNTM) {
				IRailNTM rail = (IRailNTM) block;
				
				if(it == 1) {
					next=legacy(rail.getTravelLocation(worldObj,anchor,next.xCoord,next.yCoord,next.zCoord,rot.xCoord,rot.yCoord,rot.zCoord,0,new RailContext(),context));
				}
				
				boolean flip = distanceToCover < 0;
				
				if(rail.getGauge() == gauge) {
					RailContext info = new RailContext();
					RailVector prev = next;
					next=legacy(rail.getTravelLocation(worldObj,anchor,prev.xCoord,prev.yCoord,prev.zCoord,rot.xCoord,rot.yCoord,rot.zCoord,distanceToCover,info,context));
					distanceToCover = info.overshoot;
					anchor = info.pos;
					
					yaw = yaw(next, prev) * (flip ? -1 : 1);
					
				} else {
					return null;
				}
			} else {
				return null;
			}
			
		} while(distanceToCover != 0); //if there's still length to cover, keep going
		
		return next;
	
}
public static void updateMotion(ServerLevel world){
		Set<LogicalTrainUnit> ltus = new HashSet();
		
		/* gather all LTUs */
		for(Object o : world.getAllEntities()) {
			if(o instanceof RailCarEntity) {
				RailCarEntity train = (RailCarEntity) o;
				if(train.ltu != null) ltus.add(train.ltu);
			}
		}
		
		/* Move carts together with links */
		//for(LogicalTrainUnit ltu : ltus) ltu.combineWagons();
		
		/* Move carts with unified speed */
		//for(LogicalTrainUnit ltu : ltus) ltu.moveTrain();
		
		for(LogicalTrainUnit ltu : ltus) {
var before=new ArrayList<Snapshot>();for(var car:ltu.trains)before.add(new Snapshot(car));double oldPush=ltu.pushForce;try{
			
			double speed = ltu.getTotalSpeed() + ltu.pushForce;
			
			if(Math.abs(speed) < 0.001) speed = 0;
			
			for(RailCarEntity car : ltu.trains) car.cachedSpeed = speed;
			
			if(ltu.trains.length == 1) {

				RailCarEntity train = ltu.trains[0];
				
				BlockPos anchor = BlockPos.containing(train.getX(),train.getY(),train.getZ());
				RailVector newPos = train.getRelPosAlongRail(anchor, speed, new MoveContext(RailCheckType.CORE, 0));
				if(newPos == null) {
					train.derail();
					ltu.dissolveTrain();
					continue;
				}
				train.setPos(newPos.xCoord, newPos.yCoord, newPos.zCoord);
				anchor = train.getCurrentAnchorPos();
				RailVector frontPos = train.getRelPosAlongRail(anchor, train.getLengthSpan(), new MoveContext(RailCheckType.FRONT, train.getCollisionSpan() - train.getLengthSpan()));
				RailVector backPos = train.getRelPosAlongRail(anchor, -train.getLengthSpan(), new MoveContext(RailCheckType.BACK, train.getCollisionSpan() - train.getLengthSpan()));

				if(frontPos == null || backPos == null) {
					train.derail();
					ltu.dissolveTrain();
					continue;
				} else {
					ltu.setRenderPos(train, frontPos, backPos);
				}

				//ltu.pushForce *= 0.95;
				ltu.pushForce = 0;
				for(var car:ltu.trains)if(!readyBox(car.level(),car.getBoundingBox()))throw Unavailable.INSTANCE;ltu.collideTrain(speed);
				
				continue;
			}
			
			if(speed == 0) {
				ltu.combineWagons();
			} else {
				ltu.moveTrainByApproach(speed);
			}
			
			//ltu.pushForce *= 0.95;
			ltu.pushForce = 0;
			for(var car:ltu.trains)if(!readyBox(car.level(),car.getBoundingBox()))throw Unavailable.INSTANCE;ltu.collideTrain(speed);
}catch(Unavailable unavailable){for(var snapshot:before)snapshot.restore();ltu.pushForce=oldPush;}
		}
	
}
public static class LogicalTrainUnit{
		
		protected double pushForce;
		protected RailCarEntity trains[];
		
		/** Assumes that the train is an endpoint, i.e. that only one coupling is in use */
		public static LogicalTrainUnit generateTrain(RailCarEntity train) {
			List<RailCarEntity> links = new ArrayList();
			Set<RailCarEntity> brake = new HashSet();
			LogicalTrainUnit ltu = new LogicalTrainUnit();
			
			if(train.coupledFront == null && train.coupledBack == null) {
				ltu.trains = new RailCarEntity[] {train};
				train.ltu = ltu;
				train.ltuIndex = 0;
				return ltu;
			}
			
			RailCarEntity current = train;
			RailCarEntity next = null;
			
			do {
				next = null;

				if(current.coupledFront != null && !brake.contains(current.coupledFront)) next = current.coupledFront;
				if(current.coupledBack != null && !brake.contains(current.coupledBack)) next = current.coupledBack;
				
				links.add(current);
				brake.add(current);
				
				current = next;
				
			} while(next != null);
			
			ltu.trains = new RailCarEntity[links.size()];
			for(int i = 0; i < ltu.trains.length; i++) {
				ltu.trains[i] = links.get(i);
				ltu.trains[i].ltu = ltu;
				ltu.trains[i].ltuIndex = i;
			}
			
			return ltu;
		}
		
		/** Removes the LTU from all wagons */
		public void dissolveTrain() {
			for(RailCarEntity train : trains) {
				train.ltu = null;
				train.ltuIndex = 0;
			}
		}
		
		/** Find the center fo the train, then moves all wagons towards that center until the coupling points roughly touch */
		public void combineWagons() {
			
			if(trains.length <= 1) return;
			
			boolean odd = trains.length % 2 == 1;
			int centerIndex = odd ? trains.length / 2 : trains.length / 2 - 1;
			RailCarEntity center = trains[centerIndex];
			RailCarEntity prev = center;
			
			for(int i = centerIndex - 1; i >= 0; i--) {
				RailCarEntity next = trains[i];
				moveWagonTo(prev, next);
				prev = next;
			}
			
			prev = center;
			for(int i = centerIndex + 1; i < trains.length; i++) {
				RailCarEntity next = trains[i];
				moveWagonTo(prev, next);
				prev = next;
			}
		}
		
		/** Moves one wagon to ne next until the coupling points roughly touch */
		public void moveWagonTo(RailCarEntity moveTo, RailCarEntity moving) {
			TrainCoupling prevCouple = moveTo.getCouplingFrom(moving);
			TrainCoupling nextCouple = moving.getCouplingFrom(moveTo);
			RailVector prevLoc = moveTo.getCouplingPos(prevCouple);
			RailVector nextLoc = moving.getCouplingPos(nextCouple);
			RailVector delta = RailVector.createVectorHelper(prevLoc.xCoord - nextLoc.xCoord, 0, prevLoc.zCoord - nextLoc.zCoord);
			double len = delta.lengthVector();
			//len *= 0.25; //suspension, causes movements to be less rigid
			len = (len / (0.5D / (len * len) + 1D)); //smart suspension
			BlockPos anchor = BlockPos.containing(moving.getX(),moving.getY(),moving.getZ());
			RailVector trainPos = RailVector.createVectorHelper(moving.getX(), moving.getY(), moving.getZ());
			float yaw = yaw(prevLoc, nextLoc);
			RailVector newPos = getRelPosAlongRail(anchor, len, moving.getGauge(), moving.level(), trainPos, yaw, new MoveContext(RailCheckType.CORE, 0));
			if(newPos==null){moving.derail();dissolveTrain();return;}moving.setPos(newPos.xCoord,newPos.yCoord,newPos.zCoord);
			anchor = moving.getCurrentAnchorPos(); //reset origin to new position
			RailVector frontPos = moving.getRelPosAlongRail(anchor, moving.getLengthSpan(), new MoveContext(RailCheckType.FRONT, moving.getCollisionSpan() - moving.getLengthSpan()));
			RailVector backPos = moving.getRelPosAlongRail(anchor, -moving.getLengthSpan(), new MoveContext(RailCheckType.BACK, moving.getCollisionSpan() - moving.getLengthSpan()));

			if(frontPos == null || backPos == null) {
				moving.derail();
				this.dissolveTrain();
				return;
			} else {
				setRenderPos(moving, frontPos, backPos);
			}
		}
		
		/** Generates the speed of the train, then moves the rain along the rail */
		@Deprecated public void moveTrain() {
			
			RailCarEntity prev = trains[0];
			TrainCoupling dir = prev.getCouplingFrom(null);
			double totalSpeed = 0;
			double maxSpeed = Double.POSITIVE_INFINITY;
			
			for(RailCarEntity train : this.trains) {
				boolean con = train.getCouplingFrom(prev) == dir;
				double speed = train.getCurrentSpeed();
				if(!con) speed *= -1;
				totalSpeed += speed;
				maxSpeed = Math.min(maxSpeed, train.getMaxRailSpeed());
				prev = train;
			}
			
			if(Math.abs(totalSpeed) > maxSpeed) {
				totalSpeed = maxSpeed * Math.signum(totalSpeed);
			}
			
			this.moveTrainBy(totalSpeed);
		}
		
		/** Moves the entire train along the rail by a certain speed */
		@Deprecated public void moveTrainBy(double totalSpeed) {
			
			for(RailCarEntity train : this.trains) {
				
				BlockPos anchor = train.getCurrentAnchorPos();
				RailVector corePos = train.getRelPosAlongRail(anchor, totalSpeed, new MoveContext(RailCheckType.CORE, 0));
				
				if(corePos == null) {
					train.derail();
					this.dissolveTrain();
					return;
				} else {
					train.setPos(corePos.xCoord, corePos.yCoord, corePos.zCoord);
					anchor = train.getCurrentAnchorPos(); //reset origin to new position
					RailVector frontPos = train.getRelPosAlongRail(anchor, train.getLengthSpan(), new MoveContext(RailCheckType.FRONT, 0));
					RailVector backPos = train.getRelPosAlongRail(anchor, -train.getLengthSpan(), new MoveContext(RailCheckType.BACK, 0));

					if(frontPos == null || backPos == null) {
						train.derail();
						this.dissolveTrain();
						return;
					} else {
						train.renderX = (frontPos.xCoord + backPos.xCoord) / 2D;
						train.renderY = (frontPos.yCoord + backPos.yCoord) / 2D;
						train.renderZ = (frontPos.zCoord + backPos.zCoord) / 2D;
						train.prevRotationYaw = train.rotationYaw;
						train.rotationYaw = train.movementYaw = yaw(frontPos, backPos);
						
						train.publishAngles();
					}
				}
			}
		}
		
		/** Returns the total speed of the LTU, negative if it is backwards compared to the arbitrary "front" wagon */
		public double getTotalSpeed() {
			
			RailCarEntity prev = trains[0];
			double totalSpeed = 0;
			double maxSpeed = Double.POSITIVE_INFINITY;
			//if the first car is in reverse, flip all subsequent cars as well
			boolean reverseTheReverse = prev.getCouplingFrom(null) == TrainCoupling.BACK;
			
			if(trains.length == 1) {
				return prev.getCurrentSpeed();
			}
			
			for(RailCarEntity train : this.trains) {
				//if the car's linked indices are the wrong way, it is in reverse and speed applies negatively
				boolean reverse = false;

				RailCarEntity conFront = train.getCoupledTo(TrainCoupling.FRONT);
				RailCarEntity conBack = train.getCoupledTo(TrainCoupling.BACK);

				if(conFront != null && conFront.ltuIndex > train.ltuIndex) reverse = true;
				if(conBack != null && conBack.ltuIndex < train.ltuIndex) reverse = true;
				
				reverse ^= reverseTheReverse;
				
				double speed = train.getCurrentSpeed();
				if(reverse) speed *= -1;
				totalSpeed += speed;
				maxSpeed = Math.min(maxSpeed, train.getMaxRailSpeed());
				prev = train;
			}
			
			if(Math.abs(totalSpeed) > maxSpeed) {
				totalSpeed = maxSpeed * Math.signum(totalSpeed);
			}
			
			return totalSpeed;
		}
		
		/*
		 * This method has no rhyme or reason behind it. Nothing of this was calculated, instead it was an old system that worked with older constraints,
		 * which was retrofitted with a slightly newer system and beaten into submission for two consecutive hours until it yielded the results it should.
		 * Booleans are flipped back and forth based on seemingly random conditions, numbers are inverted and then inverted again and finally smashed into
		 * the rail system in the hopes that it would make trains work. My apologies extend towards Bob in the future who will inevitably have to rewrite this
		 * abhorrence because of some constraint change which will cause the entire system to break. Part of me wishes to never touch the train code ever again,
		 * to abandon the idea and to ban the annoying people on Discord who keep asking about it. Another part wants me to slam my head against this project
		 * until either it or my skull gives way; and considering I got this far, it appears as if this side is the one that is winning.
		 */
		/** Determines the "front" wagon based on the movement and moves it, then moves all other wagons towards that */
		public void moveTrainByApproach(double speed) {
			RailCarEntity previous = null;
			RailCarEntity first = this.trains[0];
			boolean forward = speed > 0;
			boolean order = forward ^ first.getCouplingFrom(null) == TrainCoupling.BACK;
			
			for(int i = order ? 0 : this.trains.length - 1; order ? i < this.trains.length : i >= 0; i += order ? 1 : -1) {
				RailCarEntity current = this.trains[i];
				
				if(previous == null) {
					
					if(first == current) speed *= -1;
					
					boolean inReverse = first.getCouplingFrom(null) == current.getCouplingFrom(null);
					int sigNum = inReverse ? 1 : -1;
					BlockPos anchor = current.getCurrentAnchorPos();
					
					RailVector frontPos = current.getRelPosAlongRail(anchor, (speed + current.getLengthSpan()) * -sigNum, new MoveContext(RailCheckType.FRONT, current.getCollisionSpan() - current.getLengthSpan()));
					
					if(frontPos == null) {
						current.derail();
						this.dissolveTrain();
						return;
					} else {
						anchor = current.getCurrentAnchorPos(); //reset origin to new position
						RailVector corePos = current.getRelPosAlongRail(anchor, speed * -sigNum, new MoveContext(RailCheckType.CORE, 0));
						if(corePos==null){current.derail();dissolveTrain();return;}current.setPos(corePos.xCoord,corePos.yCoord,corePos.zCoord);
						RailVector backPos = current.getRelPosAlongRail(anchor, (speed - current.getLengthSpan()) * -sigNum, new MoveContext(RailCheckType.BACK, current.getCollisionSpan() - current.getLengthSpan()));

						if(frontPos == null || backPos == null) {
							current.derail();
							this.dissolveTrain();
							return;
						} else {
							setRenderPos(current, inReverse ? backPos : frontPos, inReverse ? frontPos : backPos);
						}
					}
					
				} else {
					this.moveWagonTo(previous, current);
				}
				
				previous = current;
			}
		}
		
		/** Uses the front and back bogey positions to set the render pos and angles of a wagon */
		public void setRenderPos(RailCarEntity current, RailVector frontPos, RailVector backPos) {
			current.renderX = (frontPos.xCoord + backPos.xCoord) / 2D;
			current.renderY = (frontPos.yCoord + backPos.yCoord) / 2D;
			current.renderZ = (frontPos.zCoord + backPos.zCoord) / 2D;
			current.prevRotationYaw = current.rotationYaw;
			current.rotationYaw = current.movementYaw = yaw(frontPos, backPos);
			RailVector delta = RailVector.createVectorHelper(frontPos.xCoord - backPos.xCoord, frontPos.yCoord - backPos.yCoord, frontPos.zCoord - backPos.zCoord);
			current.rotationPitch = current.movementPitch = (float) (Math.asin(delta.yCoord / delta.lengthVector()) * 180D / Math.PI);
			// Native synchronized float YAW transports the same intended source value; do not inject fake movement.
			
			current.publishAngles();
		}
		
		public void collideTrain(double speed) {
			RailCarEntity collidingTrain = speed > 0 ? trains[0] : trains[trains.length - 1];
			List<RailCarEntity> intersect = collidingTrain.level().getEntitiesOfClass(RailCarEntity.class, collidingTrain.getBoundingBox().inflate(1, 1, 1));
			RailCarEntity collidesWith = null;
			
			for(RailCarEntity train : intersect) {
				if(train.ltu != null && train.ltu != this) {
					collidesWith = train;
					break;
				}
			}
			
			if(collidesWith == null) return;
			
			RailVector delta = RailVector.createVectorHelper(collidingTrain.getX() - collidesWith.getX(), 0, collidingTrain.getZ() - collidesWith.getZ());
			double totalSpan = collidingTrain.getCollisionSpan() + collidesWith.getCollisionSpan();
			double diff = delta.lengthVector();
			if(diff > totalSpan) return;
			double push = (totalSpan - diff);
			
			//PacketDispatcher.wrapper.sendToAllAround(new PlayerInformPacket(ChatBuilder.start("" + collidesWith.ltuIndex + " " + collidingTrain.ltuIndex).color(EnumChatFormatting.RED).flush(), 1),
			//		new TargetPoint(collidingTrain.dimension, collidingTrain.getX(), collidingTrain.getY() + 1, collidingTrain.getZ(), 50));
			
			RailCarEntity[][] whatever = new RailCarEntity[][] {{collidingTrain, collidesWith}, {collidesWith, collidingTrain}};
			for(RailCarEntity[] array : whatever) {
				LogicalTrainUnit ltu = array[0].ltu;
				if(ltu.trains.length == 1) {
					RailVector rot = RailVector.createVectorHelper(0, 0, array[0].getCollisionSpan());
					rot.rotateAroundX((float) (array[0].rotationPitch * Math.PI / 180D));
					rot.rotateAroundY((float) (-array[0].rotationYaw * Math.PI / 180));
					RailVector forward = RailVector.createVectorHelper(array[1].getX() - (array[0].getX() + rot.xCoord), 0, array[1].getZ() - (array[0].getZ() + rot.zCoord));
					RailVector backward = RailVector.createVectorHelper(array[1].getX() - (array[0].getX() - rot.xCoord), 0, array[1].getZ() - (array[0].getZ() - rot.zCoord));
					
					if(forward.lengthVector() > backward.lengthVector()) {
						ltu.pushForce += push;
					} else {
						ltu.pushForce -= push;
					}
				} else {
					
					if(array[0].ltuIndex < ltu.trains.length / 2) {
						ltu.pushForce -= push;
					} else {
						ltu.pushForce += push;
					}
				}
			}
		}
	
}

    static RailVector legacy(net.minecraft.world.phys.Vec3 value){return vector(value.x,value.y,value.z);}
    static Block readRail(Level level,BlockPos pos){if(pos==null)return net.minecraft.world.level.block.Blocks.AIR;if(!SourceRailBlock.full(level,pos))throw Unavailable.INSTANCE;var state=level.getBlockState(pos);if(state.getBlock() instanceof SourceRailBlock block){var seen=new HashSet<BlockPos>();var p=pos;while(seen.size()<=2000&&seen.add(p)){if(!SourceRailBlock.full(level,p))throw Unavailable.INSTANCE;var part=level.getBlockState(p);if(!part.is(block))break;int meta=part.getValue(DummyableBlock.LEGACY_META);if(meta>=12)break;p=p.relative(Direction.from3DDataValue(meta%6).getOpposite());}}return state.getBlock();}
    static boolean readyBox(Level level,AABB box){for(int x=Mth.floor(box.minX)>>4;x<=Mth.floor(box.maxX)>>4;x++)for(int z=Mth.floor(box.minZ)>>4;z<=Mth.floor(box.maxZ)>>4;z++)if(level instanceof ServerLevel server?server.getChunkSource().getChunkNow(x,z)==null:!level.hasChunkAt(new BlockPos(x<<4,0,z<<4)))return false;return true;}
    static final class Unavailable extends RuntimeException{static final Unavailable INSTANCE=new Unavailable();private Unavailable(){super(null,null,false,false);}}
    private static final class Snapshot {
        final RailCarEntity car;final double x,y,z,rx,ry,rz,cached,engine;final float yaw,pitch,previousYaw,previousPitch,myaw,mpitch;final boolean onRail;final LogicalTrainUnit unit;final int index;
        Snapshot(RailCarEntity car){this.car=car;onRail=car.isOnRail;unit=car.ltu;index=car.ltuIndex;x=car.getX();y=car.getY();z=car.getZ();rx=car.renderX;ry=car.renderY;rz=car.renderZ;cached=car.cachedSpeed;engine=car.engineSpeed;yaw=car.rotationYaw;pitch=car.rotationPitch;previousYaw=car.prevRotationYaw;previousPitch=car.prevRotationPitch;myaw=car.movementYaw;mpitch=car.movementPitch;}
        void restore(){car.isOnRail=onRail;car.ltu=unit;car.ltuIndex=index;car.setPos(x,y,z);car.renderX=rx;car.renderY=ry;car.renderZ=rz;car.cachedSpeed=cached;car.engineSpeed=engine;car.rotationYaw=yaw;car.rotationPitch=pitch;car.prevRotationYaw=previousYaw;car.prevRotationPitch=previousPitch;car.movementYaw=myaw;car.movementPitch=mpitch;car.publishAngles();}
    }
    private RailTrainMotion(){}
}
