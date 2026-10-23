package com.hbm.client.storagedrum;

import com.hbm.machine.storagedrum.StorageDrumContent;
import com.hbm.machine.storagedrum.StorageDrumDecay;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Original RBMKWasteDecayHandler exposes26 waste rows only; it does not display rates or invent metal recipes. */
final class StorageDrumViews {
    record Row(ItemStack input, ItemStack output) {}
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
    static List<Row> recipes() {
        var result = new ArrayList<Row>();
        for (int family : new int[]{2, 1}) {
            var rules = StorageDrumDecay.RULES.stream().filter(rule -> rule.isotopeFamily() == family).toList();
            for (int meta = 0; meta < rules.getFirst().variants(); meta++) for (var rule : rules) {
                result.add(new Row(StorageDrumDecay.stack(rule.input(), meta), StorageDrumDecay.stack(rule.output(), meta)));
            }
        }
        return List.copyOf(result);
    }
    static ItemStack catalyst() { return new ItemStack(StorageDrumContent.ITEM.get()); }
    static void draw(GuiGraphics graphics, int x, int y) {
        com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics, TEXTURE, x, y);
        graphics.blit(TEXTURE, x + 47, y + 23, 5, 87, 18, 18, 256, 256);
        graphics.blit(TEXTURE, x + 101, y + 23, 5, 87, 18, 18, 256, 256);
        graphics.blit(TEXTURE, x + 74, y + 14, 59, 87, 18, 36, 256, 256);
    }
    private StorageDrumViews() {}
}
