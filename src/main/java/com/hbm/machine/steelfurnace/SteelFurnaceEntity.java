package com.hbm.machine.steelfurnace;
import com.hbm.api.machine.MachineView.ProgressView;
import com.hbm.heat.HeatSource;
import com.hbm.machine.electricfurnace.ElectricFurnaceBlockEntity;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.ore.*;
import com.hbm.pollution.*;
import com.hbm.fuel.HbmFuels;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Original fixed-TU three-lane loop and cumulative source ore/log/tar bonuses; no invented HE or fuel. */
public final class SteelFurnaceEntity extends ThermalMachine {
    public static final int PROCESS_TIME=40000,MAX_HEAT=100000;
    public final int[] progress=new int[3],bonus=new int[3];
    private final ItemStack[] lastItems={ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY};
    public static Consumer<SteelFurnaceEntity> CLIENT_TICK=machine->{};
    public int heat;public boolean wasOn,tilted;
    private final ContainerData data=new ContainerData(){
        public int getCount(){return 15;}
        public int get(int index){if(index<6)return index%2==0?progress[index/2]&65535:progress[index/2]>>>16;if(index<12)return index%2==0?bonus[(index-6)/2]&65535:bonus[(index-6)/2]>>>16;return switch(index){case 12->heat&65535;case 13->heat>>>16;case 14->wasOn?1:0;default->0;};}
        public void set(int index,int value){}
    };
    public SteelFurnaceEntity(BlockPos pos,BlockState state){super(SteelFurnaceContent.ENTITY.get(),pos,state,6);}
    @Override protected String title(){return "container.furnaceSteel";}
    @Override protected void saveMachine(CompoundTag tag){tag.putIntArray("progress",progress);tag.putIntArray("bonus",bonus);tag.putInt("heat",heat);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){loadArray(progress,tag.getIntArray("progress"));loadArray(bonus,tag.getIntArray("bonus"));heat=tag.getInt("heat");tilted=tag.getBoolean("tilted");}
    private static void loadArray(int[] target,int[] source){Arrays.fill(target,0);System.arraycopy(source,0,target,0,Math.min(target.length,source.length));}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);saveLastItems(tag,lookup);}
    private void saveLastItems(CompoundTag tag,HolderLookup.Provider lookup){ListTag items=new ListTag();for(int i=0;i<3;i++)if(!lastItems[i].isEmpty()){CompoundTag item=(CompoundTag)lastItems[i].save(lookup);item.putByte("lastItem",(byte)i);items.add(item);}tag.put("lastItems",items);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);Arrays.fill(lastItems,ItemStack.EMPTY);for(Tag value:tag.getList("lastItems",Tag.TAG_COMPOUND)){CompoundTag item=(CompoundTag)value;int index=item.getByte("lastItem")&255;if(index<3)lastItems[index]=ItemStack.parseOptional(lookup,item);}
        if(tag.contains("items",Tag.TAG_LIST)){slots.replaceAll(stack->ItemStack.EMPTY);for(Tag value:tag.getList("items",Tag.TAG_COMPOUND)){CompoundTag item=(CompoundTag)value;int index=item.getByte("slot")&255;if(index<6)slots.set(index,ItemStack.parseOptional(lookup,item));}}
        if(tag.contains("name",Tag.TAG_STRING)&&!tag.getString("name").isEmpty())setCustomName(Component.literal(tag.getString("name")));
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){CompoundTag tag=new CompoundTag();tag.putBoolean("muffled",isMuffled());tag.putIntArray("progress",progress);tag.putIntArray("bonus",bonus);tag.putInt("heat",heat);tag.putBoolean("wasOn",wasOn);tag.putBoolean("tilted",tilted);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);wasOn=tag.getBoolean("wasOn");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new SteelFurnaceMenu(id,inventory,this,data);}
    private ItemStack result(ItemStack stack){if(level==null||stack.isEmpty())return ItemStack.EMPTY;SingleRecipeInput input=new SingleRecipeInput(RawOreContent.processingStack(stack));return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level).map(recipe->recipe.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);}
    public boolean canSmelt(int index){
        if(heat<MAX_HEAT/3)return false;ItemStack result=result(slots.get(index));if(result.isEmpty())return false;
        ItemStack maximum=OreProcessing.maximumOutput(result,OreProcessing.rate(slots.get(index))),out=slots.get(index+3);
        if(out.isEmpty())return maximum.isEmpty()||maximum.getCount()<=Math.min(getMaxStackSize(),maximum.getMaxStackSize());
        return ElectricFurnaceBlockEntity.sameLegacyItem(out,result)&&(long)out.getCount()+maximum.getCount()<=out.getMaxStackSize();
    }
    private void pullHeat(){
        if(heat>=MAX_HEAT)return;
        if(level.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int diff=source.getHeatStored()-heat;if(diff==0)return;if(diff>0){diff=(int)Math.ceil(diff*.05D);source.useUpHeat(diff);heat+=diff;if(heat>MAX_HEAT)heat=MAX_HEAT;return;}}
        heat=Math.max(heat-Math.max(heat/1000,1),0);
    }
    private void addBonus(ItemStack stack,int index){for(String name:HbmFuels.tags(RawOreContent.processingStack(stack))){if(HbmFuels.isOreTag(name)){bonus[index]+=25;return;}if(HbmFuels.isLogTag(name)||name.equals("hbm:tars")){bonus[index]+=50;return;}}}
    public static void tick(Level level,BlockPos pos,BlockState state,SteelFurnaceEntity machine){
        if(level.isClientSide){CLIENT_TICK.accept(machine);return;}
        machine.pullHeat();machine.wasOn=false;int burn=(machine.heat-MAX_HEAT/3)/10;
        for(int i=0;i<3;i++){
            ItemStack input=machine.slots.get(i);
            if(input.isEmpty()||machine.lastItems[i].isEmpty()||!ElectricFurnaceBlockEntity.sameLegacyItem(input,machine.lastItems[i])){machine.progress[i]=0;machine.bonus[i]=0;}
            if(machine.canSmelt(i)){machine.progress[i]+=burn;machine.heat-=burn;machine.wasOn=true;if(level.getGameTime()%20==0)PollutionSystem.increment((ServerLevel)level,pos,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*2);}
            machine.lastItems[i]=input;
            if(machine.progress[i]>=PROCESS_TIME){
                ItemStack result=machine.result(input);machine.addBonus(input,i);int extras=machine.bonus[i]/100;machine.bonus[i]%=100;
                ItemStack output=OreProcessing.output(result.copyWithCount(result.getCount()*(1+extras)),OreProcessing.rate(input),level.random);
                ItemStack current=machine.slots.get(i+3);
                if(current.isEmpty())machine.slots.set(i+3,extras>0&&!output.isEmpty()?output.copyWithCount(Math.min(output.getCount(),output.getMaxStackSize())):output);
                else current.setCount(extras>0?Math.min(current.getMaxStackSize(),current.getCount()+output.getCount()):current.getCount()+output.getCount());
                machine.removeItem(i,1);machine.progress[i]=0;
            }
        }
        machine.sync(50);
    }
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<3&&!result(stack).isEmpty();}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4,5};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>2;}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.steel_furnace.heat",heat,MAX_HEAT,0xE08020),new ProgressView("gui.hbm.steel_furnace.progress1",progress[0],PROCESS_TIME,0xD08020),new ProgressView("gui.hbm.steel_furnace.progress2",progress[1],PROCESS_TIME,0xD08020),new ProgressView("gui.hbm.steel_furnace.progress3",progress[2],PROCESS_TIME,0xD08020));}
}
