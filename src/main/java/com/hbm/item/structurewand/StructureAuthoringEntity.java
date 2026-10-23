package com.hbm.item.structurewand;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class StructureAuthoringEntity extends BlockEntity {
    public String name="";public int sizeX=1,sizeY=1,sizeZ=1;public Set<StructureBlacklist.Key> blacklist=new HashSet<>();
    public StructureAuthoringEntity(BlockPos pos,BlockState state){super(StructureWandContent.ENTITY.get(),pos,state);}
    public CompoundTag settings(){var tag=new CompoundTag();tag.putString("name",name);tag.putInt("sizeX",sizeX);tag.putInt("sizeY",sizeY);tag.putInt("sizeZ",sizeZ);StructureBlacklist.write(tag,blacklist);return tag;}
    public void readSettings(CompoundTag tag){name=tag.getString("name");sizeX=tag.getInt("sizeX");sizeY=tag.getInt("sizeY");sizeZ=tag.getInt("sizeZ");blacklist=StructureBlacklist.read(tag);}
    public void toggleBlock(Block block,ItemStack held){
        var state=block.defaultBlockState();var property=block.getStateDefinition().getProperty("legacy_meta");
        // Still-unflattened source blocks expose their actual property; authoring itself stores native states only.
        int sourceMeta=held.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0);
        if(property instanceof net.minecraft.world.level.block.state.properties.IntegerProperty numeric&&numeric.getPossibleValues().contains(sourceMeta))state=state.setValue(numeric,sourceMeta);
        var itemState=held.get(DataComponents.BLOCK_STATE);if(itemState!=null)state=itemState.apply(state);
        var key=StructureBlacklist.capture(state);if(!blacklist.remove(key))blacklist.add(key);setChanged();
    }
    public static void tick(Level level,BlockPos pos,BlockState state,StructureAuthoringEntity tile){if(level instanceof ServerLevel server){var packet=tile.getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=256D*256D)player.connection.send(packet);}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.merge(settings());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readSettings(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);}
}
