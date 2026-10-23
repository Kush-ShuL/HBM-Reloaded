package com.hbm.machine.ironfurnace;

import com.hbm.api.machine.MachineView.ProgressView;
import com.hbm.fuel.ModuleBurnTime;
import com.hbm.machine.electricfurnace.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.ore.*;
import com.hbm.pollution.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Direct source loop: preloads fuel even without input; fuel only decreases during actual processing. */
public final class IronFurnaceEntity extends ThermalMachine {
    public static final int BASE_TIME=160;
    public static final ModuleBurnTime BURN=new ModuleBurnTime().setLigniteTimeMod(1.25).setCoalTimeMod(1.25).setCokeTimeMod(1.5).setSolidTimeMod(2).setRocketTimeMod(2).setBalefireTimeMod(2);
    /** Client-only source particles, assigned by the real client setup. */
    public static Consumer<IronFurnaceEntity> CLIENT_TICK=machine->{};
    public int maxBurnTime,burnTime,progress,processingTime;
    public boolean wasOn,tilted;
    private final ContainerData data=new ContainerData(){
        public int getCount(){return 9;}
        public int get(int index){return switch(index){case 0->maxBurnTime&65535;case 1->maxBurnTime>>>16;case 2->burnTime&65535;case 3->burnTime>>>16;case 4->progress&65535;case 5->progress>>>16;case 6->processingTime;case 7->wasOn?1:0;case 8->canSmelt()?1:0;default->0;};}
        public void set(int index,int value){}
    };
    public IronFurnaceEntity(BlockPos pos,BlockState state){super(IronFurnaceContent.ENTITY.get(),pos,state,5);}
    @Override protected String title(){return "container.furnaceIron";}
    @Override protected void saveMachine(CompoundTag tag){tag.putInt("maxBurnTime",maxBurnTime);tag.putInt("burnTime",burnTime);tag.putInt("progress",progress);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){maxBurnTime=tag.getInt("maxBurnTime");burnTime=tag.getInt("burnTime");progress=tag.getInt("progress");tilted=tag.getBoolean("tilted");}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);
        if(tag.contains("items",Tag.TAG_LIST)){slots.replaceAll(stack->ItemStack.EMPTY);for(Tag entry:tag.getList("items",Tag.TAG_COMPOUND)){CompoundTag item=(CompoundTag)entry;int index=item.getByte("slot")&255;if(index<slots.size())slots.set(index,ItemStack.parseOptional(lookup,item));}}
        if(tag.contains("name",Tag.TAG_STRING)&&!tag.getString("name").isEmpty())setCustomName(Component.literal(tag.getString("name")));
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){CompoundTag tag=super.getUpdateTag(lookup);tag.putInt("processingTime",processingTime);tag.putBoolean("wasOn",wasOn);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);processingTime=tag.getInt("processingTime");wasOn=tag.getBoolean("wasOn");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new IronFurnaceMenu(id,inventory,this,data);}
    // Client menu uses only standard slot/data synchronization: no positional custom buffer is required.
    private ItemStack result(){
        if(level==null||slots.get(0).isEmpty())return ItemStack.EMPTY;
        ItemStack canonical=RawOreContent.processingStack(slots.get(0));
        SingleRecipeInput input=new SingleRecipeInput(canonical);
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level).map(recipe->recipe.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    public boolean canSmelt(){
        if(burnTime<=0)return false;ItemStack result=result();if(result.isEmpty())return false;
        ItemStack maximum=OreProcessing.maximumOutput(result,OreProcessing.rate(slots.get(0)));
        if(slots.get(3).isEmpty())return maximum.isEmpty()||maximum.getCount()<=Math.min(getMaxStackSize(),maximum.getMaxStackSize());
        return ElectricFurnaceBlockEntity.sameLegacyItem(slots.get(3),result)&&(long)slots.get(3).getCount()+maximum.getCount()<=Math.min(getMaxStackSize(),slots.get(3).getMaxStackSize());
    }
    public static void tick(Level level,BlockPos pos,BlockState state,IronFurnaceEntity machine){
        if(level.isClientSide){CLIENT_TICK.accept(machine);return;}
        int speed=0;ItemStack upgrade=machine.slots.get(4);
        if(upgrade.getItem() instanceof FurnaceUpgradeItem item&&item.type()==FurnaceUpgradeItem.Type.SPEED)speed=Math.clamp(item.tier(),0,3);
        machine.processingTime=BASE_TIME-(BASE_TIME/2)*speed/3;machine.wasOn=false;
        if(machine.burnTime<=0)for(int slot=1;slot<3;slot++){
            ItemStack fuel=machine.slots.get(slot);int burn=BURN.getBurnTime(fuel);if(burn<=0)continue;
            machine.maxBurnTime=machine.burnTime=burn;
            ItemStack remainder=fuel.getCraftingRemainingItem();fuel.shrink(1);if(fuel.isEmpty())machine.slots.set(slot,remainder);break;
        }
        if(machine.canSmelt()){
            machine.wasOn=true;machine.progress++;machine.burnTime--;
            if(machine.progress%15==0&&!machine.isMuffled())level.playSound(null,pos,SoundEvents.FIRE_AMBIENT,SoundSource.BLOCKS,1,.5F+level.random.nextFloat()*.5F);
            if(machine.progress>=machine.processingTime){
                ItemStack output=OreProcessing.output(machine.result(),OreProcessing.rate(machine.slots.get(0)),level.random);
                if(machine.slots.get(3).isEmpty())machine.slots.set(3,output);else machine.slots.get(3).grow(output.getCount());
                machine.slots.get(0).shrink(1);machine.progress=0;machine.setChanged();
            }
            if(level.getGameTime()%20==0)PollutionSystem.increment((ServerLevel)level,pos,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND);
        }else machine.progress=0;
        machine.sync(50);
    }
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0?!smeltingResult(stack).isEmpty():slot==1||slot==2?BURN.getBurnTime(stack)>0:false;}
    private ItemStack smeltingResult(ItemStack stack){if(level==null||stack.isEmpty())return ItemStack.EMPTY;SingleRecipeInput input=new SingleRecipeInput(RawOreContent.processingStack(stack));return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level).map(recipe->recipe.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==3;}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.iron_furnace.progress",progress,processingTime,0xD08020),new ProgressView("gui.hbm.iron_furnace.fuel",burnTime,maxBurnTime,0xD05010));}
}
