package com.hbm.item.fluid.client;

import com.hbm.HbmReloaded;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIdentifierItem;
import com.hbm.client.gui.LegacyScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original 176x72 GUI, 18 results/bookmarks and left/right/middle selection. */
public final class FluidIdentifierScreen extends LegacyScreen {
    private static final ResourceLocation TEXTURE = HbmReloaded.id("textures/gui/machine/gui_fluid.png");
    private final InteractionHand hand;
    private final LinkedHashSet<HbmFluidType> bookmarks = new LinkedHashSet<>();
    private HbmFluidType[] results = new HbmFluidType[18];
    private HbmFluidType primary = HbmFluids.NONE, secondary = HbmFluids.NONE;
    private EditBox search;
    private int left, top;
    public FluidIdentifierScreen(InteractionHand hand) { super(Component.empty()); this.hand = hand; }
    @Override protected void init() {
        font = com.hbm.client.gui.font.LegacyFonts.get();
        minecraft.getTextureManager().getTexture(TEXTURE).setFilter(false, false);
        left = (width - 176) / 2; top = (height - 72) / 2;
        search = new EditBox(font, left + 46, top + 11, 86, 12, Component.empty());
        search.setTextColor(-1); search.setTextColorUneditable(-1); search.setBordered(false);
        search.setMaxLength(32); search.setResponder(value -> updateSearch());
        addRenderableWidget(search); setInitialFocus(search);
        var held = minecraft.player.getItemInHand(hand);
        primary = FluidIdentifierItem.getType(held, true); secondary = FluidIdentifierItem.getType(held, false);
        bookmarks.clear();
        Arrays.stream(FluidIdentifierItem.bookmarks(held)).limit(18).forEach(id -> bookmarks.add(HbmFluids.fromID(id)));
        int index = 0; for (var type : bookmarks) results[index++] = type;
    }
    @Override public void tick() { if (!(minecraft.player.getItemInHand(hand).getItem() instanceof FluidIdentifierItem)) onClose(); }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(TEXTURE, left, top, 0, 0, 176, 72);
        if (search.isFocused()) graphics.blit(TEXTURE, left + 43, top + 7, 166, 72, 90, 18);
        for (int index = 0; index < results.length && results[index] != null; index++) {
            var type = results[index]; int x = left + 7 + index % 9 * 18, y = top + 29 + index / 9 * 18;
            int color = type.getColor();
            RenderSystem.setShaderColor((color >>> 16 & 255) / 255F, (color >>> 8 & 255) / 255F, (color & 255) / 255F, 1);
            graphics.blit(TEXTURE, x + 5, y + 2, 12 + index % 9 * 18, 74, 8, 14);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            if (type == primary || type == secondary) graphics.blit(TEXTURE, x, y, 176, type == primary && type == secondary ? 36 : type == primary ? 0 : 18, 18, 18);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        for (int index = 0; index < results.length && results[index] != null; index++) {
            if (inside(index, mouseX, mouseY)) {
                var tooltip = new java.util.ArrayList<Component>(); tooltip.add(results[index].displayName());
                FluidInfo.append(results[index], tooltip, Screen.hasShiftDown());
                graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            }
        }
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        search.mouseClicked(x, y, button);
        for (int index = 0; index < results.length && results[index] != null; index++) if (inside(index, x, y)) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
            var type = results[index]; var tag = new CompoundTag();
            if (button == 0) { primary = type; tag.putInt("primary", type.getID()); send(tag); }
            else if (button == 1) { secondary = type; tag.putInt("secondary", type.getID()); send(tag); }
            else if (button == 2) {
                if (!bookmarks.add(type)) bookmarks.remove(type);
                if (bookmarks.size() > 18) bookmarks.remove(bookmarks.getFirst());
                if (search.getValue().trim().isEmpty()) results = bookmarks.toArray(new HbmFluidType[18]);
            }
            return true;
        }
        return super.mouseClicked(x, y, button);
    }
    @Override public void removed() {
        var tag = new CompoundTag(); tag.putIntArray("bookmarks", bookmarks.stream().mapToInt(HbmFluidType::getID).toArray()); send(tag);
    }
    private void send(CompoundTag tag) { PacketDistributor.sendToServer(new FluidIdentifierItem.Control(hand, tag)); }
    private boolean inside(int index, double x, double y) {
        int minX = left + 7 + index % 9 * 18, minY = top + 29 + index / 9 * 18;
        return minX <= x && x < minX + 18 && minY < y && y <= minY + 18;
    }
    private void updateSearch() {
        results = new HbmFluidType[18];
        String value = search.getValue();
        if (!bookmarks.isEmpty() && value.trim().isEmpty()) { results = bookmarks.toArray(results); return; }
        int next = 0;
        for (var type : HbmFluids.getInNiceOrder()) if (!type.hasNoID() && type.displayName().getString().toLowerCase(Locale.US).contains(value.toLowerCase(Locale.US))) {
            results[next++] = type; if (next >= results.length) return;
        }
    }
}
