package com.hbm.machine.siren;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Plain source TileEntity (not LoadedBase). The redstone edge latch is deliberately transient. */
public final class SirenBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider {
    private ItemStack stack=ItemStack.EMPTY;
    private String customName;
    public boolean lock;
    public SirenBlockEntity(BlockPos pos,BlockState state){super(SirenContent.ENTITY.get(),pos,state);}
    public SirenTrack track(){return CassetteItem.track(stack);}
    public void tickServer(){if(!(level instanceof ServerLevel server))return;var track=track();if(track==SirenTrack.NULL){SirenPacket.send(server,worldPosition,track.ordinal(),false);return;}boolean active=server.hasNeighborSignal(worldPosition);if(track.type==SirenTrack.SoundType.LOOP){SirenPacket.send(server,worldPosition,track.ordinal(),active);}else{if(!lock&&active){lock=true;SirenPacket.send(server,worldPosition,track.ordinal(),false);SirenPacket.send(server,worldPosition,track.ordinal(),true);}if(lock&&!active)lock=false;}}
    public void setCustomName(String name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName!=null&&!customName.isEmpty()?Component.literal(customName):Component.translatable("container.siren");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new SirenMenu(id,inventory,this);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=new ListTag();if(!stack.isEmpty()){var item=(CompoundTag)stack.save(lookup);item.putByte("slot",(byte)0);items.add(item);}tag.put("items",items);if(customName!=null)tag.putString("name",customName);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);stack=ItemStack.EMPTY;var items=tag.getList("items",Tag.TAG_COMPOUND);for(int i=0;i<items.size();i++){var item=items.getCompound(i);if(item.getByte("slot")==0)stack=ItemStack.parseOptional(lookup,item);}customName=tag.getString("name");}
    @Override public int getContainerSize(){return 1;}
    @Override public int getMaxStackSize(){return 64;}
    @Override public boolean isEmpty(){return stack.isEmpty();}
    @Override public ItemStack getItem(int slot){return stack;}
    @Override public ItemStack removeItem(int slot,int amount){var result=stack.split(amount);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=stack;stack=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack value){stack=value;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){stack=ItemStack.EMPTY;setChanged();}
    // Original exact8-block range belongs to this plain TileEntity, not the12-block machine base.
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int slot,ItemStack value){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack value,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack value,Direction side){return false;}
}
