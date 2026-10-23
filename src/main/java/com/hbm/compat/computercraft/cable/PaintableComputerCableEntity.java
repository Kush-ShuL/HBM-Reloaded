package com.hbm.compat.computercraft.cable;
import com.hbm.api.machine.ICopiable;
import java.util.function.Function;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.*;
/** Original facade/colour/settings state; native CC graph itself lives only in the optional adapter. */
public final class PaintableComputerCableEntity extends BlockEntity implements ICopiable,com.hbm.multiblock.MuffledMachine {
    public static final ModelProperty<BlockState> PAINT=new ModelProperty<>();
    public static final ModelProperty<Integer> COLOR=new ModelProperty<>();
    static Function<PaintableComputerCableEntity,PaintableCableNetwork> NETWORK_FACTORY=ignored->null;
    private PaintableCableNetwork network;
    private boolean muffled,tilted;
    private BlockState paint;private int paintMeta;private SourceCableColors color=SourceCableColors.LIGHTGRAY;
    public PaintableComputerCableEntity(BlockPos position,BlockState state){super(PaintableComputerCableContent.ENTITY.get(),position,state);}
    public BlockState paint(){return paint;}
    public int color(){return color.rgb;}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;changed();}
    PaintableCableNetwork network(){if(network==null&&!isRemoved()&&level instanceof ServerLevel)network=NETWORK_FACTORY.apply(this);return network;}
    public static void tick(Level level,BlockPos position,BlockState state,PaintableComputerCableEntity cable){if(level.isClientSide||cable.isRemoved())return;var connection=cable.network();if(connection!=null)connection.tick();}
    public void connectionsChanged(){if(network!=null)network.changed();if(level!=null&&!level.isClientSide)invalidateCapabilities();}
    public void setColor(SourceCableColors color){if(this.color!=color){this.color=color;connectionsChanged();}changed();}
    public void setPaint(BlockState paint,int sourceMetadata){this.paint=paint;paintMeta=sourceMetadata;changed();}
    private void changed(){setChanged();requestModelDataUpdate();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();if(paint!=null){tag.put("paintblock",NbtUtils.writeBlockState(paint));tag.putInt("paintmeta",paintMeta);}return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){if(tag.contains("paintblock",Tag.TAG_COMPOUND)){paint=NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK),tag.getCompound("paintblock"));paintMeta=tag.getInt("paintmeta");setColor(SourceCableColors.fromInt(tag.getInt("dyeColor")));}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(paint!=null)tag.put("block",NbtUtils.writeBlockState(paint));tag.putInt("meta",paintMeta);tag.putInt("dyeColor",color.rgb);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");paint=tag.contains("block",Tag.TAG_COMPOUND)?NbtUtils.readBlockState(lookup.lookupOrThrow(Registries.BLOCK),tag.getCompound("block")):null;paintMeta=tag.getInt("meta");color=SourceCableColors.fromInt(tag.getInt("dyeColor"));requestModelDataUpdate();connectionsChanged();}
    @Override public ModelData getModelData(){var builder=ModelData.builder().with(COLOR,color.rgb);if(paint!=null)builder.with(PAINT,paint);return builder.build();}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){super.onDataPacket(connection,packet,lookup);requestModelDataUpdate();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);}
    private void closeNetwork(){if(network!=null){network.close();network=null;}}
    @Override public void onChunkUnloaded(){closeNetwork();super.onChunkUnloaded();}
    @Override public void setRemoved(){closeNetwork();super.setRemoved();}
    @Override public void onLoad(){super.onLoad();connectionsChanged();}
}
