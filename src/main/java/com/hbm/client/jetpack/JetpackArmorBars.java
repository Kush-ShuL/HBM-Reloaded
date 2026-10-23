package com.hbm.client.jetpack;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.equipment.jetpack.SourceJetpackItem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** The existing FSB HUD delegates here: source same-slot energy/fuel segments, not another overlay listener. */
public final class JetpackArmorBars {
    private record Bar(float fraction, int color) { }
    private static void bars(List<Bar> result, ItemStack stack, Player player) {
        if (stack.getItem() instanceof SourcePoweredArmor powered && T51Runtime.fullSet(player, false))
            result.add(new Bar((float) ((double) powered.getCharge(stack) / powered.getMaxCharge(stack)), 0x00ff00));
        if (stack.getItem() instanceof SourceJetpackItem jetpack)
            result.add(new Bar((float) SourceJetpackItem.getFuel(stack) / jetpack.maxFuel, jetpack.sourceFuelType().getColor()));
    }
    public static void render(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance(); var player = mc.player;
        if (!VanillaGuiLayers.ARMOR_LEVEL.equals(event.getName()) || player == null || mc.options.hideGui) return;
        var gui = event.getGuiGraphics(); float left = gui.guiWidth() / 2 - 91;
        var vertices = gui.bufferSource().getBuffer(RenderType.gui()); var matrix = gui.pose().last().pose();
        for (var slot : T51Runtime.ORDER) {
            var stack = player.getItemBySlot(slot); var row = new ArrayList<Bar>(); bars(row, stack, player);
            if (ArmorModStorage.hasMods(stack)) for (var mod : ArmorModStorage.pryMods(stack, player.level().registryAccess())) bars(row, mod, player);
            if (row.isEmpty()) continue;
            int top = gui.guiHeight() - mc.gui.leftHeight + 7;
            for (int i = 0; i < row.size(); i++) {
                int start, end;
                if (i == 0) { start = (int) left; end = start + (row.size() == 1 ? 81 : 40); }
                else { int width = (int) Math.ceil(40F / (row.size() - 1)); start = (int) left + 41 + width * (i - 1); end = i == row.size() - 1 ? (int) left + 81 : start + width; if (i != 1) start++; }
                quad(vertices, matrix, start, top - 1, end, top + 2, 0xff3f3f3f);
                quad(vertices, matrix, start + 1, top, start + (end - start - 1) * row.get(i).fraction(), top + 1, 0xff000000 | row.get(i).color());
            }
            mc.gui.leftHeight += 4;
        }
        // Finish this native HUD layer before later GUI blits/screens and their depth clear.
        // GuiGraphics.flush() uses the native GUI depth-disable/endBatch/depth-enable boundary.
        gui.flush();
    }
    private static void quad(VertexConsumer out, org.joml.Matrix4f matrix, float left, float top, float right, float bottom, int color) {
        out.addVertex(matrix, left, top, 0).setColor(color); out.addVertex(matrix, left, bottom, 0).setColor(color);
        out.addVertex(matrix, right, bottom, 0).setColor(color); out.addVertex(matrix, right, top, 0).setColor(color);
    }
    private JetpackArmorBars() { }
}
