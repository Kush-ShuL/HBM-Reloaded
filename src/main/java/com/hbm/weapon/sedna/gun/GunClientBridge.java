package com.hbm.weapon.sedna.gun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

/** Physical-client implementation is installed by the real client subscriber; common gun logic never loads Minecraft client classes. */
public interface GunClientBridge {
    Player localPlayer();
    boolean isWeaponTable();
    void animation(ItemStack stack,int slot,InteractionHand hand,GunAnimation type,int receiver,int config);
    void muzzleFlash(int entityId,InteractionHand hand);
    void blackPowder(GunNetwork.BlackPowder packet);
}
