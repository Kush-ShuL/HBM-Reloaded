package com.hbm.item.fluid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

public final class FluidIdentifierClient {
    private FluidIdentifierClient() {}
    public static void open(InteractionHand hand) { Minecraft.getInstance().setScreen(new FluidIdentifierScreen(hand)); }
}
