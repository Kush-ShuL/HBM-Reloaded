package com.hbm.client.turret.rotary;
import com.hbm.client.turret.SentryScreen;
import com.hbm.machine.turret.rotary.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Source GUITurretChekhov uses original common base artwork; Friendly changes only its original background. */
public final class RotaryTurretScreen extends SentryScreen<RotaryTurretMenu> {
    public RotaryTurretScreen(RotaryTurretMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/"+(menu.kind==RotaryTurretKind.CHEKHOV?"gui_turret_base":"gui_turret_friendly")+".png"));}
}
