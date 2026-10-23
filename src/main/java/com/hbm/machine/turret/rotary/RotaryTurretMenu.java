package com.hbm.machine.turret.rotary;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Actual shared11-slot source container, with the native menu type of the real turret family. */
public final class RotaryTurretMenu extends TurretMenu {
    public final RotaryTurretKind kind;
    public RotaryTurretMenu(RotaryTurretKind kind,int id,Inventory inventory,BlockPos pos){this(kind,id,inventory,inventory.player.level().getBlockEntity(pos) instanceof RotaryTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public RotaryTurretMenu(RotaryTurretKind kind,int id,Inventory inventory,Container machine,BlockPos pos){super(RotaryTurretContent.menu(kind),id,inventory,machine,pos);this.kind=kind;}
}
