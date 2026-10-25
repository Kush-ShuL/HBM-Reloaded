package com.hbm.client.soyuzlauncher;

import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.machine.reactor.icf.IcfReactorContent;
import com.hbm.machine.reactor.iter.torus.FusionComponent;
import com.hbm.machine.reactor.iter.torus.TorusContent;
import com.hbm.machine.reactor.iter.torus.TorusLayout;
import com.hbm.machine.reactor.watz.WatzCasing;
import com.hbm.machine.reactor.watz.WatzContent;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import com.hbm.material.HbmMaterials;
import com.hbm.missile.custom.launcher.CustomLauncherContent;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

/** The six actual ConstructionHandler material lists. Displays of world builds, never executable recipes. */
public final class SoyuzConstruction {
    public record Build(LegacyRecipeDisplays.Display display, ItemStack core) {}

    public static LegacyRecipeDisplays.Category category() {
        return new LegacyRecipeDisplays.Category("soyuz_construction", "Construction", "universal",
                SoyuzLauncherContent.id("textures/gui/nei/gui_nei.png"), 166, 65, tools());
    }

    public static List<ItemStack> tools() {
        return List.of(item("acetylene_torch"), item("blowtorch"), item("boltgun"));
    }

    public static List<Build> recipes() {
        var watz = slots(WatzCasing.stack(0, 48), material("bolt", "MAT_DURA", 64),
                material("bolt", "MAT_DURA", 64), material("bolt", "MAT_DURA", 64),
                new ItemStack(WatzContent.ELEMENT.get(), 36), new ItemStack(WatzContent.COOLER.get(), 26), item("boltgun"));
        var compact = slots(new ItemStack(CustomLauncherContent.STRUCTURE.get(), 8));
        var table = slots(new ItemStack(CustomLauncherContent.STRUCTURE.get(), 16),
                new ItemStack(CustomLauncherContent.STRUCTURE.get(), 64), new ItemStack(CustomLauncherContent.SCAFFOLD.get(), 11));
        var icf = slots(IcfReactorContent.COMPONENT.get().stack(0, 50), IcfReactorContent.COMPONENT.get().stack(3, 240),
                material("bolt", "MAT_DURA", 960), material("plate_cast", "MAT_STEEL", 240), IcfReactorContent.COMPONENT.get().stack(1, 117));
        icf.add(new LegacyRecipeDisplays.Slot(List.of(label(material("plate_cast", "MAT_BBRONZE", 117)),
                label(material("plate_cast", "MAT_ABRONZE", 117)))));
        icf.addAll(slots(item("blowtorch"), item("boltgun")));
        return List.of(
                build("watz_source", watz, new ItemStack(WatzContent.REACTOR.get()), new ItemStack(WatzContent.STRUCT.get())),
                build("compact_launcher_source", compact, new ItemStack(CustomLauncherContent.COMPACT_ITEM.get()), new ItemStack(CustomLauncherContent.CORE_ITEM.get())),
                build("launch_table_source", table, new ItemStack(CustomLauncherContent.TABLE_ITEM.get()), new ItemStack(CustomLauncherContent.LARGE_CORE_ITEM.get())),
                new Build(display(), new ItemStack(SoyuzLauncherContent.STRUCTURE_ITEM.get())),
                build("icf_source", icf, new ItemStack(IcfReactorContent.REACTOR_ITEM.get()), new ItemStack(IcfReactorContent.STRUCT_ITEM.get())),
                build("fusion_torus_source", torus(), new ItemStack(TorusContent.ITEM.get()), new ItemStack(TorusContent.STRUCT_ITEM.get())));
    }

    /** Keep the existing complete Soyuz record and its identifier. */
    public static LegacyRecipeDisplays.Display display() {
        var inputs = new ArrayList<LegacyRecipeDisplays.Slot>();
        for (var row : new String[][]{{"struct_launcher", "30"}, {"struct_launcher", "384"}, {"struct_scaffold", "63"},
                {"struct_scaffold", "384"}, {"concrete_smooth", "38"}, {"concrete_smooth", "320"}}) {
            inputs.add(new LegacyRecipeDisplays.Slot(List.of(label(new ItemStack(HbmContent.block(row[0]), Integer.parseInt(row[1]))))));
        }
        return display("soyuz_source_72", inputs, new ItemStack(SoyuzLauncherContent.ITEM.get()));
    }

    private static List<LegacyRecipeDisplays.Slot> torus() {
        int walls = 0, blankets = 0, motors = -1; // Source: one motor is replaced by the core.
        for (var part : TorusLayout.PARTS) {
            if (part.material() == 1) walls++;
            if (part.material() == 2) blankets++;
            if (part.material() == 3) motors++;
        }
        var inputs = new ArrayList<LegacyRecipeDisplays.Slot>();
        split(inputs, walls, count -> FusionComponent.stack(0, count));
        split(inputs, walls, count -> material("plate_cast", "MAT_STEEL", count));
        split(inputs, blankets, count -> FusionComponent.stack(2, count));
        split(inputs, motors, count -> FusionComponent.stack(3, count));
        inputs.addAll(slots(item("blowtorch")));
        return inputs;
    }

    private static void split(List<LegacyRecipeDisplays.Slot> inputs, int count, IntFunction<ItemStack> stack) {
        while (count > 0) {
            int amount = Math.min(count, 256);
            inputs.addAll(slots(stack.apply(amount)));
            count -= amount;
        }
    }

    private static Build build(String id, List<LegacyRecipeDisplays.Slot> inputs, ItemStack output, ItemStack core) {
        return new Build(display(id, inputs, output), core);
    }

    private static LegacyRecipeDisplays.Display display(String id, List<LegacyRecipeDisplays.Slot> inputs, ItemStack output) {
        return new LegacyRecipeDisplays.Display(SoyuzLauncherContent.id("construction/" + id), "soyuz_construction",
                List.copyOf(inputs), List.of(new LegacyRecipeDisplays.Slot(List.of(output))), null, null, List.of());
    }

    private static ArrayList<LegacyRecipeDisplays.Slot> slots(ItemStack... stacks) {
        var slots = new ArrayList<LegacyRecipeDisplays.Slot>();
        for (var stack : stacks) slots.add(new LegacyRecipeDisplays.Slot(List.of(label(stack))));
        return slots;
    }

    private static ItemStack item(String id) { return new ItemStack(HbmContent.item(id)); }
    private static ItemStack material(String shape, String symbol, int count) {
        return AutogenContent.item(shape).stack(HbmMaterials.bySymbol(symbol), count);
    }

    private static ItemStack label(ItemStack stack) {
        if (stack.getCount() > 64) stack.set(DataComponents.LORE, new ItemLore(List.of(Component.literal(
                (stack.getCount() / 64) + "x64" + (stack.getCount() % 64 > 0 ? " + " + (stack.getCount() % 64) : ""))
                .withStyle(ChatFormatting.RED))));
        return stack;
    }
    private SoyuzConstruction() {}
}
