package com.hbm.railway.track;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import static com.hbm.railway.track.IRailNTM.*;

/** Mechanical source math; deliberately retains source quirks and callback order. */
final class SourceRailPaths {
static RailVector narrowStraight(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
			double targetX = trainX;
			if(motionX > 0) {
				targetX += speed;
				info.yaw(-90F);
			} else {
				targetX -= speed;
				info.yaw(90F);
			}
			vec.xCoord = net.minecraft.util.Mth.clamp(targetX, cX - 2, cX + 3);
			vec.yCoord = y;
			vec.zCoord = cZ + 0.5;
			info.dist(Math.abs(targetX - vec.xCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX + (motionX * speed > 0 ? 3 : -3), y, cZ));
		} else {
			double targetZ = trainZ;
			if(motionZ > 0) {
				targetZ += speed;
				info.yaw(0F);
			} else {
				targetZ -= speed;
				info.yaw(180F);
			}
			vec.xCoord = cX + 0.5;
			vec.yCoord = y;
			vec.zCoord = net.minecraft.util.Mth.clamp(targetZ, cZ - 2,cZ + 3);
			info.dist(Math.abs(targetZ - vec.zCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX, y, cZ + (motionZ * speed > 0 ? 3 : -3)));
		}
		
		return vec;
	
}
static RailVector narrowCurve(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
		
		double turnRadius = 4.5D;

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		double axisX = cX + 0.5  + dir.offsetX * 0.5 + rot.offsetX * turnRadius;
		double axisZ = cZ + 0.5  + dir.offsetZ * 0.5 + rot.offsetZ * turnRadius;
		
		RailVector dist = RailVector.createVectorHelper(vec.xCoord - axisX, 0, vec.zCoord - axisZ);
		dist = dist.normalize();
		dist.xCoord *= turnRadius;
		dist.zCoord *= turnRadius;
		
		double moveAngle = Math.atan2(motionX, motionZ) * 180D / Math.PI + 90;
		
		if(speed == 0) {
			info.dist(0).pos(new BlockPos(x, y, z)).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX + dist.xCoord, y, axisZ + dist.zCoord);
		}
		
		double angleDeg = Math.atan2(dist.xCoord, dist.zCoord) * 180D / Math.PI + 90;
		if(dir == ForgeDirection.WEST) angleDeg -= 90;
		if(dir == ForgeDirection.EAST) angleDeg += 90;
		if(dir == ForgeDirection.SOUTH) angleDeg += 180;
		angleDeg = net.minecraft.util.Mth.wrapDegrees(angleDeg);
		double length90Deg = turnRadius * Math.PI / 2D;
		double angularChange = speed / length90Deg * 90D;
		
		ForgeDirection moveDir = ForgeDirection.UNKNOWN;
		
		if(Math.abs(motionX) > Math.abs(motionZ)) {
			moveDir = motionX > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
		} else {
			moveDir = motionZ > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
		}
		
		if(moveDir == dir || moveDir == rot.getOpposite()) {
			angularChange *= -1;
		}
		
		double effAngle = angleDeg + angularChange;
		moveAngle += angularChange;
		
		if(effAngle > 90) {
			double angleOvershoot = effAngle - 90D;
			moveAngle -= angleOvershoot;
			double lengthOvershoot = angleOvershoot * length90Deg / 90D;
			info.dist(lengthOvershoot * Math.signum(speed * angularChange)).pos(new BlockPos(cX - dir.offsetX * 4 + rot.offsetX * 5, y, cZ - dir.offsetZ * 4 + rot.offsetZ * 5)).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX - dir.offsetX * turnRadius, y, axisZ - dir.offsetZ * turnRadius);
		}
		
		if(effAngle < 0) {
			double angleOvershoot = -effAngle;
			moveAngle -= angleOvershoot;
			double lengthOvershoot = angleOvershoot * length90Deg / 90D;
			info.dist(-lengthOvershoot * Math.signum(speed * angularChange)).pos(new BlockPos(cX + dir.offsetX , y, cZ + dir.offsetZ)).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX - rot.offsetX * turnRadius, y, axisZ -rot.offsetZ * turnRadius);
		}
		
		double radianChange = angularChange * Math.PI / 180D;
		dist.rotateAroundY((float) radianChange);
		
		return RailVector.createVectorHelper(axisX + dist.xCoord, y, axisZ + dist.zCoord);
	
}
static RailVector straight(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		if(speed == 0) {
			//return vec;
		}
		
		if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
			double targetX = trainX;
			if(motionX > 0) {
				targetX += speed;
				info.yaw(-90F);
			} else {
				targetX -= speed;
				info.yaw(90F);
			}
			vec.xCoord = net.minecraft.util.Mth.clamp(targetX, cX - 2, cX + 3);
			vec.yCoord = y + 0.1875;
			vec.zCoord = cZ + 0.5 + rot.offsetZ * 0.5;
			info.dist(Math.abs(targetX - vec.xCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX + (motionX * speed > 0 ? 3 : -3), y, cZ));
		} else {
			double targetZ = trainZ;
			if(motionZ > 0) {
				targetZ += speed;
				info.yaw(0F);
			} else {
				targetZ -= speed;
				info.yaw(180F);
			}
			vec.xCoord = cX + 0.5 + rot.offsetX * 0.5;
			vec.yCoord = y + 0.1875;
			vec.zCoord = net.minecraft.util.Mth.clamp(targetZ, cZ - 2,cZ + 3);
			info.dist(Math.abs(targetZ - vec.zCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX, y, cZ + (motionZ * speed > 0 ? 3 : -3)));
		}
		
		return vec;
	
}
static RailVector shortStraight(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
			double targetX = trainX;
			if(motionX > 0) {
				targetX += speed;
				info.yaw(-90F);
			} else {
				targetX -= speed;
				info.yaw(90F);
			}
			vec.xCoord = net.minecraft.util.Mth.clamp(targetX, cX, cX + 1);
			vec.yCoord = y + 0.1875;
			vec.zCoord = cZ + 0.5 + rot.offsetZ * 0.5;
			info.dist(Math.abs(targetX - vec.xCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX + (motionX * speed > 0 ? 1 : -1), y, cZ));
		} else {
			double targetZ = trainZ;
			if(motionZ > 0) {
				targetZ += speed;
				info.yaw(0F);
			} else {
				targetZ -= speed;
				info.yaw(180F);
			}
			vec.xCoord = cX + 0.5 + rot.offsetX * 0.5;
			vec.yCoord = y + 0.1875;
			vec.zCoord = net.minecraft.util.Mth.clamp(targetZ, cZ,cZ + 1);
			info.dist(Math.abs(targetZ - vec.zCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX, y, cZ + (motionZ * speed > 0 ? 1 : -1)));
		}
		
		return vec;
	
}
static RailVector curve(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
 int width=block.kind.width;
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		double turnRadius = width;
		double axisDist = width + 0.5D;

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		double axisX = cX + 0.5  + dir.offsetX * 0.5 + rot.offsetX * axisDist;
		double axisZ = cZ + 0.5  + dir.offsetZ * 0.5 + rot.offsetZ * axisDist;
		
		RailVector dist = RailVector.createVectorHelper(vec.xCoord - axisX, 0, vec.zCoord - axisZ);
		dist = dist.normalize();
		dist.xCoord *= turnRadius;
		dist.zCoord *= turnRadius;
		
		double moveAngle = Math.atan2(motionX, motionZ) * 180D / Math.PI + 90;
		
		if(speed == 0) {
			info.dist(0).pos(new BlockPos(x, y, z)).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX + dist.xCoord, y, axisZ + dist.zCoord);
		}
		
		double angleDeg = Math.atan2(dist.xCoord, dist.zCoord) * 180D / Math.PI + 90;
		if(dir == ForgeDirection.WEST) angleDeg -= 90;
		if(dir == ForgeDirection.EAST) angleDeg += 90;
		if(dir == ForgeDirection.SOUTH) angleDeg += 180;
		angleDeg = net.minecraft.util.Mth.wrapDegrees(angleDeg);
		double length90Deg = turnRadius * Math.PI / 2D;
		double angularChange = speed / length90Deg * 90D;
		
		ForgeDirection moveDir = ForgeDirection.UNKNOWN;
		
		if(Math.abs(motionX) > Math.abs(motionZ)) {
			moveDir = motionX > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
		} else {
			moveDir = motionZ > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
		}
		
		if(moveDir == dir || moveDir == rot.getOpposite()) {
			angularChange *= -1;
		}
		
		double effAngle = angleDeg + angularChange;
		moveAngle += angularChange;
		
		if(effAngle > 90) {
			double angleOvershoot = effAngle - 90D;
			moveAngle -= angleOvershoot;
			double lengthOvershoot = angleOvershoot * length90Deg / 90D;
			info.dist(lengthOvershoot * Math.signum(speed * angularChange)).pos(new BlockPos(cX - dir.offsetX * width + rot.offsetX * (width + 1), y, cZ - dir.offsetZ * width + rot.offsetZ * (width + 1))).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX - dir.offsetX * turnRadius, y + 0.1875, axisZ - dir.offsetZ * turnRadius);
		}
		
		if(effAngle < 0) {
			double angleOvershoot = -effAngle;
			moveAngle -= angleOvershoot;
			double lengthOvershoot = angleOvershoot * length90Deg / 90D;
			info.dist(-lengthOvershoot * Math.signum(speed * angularChange)).pos(new BlockPos(cX + dir.offsetX , y, cZ + dir.offsetZ)).yaw((float) moveAngle);
			return RailVector.createVectorHelper(axisX - rot.offsetX * turnRadius, y + 0.1875, axisZ -rot.offsetZ * turnRadius);
		}
		
		double radianChange = angularChange * Math.PI / 180D;
		dist.rotateAroundY((float) radianChange);
		
		return RailVector.createVectorHelper(axisX + dist.xCoord, y + 0.1875, axisZ + dist.zCoord);
	
}
static RailVector ramp(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		if(speed == 0) {
			//return vec;
		}
		
		if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
			double targetX = trainX;
			if(motionX > 0) {
				targetX += speed;
				info.yaw(-90F);
			} else {
				targetX -= speed;
				info.yaw(90F);
			}
			double dist = (cX + 0.5 - targetX + 2.5) / 5;
			vec.xCoord = net.minecraft.util.Mth.clamp(targetX, cX - 2, cX + 3);
			vec.yCoord = net.minecraft.util.Mth.clamp(dir == ForgeDirection.EAST ? cY + dist : cY + 1 - dist, cY, cY + 1) + 0.1875;
			vec.zCoord = cZ + 0.5 + rot.offsetZ * 0.5;
			info.dist(Math.abs(targetX - vec.xCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX + (motionX * speed > 0 ? 3 : -3), cY + (motionX * speed > 0 ^ dir == ForgeDirection.EAST ? 1 : 0), cZ));
		} else {
			double targetZ = trainZ;
			if(motionZ > 0) {
				targetZ += speed;
				info.yaw(0F);
			} else {
				targetZ -= speed;
				info.yaw(180F);
			}
			double dist = (cZ + 0.5 - targetZ + 2.5) / 5;
			vec.xCoord = cX + 0.5 + rot.offsetX * 0.5;
			vec.yCoord = net.minecraft.util.Mth.clamp(dir == ForgeDirection.SOUTH ? cY + dist : cY + 1 - dist, cY, cY + 1) + 0.1875;
			vec.zCoord = net.minecraft.util.Mth.clamp(targetZ, cZ - 2,cZ + 3);
			info.dist(Math.abs(targetZ - vec.zCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX, cY + (motionZ * speed > 0 ^ dir == ForgeDirection.SOUTH ? 1 : 0), cZ + (motionZ * speed > 0 ? 3 : -3)));
		}
		
		return vec;
	
}
static RailVector buffer(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META) - 10;
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection rot = dir.getRotation(ForgeDirection.UP);

		RailVector vec = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		if(speed == 0) {
			//return vec;
		}
		
		if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
			double targetX = trainX;
			if(motionX > 0) {
				targetX += speed;
				info.yaw(-90F);
			} else {
				targetX -= speed;
				info.yaw(90F);
			}
			vec.xCoord = net.minecraft.util.Mth.clamp(targetX, cX - 2, cX + 3);
			vec.yCoord = y + 0.1875;
			vec.zCoord = cZ + 0.5 + rot.offsetZ * 0.5;

			double nX = (dir == ForgeDirection.EAST ? -1 - context.collisionBogieDistance : 2);
			double pX = (dir == ForgeDirection.WEST ? 0 - context.collisionBogieDistance : 3);
			double buffer = net.minecraft.util.Mth.clamp(targetX, cX - nX, cX + pX);
			
			if(buffer != vec.xCoord) {
				context.collision = true;
				context.overshoot = Math.abs(buffer - vec.xCoord);
				vec.xCoord = buffer;
				return vec;
			}
			
			info.dist(Math.abs(targetX - vec.xCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX + (motionX * speed > 0 ? 3 : -3), y, cZ));
		} else {
			double targetZ = trainZ;
			if(motionZ > 0) {
				targetZ += speed;
				info.yaw(0F);
			} else {
				targetZ -= speed;
				info.yaw(180F);
			}
			vec.xCoord = cX + 0.5 + rot.offsetX * 0.5;
			vec.yCoord = y + 0.1875;
			vec.zCoord = net.minecraft.util.Mth.clamp(targetZ, cZ - 2,cZ + 3);

			double nZ = (dir == ForgeDirection.SOUTH ? -1 - context.collisionBogieDistance : 2);
			double pZ = (dir == ForgeDirection.NORTH ? 0 - context.collisionBogieDistance : 3);
			double buffer = net.minecraft.util.Mth.clamp(targetZ, cZ - nZ, cZ + pZ);
			
			if(buffer != vec.xCoord) {
				context.collision = true;
				context.overshoot = Math.abs(buffer - vec.zCoord);
				vec.zCoord = buffer;
				return vec;
			}
			
			info.dist(Math.abs(targetZ - vec.zCoord) * Math.signum(speed));
			info.pos(new BlockPos(cX, y, cZ + (motionZ * speed > 0 ? 3 : -3)));
		}
		
		return vec;
	
}
static RailVector waypoint(SourceRailBlock block,Level world,int x,int y,int z,double trainX,double trainY,double trainZ,double motionX,double motionY,double motionZ,double speed,RailContext info,MoveContext context){
		int[] pos = coreCoordinates(block,world,x,y,z);
		if(pos == null) return RailVector.createVectorHelper(trainX, trainY, trainZ);
		int cX = pos[0];
		int cY = pos[1];
		int cZ = pos[2];
		int meta = world.getBlockState(new BlockPos(cX,cY,cZ)).getValue(DummyableBlock.LEGACY_META);
		double moveAngle = Math.atan2(motionX, motionZ) * 180D / Math.PI + 90;
		RailVector trainPos = RailVector.createVectorHelper(trainX, trainY, trainZ);
		
		//convert nodes to a list of links with in-world positions
		RailVector train = RailVector.createVectorHelper(trainX, trainY, trainZ);
		RailVector core = RailVector.createVectorHelper(cX + 0.5, cY, cZ + 0.5);
		List<List<Pair<RailVector[], RailDef>>> links = new ArrayList();
		
		for(RailDef def : definitions(block.kind == SourceRailKind.SWITCH_FLIPPED)) {
			List<Pair<RailVector[], RailDef>> linkList = new ArrayList();
			links.add(linkList);
			
			for(int i = 0; i < def.nodes.size() - 1; i++) {
				RailVector vec1 = getPositionFromNode(world, x, y, z, core, def.nodes.get(i), meta);
				RailVector vec2 = getPositionFromNode(world, x, y, z, core, def.nodes.get(i + 1), meta);
				debugLine(world, vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord - vec1.xCoord, vec2.yCoord - vec1.yCoord, vec2.zCoord - vec1.zCoord, 0xff0000);
				linkList.add(new Pair<RailVector[], RailDef>(new RailVector[] {vec1, vec2}, def));
			}
		}
		
		//find closest node
		Pair<RailVector[], RailDef> closest = null;
		RailVector startingPos = null;
		/** closest chain of link definitions */
		List<Pair<RailVector[], RailDef>> cDef = null;
		double angularDiff = 0;
		double linkAngle = 0;
		double dist = Double.MAX_VALUE;
		/** direction */
		boolean d = true;
		
		for(List<Pair<RailVector[], RailDef>> chain : links) {
			for(Pair<RailVector[], RailDef> link : chain) {
				RailVector[] array = link.getKey();
				RailVector point = getClosestPointOnLink(array[0], array[1], train);
				
				if(point != null) {
					RailVector delta = point.subtract(train);
					double length = delta.lengthVector();
					
					if(!canCross(block,world, cX, cY, cZ, trainPos, point, link.getValue())) continue;
					
					linkAngle = generateYaw(array[1], array[0]);
					angularDiff = angularDifference(linkAngle, -moveAngle);
					if(angularDiff < -180) { angularDiff += 180; linkAngle += 180; d = false; }
					if(angularDiff > 0) { angularDiff -= 180; linkAngle -= 180; d = false; }
					
					if(length < dist) {
						closest = link;
						startingPos = point;
						cDef = chain;
						dist = length;
					}
				}
			}
		}
		
		if(closest == null) {
			return RailVector.createVectorHelper(trainX, trainY, trainZ);
		}
		
		double distRemaining = speed;
		boolean engaged = false;
		RailVector currentPos = startingPos;
		for(int i = d ? 0 : cDef.size() - 1; d ? (i < cDef.size()) : (i >= 0); i += d ? 1 : -1) {
			
			Pair<RailVector[], RailDef> link = cDef.get(i);
			RailVector[] array = link.getKey();
			
			if(!engaged) {
				if(link == closest) {
					engaged = true;
				} else {
					continue;
				}
			}
			
			RailVector nextNode = array[d?1:0];
			RailVector delta = nextNode.subtract(currentPos);
			
			if(!canCross(block,world, cX, cY, cZ, currentPos, nextNode, link.getValue())) break;
			
			double len = delta.lengthVector();
			if(len >= distRemaining) {
				info.overshoot = 0;
				double newYaw = generateYaw(nextNode, currentPos);
				if(Math.abs(angularDifference(newYaw, moveAngle)) < 45) info.yaw = (float) newYaw;
				else info.yaw = (float) moveAngle;
				delta.normalize();
				return RailVector.createVectorHelper(currentPos.xCoord - delta.xCoord * distRemaining / len, currentPos.yCoord - delta.yCoord * distRemaining / len, currentPos.zCoord - delta.zCoord * distRemaining / len);
			}
			
			distRemaining -= len;
			currentPos = nextNode;
		}

		info.overshoot = distRemaining;
		info.pos = BlockPos.containing(currentPos.xCoord,currentPos.yCoord,currentPos.zCoord);
		
		return currentPos;
	
}
static RailVector getClosestPointOnLink(RailVector pointA,RailVector pointB,RailVector pointP){
		RailVector ap = RailVector.createVectorHelper(pointP.xCoord - pointA.xCoord, 0, pointP.zCoord - pointA.zCoord);
		RailVector ab = RailVector.createVectorHelper(pointB.xCoord - pointA.xCoord, 0, pointB.zCoord - pointA.zCoord);
		
		double magAB = ab.xCoord * ab.xCoord + ab.zCoord * ab.zCoord;
		double dotProd = ap.xCoord * ab.xCoord + ap.zCoord * ab.zCoord;
		double dist = dotProd / magAB;
		
		if(dist < 0) return pointA;
		if(dist > 1) return pointB;
		if(dist < 0 || dist > 1) return null;
		
		return RailVector.createVectorHelper(pointA.xCoord + ab.xCoord * dist, pointA.yCoord + (pointB.yCoord - pointA.yCoord) * dist, pointA.zCoord + ab.zCoord * dist);
	
}
static RailVector getPositionFromNode(Level world,int x,int y,int z,RailVector core,RailVector node,int meta){
		float rotation = 0;
		if(meta == 12) rotation = 90F / 180F * (float) Math.PI;
		if(meta == 14) rotation = 180F / 180F * (float) Math.PI;
		if(meta == 13) rotation = 270F / 180F * (float) Math.PI;
		RailVector copy = RailVector.createVectorHelper(node.xCoord, node.yCoord, node.zCoord);
		copy.rotateAroundY(rotation);
		return core.addVector(copy.xCoord, copy.yCoord, copy.zCoord);
	
}
static boolean canCross(SourceRailBlock block,Level world,int x,int y,int z,RailVector from,RailVector to,RailDef def){
		RailSwitchEntity tile=world.getBlockEntity(new BlockPos(x,y,z)) instanceof RailSwitchEntity sw?sw:null;
		if(tile == null) return true;
		
		ForgeDirection dir = ForgeDirection.getOrientation(tile.getBlockState().getValue(DummyableBlock.LEGACY_META) - 10);

		if(dir == ForgeDirection.EAST) if(from.xCoord < to.xCoord) return true;
		if(dir == ForgeDirection.WEST) if(from.xCoord > to.xCoord) return true;
		if(dir == ForgeDirection.SOUTH) if(from.zCoord < to.zCoord) return true;
		if(dir == ForgeDirection.NORTH) if(from.zCoord > to.zCoord) return true;
		
		if(dir == ForgeDirection.EAST) if(to.xCoord < x + 0.5 + 7) return true;
		if(dir == ForgeDirection.WEST) if(to.xCoord > x + 0.5 - 7) return true;
		if(dir == ForgeDirection.SOUTH) if(to.zCoord < z + 0.5 + 7) return true;
		if(dir == ForgeDirection.NORTH) if(to.zCoord > z + 0.5 - 7) return true;
		
		if(tile.isSwitched) {
			if("side".equals(def.name)) return true;
		} else {
			if("main".equals(def.name)) return true;
		}
		
		return false;
	
}
static List<RailDef> definitions(boolean flipped){return flipped?FLIPPED:NORMAL;}
private static final List<RailDef> NORMAL=List.of(new RailDef("main",List.of(RailVector.createVectorHelper(-8.5, 0.1875, 0.5),RailVector.createVectorHelper(-7.5, 0.1875, 0.5),RailVector.createVectorHelper(6.5, 0.1875, 0.5),RailVector.createVectorHelper(7.5, 0.1875, 0.5),RailVector.createVectorHelper(8.5, 0.1875, 0.5))),new RailDef("side",List.of(RailVector.createVectorHelper(-8.5, 0.1875, 4.5),RailVector.createVectorHelper(-7.5, 0.1875, 4.5),RailVector.createVectorHelper(-6.5, 0.1875, 4.5),RailVector.createVectorHelper(-5.5, 0.1875, 4.5),RailVector.createVectorHelper(-4.5, 0.1875, 4.5),RailVector.createVectorHelper(-3.5, 0.1875, 4.5),RailVector.createVectorHelper(-2.5, 0.1875, 4.5),RailVector.createVectorHelper(-1.5, 0.1875, 4.5),RailVector.createVectorHelper(-0.5, 0.1875, 4.25),RailVector.createVectorHelper(0.5, 0.1875, 3.9375),RailVector.createVectorHelper(1.5, 0.1875, 3.375),RailVector.createVectorHelper(2.5, 0.1875, 2.4625),RailVector.createVectorHelper(3.5, 0.1875, 1.75),RailVector.createVectorHelper(4.5, 0.1875, 1.1875),RailVector.createVectorHelper(5.5, 0.1875, 0.875),RailVector.createVectorHelper(6.5, 0.1875, 0.625),RailVector.createVectorHelper(7.5, 0.1875, 0.5),RailVector.createVectorHelper(8.5, 0.1875, 0.5))));
private static final List<RailDef> FLIPPED=List.of(new RailDef("main",List.of(RailVector.createVectorHelper(-8.5, 0.1875, 0.5),RailVector.createVectorHelper(-7.5, 0.1875, 0.5),RailVector.createVectorHelper(6.5, 0.1875, 0.5),RailVector.createVectorHelper(7.5, 0.1875, 0.5),RailVector.createVectorHelper(8.5, 0.1875, 0.5))),new RailDef("side",List.of(RailVector.createVectorHelper(-8.5, 0.1875, -3.5),RailVector.createVectorHelper(-7.5, 0.1875, -3.5),RailVector.createVectorHelper(-6.5, 0.1875, -3.5),RailVector.createVectorHelper(-5.5, 0.1875, -3.5),RailVector.createVectorHelper(-4.5, 0.1875, -3.5),RailVector.createVectorHelper(-3.5, 0.1875, -3.5),RailVector.createVectorHelper(-2.5, 0.1875, -3.5),RailVector.createVectorHelper(-1.5, 0.1875, -3.5),RailVector.createVectorHelper(-0.5, 0.1875, -3.25),RailVector.createVectorHelper(0.5, 0.1875, -2.9375),RailVector.createVectorHelper(1.5, 0.1875, -2.375),RailVector.createVectorHelper(2.5, 0.1875, -1.4625),RailVector.createVectorHelper(3.5, 0.1875, -0.75),RailVector.createVectorHelper(4.5, 0.1875, -0.1875),RailVector.createVectorHelper(5.5, 0.1875, 0.175),RailVector.createVectorHelper(6.5, 0.1875, 0.375),RailVector.createVectorHelper(7.5, 0.1875, 0.5),RailVector.createVectorHelper(8.5, 0.1875, 0.5))));

 static int[] coreCoordinates(SourceRailBlock block,Level level,int x,int y,int z){var p=block.findCore(level,new BlockPos(x,y,z));return p==null?null:new int[]{p.getX(),p.getY(),p.getZ()};}
 static void debugLine(Level level,double x,double y,double z,double dx,double dy,double dz,int color){if(level instanceof net.minecraft.server.level.ServerLevel server)com.hbm.network.cargo.CargoEffectPacket.send(server,x,y,z,dx,dy,dz,color);else com.hbm.network.cargo.CargoVisuals.line.add(x,y,z,dx,dy,dz,color);}
 public static float generateYaw(RailVector front,RailVector back){double dx=front.xCoord-back.xCoord,dz=front.zCoord-back.zCoord;return (float)net.minecraft.util.Mth.wrapDegrees(-Math.atan2(dx,dz)*180D/Math.PI);}
 static double angularDifference(double alpha,double beta){double delta=(beta-alpha+180)%360-180;return delta< -180?delta+360:delta;}
 private record RailDef(String name,List<RailVector> nodes){}
 private record Pair<K,V>(K getKey,V getValue){}
 private SourceRailPaths(){}
}
