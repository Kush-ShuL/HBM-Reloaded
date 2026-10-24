package com.hbm.fluid;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.fluid.FluidItemContent;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/** Original first-match fixed-container registry, including consumable containers. */
public final class FluidContainerRegistry {
    public record Container(ItemStack fullContainer, ItemStack emptyContainer, HbmFluidType type, int content) {}
    public static final List<Container> allContainers = new ArrayList<>();
    private static final Map<HbmFluidType, List<Container>> BY_TYPE = new IdentityHashMap<>();
    // 仅抑制 reloadFluids 中本注册表原有的内部重建回调；外部 listener 仍完整运行。
    private static boolean reloading;
    // 远程容器字典身份只覆盖实际消费值，不修改本机COMMON配置。null表示本机原生值。
    private static Boolean remoteContainerCompat;
    private FluidContainerRegistry() {}
    public static void rebuild() {
        if (reloading) return;
        clear();
        registerDefaults();
    }
    private static void registerDefaults() {
        try (var stream = FluidContainerRegistry.class.getResourceAsStream("/hbm/fluid-items.json")) {
            if (stream == null) throw new IllegalStateException("Missing fluid-items.json: run tools/port_fluid_items.py");
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (var value : root.getAsJsonArray("containers")) {
                var row = value.getAsJsonObject();
                var full = decode(row.getAsJsonObject("full"));
                if (row.has("potion")) full.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
                var empty = row.get("empty").isJsonNull() ? ItemStack.EMPTY : decode(row.getAsJsonObject("empty"));
                int amount = row.has("amount528") && com.hbm.config.GeneralModes.mode528() ? row.get("amount528").getAsInt() : row.get("amount").getAsInt();
                registerContainer(new Container(full, empty, HbmFluids.fromName(row.get("type").getAsString()), amount));
            }
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load original fluid containers", error); }
        var fluids = HbmFluids.getAll();
        for (int i = 1; i < fluids.length; i++) {
            var type = fluids[i];
            if (type.getContainer(HbmFluidType.Canister.class) != null) dynamic(FluidItemContent.CANISTER.get(), "canister_empty", type, 1000);
            if (type.getContainer(HbmFluidType.Gastank.class) != null) dynamic(FluidItemContent.GAS.get(), "gas_empty", type, 1000);
            if (type.hasNoContainer()) continue;
            if (type.isDispersable()) com.hbm.item.disperser.DisperserContent.registerContainers(i);
            dynamic(FluidItemContent.LEAD_TANK.get(), "fluid_tank_lead_empty", type, 1000);
            if (type.needsLeadContainer()) continue;
            dynamic(FluidItemContent.TANK.get(), "fluid_tank_empty", type, 1000);
            dynamic(FluidItemContent.BARREL.get(), "fluid_barrel_empty", type, 16000);
        }
    }
    /** Clear once, preserve external listener registrations, then append original defaults. */
    public static void reload() {
        clear();
        reloading = true;
        try { HbmFluids.reloadFluids(); }
        finally {
            reloading = false;
            registerDefaults();
        }
    }

    private static void clear() { allContainers.clear(); BY_TYPE.clear(); }

    /** Raw fluid IDs deliberately remain unresolved during network decoding. */
    public record ContainerSnapshot(ItemStack fullContainer, ItemStack emptyContainer, int fluidId, int content) {
        public ContainerSnapshot { fullContainer = fullContainer.copy(); emptyContainer = emptyContainer.copy(); }
    }

    public static List<ContainerSnapshot> snapshot() {
        return allContainers.stream().map(value -> new ContainerSnapshot(value.fullContainer(), value.emptyContainer(),
                value.type().getID(), value.content())).toList();
    }

    /** Called on MAIN after definitions; preserve first-match ordering and every stack component. */
    public static boolean containerCompat() {
        return remoteContainerCompat != null ? remoteContainerCompat : FluidContainerConfig.CONTAINER_COMPAT.get();
    }
    public static Boolean containerCompatOverride() { return remoteContainerCompat; }

    public static void applySnapshot(List<ContainerSnapshot> snapshot, Boolean containerCompat) {
        var resolved = new ArrayList<Container>();
        for (var value : snapshot) {
            var type = HbmFluids.fromID(value.fluidId());
            if (type.getID() != value.fluidId()) throw new IllegalArgumentException("Unknown container fluid " + value.fluidId());
            resolved.add(new Container(value.fullContainer().copy(), value.emptyContainer().copy(), type, value.content()));
        }
        clear();
        resolved.forEach(FluidContainerRegistry::registerContainer);
        remoteContainerCompat = containerCompat;
    }

    /** 按完整注册顺序筛选当前流体与容量，保留同一物品的多个真实容器身份。 */
    public static java.util.stream.Stream<Container> matchingContainers(HbmFluidType type, int amount) {
        return allContainers.stream().filter(container -> container.type() == type && container.content() == amount);
    }

    private static void dynamic(net.minecraft.world.item.Item full, String empty, HbmFluidType type, int amount) {
        registerContainer(new Container(FluidItemContent.stack(full, type), new ItemStack(com.hbm.registry.HbmContent.item(empty)), type, amount));
    }
    public static void registerContainer(Container container) {
        allContainers.add(container); BY_TYPE.computeIfAbsent(container.type(), ignored -> new ArrayList<>()).add(container);
    }
    public static List<Container> getContainers(HbmFluidType type) { return BY_TYPE.get(type); }
    public static Container getContainer(HbmFluidType type, ItemStack empty) {
        for (var value : BY_TYPE.getOrDefault(type, List.of())) if (!value.emptyContainer().isEmpty() && sameItem(value.emptyContainer(), empty)) return value;
        return null;
    }
    public static int getFluidContent(ItemStack stack, HbmFluidType type) {
        for (var value : BY_TYPE.getOrDefault(type, List.of())) if (sameItem(value.fullContainer(), stack)) return value.content();
        return 0;
    }
    public static HbmFluidType getFluidType(ItemStack stack) {
        for (var value : allContainers) if (sameItem(value.fullContainer(), stack)) return value.type();
        return HbmFluids.NONE;
    }
    public static ItemStack getFullContainer(ItemStack empty, HbmFluidType type) { var value = getContainer(type, empty); return value == null ? ItemStack.EMPTY : value.fullContainer().copy(); }
    public static ItemStack getEmptyContainer(ItemStack stack) {
        for (var value : allContainers) if (sameItem(value.fullContainer(), stack)) return value.emptyContainer().copy();
        return ItemStack.EMPTY;
    }
    public static boolean sameItem(ItemStack first, ItemStack second) {
        if (first == null || second == null || first.isEmpty() || second.isEmpty() || first.getItem() != second.getItem()) return false;
        int a = first.has(FluidItemContent.FLUID_TYPE.get()) ? first.get(FluidItemContent.FLUID_TYPE.get()) : first.getOrDefault(HbmHazards.LEGACY_METADATA.get(), first.getDamageValue());
        int b = second.has(FluidItemContent.FLUID_TYPE.get()) ? second.get(FluidItemContent.FLUID_TYPE.get()) : second.getOrDefault(HbmHazards.LEGACY_METADATA.get(), second.getDamageValue());
        if (a != b) return false;
        var potion = first.get(DataComponents.POTION_CONTENTS);
        var otherPotion = second.get(DataComponents.POTION_CONTENTS);
        return potion == null || (otherPotion != null && potion.potion().equals(otherPotion.potion()));
    }
    private static ItemStack decode(JsonObject value) {
        var id = ResourceLocation.parse(value.get("id").getAsString());
        if (!BuiltInRegistries.ITEM.containsKey(id)) throw new IllegalStateException("Container item is not registered: " + id);
        var result = new ItemStack(BuiltInRegistries.ITEM.get(id), value.get("count").getAsInt());
        if (value.has("components")) result.set(HbmHazards.LEGACY_METADATA.get(), value.getAsJsonObject("components").get("hbm:legacy_metadata").getAsInt());
        return result;
    }
}
