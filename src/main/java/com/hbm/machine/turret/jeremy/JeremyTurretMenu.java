package com.hbm.machine.turret.jeremy;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Actual shared11-slot source container, with the native menu type of the real turret family. */
public final class JeremyTurretMenu extends TurretMenu {
    public final JeremyTurretKind kind;
    public JeremyTurretMenu(JeremyTurretKind kind,int id,Inventory inventory,BlockPos pos){this(kind,id,inventory,inventory.player.level().getBlockEntity(pos) instanceof JeremyTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public JeremyTurretMenu(JeremyTurretKind kind,int id,Inventory inventory,Container machine,BlockPos pos){super(JeremyTurretContent.menu(kind),id,inventory,machine,pos);this.kind=kind;}
}
