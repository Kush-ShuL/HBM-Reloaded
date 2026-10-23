package com.hbm.weapon.sedna.gun.chemthrower;
import com.hbm.api.machine.IRepairable;
import com.hbm.api.machine.IRepairable.EnumExtinguishType;
import com.hbm.block.masonry.*;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.hbm.fluid.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.radiation.HbmRadiation;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
/** Real source block impact: fire, machine extinguishing, fallout cleanup and seedslurry masonry restoration. */
public final class ChemicalWorldImpact {
 public static void hit(EntityChemical chemical,BlockPos pos){var world=chemical.level();var fluid=chemical.getFluidType();var style=chemical.getStyle();var radiation=fluid.getTrait(FluidTraits.VentRadiation.class);if(radiation!=null&&world instanceof ServerLevel server)HbmRadiation.incrementRad(server,pos.getX(),pos.getY(),pos.getZ(),radiation.getRadPerMB()*5);
  // Source repeats these exact6-neighbour fire loops twice; keep order and synchronous block updates.
  for(int pass=0;pass<2;pass++)if(style==ChemicalStyle.BURNING||style==ChemicalStyle.GASFLAME)for(var direction:Direction.values()){var at=pos.relative(direction);if(world.getBlockState(at).isAir())world.setBlockAndUpdate(at,(fluid==HbmFluids.BALEFIRE?Data.BALEFIRE:Blocks.FIRE).defaultBlockState());}
  if(chemical.isExtinguishing())for(var direction:Direction.values()){var at=pos.relative(direction);if(world.getBlockState(at).is(Blocks.FIRE))world.setBlockAndUpdate(at,Blocks.AIR.defaultBlockState());}
  EnumExtinguishType extinguish=fluid==HbmFluids.CARBONDIOXIDE?EnumExtinguishType.CO2:fluid==HbmFluids.WATER||fluid==HbmFluids.HEAVYWATER||fluid==HbmFluids.COOLANT?EnumExtinguishType.WATER:null;
  if(extinguish!=null){var state=world.getBlockState(pos);var core=state.getBlock()instanceof DummyableBlock dummy?dummy.findCore(world,pos):pos;if(core!=null&&world.getBlockEntity(core)instanceof IRepairable machine)machine.tryExtinguish(world,pos,extinguish);if(extinguish==EnumExtinguishType.WATER&&style==ChemicalStyle.LIQUID)for(int x=-2;x<=2;x++)for(int y=0;y<=1;y++)for(int z=-2;z<=2;z++){var at=pos.offset(x,y,z);if(world.getBlockState(at).is(Data.FALLOUT))world.setBlockAndUpdate(at,Blocks.AIR.defaultBlockState());}}
  if(fluid==HbmFluids.SEEDSLURRY)for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){var at=pos.offset(x,y,z);var state=world.getBlockState(at);var block=state.getBlock();if(block==Blocks.DIRT||block==Data.WASTE_EARTH||block==Data.DIRT_DEAD||block==Data.DIRT_OILY){var above=at.above();if(world.getMaxLocalRawBrightness(above)>=9&&com.hbm.block.light.floodlight.FloodlightOpacity.sourceOpacity(world,above)<=2)world.setBlockAndUpdate(at,Blocks.GRASS_BLOCK.defaultBlockState());}if(block==Blocks.COBBLESTONE)world.setBlockAndUpdate(at,Blocks.MOSSY_COBBLESTONE.defaultBlockState());if(block==Blocks.STONE_BRICKS)world.setBlockAndUpdate(at,Blocks.MOSSY_STONE_BRICKS.defaultBlockState());if(block==Data.WASTE_EARTH)world.setBlockAndUpdate(at,Blocks.GRASS_BLOCK.defaultBlockState());if(block==Data.CONCRETE)world.setBlockAndUpdate(at,Data.MOSSY.defaultBlockState());if(block==MasonryContent.BRICK.get()&&state.getValue(LegacyMultiSlabBlock.MATERIAL)==0)world.setBlockAndUpdate(at,state.setValue(LegacyMultiSlabBlock.MATERIAL,1));if(block==Data.CONCRETE_STAIRS)world.setBlockAndUpdate(at,Data.MOSSY_STAIRS.defaultBlockState().setValue(StairBlock.FACING,state.getValue(StairBlock.FACING)).setValue(StairBlock.HALF,state.getValue(StairBlock.HALF)));}
 }
 private static Block require(String name){var id=ResourceLocation.parse("hbm:"+name);if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalStateException("Missing actual chemthrower source block "+id);return BuiltInRegistries.BLOCK.get(id);}
 private static final class Data {static final Block BALEFIRE=require("balefire"),FALLOUT=require("fallout_block"),WASTE_EARTH=require("waste_earth"),DIRT_DEAD=require("dirt_dead"),DIRT_OILY=require("dirt_oily"),CONCRETE=require("brick_concrete"),MOSSY=require("brick_concrete_mossy"),CONCRETE_STAIRS=require("brick_concrete_stairs"),MOSSY_STAIRS=require("brick_concrete_mossy_stairs");}
 private ChemicalWorldImpact(){}
}
