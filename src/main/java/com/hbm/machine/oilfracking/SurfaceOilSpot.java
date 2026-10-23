package com.hbm.machine.oilfracking;
import com.hbm.block.oilplant.*;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
/** True source OilSpot algorithm, including vegetation death, protected willow stages and decaying leaves. */
public final class SurfaceOilSpot {
    private static BlockState state(String name){var id=ResourceLocation.parse("hbm:"+name);if(!BuiltInRegistries.BLOCK.containsKey(id))throw new IllegalStateException("Missing real OilSpot block "+id);return BuiltInRegistries.BLOCK.get(id).defaultBlockState();}
    private static BlockState dead(int meta){return state("plant_dead").setValue(LegacyOreBlock.LEGACY_META,meta);}
    /** Legacy heightmap measures the first block above a nonzero-light-opacity surface, not above decorative plants. */
    private static int surface(ServerLevel level,int x,int z){int top=level.getHeight(Heightmap.Types.WORLD_SURFACE,x,z);for(int y=top-1;y>=level.getMinBuildHeight();y--){var pos=new BlockPos(x,y,z);if(level.getBlockState(pos).getLightBlock(level,pos)>0)return y+1;}return level.getMinBuildHeight();}
    private static int metadata(BlockState s){if(s.hasProperty(LegacyOreBlock.LEGACY_META))return s.getValue(LegacyOreBlock.LEGACY_META);return s.is(Blocks.FERN)?2:0;}
    /** NeoForge1.21 removed IPlantable; retain its exact source non-Bush implementors/counterparts. */
    private static boolean legacyPlantable(BlockState state){return state.getBlock() instanceof CactusBlock||state.getBlock() instanceof SugarCaneBlock||BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals("hbm:mush");}
    public static void generate(ServerLevel level,int x,int z,int width,int count,boolean willows){for(int i=0;i<count;i++){int rx=x+(int)(level.random.nextGaussian()*width),rz=z+(int)(level.random.nextGaussian()*width),height=surface(level,rx,rz);for(int y=height;y>height-4&&y>=level.getMinBuildHeight();y--){var pos=new BlockPos(rx,y,rz);var below=level.getBlockState(pos.below());var ground=level.getBlockState(pos);int meta=metadata(ground);if(ground.is(OilBotanyContent.FLOWER.get())&&(meta==4||meta==5))continue;if(ground.is(OilBotanyContent.TALL.get())&&(meta==1||meta==2||meta==3))continue;
                if(below.isSolidRender(level,pos.below())&&!ground.is(state("plant_dead").getBlock())){var block=ground.getBlock();if(block instanceof TallGrassBlock){if(level.random.nextInt(10)==0)level.setBlock(pos,dead(metadata(level.getBlockState(pos.above()))==2?4:1),3);else level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}else if(block instanceof FlowerBlock)level.setBlock(pos,dead(2),3);else if(block instanceof DoublePlantBlock||block instanceof OilTallPlantBlock)level.setBlock(pos,dead(3),3);else if(block instanceof BushBlock||block instanceof OilFlowerBlock||legacyPlantable(ground))level.setBlock(pos,dead(0),3);}
                if(ground.is(Blocks.GRASS_BLOCK)||ground.is(Blocks.DIRT)||ground.is(Blocks.COARSE_DIRT)||ground.is(Blocks.PODZOL)){level.setBlock(pos,state(level.random.nextInt(10)==0?"dirt_oily":"dirt_dead"),3);if(willows&&level.random.nextInt(50)==0&&level.getBlockState(pos.above()).canBeReplaced()&&OilFlowerBlock.soil(level.getBlockState(pos)))level.setBlock(pos.above(),OilBotanyContent.FLOWER.get().defaultBlockState().setValue(OilFlowerBlock.META,4),3);break;}
                if(ground.is(Blocks.SAND)||ground.is(Blocks.RED_SAND)||ground.is(state("ore_oil_sand").getBlock())){level.setBlock(pos,state(ground.is(Blocks.RED_SAND)||metadata(ground)==1?"sand_dirty_red":"sand_dirty"),3);break;}
                if(ground.is(Blocks.STONE)||ground.is(Blocks.DEEPSLATE)){level.setBlock(pos,state("stone_cracked"),3);break;}
                if(LegacyMaterials.of(ground).name().equals("leaves")&&ground.hasProperty(LeavesBlock.PERSISTENT)&&ground.hasProperty(LeavesBlock.DISTANCE)&&!ground.getValue(LeavesBlock.PERSISTENT)&&ground.getValue(LeavesBlock.DISTANCE)==7){level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);break;}
            }}}
    private SurfaceOilSpot(){}
}
