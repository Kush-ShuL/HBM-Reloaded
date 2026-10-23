package com.hbm.machine.press;

import com.hbm.api.machine.MachineView;
import com.hbm.item.StampItem;
import com.hbm.multiblock.NamedMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Direct translation of TileEntityMachinePress's server ordering, integer motion and fuel budget. */
public final class PressBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, NamedMultiblock, MachineView {
    public static final int MAX_SPEED = 400, MAX_PRESS = 200;
    private static final int[] AUTOMATION = {0,1,2,3};
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(13, ItemStack.EMPTY);
    private int speed, burnTime, press, delay, syncPress, interpolationTicks;
    private boolean retracting;
    private double renderPress, lastPress;
    private ItemStack syncStack = ItemStack.EMPTY;
    private Component customName;
    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return switch(index) { case 0 -> speed; case 1 -> burnTime & 65535; case 2 -> burnTime >>> 16; case 3 -> press; default -> 0; }; }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return 4; }
    };
    public PressBlockEntity(BlockPos pos, BlockState state) { super(PressContent.ENTITY.get(),pos,state); }
    public static int fuelTime(ItemStack stack) { return stack.isEmpty() ? 0 : stack.getBurnTime(RecipeType.SMELTING); }
    public int speed() { return speed; }
    public int burnTime() { return burnTime; }
    public double renderPress(float partial) { return lastPress + (renderPress-lastPress)*partial; }
    public ItemStack renderedInput() { return syncStack; }
    @Override public java.util.List<ProgressView> progressBars() { if (!(press > 0 || retracting)) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",press,MAX_PRESS,0x71C78B),new ProgressView("jade.hbm.speed",speed,MAX_SPEED,0x65A7D8)); }
    @Override public long energyStored() { return 0; }
    @Override public long energyCapacity() { return 0; }
    @Override public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public Component getDisplayName() { return customName == null ? Component.translatable("container.press") : customName; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory player, Player viewer) { return new PressMenu(id,player,this,data,worldPosition); }
    public static void tick(Level level, BlockPos pos, BlockState state, PressBlockEntity machine) {
        if (level.isClientSide) {
            machine.lastPress = machine.renderPress;
            if (machine.interpolationTicks > 0) { machine.renderPress += (machine.syncPress-machine.renderPress)/machine.interpolationTicks; machine.interpolationTicks--; }
            else machine.renderPress = machine.syncPress;
        } else machine.tickServer();
    }
    private ItemStack output() {
        return level.getRecipeManager().getRecipeFor(PressContent.RECIPE_TYPE.get(), new PressInput(getItem(2),getItem(1)), level)
                .map(recipe -> recipe.value().result()).orElse(ItemStack.EMPTY);
    }
    private boolean canProcess() {
        if (burnTime < 200 || getItem(1).isEmpty() || getItem(2).isEmpty()) return false;
        ItemStack output = output(), current = getItem(3);
        if (output.isEmpty()) return false;
        return current.isEmpty() || sameLegacyItem(current,output) && current.getCount()+output.getCount() <= current.getMaxStackSize();
    }
    // Source output matching ignores NBT, but keeps the old shared damage/metadata value.
    private static boolean sameLegacyItem(ItemStack a, ItemStack b) { return a.is(b.getItem()) && a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue())); }
    private void tickServer() {
        int oldSpeed = speed, oldPress = press, oldBurn = burnTime;
        boolean preheated = false;
        for (Direction side : Direction.values()) {
            BlockPos next = worldPosition.relative(side);
            if (level.hasChunkAt(next) && BuiltInRegistries.BLOCK.getKey(level.getBlockState(next).getBlock()).equals(ResourceLocation.fromNamespaceAndPath("hbm","press_preheater"))) { preheated=true; break; }
        }
        boolean canProcess = canProcess();
        if ((canProcess || retracting) && burnTime >= 200) speed = Math.min(MAX_SPEED,speed+(preheated?4:1));
        else speed = Math.max(0,speed-1);
        if (delay <= 0) {
            int stampSpeed = speed*25/MAX_SPEED;
            if (retracting) {
                press -= stampSpeed;
                if (press <= 0) { retracting=false; delay=5; }
            } else if (canProcess) {
                press += stampSpeed;
                if (press >= MAX_PRESS) {
                    level.playSound(null,worldPosition,SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("hbm","block.pressoperate")),SoundSource.BLOCKS,1.5F,1);
                    ItemStack output = output();
                    if (getItem(3).isEmpty()) inventory.set(3,output.copy()); else getItem(3).grow(output.getCount());
                    removeItem(2,1);
                    ItemStack stamp = getItem(1);
                    if (stamp.getMaxDamage() != 0) { stamp.setDamageValue(stamp.getDamageValue()+1); if (stamp.getDamageValue() >= stamp.getMaxDamage()) inventory.set(1,ItemStack.EMPTY); }
                    retracting=true; delay=5;
                    if (burnTime >= 200) burnTime-=200;
                    setChanged();
                }
            } else if (press > 0) retracting=true;
        } else delay--;
        ItemStack fuel = getItem(0);
        if (!fuel.isEmpty() && burnTime < 200 && fuelTime(fuel) > 0) {
            burnTime += fuelTime(fuel);
            if (fuel.getCount()==1 && fuel.hasCraftingRemainingItem()) inventory.set(0,fuel.getCraftingRemainingItem().copy());
            else fuel.shrink(1);
            setChanged();
        }
        if (oldSpeed!=speed || oldPress!=press || oldBurn!=burnTime) setChanged();
        if (oldSpeed!=speed || oldPress!=press || oldBurn!=burnTime || !ItemStack.matches(syncStack,getItem(2)) || level.getGameTime()%20==0) {
            syncStack = getItem(2).copy();
            level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);
        }
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag,registries); ContainerHelper.saveAllItems(tag,inventory,registries); tag.putInt("press",press); tag.putInt("burnTime",burnTime); tag.putInt("speed",speed); tag.putBoolean("ret",retracting); if(customName!=null) tag.putString("CustomName",Component.Serializer.toJson(customName,registries)); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag,registries); ContainerHelper.loadAllItems(tag,inventory,registries); press=tag.getInt("press"); burnTime=tag.getInt("burnTime"); speed=tag.getInt("speed"); retracting=tag.getBoolean("ret"); if(tag.contains("CustomName")) customName=Component.Serializer.fromJson(tag.getString("CustomName"),registries); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { CompoundTag tag=new CompoundTag(); tag.putInt("speed",speed); tag.putInt("burnTime",burnTime); tag.putInt("press",press); if(!getItem(2).isEmpty()) tag.put("input",getItem(2).save(registries)); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) { speed=tag.getInt("speed"); burnTime=tag.getInt("burnTime"); syncPress=tag.getInt("press"); syncStack=tag.contains("input")?ItemStack.parseOptional(registries,tag.getCompound("input")):ItemStack.EMPTY; interpolationTicks=2; }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) { handleUpdateTag(packet.getTag(),registries); }
    @Override public int getContainerSize() { return 13; }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot,int count) { ItemStack removed=ContainerHelper.removeItem(inventory,slot,count); if(!removed.isEmpty()) setChanged(); return removed; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory,slot); }
    @Override public void setItem(int slot,ItemStack stack) { inventory.set(slot,stack); if(stack.getCount()>getMaxStackSize(stack)) stack.setCount(getMaxStackSize(stack)); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public boolean canPlaceItem(int slot,ItemStack stack) { if(stack.getItem() instanceof StampItem) return slot==1; if(slot==0 && fuelTime(stack)>0) return true; return slot==2; }
    @Override public int[] getSlotsForFace(Direction face) { return AUTOMATION.clone(); }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face) { return canPlaceItem(slot,stack); }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face) { return slot==3; }
}
