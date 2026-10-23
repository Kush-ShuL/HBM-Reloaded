package com.hbm.weapon.sedna.gun.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHUDComponent {
    int getComponentHeight(Player player,ItemStack stack);
    void renderHUDComponent(GuiGraphics graphics,Player player,ItemStack stack,int bottomOffset,int gunIndex);
}
