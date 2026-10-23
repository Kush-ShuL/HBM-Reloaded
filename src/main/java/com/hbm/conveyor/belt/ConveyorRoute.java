package com.hbm.conveyor.belt;

import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** The original ItemConveyorWand two-pass Manhattan algorithm. No shortest-path replacement or truncation. */
public final class ConveyorRoute {
 @FunctionalInterface public interface Placement {void set(BlockPos pos,BlockState state);}
 public static int construct(Level world,Placement build,ConveyorWandItem.Type type,Player player,int x1,int y1,int z1,Direction side1,int x2,int y2,int z2,Direction side2,int maximum){
  Direction dir=side1,targetDir=side2;
  if(x1==x2&&y1==y2&&z1==z2&&side1==side2&&side1.getAxis().isVertical()){y1+=dir.getStepY();var pos=new BlockPos(x1,y1,z1);if(!world.getBlockState(pos).canBeReplaced())return -1;if(build!=null)build.set(pos,ConveyorContent.belt(type).get().defaultBlockState().setValue(ConveyorBeltBlock.META,ConveyorBeltBlock.facingMeta(player)));return 1;}
  boolean vertical=type==ConveyorWandItem.Type.REGULAR;int tx=x2+targetDir.getStepX(),ty=y2+targetDir.getStepY(),tz=z2+targetDir.getStepZ();int x=x1+dir.getStepX(),y=y1+dir.getStepY(),z=z1+dir.getStepZ();if(dir.getAxis().isVertical())dir=targetDirection(x,y,z,x2,y2,z2,x2,y2,z2,null,false,vertical);
  var targetBlock=world.getBlockState(new BlockPos(x2,y2,z2)).getBlock();boolean turnToTarget=!targetDir.getAxis().isVertical()||targetBlock instanceof ConveyorCraneEndpoint||targetBlock==ConveyorContent.LIFT.get()||targetBlock==ConveyorContent.CHUTE.get();Direction horizontal=dir.getAxis().isVertical()?ConveyorBeltBlock.direction(ConveyorBeltBlock.facingMeta(player)).getOpposite():dir;
  if(vertical&&y>ty&&world.getBlockState(new BlockPos(x,y-1,z)).canBeReplaced())dir=Direction.DOWN;
  for(int depth=1;depth<=maximum;depth++){var pos=new BlockPos(x,y,z);if(!world.getBlockState(pos).canBeReplaced())return -1;var block=forDirection(type,dir);int meta=metadata(block,dir,targetDir,horizontal);int ox=x+dir.getStepX(),oy=y+dir.getStepY(),oz=z+dir.getStepZ(),from=distance(x,y,z,tx,ty,tz),to=distance(ox,oy,oz,tx,ty,tz),finalDistance=distance(ox,oy,oz,x2,y2,z2);boolean notAtTarget=(turnToTarget?finalDistance:from)>0,obstructed=notAtTarget&&!world.getBlockState(new BlockPos(ox,oy,oz)).canBeReplaced(),turn=to>=from&&notAtTarget||obstructed;
   if(turn){var next=targetDirection(x,y,z,turnToTarget?x2:tx,turnToTarget?y2:ty,turnToTarget?z2:tz,tx,ty,tz,dir,obstructed,vertical);if(next==Direction.UP)block=ConveyorContent.LIFT.get();else if(next==Direction.DOWN)block=ConveyorContent.CHUTE.get();else if(ConveyorBeltBlock.rotationUp(dir)==next)meta+=8;else if(ConveyorBeltBlock.rotationDown(dir)==next)meta+=4;dir=next;if(!dir.getAxis().isVertical())horizontal=dir;}
   if(build!=null)build.set(pos,block.defaultBlockState().setValue(ConveyorBeltBlock.META,meta));if(x==tx&&y==ty&&z==tz)return depth;x+=dir.getStepX();y+=dir.getStepY();z+=dir.getStepZ();
  }return 0;
 }
 private static ConveyorBeltBlock forDirection(ConveyorWandItem.Type type,Direction dir){return dir==Direction.UP?ConveyorContent.LIFT.get():dir==Direction.DOWN?ConveyorContent.CHUTE.get():ConveyorContent.belt(type).get();}
 private static int metadata(ConveyorBeltBlock block,Direction dir,Direction target,Direction horizontal){if(block.kind!=ConveyorBeltBlock.Kind.CHUTE&&block.kind!=ConveyorBeltBlock.Kind.LIFT)return dir.getOpposite().get3DDataValue();if(target.getAxis().isVertical())return horizontal.getOpposite().get3DDataValue();return target.get3DDataValue();}
 private static Direction targetDirection(int x1,int y1,int z1,int x2,int y2,int z2,int tx,int ty,int tz,Direction heading,boolean obstructed,boolean vertical){if(vertical&&(y1!=y2||y1!=ty)&&(obstructed||x1==x2&&z1==z2||x1==tx&&z1==tz))return y1>y2?Direction.DOWN:Direction.UP;if(Math.abs(x1-x2)>Math.abs(z1-z2)){if(heading==Direction.EAST||heading==Direction.WEST)return z1>z2?Direction.NORTH:Direction.SOUTH;return x1>x2?Direction.WEST:Direction.EAST;}if(heading==Direction.NORTH||heading==Direction.SOUTH)return x1>x2?Direction.WEST:Direction.EAST;return z1>z2?Direction.NORTH:Direction.SOUTH;}
 private static int distance(int x1,int y1,int z1,int x2,int y2,int z2){return Math.abs(x1-x2)+Math.abs(y1-y2)+Math.abs(z1-z2);}private ConveyorRoute(){}
}
