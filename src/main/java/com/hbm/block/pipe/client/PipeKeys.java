package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.hbm.block.pipe.PipeControls;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT)
public final class PipeKeys {
    public static final KeyMapping CONTROL = new KeyMapping("hbm.key.copyToolCtrl", InputConstants.Type.KEYSYM, 341, "hbm.key");
    public static final KeyMapping ALT = new KeyMapping("hbm.key.copyToolAlt", InputConstants.Type.KEYSYM, 342, "hbm.key");
    private static int last = -1;
    private PipeKeys() {}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().getConnection() == null || Minecraft.getInstance().player == null) { last = -1; return; }
        int state = (CONTROL.isDown() ? 1 : 0) | (ALT.isDown() ? 2 : 0);
        if (state != last) { last = state; PacketDistributor.sendToServer(new PipeControls.Keys(CONTROL.isDown(), ALT.isDown())); }
    }
}
