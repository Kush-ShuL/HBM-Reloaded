package com.hbm.block.junglemechanics;
import com.hbm.block.junglemechanics.JungleTrapBlock.Trap;
import com.hbm.bomb.conventional.RubbleProjectile;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Source cached detector/direction are transient: original TileEntityTrappedBrick saves no custom fields. */
public final class JungleTrapBlockEntity extends BlockEntity {
 private AABB detector;private Direction direction;
 public JungleTrapBlockEntity(BlockPos pos,BlockState state){super(JungleMechanicsContent.TRAP_ENTITY.get(),pos,state);}
 static void tick(Level level,BlockPos pos,BlockState state,JungleTrapBlockEntity entity){if(level.isClientSide)return;if(entity.detector==null)entity.setDetector();if(!level.getEntitiesOfClass(Player.class,entity.detector).isEmpty())entity.trigger();}
 private Trap trap(){return Trap.get(getBlockState().getValue(JungleMechanicsContent.META));}
 private void trigger(){var pos=worldPosition;int x=pos.getX(),y=pos.getY(),z=pos.getZ();switch(trap()){
  case FALLING_ROCKS->{for(int a=0;a<3;a++)for(int b=0;b<3;b++){var rubble=new RubbleProjectile(level,x-.5+a,y-.5,z-.5+b);rubble.setMetaBasedOnBlock(HbmContent.block("reinforced_stone").defaultBlockState());level.addFreshEntity(rubble);}}
  case ARROW,FLAMING_ARROW->{int dx=direction==null?0:direction.getStepX(),dz=direction==null?0:direction.getStepZ();var arrow=new Arrow(EntityType.ARROW,level);arrow.setPos(x+.5+dx,y+.5,z+.5+dz);arrow.setDeltaMovement(dx,0,dz);if(trap()==Trap.FLAMING_ARROW)arrow.igniteForSeconds(60);level.addFreshEntity(arrow);}
  case PILLAR->{for(int i=0;i<3;i++)level.setBlock(pos.below(1+i),HbmContent.block("concrete_pillar").defaultBlockState(),3);}
  case POISON_DART->{} // Original TBI: still clicks and consumes the trap, not an invented poison projectile.
  case ZOMBIE->{var zombie=new Zombie(EntityType.ZOMBIE,level);zombie.setPos(x+.5,y+1,z+.5);var weapon=switch(level.random.nextInt(3)){case 0->HbmContent.item("chernobylsign");case 1->JungleMechanicsContent.COBALT_SWORD.get();default->JungleMechanicsContent.CMB_HOE.get();};zombie.setItemSlot(EquipmentSlot.MAINHAND,new net.minecraft.world.item.ItemStack(weapon));zombie.setDropChance(EquipmentSlot.MAINHAND,1);level.addFreshEntity(zombie);}
  case SPIDERS->{for(int i=0;i<3;i++){var spider=new CaveSpider(EntityType.CAVE_SPIDER,level);spider.setPos(x+.5,y-1,z+.5);level.addFreshEntity(spider);}}
  default->{}
 }JungleTrapBlock.finish(level,pos);}
 private void setDetector(){double x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();switch(trap()){
  case FALLING_ROCKS,SPIDERS->detector=new AABB(x-1,y-3,z-1,x+2,y,z+2);
  case PILLAR->detector=new AABB(x+.2,y-3,z+.2,x+.8,y,z+.8);
  case ARROW,FLAMING_ARROW,POISON_DART->setDirectionalDetector();
  case ZOMBIE->detector=new AABB(x-1,y+1,z-1,x+2,y+2,z+2);
  default->detector=new AABB(x,y,z,x+1,y+1,z+1);
 }}
 private void setDirectionalDetector(){var directions=new ArrayList<>(List.of(Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST));Collections.shuffle(directions);double x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();for(var candidate:directions){if(!level.getBlockState(worldPosition.relative(candidate)).is(Blocks.AIR))continue;double minX=x+.4,minY=y+.4,minZ=z+.4,maxX=x+.6,maxY=y+.6,maxZ=z+.6;if(candidate.getStepX()>0)maxX+=3;else if(candidate.getStepX()<0)minX-=3;if(candidate.getStepZ()>0)maxZ+=3;else if(candidate.getStepZ()<0)minZ-=3;detector=new AABB(minX,minY,minZ,maxX,maxY,maxZ);direction=candidate;return;}detector=new AABB(x,y,z,x+1,y+1,z+1);}
}
