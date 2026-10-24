package com.hbm.machine.reactor.rbmk.panels;
import com.hbm.multiblock.MuffledMachine;
import io.netty.buffer.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Actual LoadedBase wire/NBT split for the genuine ROR panels; no generic processing or duplicate radio bus. */
public abstract class RbmkRadioPanelEntity extends BlockEntity implements MuffledMachine{
    private boolean muffled;
    protected RbmkRadioPanelEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    protected abstract void serverTick(ServerLevel level);protected void clientTick(){}
    protected int syncRadius(){return 50;}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkRadioPanelEntity panel){if(level instanceof ServerLevel world){panel.serverTick(world);var packet=ClientboundBlockEntityDataPacket.create(panel);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(world.getServer(),packet)){for(var player:world.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<panel.syncRadius()*panel.syncRadius())player.connection.send(packet);dispatchScope.commit();}}else panel.clientTick();}
    public abstract void receiveControl(Player player,CompoundTag data);public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<225;}
    protected void writeWire(ByteBuf buffer){buffer.writeBoolean(muffled);}protected void readWire(ByteBuf buffer){muffled=buffer.readBoolean();}
    protected void savePanel(CompoundTag tag){tag.putBoolean("muffled",muffled);}protected void loadPanel(CompoundTag tag){muffled=tag.getBoolean("muffled");}
    @Override protected final void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);savePanel(tag);}@Override protected final void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadPanel(tag);}
    @Override public final CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();var buffer=Unpooled.buffer();try{writeWire(buffer);byte[] bytes=new byte[buffer.readableBytes()];buffer.readBytes(bytes);tag.putByteArray("wire",bytes);}finally{buffer.release();}return tag;}
    @Override public final void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){var bytes=tag.getByteArray("wire");if(bytes.length==0)return;var buffer=Unpooled.wrappedBuffer(bytes);try{readWire(buffer);}finally{buffer.release();}}
    @Override public final ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public final void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    public static void string(ByteBuf buffer,String value){if(value==null){buffer.writeInt(-1);return;}var bytes=value.getBytes(java.nio.charset.StandardCharsets.UTF_8);buffer.writeInt(bytes.length);buffer.writeBytes(bytes);}
    public static String string(ByteBuf buffer){int length=buffer.readInt();if(length<0)return null;byte[] bytes=new byte[length];buffer.readBytes(bytes);return new String(bytes,java.nio.charset.StandardCharsets.UTF_8);}
}
