package com.hbm.block;
import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
/** Shared original1.7 normal-cube contract, independent of modern opacity/collision-only shortcuts. */
public final class LegacyBlockTraits {
    public static boolean isNormalCube(BlockGetter level,BlockPos pos,BlockState state){
        // 1.7.10 Block.isNormalCube(): material.blocksMovement()
        // && renderAsNormalBlock() && !canProvidePower(). Neither opacity nor
        // collision alone is the source predicate (notably leaves and ice).
        return LegacyMaterials.of(state).blocksMovement()&&!state.isSignalSource()&&renderAsNormalBlock(level,pos,state);
    }
    /** Source block renderer predicate alone, also used by actual authoring disguise tools. */
    public static boolean renderAsNormalBlock(BlockGetter level,BlockPos pos,BlockState state){
        var block=state.getBlock();
        if(block instanceof LeavesBlock||block instanceof IceBlock||state.is(Blocks.PACKED_ICE)
                ||state.is(Blocks.GLOWSTONE)||state.is(Blocks.SOUL_SAND)
                ||state.is(Blocks.SPAWNER)||state.is(Blocks.END_PORTAL_FRAME))return true;
        // These source classes explicitly return false from renderAsNormalBlock,
        // even when a modern state exposes a complete solid/redstone face.
        if(state.is(Blocks.GLASS)||block instanceof StainedGlassBlock||block instanceof BeaconBlock
                ||block instanceof PistonBaseBlock||block instanceof PistonHeadBlock
                ||block instanceof MovingPistonBlock||block instanceof SnowLayerBlock)return false;
        if(block instanceof SlabBlock)return state.getValue(SlabBlock.TYPE)==SlabType.DOUBLE;
        // Partial source models (including WasteLeaves and our formations) keep
        // their real non-solid contract; ordinary host rock remains a cube.
        return state.isRedstoneConductor(level,pos);
    }
    private LegacyBlockTraits(){}
}
