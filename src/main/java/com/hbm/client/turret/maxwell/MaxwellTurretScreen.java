package com.hbm.client.turret.maxwell;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.maxwell.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
/** Source GUITurretMaxwell overrides only its texture; all176x222 controls/slots/whitelist/ammunition remain the real shared GUI. */
public final class MaxwellTurretScreen extends SentryScreen<MaxwellTurretMenu> {
    public MaxwellTurretScreen(MaxwellTurretMenu menu,Inventory inventory,Component title) { super(menu,inventory,title,MaxwellTurretContent.id("textures/gui/weapon/gui_turret_maxwell.png")); }
}
