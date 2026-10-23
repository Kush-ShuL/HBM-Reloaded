package com.hbm.client.turret.jeremy;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.jeremy.JeremyTurretMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original GUITurretJeremy changes only the shared GUI's cannon atlas. */
public final class JeremyTurretScreen extends SentryScreen<JeremyTurretMenu> {
 public JeremyTurretScreen(JeremyTurretMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/gui_turret_cannon.png"));}
}
