package com.hbm.network.cargo.request;
import com.hbm.network.rtty.RadioTorchBlock;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
public final class RequestWaypointEntity extends RequestNetworkEntity {
 public int height=5;public RequestWaypointEntity(BlockPos pos,BlockState state){super(CargoRequestContent.WAYPOINT_ENTITY.get(),pos,state);}@Override public BlockPos getCoord(){var dir=RadioTorchBlock.outward(getBlockState());return dir==null?worldPosition:worldPosition.relative(dir,height);}public void addHeight(int delta){height=net.minecraft.util.Mth.clamp(height+delta,1,15);setChanged();}@Override public RequestNetwork.PathNode createNode(BlockPos pos){return new RequestNetwork.PathNode(pos,reachableNodes);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);height=tag.getInt("height");}@Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("height",height);}
}
