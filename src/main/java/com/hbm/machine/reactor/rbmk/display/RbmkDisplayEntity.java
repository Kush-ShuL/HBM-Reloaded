package com.hbm.machine.reactor.rbmk.display;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.console.*;
import com.hbm.machine.reactor.rbmk.control.RbmkControlEntity;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import static com.hbm.machine.reactor.rbmk.console.RbmkConsoleData.*;
/** Genuine7x7 physical live display,10tick scan,50block packet range and linker/screwdriver interface. */
public final class RbmkDisplayEntity extends BlockEntity implements RbmkTargetConsole,MuffledMachine{
    private BlockPos target=BlockPos.ZERO;private byte rotation;private boolean muffled;public final Column[] columns=new Column[49];
    public RbmkDisplayEntity(BlockPos pos,BlockState state){super(RbmkDisplayContent.ENTITY.get(),pos,state);}
    @Override public void setTarget(BlockPos pos){target=pos.immutable();setChanged();}public void rotate(){rotation=(byte)((rotation+1)%4);setChanged();}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    public int xFromIndex(int index){int x=index%7-3,z=index/7-3;return switch(rotation){case 1->-z;case 2->-x;case 3->z;default->x;};}public int zFromIndex(int index){int x=index%7-3,z=index/7-3;return switch(rotation){case 1->x;case 2->-z;case 3->-x;default->z;};}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkDisplayEntity display){if(level instanceof ServerLevel world&&world.getGameTime()%10==0){display.rescan(world);var packet=ClientboundBlockEntityDataPacket.create(display);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(world.getServer(),packet)){for(var player:world.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500)player.connection.send(packet);dispatchScope.commit();}}}
    private void rescan(ServerLevel world){for(int index=0;index<49;index++){var pos=target.offset(xFromIndex(index),0,zFromIndex(index));if(!world.hasChunkAt(pos)||!(world.getBlockEntity(pos) instanceof RbmkColumnEntity column)){columns[index]=null;continue;}var data=column.consoleData();if(data==null)data=new CompoundTag();data.putDouble("heat",column.heat);data.putDouble("maxHeat",column.maxHeat());data.putByte("indicator",(byte)column.craneIndicator);if(column instanceof RbmkControlEntity control&&!control.automatic())data.putByte("color",(byte)(control.color==null?-1:control.color.ordinal()));columns[index]=new Column(ColumnType.valueOf(column.consoleType()),data);}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putInt("tX",target.getX());tag.putInt("tY",target.getY());tag.putInt("tZ",target.getZ());tag.putByte("rotation",rotation);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");target=new BlockPos(tag.getInt("tX"),tag.getInt("tY"),tag.getInt("tZ"));rotation=tag.getByte("rotation");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",muffled);for(int i=0;i<49;i++)if(columns[i]!=null){var data=new CompoundTag();data.putByte("type",(byte)columns[i].type().ordinal());data.put("data",columns[i].data());tag.put("column"+i,data);}return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");for(int i=0;i<49;i++){var data=tag.getCompound("column"+i);columns[i]=tag.contains("column"+i)?new Column(ColumnType.values()[net.minecraft.util.Mth.clamp(data.getByte("type"),0,13)],data.getCompound("data")):null;}}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
