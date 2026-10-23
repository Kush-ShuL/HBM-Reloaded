package com.hbm.client.turret.howard;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.howard.HowardTurretMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original GUITurretHoward changes only the shared GUI's cannon atlas. */
public final class HowardTurretScreen extends SentryScreen<HowardTurretMenu> {
 public HowardTurretScreen(HowardTurretMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/gui_turret_howard.png"));}
}
