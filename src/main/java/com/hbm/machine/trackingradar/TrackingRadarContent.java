package com.hbm.machine.trackingradar;

import com.hbm.energy.*;
import com.hbm.item.battery.HbmBattery;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.satellite.SatelliteSavedData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Upstream tracking radar multiblock, menu and client deployment animation. */
public final class TrackingRadarContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<TrackingRadarBlock> BLOCK = BLOCKS.register("machine_tracking_radar", TrackingRadarBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_tracking_radar", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadarEntity>> ENTITY = ENTITIES.register("tracking_radar", () -> BlockEntityType.Builder.of(RadarEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<TrackingRadarMenu>> MENU = MENUS.register("tracking_radar",
            () -> new MenuType<>((IContainerFactory<TrackingRadarMenu>) TrackingRadarMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final class RadarEntity extends BaseContainerBlockEntity implements EnergyReceiver, SidedEnergyConnector,
            api.hbm.redstoneoverradio.IRORValueProvider, api.hbm.redstoneoverradio.IRORInteractive {
        public static final float SPEED=.25F, ACTIVE_ROT=-15F, ACTIVE_LIFT=-45F, INACTIVE_ROT=0F, INACTIVE_LIFT=-85F;
        public static final long CAPACITY=100_000_000;
        private final LinkedHashMap<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
        private NonNullList<ItemStack> items=NonNullList.withSize(1,ItemStack.EMPTY);
        private long power;
        private int mode = -1;
        private final List<int[]> knownSatellites = new ArrayList<>();
        public float rot=INACTIVE_ROT,prevRot=INACTIVE_ROT,lift=INACTIVE_LIFT,prevLift=INACTIVE_LIFT;
        public RadarEntity(BlockPos pos, BlockState state) { super(ENTITY.get(), pos, state); }
        public void serverTick(ServerLevel level){
            for(var pos:com.hbm.machine.refinery.RefineryBlockEntity.portPositions(worldPosition)){
                if(!level.hasChunkAt(pos)||!level.getBlockState(pos).is(BLOCK.get())||!DummyableBlock.isPort(level.getBlockState(pos))){
                    var node=nodes.remove(pos);
                    if(node!=null)HbmEnergy.nodespace(level).destroyNode(node);
                    continue;
                }
                var node=nodes.get(pos);
                if(node==null||node.isExpired()){
                    var sides=com.hbm.machine.refinery.RefineryBlockEntity.portSides(worldPosition,pos);
                    node=new PowerNode(pos).setConnections(java.util.Arrays.stream(sides)
                            .map(side->new NodeConnection(pos.relative(side),side)).toArray(NodeConnection[]::new));
                    HbmEnergy.nodespace(level).createNode(node);nodes.put(pos,node);
                }
                if(node.hasValidNet())node.addReceiver(this);
            }
            var stack=getItem(0);
            if(stack.getItem() instanceof com.hbm.item.battery.CreativeBatteryItem&&power<CAPACITY)setPower(CAPACITY);
            else if(stack.getItem() instanceof HbmBattery battery&&power<CAPACITY){
                long amount=Math.min(CAPACITY-power,Math.min(battery.getDischargeRate(stack),battery.getCharge(stack)));
                if(amount>0){battery.dischargeBattery(stack,amount);setPower(power+amount);}
            }
        }
        @Override public boolean isLoaded(){return !isRemoved()&&level!=null;}
        @Override public long getPower(){return power;}
        @Override public long getMaxPower(){return CAPACITY;}
        @Override public void setPower(long value){power=Math.clamp(value,0,CAPACITY);setChanged();}
        @Override public int getContainerSize(){return 1;}
        @Override protected NonNullList<ItemStack> getItems(){return items;}
        @Override protected void setItems(NonNullList<ItemStack> items){this.items=items;}
        @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&stack.getItem() instanceof HbmBattery;}
        @Override protected Component getDefaultName(){return Component.translatable("block.hbm.machine_tracking_radar");}
        @Override protected AbstractContainerMenu createMenu(int id, Inventory inv) {
            return new TrackingRadarMenu(id, inv, this);
        }
        @Override
        public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
            List<TrackingRadarMenu.Contact> contacts = new ArrayList<>();
            if (level instanceof ServerLevel server && !knownSatellites.isEmpty()) {
                var satellites = SatelliteSavedData.getData(server);
                for (int[] entry : knownSatellites) {
                    var satellite = satellites.getSatFromFreq(entry[0]);
                    if (satellite != null) contacts.add(new TrackingRadarMenu.Contact(satellite.getID(), entry[1], entry[2], entry[3]));
                }
            }
            buffer.writeCollection(contacts, (out, contact) -> {
                out.writeVarInt(contact.type());
                out.writeVarInt(contact.x());
                out.writeVarInt(contact.y());
                out.writeVarInt(contact.z());
            });
        }
        int mode() { return mode; }
        boolean toggleMode(int selected) {
            if (selected < 0 || selected > 2) return false;
            mode = mode == selected ? -1 : selected;
            setChanged();
            return true;
        }
        @Override public String[] getFunctionInfo(){return new String[0];}
        @Override public String provideRORValue(String name){return null;}
        @Override public String runRORFunction(String name,String[] args){return null;}
        @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
            super.saveAdditional(tag,lookup);net.minecraft.world.ContainerHelper.saveAllItems(tag,items,lookup);tag.putLong("power",power);
            tag.putInt("mode", mode);
            tag.putInt("satCount", knownSatellites.size());
            for (int i = 0; i < knownSatellites.size(); i++) tag.putIntArray("s" + i, knownSatellites.get(i));
        }
        @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
            super.loadAdditional(tag,lookup);items=NonNullList.withSize(1,ItemStack.EMPTY);
            net.minecraft.world.ContainerHelper.loadAllItems(tag,items,lookup);power=Math.clamp(tag.getLong("power"),0,CAPACITY);
            mode = tag.contains("mode") ? Math.clamp(tag.getInt("mode"), -1, 2) : -1;
            knownSatellites.clear();
            for (int i = 0; i < tag.getInt("satCount"); i++) {
                int[] entry = tag.getIntArray("s" + i);
                if (entry.length == 4) knownSatellites.add(entry);
            }
        }
        @Override public void setRemoved(){removeNodes();super.setRemoved();}
        @Override public void onChunkUnloaded(){removeNodes();super.onChunkUnloaded();}
        private void removeNodes(){
            if(level instanceof ServerLevel server)nodes.values().forEach(HbmEnergy.nodespace(server)::destroyNode);
            nodes.clear();
        }
        public void clientTick(){
            prevRot=rot;prevLift=lift;
            rot=net.minecraft.util.Mth.approach(rot,ACTIVE_ROT,SPEED);
            lift=net.minecraft.util.Mth.approach(lift,ACTIVE_LIFT,SPEED);
        }
    }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return "missile".equals(tab) ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private TrackingRadarContent() { }
}
