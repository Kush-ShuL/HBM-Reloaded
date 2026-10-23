package com.hbm.railway.track;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original non-ticking TileEntityRailSwitch and exact isSwitched persisted/synchronized flag. */
public final class RailSwitchEntity extends BlockEntity {
    public boolean isSwitched;
    public RailSwitchEntity(BlockPos pos,BlockState state){super(SourceRailContent.SWITCH_ENTITY.get(),pos,state);}
    public void toggle(){isSwitched=!isSwitched;setChanged();if(level!=null){requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putBoolean("isSwitched",isSwitched);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);isSwitched=tag.getBoolean("isSwitched");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveWithoutMetadata(registries);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider registries){loadAdditional(tag,registries);changedModel();}
    @Override public void onDataPacket(Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider registries){var tag=packet.getTag();if(tag!=null)loadAdditional(tag,registries);changedModel();}
    private void changedModel(){requestModelDataUpdate();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);}
}
