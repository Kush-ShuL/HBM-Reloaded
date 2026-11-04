package com.hbm.client.network.rtty.relay;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.network.rtty.relay.RadioRelayControl;
import com.hbm.network.rtty.relay.RadioRelayEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public final class RadioRelayScreen extends LegacyScreen {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/gui/machine/gui_rtty_relay.png");
    private final RadioRelayEntity relay;
    private EditBox frequency;
    private EditBox expression;
    private int left;
    private int top;

    public RadioRelayScreen(RadioRelayEntity relay) {
        super(Component.translatable("container.rttyRelay"));
        this.relay = relay;
    }

    @Override
    protected void init() {
        left = (width - 256) / 2;
        top = (height - 78) / 2;
        frequency = field(left + 29, top + 21, 82, 15, relay.channel, "gui.hbm.rtty.frequency");
        expression = field(left + 11, top + 57, 212, 100, relay.expression, "gui.hbm.rtty.expression");
    }

    private EditBox field(int x, int y, int width, int length, String value, String key) {
        var field = new EditBox(font, x, y, width, 14, Component.translatable(key));
        field.setTextColor(0x00ff00);
        field.setTextColorUneditable(0x00ff00);
        field.setBordered(false);
        field.setMaxLength(length);
        field.setValue(value);
        addWidget(field);
        return field;
    }

    private boolean button(double x, double y, int offset) {
        return left + offset <= x && x < left + offset + 18 && top + 17 < y && y <= top + 35;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTick) {
        renderBackground(graphics, x, y, partialTick);
        graphics.blit(TEXTURE, left, top, 0F, 0F, 256, 78, 256, 256);
        if (relay.polling) graphics.blit(TEXTURE, left + 173, top + 17, 0F, 78F, 18, 18, 256, 256);
        frequency.render(graphics, x, y, partialTick);
        expression.render(graphics, x, y, partialTick);
        graphics.drawString(font, title, left + 128 - font.width(title) / 2, top + 6, 4210752, false);
        if (button(x, y, 173)) graphics.renderTooltip(font, Component.translatable(relay.polling ? "gui.hbm.rtty.polling" : "gui.hbm.rtty.state_change"), x, y);
        if (button(x, y, 209)) graphics.renderTooltip(font, Component.translatable("gui.hbm.radio.save_settings"), x, y);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean handled = frequency.mouseClicked(x, y, button);
        handled = expression.mouseClicked(x, y, button) || handled;
        setFocused(frequency.isFocused() ? frequency : expression.isFocused() ? expression : null);
        var data = new CompoundTag();
        if (button(x, y, 173)) data.putBoolean("p", !relay.polling);
        if (button(x, y, 209)) {
            data.putString("c", frequency.getValue());
            data.putString("e", expression.getValue());
        }
        if (!data.isEmpty()) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
            PacketDistributor.sendToServer(new RadioRelayControl(relay.getBlockPos(), data));
            return true;
        }
        return handled;
    }

    @Override public boolean charTyped(char character, int modifiers) {
        return frequency.charTyped(character, modifiers) || expression.charTyped(character, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        if (frequency.keyPressed(key, scan, modifiers) || expression.keyPressed(key, scan, modifiers)) return true;
        if (key == GLFW.GLFW_KEY_ESCAPE || !frequency.isFocused() && !expression.isFocused() && minecraft.options.keyInventory.matches(key, scan)) {
            onClose();
            return true;
        }
        return false;
    }

    @Override public boolean isPauseScreen() { return false; }
}
