package com.hbm.machine.brickfurnace;

import com.hbm.registry.HbmContent;
import com.hbm.api.machine.MachineView;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.ashpit.AshpitBlockEntity;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.multiblock.NamedMultiblock;
import com.hbm.ore.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Actual four-slot TileEntityFurnaceBrick, not an HE furnace or external heat producer. */
public final class BrickFurnaceEntity extends BlockEntity implements WorldlyContainer,MenuProvider,MachineView,NamedMultiblock,MuffledMachine {
    private static final int[] TOP={0},BOTTOM={2,1,3},SIDES={1};
    /** Source public mutable identity table; setup waits until all real items are bound. */
    public static final Map<Item,Integer> burnSpeed=new HashMap<>();
    private final ItemStack[] items=new ItemStack[4];
    public int burnTime,maxBurnTime,progress,ashLevelWood,ashLevelCoal,ashLevelMisc;
    public boolean muffled,tilted;
    private String customName="";
    private CompoundTag lastPacked;
    private final ContainerData data=new ContainerData(){
        @Override public int get(int index){int value=switch(index/2){case 0->burnTime;case 1->maxBurnTime;case 2->progress;default->0;};return value>>>(index%2*16)&0xffff;}
        @Override public void set(int index,int value){}
        @Override public int getCount(){return 6;}
    };
    public BrickFurnaceEntity(BlockPos pos,BlockState state){super(BrickFurnaceContent.ENTITY.get(),pos,state);Arrays.fill(items,ItemStack.EMPTY);}
    public static void initializeBurnSpeeds(){
        burnSpeed.put(Items.CLAY_BALL,4);burnSpeed.put(HbmContent.item("ball_fireclay"),4);burnSpeed.put(Items.NETHERRACK,4);
        burnSpeed.put(Items.COBBLESTONE,2);burnSpeed.put(Items.SAND,2);burnSpeed.put(Items.RED_SAND,2);
        for(var log:List.of(Items.OAK_LOG,Items.SPRUCE_LOG,Items.BIRCH_LOG,Items.JUNGLE_LOG,Items.ACACIA_LOG,Items.DARK_OAK_LOG))burnSpeed.put(log,2);
    }
    public static int fuelTime(ItemStack stack){return stack.isEmpty()?0:stack.getBurnTime(RecipeType.SMELTING);}
    public int getBurnSpeed(){return burnSpeed.getOrDefault(items[0].getItem(),1);}
    private ItemStack result(){
        if(level==null||items[0].isEmpty())return ItemStack.EMPTY;
        var input=new SingleRecipeInput(RawOreContent.processingStack(items[0]));
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level).map(holder->holder.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    private static boolean same(ItemStack first,ItemStack second){return ItemStack.isSameItem(first,second)&&first.getDamageValue()==second.getDamageValue()
        &&first.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0).equals(second.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
    private boolean canSmelt(){
        if(items[0].isEmpty())return false;var result=result();if(result.isEmpty())return false;
        if(items[2].isEmpty())return true;if(!same(items[2],result))return false;
        int count=items[2].getCount()+OreProcessing.maximumCount(result.getCount(),OreProcessing.rate(items[0]));
        return count<=getMaxStackSize()&&count<=items[2].getMaxStackSize();
    }
    public void smeltItem(){
        if(!canSmelt())return;var result=OreProcessing.output(result(),OreProcessing.rate(items[0]),level.random);
        if(items[2].isEmpty())items[2]=result.copy();else if(items[2].getItem()==result.getItem())items[2].grow(result.getCount());
        items[0].shrink(1);if(items[0].isEmpty())items[0]=ItemStack.EMPTY;
    }
    private boolean processAsh(int amount,int type){
        if(amount<2000)return false;
        if(items[3].isEmpty()){items[3]=AshpitBlockEntity.ashStack(type);return true;}
        var ash=AshpitBlockEntity.ashStack(type);
        if(items[3].getCount()<items[3].getMaxStackSize()&&same(items[3],ash)){items[3].grow(1);return true;}
        return false;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,BrickFurnaceEntity furnace){if(level instanceof ServerLevel server)furnace.serverTick(server);}
    private void serverTick(ServerLevel server){
        boolean wasBurning=burnTime>0,dirty=false;
        if(burnTime>0)burnTime--;
        if(burnTime!=0||!items[1].isEmpty()&&!items[0].isEmpty()){
            if(burnTime==0&&canSmelt()){
                maxBurnTime=burnTime=fuelTime(items[1]);
                if(burnTime>0){
                    dirty=true;
                    // X5846 decrements count before classifying. Keep its item/metadata at count0:
                    // modern ItemStack.getItem() instead returns AIR for an empty stack.
                    var identity=items[1].copyWithCount(1);items[1].shrink(1);
                    switch(AshpitBlockEntity.ashFromFuel(identity)){case 0->ashLevelWood+=burnTime;case 1->ashLevelCoal+=burnTime;default->ashLevelMisc+=burnTime;}
                    if(processAsh(ashLevelWood,0))ashLevelWood-=2000;
                    if(processAsh(ashLevelCoal,1))ashLevelCoal-=2000;
                    if(processAsh(ashLevelMisc,2))ashLevelMisc-=2000;
                    if(items[1].isEmpty())items[1]=identity.getCraftingRemainingItem();
                }
            }
            if(burnTime>0&&canSmelt()){progress+=getBurnSpeed();if(progress>=200){progress=0;smeltItem();dirty=true;}}
            else progress=0;
        }
        if(wasBurning!=(burnTime>0)){
            dirty=true;var block=burnTime>0?BrickFurnaceContent.ON.get():BrickFurnaceContent.OFF.get();
            server.setBlock(worldPosition,block.defaultBlockState().setValue(BrickFurnaceBlock.META,getBlockState().getValue(BrickFurnaceBlock.META)),3);
        }
        if(dirty)setChanged();
        var packed=getUpdateTag(server.registryAccess());
        if(!packed.equals(lastPacked)||server.getGameTime()%20==0){lastPacked=packed;var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225D)player.connection.send(packet);}
    }
    @Override public Component getDisplayName(){return customName.isEmpty()?Component.translatable("container.furnaceBrick"):Component.literal(customName);}
    @Override public void setCustomName(Component name){customName=name.getString();setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new BrickFurnaceMenu(id,inventory,this,data);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var list=new ListTag();for(int i=0;i<4;i++)if(!items[i].isEmpty()){var entry=(CompoundTag)items[i].save(lookup);entry.putByte("slot",(byte)i);list.add(entry);}tag.put("items",list);
        if(!customName.isEmpty())tag.putString("name",customName);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);
        tag.putInt("burnTime",burnTime);tag.putInt("maxBurn",maxBurnTime);tag.putInt("progress",progress);
        // X5846 never writes its three ash accumulators. Do not silently persist a new mechanic.
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);Arrays.fill(items,ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var entry=(CompoundTag)value;int slot=entry.getByte("slot");if(slot>=0&&slot<4)items[slot]=ItemStack.parseOptional(lookup,entry);}
        customName=tag.getString("name");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");burnTime=tag.getInt("burnTime");maxBurnTime=tag.getInt("maxBurn");progress=tag.getInt("progress");
        ashLevelWood=ashLevelCoal=ashLevelMisc=0;
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putInt("burnTime",burnTime);tag.putInt("maxBurn",maxBurnTime);tag.putInt("progress",progress);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");burnTime=tag.getInt("burnTime");maxBurnTime=tag.getInt("maxBurn");progress=tag.getInt("progress");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 4;}
    @Override public boolean isEmpty(){return Arrays.stream(items).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=items[slot].split(count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=items[slot];items[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){items[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){Arrays.fill(items,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<2&&(slot!=1||fuelTime(stack)>0);}
    @Override public int[] getSlotsForFace(Direction side){return(side==Direction.DOWN?BOTTOM:side==Direction.UP?TOP:SIDES).clone();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=2;}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<ProgressView> progressBars(){return progress>0?List.of(new ProgressView("jade.hbm.processing",progress,200,0xDC8A4A)):List.of();}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
