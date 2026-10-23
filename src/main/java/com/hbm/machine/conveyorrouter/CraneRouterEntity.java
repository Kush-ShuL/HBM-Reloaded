package com.hbm.machine.conveyorrouter;

import com.hbm.api.machine.ICopiable;
import com.hbm.multiblock.*;
import com.hbm.pneumatic.PatternMatcher;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityCraneRouter / MachineBase state, with the sole existing six PatternMatchers. */
public final class CraneRouterEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,MuffledMachine,ICopiable {
    public static final int MODE_NONE=0,MODE_WHITELIST=1,MODE_BLACKLIST=2,MODE_WILDCARD=3;
    public final ItemStack[] slots=new ItemStack[30];
    public final PatternMatcher[] patterns=new PatternMatcher[6];
    public int[] modes=new int[6];
    private Component customName;private boolean muffled;
    public CraneRouterEntity(BlockPos pos,BlockState state){super(CraneRouterContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);for(int i=0;i<6;i++)patterns[i]=new PatternMatcher(5);}
    public void serverTick(){if(level instanceof ServerLevel server){var packet=ClientboundBlockEntityDataPacket.create(this,(entity,lookup)->{var data=new CompoundTag();data.putBoolean("RouterNetworkState",true);writeRouting(data);data.putBoolean("muffled",muffled);return data;});for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<15*15)player.connection.send(packet);}}
    public void nextMode(int index){patterns[index/5].nextMode(slots[index],index%5);setChanged();}
    public void initPattern(ItemStack stack,int index){patterns[index/5].initPatternSmart(stack,index%5);setChanged();}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<20*20;}
    public int[] getFilterSlots(){return new int[]{0,30};}
    public void receiveControl(Player player,CompoundTag data){
        if(!hasPermission(player))return;
        if(data.contains("toggle")){int index=data.getInt("toggle");if(index>=0&&index<6){modes[index]++;if(modes[index]>3)modes[index]=0;setChanged();}}
        if(data.contains("slot")){int index=data.getInt("slot");if(index>=0&&index<30){var stack=ItemStack.parseOptional(level.registryAccess(),data.getCompound("stack"));setItem(index,stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));nextMode(index);}}
    }
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CraneRouterMenu(id,inventory,this);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf data){data.writeBlockPos(worldPosition);}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.craneRouter"):customName;}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    public double getUseRange(){return 12D;}
    @Override public boolean stillValid(Player player){double range=getUseRange();return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=range*range;}
    @Override public int getContainerSize(){return slots.length;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int index){return slots[index];}
    @Override public ItemStack removeItem(int index,int count){var result=slots[index].split(count);if(slots[index].isEmpty())slots[index]=ItemStack.EMPTY;setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int index){var stack=slots[index];slots[index]=ItemStack.EMPTY;return stack;}
    @Override public void setItem(int index,ItemStack stack){slots[index]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int index,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int index,ItemStack stack,Direction side){return false;}
    private void writeRouting(CompoundTag data){for(int i=0;i<6;i++){var entry=new CompoundTag();patterns[i].write(entry);data.put("pattern"+i,entry);}data.putIntArray("modes",modes);}
    private void readRouting(CompoundTag data){for(int i=0;i<6;i++)patterns[i].read(data.getCompound("pattern"+i));if(data.contains("modes",Tag.TAG_INT_ARRAY))modes=Arrays.copyOf(data.getIntArray("modes"),6);}
    @Override protected void saveAdditional(CompoundTag data,HolderLookup.Provider lookup){super.saveAdditional(data,lookup);var items=NonNullList.withSize(30,ItemStack.EMPTY);for(int i=0;i<30;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(data,items,lookup);writeRouting(data);data.putBoolean("muffled",muffled);if(customName!=null){data.putString("name",customName.getString());data.putString("CustomName",Component.Serializer.toJson(customName,lookup));}}
    @Override protected void loadAdditional(CompoundTag data,HolderLookup.Provider lookup){super.loadAdditional(data,lookup);var items=NonNullList.withSize(30,ItemStack.EMPTY);ContainerHelper.loadAllItems(data,items,lookup);for(int i=0;i<30;i++)slots[i]=items.get(i);readRouting(data);muffled=data.getBoolean("muffled");customName=data.contains("CustomName")?Component.Serializer.fromJson(data.getString("CustomName"),lookup):data.getString("name").isEmpty()?null:Component.literal(data.getString("name"));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag data,HolderLookup.Provider lookup){if(data.getBoolean("RouterNetworkState")){readRouting(data);muffled=data.getBoolean("muffled");}else loadWithComponents(data,lookup);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public CompoundTag getSettings(Level world,BlockPos pos){var data=new CompoundTag();var items=new ListTag();for(int i=0;i<30;i++)if(!slots[i].isEmpty()){var entry=(CompoundTag)slots[i].save(world.registryAccess());entry.putByte("slot",(byte)i);items.add(entry);}data.put("items",items);data.putIntArray("modes",modes);return data;}
    @Override public void pasteSettings(CompoundTag data,int index,Level world,Player player,BlockPos pos){
        var items=data.getList("items",Tag.TAG_COMPOUND);int size=items.size();
        if(size>0&&data.contains("modes")){
            for(int i=0;i<size;i++){var entry=items.getCompound(i);int slot=entry.getByte("slot");var stack=ItemStack.parseOptional(world.registryAccess(),entry);if(!stack.isEmpty()&&slot>index*5&&slot<Math.min(index*5+5,30)){setItem(slot,stack);nextMode(slot);}}
            modes=Arrays.copyOf(data.getIntArray("modes"),6);setChanged();
        }else if(size>0){
            // Exact IControlReceiverFilter fallback, including source index/list and router comparison quirks.
            for(int i=0;i<30;i++)if(i<size){var entry=items.getCompound(i);int slot=entry.getByte("slot");var stack=ItemStack.parseOptional(world.registryAccess(),entry);boolean router=data.contains("modes")&&slot>index*5&&slot<index*+5;if(!stack.isEmpty()&&index<size&&(slot<30||router)&&slot>=0&&slot<30){setItem(slot,stack);nextMode(slot);}}
        }
    }
    @Override public String[] infoForDisplay(Level world,BlockPos pos){return new String[]{"copytool.pattern0","copytool.pattern1","copytool.pattern2","copytool.pattern3","copytool.pattern4","copytool.pattern5"};}
}
