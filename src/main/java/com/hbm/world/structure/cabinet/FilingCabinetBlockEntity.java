package com.hbm.world.structure.cabinet;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Exact TileEntityFileCabinet, sharing the existing real lock base and source items/slot persistence format. */
public final class FilingCabinetBlockEntity extends LockableBlockEntity implements WorldlyContainer,MenuProvider,Nameable {
 private final NonNullList<ItemStack> slots=NonNullList.withSize(8,ItemStack.EMPTY);private String customName;
 private int timer,playersUsing;public float lowerExtent,prevLowerExtent,upperExtent,prevUpperExtent;public boolean hasSpiders;
 public FilingCabinetBlockEntity(BlockPos pos,BlockState state){super(FilingCabinetContent.ENTITY.get(),pos,state);}
 @Override public int getContainerSize(){return 8;}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots.get(slot);}
 @Override public ItemStack removeItem(int slot,int count){var stack=slots.get(slot);if(stack.isEmpty())return ItemStack.EMPTY;if(stack.getCount()<=count){slots.set(slot,ItemStack.EMPTY);return stack;}var result=stack.split(count);if(stack.isEmpty())slots.set(slot,ItemStack.EMPTY);setChanged();return result;}
 @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}@Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);stack.limitSize(64);setChanged();}@Override public void clearContent(){slots.clear();setChanged();}@Override public int getMaxStackSize(){return 64;}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4,5,6,7};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
 @Override public void startOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing++;}@Override public void stopOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing--;}
 @Override public boolean hasCustomName(){return customName!=null&&!customName.isEmpty();}@Override public Component getCustomName(){return customName==null?null:Component.literal(customName);}@Override public Component getName(){return hasCustomName()?getCustomName():Component.translatable("container.fileCabinet");}@Override public Component getDisplayName(){return getName();}public void setCustomName(String value){customName=value;setChanged();}public void fillWithSpiders(){hasSpiders=true;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FilingCabinetMenu(id,inventory,this,worldPosition);}
 public void receiveAnimation(int time,int users){timer=time;playersUsing=users;}
 public static void tick(Level level,BlockPos pos,BlockState state,FilingCabinetBlockEntity cabinet){cabinet.animate();}
 private void animate(){if(level==null)return;if(!level.isClientSide){if(playersUsing>0){if(timer<10)timer++;}else timer=0;CabinetAnimationPacket.send(this,timer,playersUsing);}else{prevLowerExtent=lowerExtent;prevUpperExtent=upperExtent;}
  float openSpeed=playersUsing>0?1F/16F:1F/25F,maxExtent=.8F;
  if(playersUsing>0){if(lowerExtent==0&&upperExtent==0)sound("block.crateOpen",.8F,1);else{if(upperExtent+openSpeed>=maxExtent&&lowerExtent<maxExtent)sound("block.crateOpen",.5F,level.random.nextFloat()*.1F+.7F);if(lowerExtent+openSpeed>=maxExtent&&lowerExtent<maxExtent)sound("block.crateOpen",.5F,level.random.nextFloat()*.1F+.7F);}lowerExtent+=openSpeed;if(timer>=10)upperExtent+=openSpeed;}
  else if(lowerExtent>0){if(upperExtent-openSpeed<maxExtent/2&&upperExtent>=maxExtent/2&&upperExtent!=lowerExtent)sound("block.crateClose",.8F,1);if(lowerExtent-openSpeed<maxExtent/2&&lowerExtent>=maxExtent/2)sound("block.crateClose",.8F,1);upperExtent-=openSpeed;lowerExtent-=openSpeed;}
  lowerExtent=Mth.clamp(lowerExtent,0,maxExtent);upperExtent=Mth.clamp(upperExtent,0,maxExtent);
 }
 private void sound(String name,float volume,float pitch){if(level.isClientSide)level.playLocalSound(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get(name),SoundSource.BLOCKS,volume,pitch,false);else level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get(name),SoundSource.BLOCKS,volume,pitch);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<8;i++)if(!slots.get(i).isEmpty()){var stack=(CompoundTag)slots.get(i).save(lookup);stack.putByte("slot",(byte)i);list.add(stack);}tag.put("items",list);tag.putBoolean("spiders",hasSpiders);if(customName!=null)tag.putString("name",customName);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);slots.clear();for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var stack=(CompoundTag)value;int slot=stack.getByte("slot");if(slot>=0&&slot<8)slots.set(slot,ItemStack.parseOptional(lookup,stack));}hasSpiders=tag.getBoolean("spiders");customName=tag.getString("name");}
}
