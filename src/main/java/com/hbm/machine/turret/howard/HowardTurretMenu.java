package com.hbm.machine.turret.howard;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Actual shared11-slot source container, with the native menu type of the real turret family. */
public final class HowardTurretMenu extends TurretMenu {
    public final HowardTurretKind kind;
    public HowardTurretMenu(HowardTurretKind kind,int id,Inventory inventory,BlockPos pos){this(kind,id,inventory,inventory.player.level().getBlockEntity(pos) instanceof HowardTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public HowardTurretMenu(HowardTurretKind kind,int id,Inventory inventory,Container machine,BlockPos pos){super(HowardTurretContent.menu(kind),id,inventory,machine,pos);this.kind=kind;}
}
