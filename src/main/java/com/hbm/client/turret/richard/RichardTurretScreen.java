package com.hbm.client.turret.richard;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.richard.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
/** Source GUITurretRichard overrides only its texture; all176x222 controls/slots/whitelist/ammunition remain the real shared GUI. */
public final class RichardTurretScreen extends SentryScreen<RichardTurretMenu> {
    public RichardTurretScreen(RichardTurretMenu menu,Inventory inventory,Component title) { super(menu,inventory,title,RichardTurretContent.id("textures/gui/weapon/gui_turret_richard.png")); }
}
