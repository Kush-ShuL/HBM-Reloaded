package com.hbm.world.structure.generated.compat;
import com.hbm.world.structure.LegacyStructureStates;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
/** Lazy strict source handles. Placement always resolves an existing real native block and raw metadata. */
public class Block {
    private static final Map<String,Block> SOURCES=new HashMap<>();
    public final String sourceId;private final BlockState observed;
    protected Block(String sourceId,BlockState observed){this.sourceId=sourceId;this.observed=observed;}
    public static synchronized Block source(String id){return SOURCES.computeIfAbsent(id,key->key.endsWith("_stairs")?new BlockStairs(key):new Block(key,null));}
    public static Block observed(BlockState state){return new Block(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString(),state);}
    public BlockState state(int metadata){return observed!=null?observed:LegacyStructureStates.resolve(sourceId,metadata,0);}
    public boolean sameAs(Block other){return state(0).getBlock()==other.state(0).getBlock();}
    public boolean canPlaceTorchOnTop(World world,int x,int y,int z){var state=world.stateAt(x,y,z);return state.isFaceSturdy(net.minecraft.world.level.EmptyBlockGetter.INSTANCE,new BlockPos(x,y,z),net.minecraft.core.Direction.UP);}
    public Material getMaterial(){var state=state(0);if(state.isAir())return Material.air;if(state.is(BlockTags.LEAVES))return Material.leaves;if(state.liquid())return Material.water;if(SourceRockMaterials.isRock(state))return Material.rock;if(state.getBlock() instanceof net.minecraft.world.level.block.BushBlock)return Material.plants;return state.blocksMotion()?Material.solid:Material.other;}
    public boolean isAir(World world,int x,int y,int z){return world.stateAt(x,y,z).isAir();}
    public boolean isFoliage(World world,int x,int y,int z){return world.stateAt(x,y,z).is(BlockTags.LEAVES);}
    public boolean isNormalCube(){return state(0).isSolidRender(EmptyBlockGetter.INSTANCE,BlockPos.ZERO);}
    public boolean isOpaqueCube(){return state(0).canOcclude();}
}
