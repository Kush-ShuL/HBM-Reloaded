package com.hbm.machine.turret.greg;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Actual shared11-slot source container, with the native menu type of the real turret family. */
public final class GregTurretMenu extends TurretMenu {
    public final GregTurretKind kind;
    public GregTurretMenu(GregTurretKind kind,int id,Inventory inventory,BlockPos pos){this(kind,id,inventory,inventory.player.level().getBlockEntity(pos) instanceof GregTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public GregTurretMenu(GregTurretKind kind,int id,Inventory inventory,Container machine,BlockPos pos){super(GregTurretContent.menu(kind),id,inventory,machine,pos);this.kind=kind;}
}
