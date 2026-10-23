package com.hbm.machine.centrifuge;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.CreativeBatteryItem;
import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.multiblock.NamedMultiblock;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 solid centrifuge: power drains before availability is evaluated, four independent output lanes. */
public final class CentrifugeBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,EnergyReceiver,SidedEnergyConnector,MachineView,NamedMultiblock {
    private static final int[] IO={0,2,3,4,5};
    public static Consumer<CentrifugeBlockEntity> CLIENT_TICK=ignored->{},CLIENT_REMOVED=ignored->{};
    private final NonNullList<ItemStack> inventory=NonNullList.withSize(8,ItemStack.EMPTY);
    private PowerNode node;
    private long power;
    private int progress,speed=1,consumption=CentrifugeConfig.baseConsumption;
    private boolean progressing;
    private Component customName;
    private final ContainerData data=new ContainerData() {
        @Override public int get(int index) {
            if(index<4) return (int)(power>>>(index*16))&65535;
            if(index<6) return CentrifugeConfig.maxPower>>>((index-4)*16)&65535;
            if(index<8) return progress>>>((index-6)*16)&65535;
            if(index<10) return CentrifugeConfig.processingSpeed>>>((index-8)*16)&65535;
            return index==10?(progressing?1:0):index==11?speed:consumption>>>((index-12)*16)&65535;
        }
        @Override public void set(int index,int value) {}
        @Override public int getCount() { return 14; }
    };
    public CentrifugeBlockEntity(BlockPos pos,BlockState state) { super(CentrifugeContent.ENTITY.get(),pos,state); }
    public ContainerData menuData() { return data; }
    public int progress() { return progress; }
    public boolean progressing() { return progressing; }
    @Override public Component getDisplayName() { return customName==null?Component.translatable("container.centrifuge"):customName; }
    @Override public void setCustomName(Component name) { customName=name; setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player) { return new CentrifugeMenu(id,inventory,this,data,worldPosition); }
    public static void tick(Level level,BlockPos pos,BlockState state,CentrifugeBlockEntity machine) { if(level instanceof ServerLevel server) machine.serverTick(server); else CLIENT_TICK.accept(machine); }
    private void serverTick(ServerLevel server) {
        ensureNode(server); long oldPower=power; int oldProgress=progress; boolean oldActive=progressing;
        chargeFromItem();
        int speedUpgrade=0,powerUpgrade=0,overdriveUpgrade=0;
        for(int slot=6;slot<=7;slot++) {
            ItemStack stack=inventory.get(slot);
            if(stack.getItem() instanceof FurnaceUpgradeItem item) {
                if(item.type()==FurnaceUpgradeItem.Type.SPEED) speedUpgrade=Math.min(3,speedUpgrade+item.tier());
                if(item.type()==FurnaceUpgradeItem.Type.POWER) powerUpgrade=Math.min(3,powerUpgrade+item.tier());
            }
            if(stack.getItem() instanceof OverdriveUpgradeItem item) overdriveUpgrade=Math.min(3,overdriveUpgrade+item.tier());
        }
        speed=(1+speedUpgrade)*(1+overdriveUpgrade*5);
        consumption=(CentrifugeConfig.baseConsumption+speedUpgrade*CentrifugeConfig.baseConsumption+overdriveUpgrade*CentrifugeConfig.baseConsumption*50)/(1+powerUpgrade);
        // The original checks old progress, charges even a now-blocked output, and never requires a full tick's HE.
        if(power>0&&progress>0) power=Math.max(0,power-consumption);
        RecipeHolder<CentrifugeRecipe> recipe=recipe(inventory.get(0));
        progressing=power>0&&canProcess(recipe);
        if(progressing) {
            progress+=speed;
            if(progress>=CentrifugeConfig.processingSpeed) { progress=0; process(recipe.value().outputs()); }
        } else progress=0;
        if(oldPower!=power||oldProgress!=progress||oldActive!=progressing) { setChanged(); server.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2); }
    }
    private void chargeFromItem() {
        ItemStack stack=inventory.get(1);
        if(stack.getItem() instanceof CreativeBatteryItem) { power=getMaxPower(); return; }
        if(stack.getItem() instanceof HbmBattery battery) {
            long amount=Math.min(Math.min(getMaxPower()-power,battery.getDischargeRate(stack)),battery.getCharge(stack));
            battery.dischargeBattery(stack,amount); power+=amount;
        }
    }
    private RecipeHolder<CentrifugeRecipe> recipe(ItemStack input) {
        if(level==null||input.isEmpty()) return null;
        var available=level.getRecipeManager().getAllRecipesFor(CentrifugeContent.TYPE.get()); var single=new SingleRecipeInput(input);
        // Legacy getOutput first probes the singular ComparableStack map, then scans general AStack matches.
        for(var recipe:available) if(recipe.value().exactInput()&&recipe.value().matches(single,level)) return recipe;
        for(var recipe:available) if(recipe.value().matches(single,level)) return recipe;
        return null;
    }
    private boolean canProcess(RecipeHolder<CentrifugeRecipe> recipe) {
        if(recipe==null) return false;
        List<ItemStack> output=recipe.value().outputs();
        for(int i=0;i<Math.min(4,output.size());i++) {
            ItemStack present=inventory.get(i+2),result=com.hbm.ore.OreProcessing.maximumOutput(output.get(i),com.hbm.ore.OreProcessing.rate(inventory.get(0)));
            if(result.isEmpty()) continue;
            if(present.isEmpty()) { if(result.getCount()>result.getMaxStackSize())return false;continue; }
            if(!sameLegacy(present,result)||present.getCount()+result.getCount()>result.getMaxStackSize()) return false;
        }
        return true;
    }
    private static boolean sameLegacy(ItemStack a,ItemStack b) { return a.getItem()==b.getItem()&&a.getOrDefault(HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(HbmHazards.LEGACY_METADATA.get(),b.getDamageValue())); }
    private void process(List<ItemStack> output) {
        double rate=com.hbm.ore.OreProcessing.rate(inventory.get(0));
        for(int i=0;i<Math.min(4,output.size());i++) if(!output.get(i).isEmpty()) { var result=com.hbm.ore.OreProcessing.output(output.get(i),rate,level.random); if(inventory.get(i+2).isEmpty()) inventory.set(i+2,result); else inventory.get(i+2).grow(result.getCount()); }
        removeItem(0,1); setChanged();
    }
    private void ensureNode(ServerLevel server) {
        if(node!=null&&!node.isExpired()) return;
        node=new PowerNode(worldPosition); var connections=new java.util.ArrayList<NodeConnection>();
        for(Direction side:Direction.Plane.HORIZONTAL) connections.add(new NodeConnection(worldPosition.relative(side),side));
        node.setConnections(connections.toArray(NodeConnection[]::new)); node.addReceiver(this); HbmEnergy.nodespace(server).createNode(node);
    }
    private void disconnect() { if(node!=null&&level instanceof ServerLevel server) HbmEnergy.nodespace(server).destroyNode(node); node=null; }
    @Override public void onLoad() { super.onLoad(); if(level instanceof ServerLevel server) ensureNode(server); }
    @Override public void onChunkUnloaded() { disconnect(); if(level!=null&&level.isClientSide) CLIENT_REMOVED.accept(this); super.onChunkUnloaded(); }
    @Override public void setRemoved() { disconnect(); if(level!=null&&level.isClientSide) CLIENT_REMOVED.accept(this); super.setRemoved(); }
    @Override public boolean isLoaded() { return level!=null&&!isRemoved(); }
    @Override public boolean canConnect(Direction side) { return side!=null&&side.getAxis()!=Direction.Axis.Y; }
    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return CentrifugeConfig.maxPower; }
    @Override public void setPower(long power) { this.power=Math.max(0,Math.min(getMaxPower(),power)); setChanged(); }
    @Override public java.util.List<ProgressView> progressBars() { if (!(progress > 0 || progressing)) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",progress,CentrifugeConfig.processingSpeed,0x71C78B)); }
    @Override public long energyStored() { return power; }
    @Override public long energyCapacity() { return getMaxPower(); }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.saveAdditional(tag,lookup); ContainerHelper.saveAllItems(tag,inventory,lookup); tag.putLong("power",power); tag.putShort("progress",(short)progress); if(customName!=null) tag.putString("CustomName",Component.Serializer.toJson(customName,lookup)); }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.loadAdditional(tag,lookup); ContainerHelper.loadAllItems(tag,inventory,lookup); power=tag.getLong("power"); progress=tag.getShort("progress"); if(tag.contains("CustomName")) customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag=new CompoundTag(); tag.putLong("power",power); tag.putInt("progress",progress); tag.putBoolean("isProgressing",progressing); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { power=tag.getLong("power"); progress=tag.getInt("progress"); progressing=tag.getBoolean("isProgressing"); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
    @Override public int getContainerSize() { return 8; }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot,int count) { var result=ContainerHelper.removeItem(inventory,slot,count); if(!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory,slot); }
    @Override public void setItem(int slot,ItemStack stack) { inventory.set(slot,stack); if(stack.getCount()>64) stack.setCount(64); setChanged(); }
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { return slot==0?recipe(stack)!=null:slot==1&&stack.getItem() instanceof HbmBattery; }
    @Override public int[] getSlotsForFace(Direction side) { return IO.clone(); }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side) { return canPlaceItem(slot,stack); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side) { return slot>1; }
}
