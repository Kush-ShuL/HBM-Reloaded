package com.hbm.client.conveyor;

import api.hbm.conveyor.*;
import com.hbm.conveyor.belt.ConveyorBeltBlock;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/** Literal RenderConveyor/Lift/Chute bounds and RenderBlocks per-face UV rotations. */
final class ConveyorGeometry {
 record Neighbors(boolean bottom,boolean top,boolean floor,boolean belowOpen,int lateral){
  static Neighbors world(BlockGetter world,BlockPos pos){boolean floor=pos.getY()<=world.getMinBuildHeight(),bottom=!belt(world.getBlockState(pos.below()));boolean top=pos.getY()<world.getMaxBuildHeight()-1&&!belt(world.getBlockState(pos.above()))&&!bottom&&!enter(world.getBlockState(pos.above()));int sides=0;for(Direction d:Direction.Plane.HORIZONTAL)if(belt(world.getBlockState(pos.relative(d))))sides|=1<<d.get3DDataValue();return new Neighbors(bottom,top,floor,belt(world.getBlockState(pos.below()))||enter(world.getBlockState(pos.below())),sides);}
  static Neighbors preview(Map<BlockPos,BlockState> cells,BlockPos pos){var air=net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();boolean bottom=!belt(cells.getOrDefault(pos.below(),air));boolean top=!belt(cells.getOrDefault(pos.above(),air))&&!bottom&&!enter(cells.getOrDefault(pos.above(),air));int sides=0;for(Direction d:Direction.Plane.HORIZONTAL)if(belt(cells.getOrDefault(pos.relative(d),air)))sides|=1<<d.get3DDataValue();return new Neighbors(bottom,top,false,belt(cells.getOrDefault(pos.below(),air))||enter(cells.getOrDefault(pos.below(),air)),sides);}
  boolean side(int side){return (lateral&(1<<side))!=0;}private static boolean belt(BlockState s){return s.getBlock() instanceof IConveyorBelt;}private static boolean enter(BlockState s){return s.getBlock() instanceof IEnterableBlock;}
 }
 static final Neighbors INVENTORY=new Neighbors(true,false,false,false,0);
 private final List<BakedQuad> out=new ArrayList<>();private final Function<String,TextureAtlasSprite> sprites;private final ConveyorBeltBlock.Kind kind;private final int meta;private final float yOffset;private final int[] rotations=new int[6];private String override;
 private ConveyorGeometry(Function<String,TextureAtlasSprite> sprites,ConveyorBeltBlock.Kind kind,int meta,boolean inventory){this.sprites=sprites;this.kind=kind;this.meta=meta;yOffset=inventory&&kind!=ConveyorBeltBlock.Kind.CHUTE?.25F:0;}
 static List<BakedQuad> build(Function<String,TextureAtlasSprite> sprites,ConveyorBeltBlock.Kind kind,int meta,Neighbors n,boolean inventory){var g=new ConveyorGeometry(sprites,kind,inventory?2:meta,inventory);if(kind==ConveyorBeltBlock.Kind.LIFT){if(!inventory)g.lift(n);}else if(kind==ConveyorBeltBlock.Kind.CHUTE)g.chute(n,inventory);else{g.rotate(g.meta,g.meta>5);g.box(0,0,0,1,.25,1);}return List.copyOf(g.out);}
 private String base(){return switch(kind){case EXPRESS->"conveyor_express";case DOUBLE->"conveyor_double";case TRIPLE->"conveyor_triple";default->"conveyor";};}
 private String texture(Direction side){if(override!=null)return override;int path=ConveyorBeltBlock.path(meta);if(path>0){if(side.get3DDataValue()>1)return "conveyor_side";return base()+(path==1?"_curve_left":"_curve_right");}if((meta==2||meta==3)&&(side==Direction.WEST||side==Direction.EAST)||(meta==4||meta==5)&&(side==Direction.NORTH||side==Direction.SOUTH))return "conveyor_side";return base();}
 // RenderBlocks names are historical: East=Z-, West=Z+, North=X-, South=X+.
 private void rotate(int m,boolean bent){Arrays.fill(rotations,0);if(m>9)m-=8;if(m>5)m-=4;switch(m){case 2->{rotations[1]=3;if(!bent)rotations[3]=3;}case 3->{rotations[0]=3;if(!bent)rotations[2]=3;}case 4->{rotations[0]=rotations[1]=1;if(!bent)rotations[5]=3;}case 5->{rotations[0]=rotations[1]=2;if(!bent)rotations[4]=3;}}}
 private void reset(){Arrays.fill(rotations,0);}
 private void box(double x0,double y0,double z0,double x1,double y1,double z1){for(Direction side:Direction.values())quad(side,(float)x0,(float)y0,(float)z0,(float)x1,(float)y1,(float)z1);}
 private void quad(Direction side,float x0,float y0,float z0,float x1,float y1,float z1){float[][] points=switch(side){case DOWN->new float[][]{{x0,y0,z1},{x1,y0,z1},{x0,y0,z0},{x1,y0,z0}};case UP->new float[][]{{x0,y1,z0},{x1,y1,z0},{x0,y1,z1},{x1,y1,z1}};case NORTH->new float[][]{{x1,y1,z0},{x0,y1,z0},{x1,y0,z0},{x0,y0,z0}};case SOUTH->new float[][]{{x0,y1,z1},{x1,y1,z1},{x0,y0,z1},{x1,y0,z1}};case WEST->new float[][]{{x0,y1,z0},{x0,y1,z1},{x0,y0,z0},{x0,y0,z1}};case EAST->new float[][]{{x1,y1,z1},{x1,y1,z0},{x1,y0,z1},{x1,y0,z0}};};int[] data=new int[32],order={0,2,3,1};var sprite=sprites.apply(texture(side));int normal=(side.getStepX()*127&255)|((side.getStepY()*127&255)<<8)|((side.getStepZ()*127&255)<<16);for(int i=0;i<4;i++){float[] p=points[order[i]],uv=com.hbm.block.pipe.client.LegacyBoxUvs.project(side,p[0],p[1],p[2],x0,y0,z0,x1,y1,z1,rotations[side.get3DDataValue()]);int j=i*8;data[j]=Float.floatToRawIntBits(p[0]);data[j+1]=Float.floatToRawIntBits(p[1]+yOffset);data[j+2]=Float.floatToRawIntBits(p[2]);data[j+3]=-1;data[j+4]=Float.floatToRawIntBits(sprite.getU(uv[0]));data[j+5]=Float.floatToRawIntBits(sprite.getV(uv[1]));data[j+7]=normal;}out.add(new BakedQuad(data,-1,side,sprite,true));}
 private void lift(Neighbors n){if(!n.floor&&n.bottom){if(meta!=5){rotations[0]=rotations[1]=1;box(0,0,.25,.25,.25,.75);}if(meta!=4){rotations[0]=rotations[1]=2;box(.75,0,.25,1,.25,.75);}if(meta!=3){rotations[1]=3;rotations[0]=0;box(.25,0,0,.75,.25,.25);}if(meta!=2){rotations[1]=0;rotations[0]=3;box(.25,0,.75,.75,.25,1);}reset();}
  override="concrete_smooth";
  if(!n.top){switch(meta){case 2->{box(0,0,0,.25,1,.25);box(.75,0,0,1,1,.25);box(0,0,.75,1,1,1);override=base();rotations[1]=3;box(.25,0,.625,.75,1,.75);}case 3->{box(0,0,0,1,1,.25);box(0,0,.75,.25,1,1);box(.75,0,.75,1,1,1);override=base();rotations[1]=0;box(.25,0,.25,.75,1,.375);}case 4->{box(0,0,0,.25,1,.25);box(0,0,.75,.25,1,1);box(.75,0,0,1,1,1);override=base();rotations[1]=1;box(.625,0,.25,.75,1,.75);}case 5->{box(.75,0,0,1,1,.25);box(.75,0,.75,1,1,1);box(0,0,0,.25,1,1);override=base();rotations[1]=2;box(.25,0,.25,.375,1,.75);}}}
  else if(meta==2||meta==3){box(0,0,0,.25,.5,1);box(.75,0,0,1,.5,1);override=null;if(meta==2){rotations[1]=rotations[3]=3;box(.25,0,.625,.75,.25,1);}else{rotations[2]=3;box(.25,0,0,.75,.25,.375);}}
  else if(meta==4||meta==5){box(0,0,0,1,.5,.25);box(0,0,.75,1,.5,1);override=null;if(meta==4){rotations[1]=1;rotations[5]=3;box(.625,0,.25,1,.25,.75);}else{rotations[1]=2;rotations[4]=3;box(0,0,.25,.375,.25,.75);}}
  reset();if(!n.floor&&n.bottom){override="minecraft:iron_block";box(.25+(meta==5?.125:0),0,.25+(meta==3?.125:0),.75-(meta==4?.125:0),.25,.75-(meta==2?.125:0));}override=null;
 }
 private void chute(Neighbors n,boolean inventory){boolean belt=false;if(inventory||!n.floor&&!n.belowOpen){rotate(meta,false);box(.25,0,0,.75,.25,1);box(0,0,.25,.25,.25,.75);box(.75,0,.25,1,.25,.75);reset();belt=true;}else if(!n.floor){if(n.side(4)){rotations[1]=1;box(0,0,.25,.125,.25,.75);}if(n.side(5)){rotations[1]=2;box(.875,0,.25,1,.25,.75);}if(n.side(2)){rotations[1]=3;box(.25,0,0,.75,.25,.125);}if(n.side(3)){rotations[1]=0;box(.25,0,.875,.75,.25,1);}reset();}
  override="concrete_smooth";box(0,0,0,.25,1,.25);box(.75,0,0,1,1,.25);box(0,0,.75,.25,1,1);box(.75,0,.75,1,1,1);override="steel_grate";
  if(inventory||!n.side(4)&&(!belt||meta!=5))box(.125,belt?.25:0,.25,.125,1,.75);
  if(inventory||!n.side(5)&&(!belt||meta!=4))box(.875,belt?.25:0,.25,.875,1,.75);
  if(inventory||!n.side(2)&&(!belt||meta!=3))box(.25,belt?.25:0,.125,.75,1,.125);
  if(!inventory&&!n.side(3)&&(!belt||meta!=2))box(.25,belt?.25:0,.875,.75,1,.875);
  override=null;
 }
}
