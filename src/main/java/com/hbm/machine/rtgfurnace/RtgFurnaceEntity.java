package com.hbm.machine.rtgfurnace;

import com.hbm.api.machine.MachineView;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.rtg.RtgPelletItem;
import com.hbm.ore.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The source deprecated five-slot furnace, sharing the only real RTG pellet decay/config implementation. */
public final class RtgFurnaceEntity extends BlockEntity implements WorldlyContainer,MenuProvider,MachineView {
    public static final int PROCESSING_SPEED=1000;
    private static final int[] TOP={0},BOTTOM={4},SIDES={1,2,3};
    private final ItemStack[] items=new ItemStack[5];
    public int dualCookTime;
    private String customName="";
    private final ContainerData data=new ContainerData(){
        @Override public int get(int index){return index==0?dualCookTime:0;}
        @Override public void set(int index,int value){}
        @Override public int getCount(){return 1;}
    };
    public RtgFurnaceEntity(BlockPos pos,BlockState state){super(RtgFurnaceContent.ENTITY.get(),pos,state);Arrays.fill(items,ItemStack.EMPTY);}
    public boolean hasPower(){return hasHeat(this);}
    public static boolean hasHeat(Container container){for(int i=1;i<=3;i++)if(container.getItem(i).getItem() instanceof RtgPelletItem)return true;return false;}
    /** Exact RTGUtil heat-before-decay order. This is a consumer, not another decay table/backend. */
    private int updateRtgs(){int heat=0;for(int slot:SIDES)if(items[slot].getItem() instanceof RtgPelletItem pellet){heat+=pellet.power(items[slot]);items[slot]=pellet.handleDecay(items[slot]);}return heat;}
    private ItemStack result(){
        if(level==null||items[0].isEmpty())return ItemStack.EMPTY;
        var input=new SingleRecipeInput(RawOreContent.processingStack(items[0]));
        var recipe=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level);
        return recipe.map(value->value.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    private static boolean same(ItemStack first,ItemStack second){return ItemStack.isSameItem(first,second)&&first.getDamageValue()==second.getDamageValue()
            &&first.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0).equals(second.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
    public boolean canProcess(){
        if(items[0].isEmpty())return false;var result=result();if(result.isEmpty())return false;
        if(items[4].isEmpty())return true;if(!same(items[4],result))return false;
        // Source only tests the current count, not count+recipeSize: retain its possible final overfill.
        if(items[4].getCount()<getMaxStackSize()&&items[4].getCount()<items[4].getMaxStackSize())return true;
        return items[4].getCount()<result.getMaxStackSize();
    }
    private void process(){
        if(!canProcess())return;
        var result=OreProcessing.output(result(),OreProcessing.rate(items[0]),level.random);
        if(items[4].isEmpty())items[4]=result.copy();else if(same(items[4],result))items[4].grow(result.getCount());
        if(items[0].getCount()<=0)items[0]=new ItemStack(items[0].getItem());else items[0].shrink(1);
        if(items[0].isEmpty())items[0]=ItemStack.EMPTY;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,RtgFurnaceEntity furnace){if(level instanceof ServerLevel server)furnace.serverTick(server);}
    private void serverTick(ServerLevel server){
        hasPower();boolean dirty=false;
        if(hasPower()&&canProcess()){
            dualCookTime+=updateRtgs();
            if(dualCookTime>=PROCESSING_SPEED){dualCookTime=0;process();dirty=true;}
        }else{dualCookTime=0;updateRtgs();} // Fuel still decays while idle, blocked or empty.
        boolean trigger=true;
        if(hasPower()&&canProcess()&&dualCookTime==0)trigger=false;
        if(trigger){
            dirty=true;var target=dualCookTime>0?RtgFurnaceContent.ON.get():RtgFurnaceContent.OFF.get();
            server.setBlock(worldPosition,target.defaultBlockState().setValue(RtgFurnaceBlock.META,getBlockState().getValue(RtgFurnaceBlock.META)),3);
        }
        if(dirty)setChanged();
    }
    @Override public Component getDisplayName(){return customName.isEmpty()?Component.translatable("container.rtgFurnace"):Component.literal(customName);}
    public void setCustomName(String value){customName=value;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RtgFurnaceMenu(id,inventory,this,data);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);tag.putShort("cookTime",(short)dualCookTime); // Exact original lowercase-write/uppercase-read mismatch.
        var list=new ListTag();for(int slot=0;slot<items.length;slot++)if(!items[slot].isEmpty()){var item=(CompoundTag)items[slot].save(lookup);item.putByte("slot",(byte)slot);list.add(item);}tag.put("items",list);
        if(!customName.isEmpty())tag.putString("name",customName);
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);dualCookTime=tag.getShort("CookTime");Arrays.fill(items,ItemStack.EMPTY);
        for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<items.length)items[slot]=ItemStack.parseOptional(lookup,item);}customName=tag.getString("name");
    }
    @Override public int getContainerSize(){return items.length;}
    @Override public boolean isEmpty(){return Arrays.stream(items).allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return items[slot];}
    @Override public ItemStack removeItem(int slot,int amount){var result=items[slot].split(amount);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){var result=items[slot];items[slot]=ItemStack.EMPTY;return result;}
    @Override public void setItem(int slot,ItemStack stack){items[slot]=stack;if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());setChanged();}
    @Override public void clearContent(){Arrays.fill(items,ItemStack.EMPTY);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public int[] getSlotsForFace(Direction side){return (side==Direction.DOWN?BOTTOM:side==Direction.UP?TOP:SIDES).clone();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return side!=Direction.DOWN||slot!=1||stack.is(Items.BUCKET);}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<ProgressView> progressBars(){return dualCookTime>0||hasPower()&&canProcess()?List.of(new ProgressView("jade.hbm.processing",dualCookTime,PROCESSING_SPEED,0xe9a04a)):List.of();}
}
