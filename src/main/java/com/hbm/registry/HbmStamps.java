package com.hbm.registry;

import com.google.gson.Gson;
import com.hbm.item.StampItem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HbmStamps {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<Entry> ALL = new ArrayList<>();
    private HbmStamps() {}

    public static void register(IEventBus bus) {
        try (var stream = HbmStamps.class.getResourceAsStream("/hbm/stamps.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_stamps.py");
            var content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
            for (var spec : content.items()) {
                var item = ITEMS.register(spec.id(), () -> new StampItem(spec.maxDamage(), spec.stackSize(),
                        spec.stampType() == null ? null : StampItem.StampType.valueOf(spec.stampType())));
                ALL.add(new Entry(item, spec.tab()));
            }
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load original stamps", error); }
        ITEMS.register(bus);
    }

    public static List<ItemStack> creativeStacks(String tab) {
        return ALL.stream().filter(entry -> tab.equals(entry.tab())).map(entry -> new ItemStack(entry.item().get())).toList();
    }

    private record Content(List<Spec> items) {}
    private record Spec(String id, int maxDamage, int stackSize, String stampType, String tab) {}
    private record Entry(DeferredItem<StampItem> item, String tab) {}
}
