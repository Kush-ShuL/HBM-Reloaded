package com.hbm.machine.electricpress;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Shared storage/HE/serialization only; the two distinct source press state machines are implemented by their real subclasses. */
public abstract class PoweredPressEntity extends BlockEntity implements WorldlyContainer,EnergyReceiver,SidedEnergyConnector,MachineView,NamedMultiblock,MuffledMachine {
    public static final BigInteger CAPACITY=BigInteger.valueOf(50000);
    public final NonNullList<ItemStack> slots;
    protected BigInteger power=BigInteger.ZERO;
    public boolean muffled;
    private Component customName;
    private PowerNode node;
    protected PoweredPressEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,int size){super(type,pos,state);slots=NonNullList.withSize(size,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&(this instanceof ElectricPressEntity||DummyableBlock.isCore(state));}
    protected abstract String sourceName();
    protected abstract void serverTick(ServerLevel level);
    protected abstract void clientTick();
    protected abstract void savePress(CompoundTag tag);
    protected abstract void loadPress(CompoundTag tag);
    protected abstract void writeSync(CompoundTag tag,HolderLookup.Provider lookup);
    protected abstract void readSync(CompoundTag tag,HolderLookup.Provider lookup);
    public static void tick(Level level,BlockPos pos,BlockState state,PoweredPressEntity machine){if(level instanceof ServerLevel server){machine.updatePower(server);machine.serverTick(server);var packet=machine.getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<2500)player.connection.send(packet);}else machine.clientTick();}
    private void updatePower(ServerLevel server){if(node==null||node.isExpired()){node=new PowerNode(worldPosition).setConnections(Arrays.stream(Direction.values()).map(side->new NodeConnection(worldPosition.relative(side),side)).toArray(NodeConnection[]::new));HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addReceiver(this);}
    private void disconnect(){if(node!=null&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(Direction side){return side!=null;}
    @Override public long getPower(){return power.longValueExact();}
    @Override public long getMaxPower(){return 50000;}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public BigInteger getMaxPowerExact(){return CAPACITY;}
    @Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return 50000;}
    @Override public void setCustomName(Component value){customName=value;setChanged();}
    public Component getDisplayName(){return customName==null?Component.translatable(sourceName()):customName;}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public int getContainerSize(){return slots.size();}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int amount){var stack=ContainerHelper.removeItem(slots,slot,amount);if(!stack.isEmpty())setChanged();return stack;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    private void writePower(CompoundTag tag){tag.putString("power",power.toString());tag.putBoolean("muffled",muffled);}
    private void readPower(CompoundTag tag){power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));muffled=tag.getBoolean("muffled");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);writePower(tag);savePress(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,slots,lookup);readPower(tag);loadPress(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writePower(tag);writeSync(tag,lookup);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readPower(tag);readSync(tag,lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
