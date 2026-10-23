package com.hbm.machine.turret.tauon;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Original Tauon uses the unchanged eleven-slot ContainerTurretBase, with chip-first shift routing. */
public final class TauonTurretMenu extends TurretMenu {
    public TauonTurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof TauonTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public TauonTurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(TauonTurretContent.MENU.get(),id,inventory,machine,pos);}
}
