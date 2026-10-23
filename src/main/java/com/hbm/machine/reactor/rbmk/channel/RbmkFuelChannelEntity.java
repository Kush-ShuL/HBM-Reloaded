package com.hbm.machine.reactor.rbmk.channel;

import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.machine.reactor.rbmk.*;
import com.hbm.machine.reactor.rbmk.column.*;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.machine.reactor.rbmk.core.*;
import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import com.hbm.satellite.SatelliteRayEvents;
import com.hbm.multiblock.NamedMultiblock;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Real normal/ReaSim TileEntityRBMKRod, sharing the one authentic fuel item and neutron kernel. */
public final class RbmkFuelChannelEntity extends RbmkColumnEntity implements WorldlyContainer, MenuProvider, NamedMultiblock, RbmkFluxColumn.Fuel, RbmkLoadable, ComputerControl, IRORValueProvider {
    private ItemStack fuel = ItemStack.EMPTY; private Component customName;
    public double fluxFastRatio, fluxQuantity, lastFluxQuantity, lastFluxRatio;
    public boolean hasRod; public int rodColor;
    private String fuelYield, fuelXenon, fuelHeat;
    public RbmkFuelChannelEntity(BlockPos pos, BlockState state) { super(RbmkFuelChannels.ENTITY.get(), pos, state); }
    public RbmkChannelKind kind() { return ((RbmkFuelChannelBlock)getBlockState().getBlock()).kind; }
    @Override public boolean isModerated() { return kind().moderated; }
    @Override public boolean reasimFuel() { return kind().reasim; }
    @Override public int trackingRange() { return 25; }
    @Override public boolean hasFuelRod() { return fuel.getItem() instanceof RbmkFuelRodItem; }
    @Override public double lastFluxQuantity() { return lastFluxQuantity; }
    @Override public RbmkFluxColumn.Type neutronType() { return RbmkFluxColumn.Type.ROD; }
    @Override public String consoleType() { return reasimFuel() ? "FUEL_SIM" : "FUEL"; }
    @Override public void receiveFlux(RbmkNeutronField.Stream stream) { double fast = fluxQuantity * fluxFastRatio, incoming = stream.fluxQuantity * stream.fluxRatio; fluxQuantity += stream.fluxQuantity; fluxFastRatio = (fast + incoming) / fluxQuantity; }
    @Override public boolean coldEnoughForAutoloader() { return !hasFuelRod() || RbmkFuelRodItem.getHullHeat(fuel) <= 1000; }
    public boolean coldEnoughForManual() { return !hasFuelRod() || RbmkFuelRodItem.getHullHeat(fuel) <= 200; }
    public static void tick(Level level, BlockPos pos, BlockState state, RbmkFuelChannelEntity machine) { if (level instanceof ServerLevel world) machine.serverTick(world); }
    private void serverTick(ServerLevel world) {
        if (fuel.getItem() instanceof RbmkFuelRodItem rod) {
            if (fluxQuantity > 0 && world.getGameTime() % 200 == 0) SatelliteRayEvents.reportEvent(world, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SatelliteRayEvents.RayEvent.INFO_NUCLEAR, 300);
            rodColor = rod.colorTint; double ratioOut, quantityOut;
            if (rod.specialFluxCurve) { ratioOut = rod.fluxRatioOut(fluxFastRatio, RbmkFuelRodItem.getEnrichment(fuel)); quantityOut = rod.burn(world, fuel, rod.fluxFromRatio(fluxQuantity, fluxFastRatio)); }
            else { ratioOut = rod.rType == NeutronType.SLOW ? 0 : 1; quantityOut = rod.burn(world, fuel, fluxFromType(rod.nType)); }
            rod.updateHeat(world, fuel, 1); heat += rod.provideHeat(world, fuel, heat, 1);
            if (!hasLid()) HbmRadiation.incrementRad(world, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), (float)(fluxQuantity * .05F));
            baseTick(world);
            if (heat > maxHeat()) {
                if (RbmkDials.getMeltdownsDisabled(world)) LegacyFluidEffects.gasFlame(world, worldPosition.getX() + .5, worldPosition.getY() + RbmkDials.getColumnHeight(world) + .5, worldPosition.getZ() + .5, 0, .2, 0);
                else meltdown();
                lastFluxRatio = lastFluxQuantity = fluxQuantity = 0; return;
            }
            if (heat > 10000) heat = 10000;
            lastFluxQuantity = fluxQuantity; lastFluxRatio = fluxFastRatio; fluxQuantity = fluxFastRatio = 0;
            RbmkNeutronField.spread(world, this, quantityOut, ratioOut); hasRod = true;
        } else { lastFluxRatio = lastFluxQuantity = fluxQuantity = fluxFastRatio = 0; hasRod = false; baseTick(world); }
        setChanged();
    }
    private double fluxFromType(NeutronType type) { double fast = fluxQuantity * fluxFastRatio, slow = fluxQuantity * (1 - fluxFastRatio); return switch (type) { case SLOW -> slow + fast * .5; case FAST -> fast + slow * .3; case ANY -> fluxQuantity; }; }
    /** Called only by the genuine block-removal path; chunk unloading must not masquerade as a broken hot channel. */
    public void broken() { if (level instanceof ServerLevel && explodeOnBroken && hasFuelRod() && RbmkFuelRodItem.getHullHeat(fuel) >= 1500 && !RbmkDials.getMeltdownsDisabled(level)) meltdown(); }
    @Override public void onMelt(int reduce) {
        boolean moderated = isModerated(); int metadata = getBlockState().getValue(com.hbm.multiblock.DummyableBlock.LEGACY_META), height = RbmkDials.getColumnHeight(level);
        reduce = net.minecraft.util.Mth.clamp(reduce, 1, height); if (level.random.nextInt(3) == 0) reduce++;
        boolean corium = hasFuelRod(); if (corium && ((RbmkFuelRodItem)fuel.getItem()).variant.equals("drx")) RbmkColumnBlock.digamma = true;
        fuel = ItemStack.EMPTY;
        if (corium) {
            var block = (com.hbm.block.fluid.LegacyFluidBlock)HbmContent.block("corium_block");
            var state = block.defaultBlockState().setValue(com.hbm.block.fluid.LegacyFluidBlock.LEVEL, 5);
            for (int i = height; i >= 0; i--) level.setBlock(worldPosition.above(i), state, Block.UPDATE_ALL);
            int count = 1 + level.random.nextInt(RbmkDials.getColumnHeight(level)); for (int i = 0; i < count; i++) spawnDebris(RbmkDebrisEntity.Type.FUEL);
        } else standardMelt(reduce);
        if (moderated) { int count = 2 + level.random.nextInt(2); for (int i = 0; i < count; i++) spawnDebris(RbmkDebrisEntity.Type.GRAPHITE); }
        spawnDebris(RbmkDebrisEntity.Type.ELEMENT); if (metadata == 15) spawnDebris(RbmkDebrisEntity.Type.LID);
    }
    @Override public CompoundTag consoleData() { var tag = new CompoundTag(); if (fuel.getItem() instanceof RbmkFuelRodItem rod) { tag.putDouble("enrichment", RbmkFuelRodItem.getEnrichment(fuel)); tag.putDouble("xenon", RbmkFuelRodItem.getPoison(fuel)); tag.putDouble("c_heat", RbmkFuelRodItem.getHullHeat(fuel)); tag.putDouble("c_coreHeat", RbmkFuelRodItem.getCoreHeat(fuel)); tag.putDouble("c_maxHeat", rod.meltingPoint); } return tag; }
    @Override public boolean canLoad(ItemStack stack) { return !stack.isEmpty() && fuel.isEmpty(); }
    @Override public void load(ItemStack stack) { fuel = stack.copy(); setChanged(); }
    @Override public boolean canUnload() { return !fuel.isEmpty(); }
    @Override public ItemStack provideNext() { return fuel; }
    @Override public void unload() { fuel = ItemStack.EMPTY; setChanged(); }
    @Override protected void saveState(CompoundTag tag) { super.saveState(tag); tag.putDouble("fluxQuantity", lastFluxQuantity); tag.putDouble("fluxMod", lastFluxRatio); tag.putBoolean("hasRod", hasRod); }
    @Override protected void loadState(CompoundTag tag) { super.loadState(tag); if (tag.contains("fluxFast") || tag.contains("fluxSlow")) { fluxQuantity = tag.getDouble("fluxFast") + tag.getDouble("fluxSlow"); fluxFastRatio = fluxQuantity > 0 ? tag.getDouble("fluxFast") / fluxQuantity : 0; } else { fluxQuantity = tag.getDouble("fluxQuantity"); fluxFastRatio = tag.getDouble("fluxMod"); } hasRod = tag.getBoolean("hasRod"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.saveAdditional(tag, lookup); ContainerHelper.saveAllItems(tag, NonNullList.of(ItemStack.EMPTY, fuel), lookup); if (customName != null) tag.putString("name", Component.Serializer.toJson(customName, lookup)); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.loadAdditional(tag, lookup); var items = NonNullList.withSize(1, ItemStack.EMPTY); ContainerHelper.loadAllItems(tag, items, lookup); fuel = items.getFirst(); if (tag.contains("name")) customName = Component.Serializer.fromJson(tag.getString("name"), lookup); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag = super.getUpdateTag(lookup); tag.putInt("rodColor", rodColor);
        if (hasRod && fuel.getItem() instanceof RbmkFuelRodItem rod) { tag.putString("f_yield", RbmkFuelRodItem.getYield(fuel) + " / " + rod.yield + " (" + RbmkFuelRodItem.getEnrichment(fuel) * 100 + "%)"); tag.putString("f_xenon", RbmkFuelRodItem.getPoison(fuel) + "%"); tag.putString("f_heat", String.format(java.util.Locale.ROOT, "%.6f / %.6f / %.2f", RbmkFuelRodItem.getCoreHeat(fuel), RbmkFuelRodItem.getHullHeat(fuel), rod.meltingPoint)); }
        return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { super.handleUpdateTag(tag, lookup); rodColor = tag.getInt("rodColor"); fuelYield = hasRod ? tag.getString("f_yield") : null; fuelXenon = hasRod ? tag.getString("f_xenon") : null; fuelHeat = hasRod ? tag.getString("f_heat") : null; }
    @Override public CompoundTag diagnosticData() { var tag = super.diagnosticData(); tag.remove("fluxQuantity"); tag.remove("fluxMod"); tag.putDouble("fluxSlow", fluxQuantity * (1 - fluxFastRatio)); tag.putDouble("fluxFast", fluxQuantity * fluxFastRatio); if (fuelYield != null && fuelXenon != null && fuelHeat != null) { tag.putString("f_yield", fuelYield); tag.putString("f_xenon", fuelXenon); tag.putString("f_heat", fuelHeat); } return tag; }
    @Override public String computerType() { return "rbmk_fuel_rod"; }
    @Override public String[] computerMethods() { return new String[] {"getHeat","getFluxQuantity","getFluxRatio","getDepletion","getXenonPoison","getCoreHeat","getSkinHeat","getType","getInfo","getModerated","getCoordinates"}; }
    private Object fuelValue(java.util.function.ToDoubleFunction<ItemStack> getter) { return hasFuelRod() ? getter.applyAsDouble(fuel) : "N/A"; }
    private Object fuelName() { return hasFuelRod() ? "item." + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(fuel.getItem()).getPath() : "N/A"; }
    @Override public Object[] callComputer(String method, ComputerArguments arguments) throws ComputerException {
        return switch (method) {
            case "getHeat" -> new Object[] {heat}; case "getFluxQuantity" -> new Object[] {lastFluxQuantity}; case "getFluxRatio" -> new Object[] {lastFluxRatio};
            case "getDepletion" -> new Object[] {fuelValue(RbmkFuelRodItem::getEnrichment)}; case "getXenonPoison" -> new Object[] {fuelValue(RbmkFuelRodItem::getPoison)};
            case "getCoreHeat" -> new Object[] {fuelValue(RbmkFuelRodItem::getCoreHeat)}; case "getSkinHeat" -> new Object[] {fuelValue(RbmkFuelRodItem::getHullHeat)}; case "getType" -> new Object[] {fuelName()};
            case "getModerated" -> new Object[] {isModerated()}; case "getCoordinates" -> new Object[] {worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};
            case "getInfo" -> new Object[] {heat,fuelValue(RbmkFuelRodItem::getHullHeat),fuelValue(RbmkFuelRodItem::getCoreHeat),lastFluxQuantity,lastFluxRatio,fuelValue(RbmkFuelRodItem::getEnrichment),fuelValue(RbmkFuelRodItem::getPoison),fuelName(),isModerated(),worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};
            default -> throw new ComputerException("No such method: " + method);
        };
    }
    @Override public String[] getFunctionInfo() { return new String[] {PREFIX_VALUE+"columnheat",PREFIX_VALUE+"rodheat",PREFIX_VALUE+"depletion",PREFIX_VALUE+"xenon",PREFIX_VALUE+"fastflux",PREFIX_VALUE+"slowflux",PREFIX_VALUE+"flux"}; }
    /** Original optional EnergyControl data keys; the compatibility consumer may reuse this same actual state. */
    public void provideExtraInfo(CompoundTag tag) { if (fuel.getItem() instanceof RbmkFuelRodItem rod) { tag.putDouble("depletion",((1-RbmkFuelRodItem.getEnrichment(fuel))*100000)/1000);tag.putDouble("xenon",RbmkFuelRodItem.getPoison(fuel));tag.putDouble("skin",RbmkFuelRodItem.getHullHeat(fuel));tag.putDouble("c_heat",RbmkFuelRodItem.getCoreHeat(fuel));tag.putDouble("melt",rod.meltingPoint); } }
    @Override public String provideRORValue(String name) {
        if (name.equals(PREFIX_VALUE+"columnheat")) return ""+(int)heat;
        if (hasFuelRod()) { if (name.equals(PREFIX_VALUE+"rodheat")) return ""+(int)RbmkFuelRodItem.getHullHeat(fuel); if (name.equals(PREFIX_VALUE+"depletion")) return ""+(int)(100-RbmkFuelRodItem.getEnrichment(fuel)*100); if (name.equals(PREFIX_VALUE+"xenon")) return ""+(int)RbmkFuelRodItem.getPoison(fuel); }
        if (name.equals(PREFIX_VALUE+"fastflux")) return ""+(int)(lastFluxQuantity*lastFluxRatio); if (name.equals(PREFIX_VALUE+"slowflux")) return ""+(int)(lastFluxQuantity*(1-lastFluxRatio)); if (name.equals(PREFIX_VALUE+"flux")) return ""+((int)(lastFluxQuantity*lastFluxRatio)+(int)(lastFluxQuantity*(1-lastFluxRatio))); return null;
    }
    @Override public Component getDisplayName() { return customName == null ? Component.translatable(kind().reasim ? "container.rbmkReaSim" : "container.rbmkRod") : customName; }
    @Override public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new RbmkChannelMenu(id, inventory, this, worldPosition); }
    @Override public int getContainerSize() { return 1; } @Override public boolean isEmpty() { return fuel.isEmpty(); } @Override public ItemStack getItem(int slot) { return slot == 0 ? fuel : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int count) { var result = fuel.split(count); if (!result.isEmpty()) setChanged(); return result; } @Override public ItemStack removeItemNoUpdate(int slot) { var result = fuel; fuel = ItemStack.EMPTY; return result; }
    @Override public void setItem(int slot, ItemStack stack) { fuel = stack; if (stack.getCount() > 64) stack.setCount(64); setChanged(); } @Override public void clearContent() { fuel = ItemStack.EMPTY; setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5) <= 128; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; } @Override public int[] getSlotsForFace(Direction side) { return new int[0]; } @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; } @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
}
