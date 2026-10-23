package com.hbm.machine.rtg;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.item.rtg.RtgPelletItem;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source 15-slot inventory, power ports, heat-before-decay accounting and long machine capacity. */
public final class RtgBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,EnergyProvider,EnergyConductor,MachineView, com.hbm.multiblock.MuffledMachine {
    public static final long MAX_POWER=100000;
    private static final int[] ALL={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14};
    private final ItemStack[] items=new ItemStack[15];
    private long power;private int heat;private String customName="";private boolean muffled,tilted;private PowerNode node;
    private final ContainerData data=new ContainerData(){
        @Override public int get(int index){return index<4?(int)(power>>>(index*16))&65535:switch(index){case 4->heat;case 5->maximumHeat();default->hasHeat()?1:0;};}
        @Override public void set(int index,int value){}
        @Override public int getCount(){return 7;}
    };
    public RtgBlockEntity(BlockPos pos,BlockState state){super(RtgContent.ENTITY.get(),pos,state);Arrays.fill(items,ItemStack.EMPTY);}
    public int heat(){return heat;}
    public int maximumHeat(){return RtgConfig.decays()?600:200;}
    public boolean hasHeat(){for(var stack:items)if(stack.getItem() instanceof RtgPelletItem)return true;return false;}
    public static void tick(Level level,BlockPos pos,BlockState state,RtgBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    private void serverTick(ServerLevel server){
        if(node==null||node.isExpired()){node=new PowerNode(worldPosition);HbmEnergy.nodespace(server).createNode(node);}
        // Original providePower only enrolls the provider; distribution runs in the common nodespace.
        node.addProvider(this);
        heat=0;boolean modified=false;
        for(int i=0;i<items.length;i++)if(items[i].getItem() instanceof RtgPelletItem pellet){heat+=pellet.power(items[i]);items[i]=pellet.handleDecay(items[i]);modified|=RtgConfig.decays();}
        if(heat>maximumHeat())heat=maximumHeat();
        long previous=power;power+=heat*5;if(power>MAX_POWER)power=MAX_POWER;
        if(modified||previous!=power)setChanged();
        // Original packet radius 50; container heat is synchronized separately.
        var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);
    }
    private void disconnect(){if(level instanceof ServerLevel server&&node!=null)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public PowerNode energyNode(){return node;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power;}
    @Override public long getMaxPower(){return MAX_POWER;}
    @Override public void setPower(long value){power=value;setChanged();}
    @Override public long energyStored(){return power;}
    @Override public long energyCapacity(){return MAX_POWER;}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.rtg.heat",heat,maximumHeat(),0xffb526),new ProgressView("jade.hbm.rtg.output",heat*5,maximumHeat()*5,0xffe36a));}
    @Override public Component getDisplayName(){return customName.isEmpty()?Component.translatable("container.rtg"):Component.literal(customName);}
    public void setCustomName(String name){customName=name;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RtgMenu(id,inventory,this,data);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<items.length;i++)if(!items[i].isEmpty()){var stack=(CompoundTag)items[i].save(lookup);stack.putByte("slot",(byte)i);list.add(stack);}tag.put("items",list);
        if(!customName.isEmpty())tag.putString("name",customName);tag.putLong("power",power);tag.putInt("heat",heat);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);Arrays.fill(items,ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var row=(CompoundTag)value;int slot=row.getByte("slot");if(slot>=0&&slot<items.length)items[slot]=ItemStack.parseOptional(lookup,row);}customName=tag.getString("name");power=tag.getLong("power");heat=tag.getInt("heat");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putLong("power",power);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){power=tag.getLong("power");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return items.length;}
    @Override public boolean isEmpty(){return Arrays.stream(items).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=items[slot].split(count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=items[slot];items[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){items[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(items,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    // TileEntityMachineBase.isItemValidForSlot is false; the old plain GUI Slot bypasses it.
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return ALL.clone();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
