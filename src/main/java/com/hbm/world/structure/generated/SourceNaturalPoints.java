package com.hbm.world.structure.generated;

import com.hbm.bomb.conventional.LandmineBlockEntity;
import com.hbm.bomb.conventional.LandmineContent;
import com.hbm.config.GeneralModes;
import com.hbm.machine.pinkcloud.PinkCloudBroadcasterBlock;
import com.hbm.machine.pinkcloud.PinkCloudBroadcasterContent;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.World;
import java.util.Random;
import java.util.Set;

/** Complete source small-object branches, executed in their original relative position within the native plan. */
public final class SourceNaturalPoints {
 public static void beforeJungle(World world,Random random,int i,int j,int broadcasterFrequency,int mineFrequency,boolean enableMines,Set<String> selected){
  // HbmWorldGen259..270: actual pink-cloud broadcaster, source random cardinal metadata2..5.
  if(broadcasterFrequency>0&&random.nextInt(broadcasterFrequency)==0){int x=i+random.nextInt(16),z=j+random.nextInt(16),y=world.getHeightValue(x,z);if(world.getBlock(x,y-1,z).canPlaceTorchOnTop(world,x,y-1,z)){int meta=random.nextInt(4)+2;var state=PinkCloudBroadcasterContent.BLOCK.get().defaultBlockState().setValue(PinkCloudBroadcasterBlock.META,meta);world.setBlock(x,y,z,Block.observed(state),meta,2);selected.add("old:Broadcaster");}}
  // HbmWorldGen272..290: first supported position from y+2 down to y, with source +8 population offsets.
  if(mineFrequency>0&&enableMines&&random.nextInt(mineFrequency)==0){int x=i+random.nextInt(16)+8,z=j+random.nextInt(16)+8,y=world.getHeightValue(x,z);for(int g=y+2;g>=y;g--){if(world.getBlock(x,g-1,z).canPlaceTorchOnTop(world,x,g-1,z)){placeMine(world,x,g,z,false);selected.add("old:LandmineAP");break;}}}
  SourceOldLantern.generate(world,random,i,j,selected); // HbmWorldGen292..315, before source Bosnia mine.
  // HbmWorldGen318..330: source 528 Bosnia branch is deliberately independent of enableMines.
  if(GeneralModes.bosnia()&&random.nextInt(16)==0){int x=i+random.nextInt(16),z=j+random.nextInt(16),y=world.getHeightValue(x,z);if(world.getBlock(x,y-1,z).canPlaceTorchOnTop(world,x,y-1,z)){placeMine(world,x,y,z,true);selected.add("old:BosniaMineHE");}}
 }
 private static void placeMine(World world,int x,int y,int z,boolean he){var state=(he?LandmineContent.HE.get():LandmineContent.AP.get()).defaultBlockState();world.setBlock(x,y,z,Block.observed(state),0,3);if(world.getTileEntity(x,y,z) instanceof LandmineBlockEntity mine)mine.waitingForPlayer=true;}
 private SourceNaturalPoints(){}
}
