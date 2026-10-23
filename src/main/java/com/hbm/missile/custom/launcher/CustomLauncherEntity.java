package com.hbm.missile.custom.launcher;

import api.hbm.item.IDesignatorItem;
import com.hbm.api.machine.MachineView;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.energy.*;
import com.hbm.entity.missile.CustomMissileEntity;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.missile.custom.*;
import com.hbm.multiblock.*;
import com.hbm.radar.IRadarCommandReceiver;
import com.hbm.registry.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import static com.hbm.missile.custom.CustomMissileParts.*;

/** Source CompactLauncher/LaunchTable common execution, with two real subclasses for their actual OC difference. */
public abstract class CustomLauncherEntity extends BlockEntity implements WorldlyContainer, MenuProvider, NamedMultiblock,
        EnergyReceiver, SidedEnergyConnector, StandardFluidReceiver, IRadarCommandReceiver, MachineView, MuffledMachine {
    public static final long MAX_POWER = 100000;
    protected final ItemStack[] slots = new ItemStack[8];
    public final boolean large;
    public final int maxSolid;
    public final FluidTank[] tanks;
    public long power;
    public int solid, height = 10;
    public PartSize padSize = PartSize.SIZE_10;
    public CustomMissileStruct load;
    public boolean muffled, tilted;
    private String customName;
    private final Map<BlockPos, PowerNode> nodes = new LinkedHashMap<>();
    private final Map<BlockPos, FluidPorts> fluidPorts = new LinkedHashMap<>();
    public static Consumer<Smoke> SMOKE = effect -> {};
    public record Smoke(Level level, double x, double y, double z, double mx, double my, double mz) {}
    protected CustomLauncherEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean large) {
        super(type, pos, state); this.large = large; maxSolid = large ? 100000 : 25000;
        tanks = new FluidTank[]{new FluidTank(HbmFluids.NONE, maxSolid), new FluidTank(HbmFluids.NONE, maxSolid)};
        Arrays.fill(slots, ItemStack.EMPTY);
    }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state) && DummyableBlock.isCore(state); }
    @Override public Component getDisplayName() { return customName == null || customName.isEmpty() ? Component.translatable(large ? "container.launchTable" : "container.compactLauncher") : Component.literal(customName); }
    @Override public void setCustomName(Component value) { customName = value.getString(); setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory player, Player opener) { return new CustomLauncherMenu(id, player, this, worldPosition); }
    public static void tick(Level level, BlockPos pos, BlockState state, CustomLauncherEntity machine) {
        if (level instanceof ServerLevel server) machine.serverTick(server);
        else if (!level.getEntitiesOfClass(CustomMissileEntity.class, new AABB(pos.getX() - .5, pos.getY(), pos.getZ() - .5, pos.getX() + 1.5, pos.getY() + 10, pos.getZ() + 1.5)).isEmpty()) {
            float speed = machine.large ? .65F : .5F;
            for (int i = 0; i < 15; i++) {
                boolean axis = level.random.nextBoolean(); float mx = (float) (axis ? 0 : level.random.nextGaussian() * speed), mz = (float) (!axis ? 0 : level.random.nextGaussian() * speed);
                SMOKE.accept(new Smoke(level, pos.getX() + .5, pos.getY() + .25, pos.getZ() + .5, mx, 0, mz));
            }
        }
    }
    private void serverTick(ServerLevel server) {
        updatePorts(server); updateTypes(); tanks[0].loadTank(2, 6, slots); tanks[1].loadTank(3, 7, slots); chargeBattery();
        if (slots[4].is(HbmContent.item("rocket_fuel")) && solid + 250 <= maxSolid) { removeItem(4, 1); solid += 250; }
        var status = clientState(); var parts = new CompoundTag();
        var struct = CustomMissileItem.getStruct(slots[0]); parts.put("multipart", struct == null ? new CustomMissileStruct((CustomMissilePartItem) null, null, null, null).write() : struct.write());
        CustomLauncherSync.send(server, this, status, 50); CustomLauncherSync.send(server, this, parts, 250);
        int range = large ? 4 : 1;
        outer: for (int x = -range; x <= range; x++) for (int z = -range; z <= range; z++)
            if (server.hasNeighborSignal(worldPosition.offset(x, 0, z)) && canLaunch()) { launchFromDesignator(); break outer; }
        setChanged();
    }
    private void chargeBattery() {
        if (power < 0) { power = 0; return; } if (power > MAX_POWER) { power = MAX_POWER; return; }
        var stack = slots[5]; if (stack.getItem() instanceof CreativeBatteryItem) { power = MAX_POWER; return; }
        if (stack.getItem() instanceof HbmBattery battery) { long amount = Math.min(Math.min(MAX_POWER - power, battery.getDischargeRate(stack)), battery.getCharge(stack)); power += amount; battery.dischargeBattery(stack, amount); }
    }
    public CustomMissileStruct missile() { return CustomMissileItem.getStruct(slots[0]); }
    public boolean isMissileValid() { var m = missile(); return m != null && m.fuselage() != null && m.fuselage().definition.top() == (large ? padSize : PartSize.SIZE_10); }
    public boolean hasDesignator() { return slots[1].getItem() instanceof IDesignatorItem target && target.isReady(level, slots[1], worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()); }
    public boolean hasFuel() { return solidState() != 0 && liquidState() != 0 && oxidizerState() != 0; }
    public boolean canLaunch() { return power >= MAX_POWER * .75 && isMissileValid() && (large || hasDesignator()) && hasFuel(); }
    private Definition fuselage() { var missile = missile(); return missile == null || missile.fuselage() == null ? null : missile.fuselage().definition; }
    public int solidState() { var f = fuselage(); return f == null || !f.enumName(0).equals("SOLID") ? -1 : solid >= f.number(1) ? 1 : 0; }
    public int liquidState() { var f = fuselage(); return f == null || f.enumName(0).equals("SOLID") ? -1 : tanks[0].getFill() >= f.number(1) ? 1 : 0; }
    public int oxidizerState() { var f = fuselage(); return f == null || f.enumName(0).equals("SOLID") || f.enumName(0).equals("XENON") ? -1 : tanks[1].getFill() >= f.number(1) ? 1 : 0; }
    private void updateTypes() {
        var f = fuselage(); if (f == null) return;
        switch (FuelType.valueOf(f.enumName(0))) {
            case KEROSENE -> { tanks[0].setTankType(HbmFluids.KEROSENE); tanks[1].setTankType(HbmFluids.PEROXIDE); }
            case HYDROGEN -> { tanks[0].setTankType(HbmFluids.HYDROGEN); tanks[1].setTankType(HbmFluids.OXYGEN); }
            case XENON -> tanks[0].setTankType(HbmFluids.XENON);
            case BALEFIRE -> { tanks[0].setTankType(HbmFluids.BALEFIRE); tanks[1].setTankType(HbmFluids.PEROXIDE); }
            default -> { }
        }
    }
    public void launchFromDesignator() {
        if (slots[1].getItem() instanceof IDesignatorItem target && target.isReady(level, slots[1], worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) {
            var coords = target.getCoords(level, slots[1], worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()); launchTo(Mth.floor(coords.x), Mth.floor(coords.z));
        }
    }
    public void launchTo(int x, int z) {
        if (level == null || level.isClientSide) return;
        level.playSound(null, worldPosition, HbmSounds.get("weapon.missileTakeOff"), SoundSource.BLOCKS, 10, 1);
        var data = slots[0].getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        var chip = CustomMissileStruct.readPart(data, "chip"); var missile = missile();
        float c = chip.definition.number(0), f = missile.fins() == null ? 1 : missile.fins().definition.number(0);
        var error = LegacyVector.createVectorHelper(worldPosition.getX() - x, 0, worldPosition.getZ() - z); error.xCoord *= c * f; error.zCoord *= c * f;
        // Source passes nextFloat()*360 directly to the radian rotate method; preserve that exact distribution and RNG call.
        error.rotateAroundY(level.random.nextFloat() * 360);
        level.addFreshEntity(new CustomMissileEntity(level, worldPosition.getX() + .5F, worldPosition.getY() + 2.5F, worldPosition.getZ() + .5F, x + (int) error.xCoord, z + (int) error.zCoord, missile));
        int amount = (int) missile.fuselage().definition.number(1);
        switch (FuelType.valueOf(missile.fuselage().definition.enumName(0))) {
            case SOLID -> solid -= amount;
            case XENON -> tanks[0].setFill(tanks[0].getFill() - amount);
            default -> { tanks[0].setFill(tanks[0].getFill() - amount); tanks[1].setFill(tanks[1].getFill() - amount); }
        }
        power -= MAX_POWER * .75; slots[0] = ItemStack.EMPTY; setChanged();
    }
    @Override public boolean sendCommandPosition(int x, int y, int z) { if (!canLaunch()) return false; launchTo(x, z); return true; }
    @Override public boolean sendCommandEntity(Entity target) {
        // Actual source sends floor(target.posX) for BOTH coordinates; do not silently substitute the modern Z value.
        return sendCommandPosition(Mth.floor(target.getX()), worldPosition.getY(), Mth.floor(target.getX()));
    }
    protected List<Port> ports() {
        var result = new ArrayList<Port>(); int radius = large ? 4 : 1;
        // Keep original port insertion order for the four refinery corners / large perimeter.
        if (!large) {
            result.add(new Port(worldPosition.offset(-1, 0, -1), new Direction[]{Direction.WEST, Direction.NORTH}));
            result.add(new Port(worldPosition.offset(1, 0, -1), new Direction[]{Direction.EAST, Direction.NORTH}));
            result.add(new Port(worldPosition.offset(1, 0, 1), new Direction[]{Direction.EAST, Direction.SOUTH}));
            result.add(new Port(worldPosition.offset(-1, 0, 1), new Direction[]{Direction.WEST, Direction.SOUTH}));
        } else {
            for (int z = radius; z >= -radius; z--) result.add(new Port(worldPosition.offset(radius, 0, z), z == radius ? new Direction[]{Direction.EAST, Direction.SOUTH} : z == -radius ? new Direction[]{Direction.EAST, Direction.NORTH} : new Direction[]{Direction.EAST}));
            for (int x = radius - 1; x >= -radius; x--) result.add(new Port(worldPosition.offset(x, 0, -radius), x == -radius ? new Direction[]{Direction.WEST, Direction.NORTH} : new Direction[]{Direction.NORTH}));
            for (int z = -radius + 1; z <= radius; z++) result.add(new Port(worldPosition.offset(-radius, 0, z), z == radius ? new Direction[]{Direction.WEST, Direction.SOUTH} : new Direction[]{Direction.WEST}));
            for (int x = -radius + 1; x < radius; x++) result.add(new Port(worldPosition.offset(x, 0, radius), new Direction[]{Direction.SOUTH}));
        }
        return result;
    }
    protected record Port(BlockPos pos, Direction[] sides) {}
    private void updatePorts(ServerLevel server) {
        if (nodes.isEmpty() || nodes.values().stream().anyMatch(PowerNode::isExpired)) {
            disconnect();
            for (var port : ports()) {
                var connections = Arrays.stream(port.sides()).map(side -> new NodeConnection(port.pos().relative(side), side)).toArray(NodeConnection[]::new);
                var node = new PowerNode(port.pos()).setConnections(connections); HbmEnergy.nodespace(server).createNode(node); nodes.put(port.pos(), node);
                fluidPorts.put(port.pos(), new FluidPorts(new BlockPos[]{port.pos()}, connections));
            }
        }
        for (var node : nodes.values()) if (node.hasValidNet()) node.addReceiver(this);
        for (var port : fluidPorts.values()) port.update(server, this);
    }
    private void disconnect() {
        if (level instanceof ServerLevel server) { for (var node : nodes.values()) HbmEnergy.nodespace(server).destroyNode(node); for (var port : fluidPorts.values()) port.remove(server); }
        nodes.clear(); fluidPorts.clear();
    }
    @Override public void onChunkUnloaded() { disconnect(); super.onChunkUnloaded(); }
    @Override public void setRemoved() { disconnect(); super.setRemoved(); }
    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return MAX_POWER; }
    @Override public void setPower(long value) { power = value; setChanged(); }
    @Override public boolean isLoaded() { return !isRemoved() && level != null; }
    @Override public boolean canConnect(Direction side) { return side != null && side != Direction.UP; }
    @Override public boolean canConnect(HbmFluidType type, Direction side) { return canConnect(side); }
    @Override public FluidTank[] getAllTanks() { return tanks; }
    @Override public FluidTank[] getReceivingTanks() { return tanks; }
    @Override public long energyStored() { return power; }
    @Override public long energyCapacity() { return MAX_POWER; }
    @Override public List<TankView> tanks() { return Arrays.stream(tanks).map(t -> new TankView(t.getTankType(), t.getFill(), t.getMaxFill())).toList(); }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; setChanged(); }
    protected CompoundTag clientState() {
        var tag = new CompoundTag(); tag.putLong("power", power); tag.putInt("solidfuel", solid); tag.putInt("padSize", padSize.ordinal());
        tanks[0].writeToNBT(tag, "fuel"); tanks[1].writeToNBT(tag, "oxidizer"); return tag;
    }
    public void receiveClient(CompoundTag tag) {
        if (tag.contains("multipart")) load = CustomMissileStruct.read(tag.getCompound("multipart"));
        if (tag.contains("power")) { power = tag.getLong("power"); solid = tag.getInt("solidfuel"); if (large) padSize = PartSize.values()[tag.getInt("padSize")]; tanks[0].readFromNBT(tag, "fuel"); tanks[1].readFromNBT(tag, "oxidizer"); }
        if (tag.contains("name")) customName = tag.getString("name");
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); tag.merge(clientState()); if (!large) tag.remove("padSize");
        tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
        var list = new ListTag(); for (int slot = 0; slot < slots.length; slot++) if (!slots[slot].isEmpty()) { var entry = (CompoundTag) slots[slot].save(lookup); entry.putByte("slot", (byte) slot); list.add(entry); }
        tag.put("items", list); if (customName != null) tag.putString("name", customName);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); receiveClient(tag); muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
        Arrays.fill(slots, ItemStack.EMPTY); for (var value : tag.getList("items", Tag.TAG_COMPOUND)) { var entry = (CompoundTag) value; int slot = entry.getByte("slot"); if (slot >= 0 && slot < slots.length) slots[slot] = ItemStack.parseOptional(lookup, entry); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag = clientState(); var missile = missile(); if (missile != null) tag.put("multipart", missile.write()); if (customName != null) tag.putString("name", customName); return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { receiveClient(tag); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    @Override public int getContainerSize() { return 8; }
    @Override public boolean isEmpty() { return Arrays.stream(slots).allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots[slot]; }
    @Override public ItemStack removeItem(int slot, int amount) { var result = slots[slot].split(amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { var result = slots[slot]; slots[slot] = ItemStack.EMPTY; return result; }
    @Override public void setItem(int slot, ItemStack stack) { slots[slot] = stack; if (stack.getCount() > 64) stack.setCount(64); setChanged(); }
    @Override public void clearContent() { Arrays.fill(slots, ItemStack.EMPTY); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
}
