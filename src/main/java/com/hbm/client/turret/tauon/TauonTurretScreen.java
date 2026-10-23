package com.hbm.client.turret.tauon;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.tauon.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
/** Source GUITurretTauon overrides only its texture; all176x222 controls/slots/whitelist/ammunition remain the real shared GUI. */
public final class TauonTurretScreen extends SentryScreen<TauonTurretMenu> {
    public TauonTurretScreen(TauonTurretMenu menu,Inventory inventory,Component title) { super(menu,inventory,title,TauonTurretContent.id("textures/gui/weapon/gui_turret_tau.png")); }
}
