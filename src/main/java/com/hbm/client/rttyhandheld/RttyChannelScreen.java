package com.hbm.client.rttyhandheld;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.rttyhandheld.RttyHandheldControl;
import com.hbm.item.rttyhandheld.RttyHandheldItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** The two original184x42 screens share identical layout; each retains its own original PNG/title. */
public final class RttyChannelScreen extends LegacyScreen {
    private final InteractionHand hand;
    private final RttyHandheldItem.Kind kind;
    private final ResourceLocation texture;
    private int left, top;
    private EditBox channel;
    public RttyChannelScreen(InteractionHand hand, RttyHandheldItem.Kind kind) {
        super(Component.translatable(kind == RttyHandheldItem.Kind.PAGER ? "container.rttyPager" : "container.rttyDesignator"));
        this.hand = hand; this.kind = kind;
        texture = ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_" + (kind == RttyHandheldItem.Kind.PAGER ? "pager" : "designator") + ".png");
    }
    @Override protected void init() {
        font = LegacyFonts.get(); left = (width - 184) / 2; top = (height - 42) / 2;
        channel = new EditBox(font, left + 31, top + 23, 82, 14, Component.translatable("gui.hbm.rtty.frequency"));
        channel.setBordered(false); channel.setTextColor(0x00ff00); channel.setTextColorUneditable(0x00ff00);
        channel.setMaxLength(15); channel.setValue(minecraft.player == null ? "" : RttyHandheldItem.channel(minecraft.player.getItemInHand(hand)));
        channel.setFocused(false); addWidget(channel);
    }
    @Override public void render(GuiGraphics graphics, int x, int y, float partial) {
        renderBackground(graphics, x, y, partial);
        graphics.blit(texture, left, top, 0, 0, 184, 42, 256, 256);
        channel.render(graphics, x, y, partial);
        graphics.drawString(font, title, left + 92 - font.width(title) / 2, top + 6, 4210752, false);
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (left + 137 <= x && x < left + 155 && top + 17 < y && y <= top + 35) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            PacketDistributor.sendToServer(new RttyHandheldControl(hand, kind, channel.getValue()));
            return true;
        }
        boolean clicked = channel.mouseClicked(x, y, button); setFocused(channel.isFocused() ? channel : null); return clicked;
    }
    @Override public boolean charTyped(char value, int modifiers) { return channel.charTyped(value, modifiers); }
    @Override public boolean keyPressed(int key, int scan, int modifiers) {
        if (channel.keyPressed(key, scan, modifiers)) return true;
        if (key == GLFW.GLFW_KEY_ESCAPE || !channel.isFocused() && minecraft.options.keyInventory.matches(key, scan)) { onClose(); return true; }
        return false;
    }
    @Override public boolean isPauseScreen() { return false; }
}
