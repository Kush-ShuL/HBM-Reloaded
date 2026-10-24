package com.hbm.conveyor.crane;

import com.hbm.api.machine.*;
import com.hbm.multiblock.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityCraneBase, sole side override/copy/persistence contract for all true crane subclasses. */
public abstract class CraneBaseEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,ICopiable,MuffledMachine {
    public interface Filter {int[] getFilterSlots();void nextMode(int slot);}
    public final ItemStack[] slots;
    private Direction outputOverride;private Component customName;private boolean muffled;
    protected CraneBaseEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,int count){super(type,pos,state);slots=new ItemStack[count];Arrays.fill(slots,ItemStack.EMPTY);}
    protected abstract String nameKey();protected abstract void serverTick(ServerLevel world);
    protected void saveCrane(CompoundTag tag){}protected void loadCrane(CompoundTag tag){}
    protected void saveNetwork(CompoundTag tag){saveCrane(tag);}protected void loadNetwork(CompoundTag tag){loadCrane(tag);}
    public static void tick(Level level,BlockPos pos,BlockState state,CraneBaseEntity crane){if(level instanceof ServerLevel server)crane.serverTick(server);}
    public Direction getInputSide(){return CraneBaseBlock.direction(getBlockState().getValue(CraneBaseBlock.META));}
    public Direction getOutputOverride(){return outputOverride;}
    public Direction getOutputSide(){return outputOverride!=null?outputOverride:opposite(getInputSide());}
    public static Direction opposite(Direction side){return side==null?null:side.getOpposite();}
    public static BlockPos relative(BlockPos pos,Direction side){return side==null?pos:pos.relative(side);}
    public void setOutputOverride(Direction direction){Direction oldSide=getOutputSide();if(oldSide==direction)direction=opposite(direction);outputOverride=direction;if(direction==getInputSide())setInput(oldSide);else onBlockChanged();}
    public void setInput(Direction direction){outputOverride=getOutputSide();Direction oldSide=getInputSide();if(oldSide==direction)direction=opposite(direction);boolean swap=direction==getOutputSide();if(level!=null)level.setBlock(worldPosition,getBlockState().setValue(CraneBaseBlock.META,direction==null?6:direction.get3DDataValue()),swap?4:3);if(swap)setOutputOverride(oldSide);}
    protected void onBlockChanged(){setChanged();if(level!=null){level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);level.updateNeighborsAt(worldPosition,getBlockState().getBlock());if(level.isClientSide)requestModelDataUpdate();}}
    protected void synchronize(int range){if(level instanceof ServerLevel server){var packet=ClientboundBlockEntityDataPacket.create(this,(entity,lookup)->{var tag=new CompoundTag();tag.putBoolean("CraneNetworkState",true);tag.putBoolean("muffled",muffled);saveNetwork(tag);return tag;});try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<range*range)player.connection.send(packet);dispatchScope.commit();}}}
    @Override public Component getDisplayName(){return customName==null?Component.translatable(nameKey()):customName;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public void writeClientSideData(net.minecraft.world.inventory.AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    public double getUseRange(){return 12D;}
    @Override public boolean stillValid(Player player){double range=getUseRange();return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=range*range;}
    @Override public int getContainerSize(){return slots.length;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){if(slots[slot].isEmpty())return ItemStack.EMPTY;var result=slots[slot].split(count);if(slots[slot].isEmpty())slots[slot]=ItemStack.EMPTY;setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var old=slots[slot];slots[slot]=ItemStack.EMPTY;return old;}
    @Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=NonNullList.withSize(slots.length,ItemStack.EMPTY);for(int i=0;i<slots.length;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(tag,items,lookup);tag.putByte("CraneOutputOverride",(byte)(outputOverride==null?6:outputOverride.get3DDataValue()));tag.putBoolean("muffled",muffled);if(customName!=null){tag.putString("name",customName.getString());tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}saveCrane(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var items=NonNullList.withSize(slots.length,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,items,lookup);for(int i=0;i<slots.length;i++)slots[i]=items.get(i);if(tag.contains("CraneOutputOverride",Tag.TAG_BYTE))outputOverride=CraneBaseBlock.direction(tag.getByte("CraneOutputOverride"));muffled=tag.getBoolean("muffled");customName=tag.contains("CustomName")?Component.Serializer.fromJson(tag.getString("CustomName"),lookup):tag.getString("name").isEmpty()?null:Component.literal(tag.getString("name"));loadCrane(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){if(tag.getBoolean("CraneNetworkState")){muffled=tag.getBoolean("muffled");loadNetwork(tag);return;}loadWithComponents(tag,lookup);requestModelDataUpdate();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public CompoundTag getSettings(Level world,BlockPos pos){var tag=new CompoundTag();tag.putInt("inputSide",getInputSide()==null?6:getInputSide().get3DDataValue());tag.putInt("outputSide",getOutputSide()==null?6:getOutputSide().get3DDataValue());if(this instanceof Filter filter){var list=new ListTag();int count=0;for(int i=filter.getFilterSlots()[0];i<filter.getFilterSlots()[1];i++){if(!slots[i].isEmpty()){var entry=(CompoundTag)slots[i].save(world.registryAccess());entry.putByte("slot",(byte)count);list.add(entry);}count++;}tag.put("items",list);}return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level world,Player player,BlockPos pos){if(index==1){if(tag.contains("outputSide")){outputOverride=CraneBaseBlock.direction(tag.getInt("outputSide"));onBlockChanged();}if(tag.contains("inputSide"))world.setBlock(pos,getBlockState().setValue(CraneBaseBlock.META,tag.getInt("inputSide")),3);}else if(this instanceof Filter filter){var items=tag.getList("items",Tag.TAG_COMPOUND);int size=items.size();if(size>0){int count=0;for(int i=filter.getFilterSlots()[0];i<filter.getFilterSlots()[1];i++){if(i<size){var entry=items.getCompound(count);int slot=entry.getByte("slot");var stack=ItemStack.parseOptional(world.registryAccess(),entry);boolean router=tag.contains("modes")&&slot>index*5&&slot<index*+5;if(!stack.isEmpty()&&(slot<filter.getFilterSlots()[1]||router)){setItem(slot+filter.getFilterSlots()[0],stack);filter.nextMode(slot);setChanged();}}count++;}}}}
    @Override public String[] infoForDisplay(Level world,BlockPos pos){return new String[]{"copytool.filter","copytool.orientation"};}
}
