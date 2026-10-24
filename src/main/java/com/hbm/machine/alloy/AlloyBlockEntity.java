package com.hbm.machine.alloy;

import com.hbm.api.machine.MachineView;
import com.hbm.hazard.HbmHazards;
import com.hbm.multiblock.NamedMultiblock;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 thermal blast furnace. Fuel is a distinct12800-unit reservoir, never an energy capability. */
public final class AlloyBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,MachineView,NamedMultiblock {
    public static final int MAX_FUEL=12800,PROCESS_TIME=400;
    private static final int[] SLOTS={0,1,2,3};
    private final NonNullList<ItemStack> inventory=NonNullList.withSize(4,ItemStack.EMPTY);
    private int fuel,progress,sideFuel=1,sideUpper=1,sideLower=1; private Component customName;
    private final ContainerData data=new ContainerData() {
        @Override public int get(int i) { return switch(i) { case 0->fuel; case 1->progress; case 2->sideFuel; case 3->sideUpper; case 4->sideLower; default->canProcess(recipe())?1:0; }; }
        @Override public void set(int i,int value) {}
        @Override public int getCount() { return 6; }
    };
    public AlloyBlockEntity(BlockPos pos,BlockState state) { super(AlloyContent.ENTITY.get(),pos,state); }
    public ContainerData menuData() { return data; }
    public int fuel() { return fuel; } public int progress() { return progress; }
    public int side(int slot) { return slot==0?sideUpper:slot==1?sideLower:sideFuel; }
    public void cycleSide(int slot) { if(slot==0) sideUpper=(sideUpper+1)%6; if(slot==1) sideLower=(sideLower+1)%6; if(slot==2) sideFuel=(sideFuel+1)%6; setChanged(); sync(); }
    @Override public Component getDisplayName() { return customName==null?Component.translatable("container.difurnace"):customName; }
    @Override public void setCustomName(Component name) { customName=name; setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id,Inventory player,Player viewer) { return new AlloyMenu(id,player,this,data,worldPosition); }
    public static void tick(Level level,BlockPos pos,BlockState state,AlloyBlockEntity machine) { if(level instanceof ServerLevel) machine.serverTick(); }
    private void serverTick() {
        int oldFuel=fuel,oldProgress=progress;
        ItemStack stack=inventory.get(2); int amount=fuelValue(stack);
        // Source loads fuel first, even if no recipe or output space is available.
        if(amount>0&&fuel<=MAX_FUEL-amount) { fuel+=amount; ItemStack remainder=stack.getCraftingRemainingItem(); stack.shrink(1); if(stack.isEmpty()) inventory.set(2,remainder); setChanged(); }
        var recipe=recipe();
        if(canProcess(recipe)) {
            fuel--; progress+=level.getBlockState(worldPosition.above()).is(AlloyContent.EXTENSION.get())?3:1;
            if(progress>=PROCESS_TIME) { progress-=PROCESS_TIME; process(recipe.value()); }
            if(fuel<0) fuel=0;
        } else progress=0;
        // This second source availability check intentionally happens after consumption and processing.
        if(!(canProcess(recipe())&&progress==0)) {
            BlockState current=getBlockState(); boolean lit=progress>0;
            if(current.getBlock() instanceof AlloyFurnaceBlock block&&block.active()!=lit) {
                BlockState replacement=(lit?AlloyContent.ON.get():AlloyContent.OFF.get()).defaultBlockState().setValue(AlloyFurnaceBlock.FACING,current.getValue(AlloyFurnaceBlock.FACING)).setValue(AlloyFurnaceBlock.COVERED,current.getValue(AlloyFurnaceBlock.COVERED));
                level.setBlock(worldPosition,replacement,3);
            }
            setChanged();
        }
        if(fuel!=oldFuel||progress!=oldProgress) { setChanged(); sync(); }
    }
    private void sync() { if(level instanceof ServerLevel server) server.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2); }
    private RecipeHolder<AlloyRecipe> recipe() {
        if(level==null||inventory.get(0).isEmpty()||inventory.get(1).isEmpty()) return null;
        var input=new AlloyInput(inventory.get(0),inventory.get(1));
        return level.getRecipeManager().getAllRecipesFor(AlloyContent.TYPE.get()).stream().sorted(Comparator.comparingInt(holder->holder.value().order())).filter(holder->holder.value().matches(input,level)).findFirst().orElse(null);
    }
    private boolean canProcess(RecipeHolder<AlloyRecipe> recipe) {
        if(fuel<=0||recipe==null) return false;
        ItemStack present=inventory.get(3),result=com.hbm.ore.OreProcessing.maximumOutput(recipe.value().result(),oreRate());
        return present.isEmpty()?result.getCount()<=result.getMaxStackSize():sameLegacy(present,result)&&present.getCount()+result.getCount()<=present.getMaxStackSize();
    }
    public static boolean sameLegacy(ItemStack a,ItemStack b) { return a.getItem()==b.getItem()&&a.getOrDefault(HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(HbmHazards.LEGACY_METADATA.get(),b.getDamageValue())); }
    private double oreRate() { return com.hbm.ore.OreProcessing.rate(new ItemStack[]{inventory.get(0),inventory.get(1)},new int[]{1,1}); }
    private void process(AlloyRecipe recipe) { var result=com.hbm.ore.OreProcessing.output(recipe.result(),oreRate(),level.random); if(inventory.get(3).isEmpty()) inventory.set(3,result); else inventory.get(3).grow(result.getCount()); inventory.get(0).shrink(1); inventory.get(1).shrink(1); setChanged(); }
    public static int fuelValue(ItemStack stack) {
        if(stack.isEmpty()) return 0; Item item=stack.getItem();
        if(item==Items.COAL||item==Items.CHARCOAL) return 200;
        if(item==Items.COAL_BLOCK) return 2000; if(item==Items.LAVA_BUCKET) return 12800; if(item==Items.BLAZE_ROD) return 1000; if(item==Items.BLAZE_POWDER) return 300;
        String path=BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("hbm")?BuiltInRegistries.ITEM.getKey(item).getPath():"";
        return switch(path) { case "block_coke_coal","block_coke_lignite","block_coke_petroleum"->4000; case "lignite","powder_lignite"->150; case "powder_coal","briquette_coal","briquette_lignite","briquette_wood"->200; case "coke_coal","coke_lignite","coke_petroleum","solid_fuel"->400; default->0; };
    }
    public static List<ItemStack> fuels() {
        var result=new ArrayList<ItemStack>(); for(Item item:List.of(Items.COAL,Items.CHARCOAL,Items.COAL_BLOCK,Items.LAVA_BUCKET,Items.BLAZE_ROD,Items.BLAZE_POWDER)) result.add(new ItemStack(item));
        for(String symbol:List.of("block_coke_coal","lignite","powder_lignite","powder_coal","briquette_coal","briquette_lignite","briquette_wood","coke_coal","coke_lignite","coke_petroleum","solid_fuel")) { Item item=BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("hbm",symbol)); if(item!=Items.AIR) result.add(new ItemStack(item)); } return result;
    }
    @Override public long energyStored() { return 0; } @Override public long energyCapacity() { return 0; }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.saveAdditional(tag,lookup); ContainerHelper.saveAllItems(tag,inventory,lookup); tag.putInt("powerTime",fuel); tag.putShort("cookTime",(short)progress); tag.putByteArray("modes",new byte[]{(byte)sideFuel,(byte)sideUpper,(byte)sideLower}); if(customName!=null) tag.putString("CustomName",Component.Serializer.toJson(customName,lookup)); }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.loadAdditional(tag,lookup); ContainerHelper.loadAllItems(tag,inventory,lookup); fuel=tag.getInt("powerTime"); progress=tag.getShort("cookTime"); byte[] modes=tag.getByteArray("modes"); if(modes.length>=3) { sideFuel=validSide(modes[0]); sideUpper=validSide(modes[1]); sideLower=validSide(modes[2]); } if(tag.contains("CustomName")) customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup); }
    private static int validSide(int side) { return side>=0&&side<6?side:1; }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag=new CompoundTag(); tag.putShort("powerTime",(short)fuel); tag.putShort("cookTime",(short)progress); tag.putByteArray("modes",new byte[]{(byte)sideFuel,(byte)sideUpper,(byte)sideLower}); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { fuel=tag.getShort("powerTime"); progress=tag.getShort("cookTime"); byte[] modes=tag.getByteArray("modes"); if(modes.length>=3) { sideFuel=validSide(modes[0]); sideUpper=validSide(modes[1]); sideLower=validSide(modes[2]); } }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
    @Override public int getContainerSize() { return 4; }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot,int count) { var result=ContainerHelper.removeItem(inventory,slot,count); if(!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory,slot); }
    @Override public void setItem(int slot,ItemStack stack) { inventory.set(slot,stack); if(stack.getCount()>64) stack.setCount(64); setChanged(); }
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { return slot!=3; }
    @Override public int[] getSlotsForFace(Direction face) { return SLOTS.clone(); }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face) { return slot>=0&&slot<3&&side(slot)==face.ordinal()&&canPlaceItem(slot,stack); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face) { return slot==3; }
}
