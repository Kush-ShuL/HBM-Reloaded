package com.hbm.machine.turret.maxwell;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Original Maxwell uses the unchanged eleven-slot ContainerTurretBase, with chip-first shift routing. */
public final class MaxwellTurretMenu extends TurretMenu {
    public MaxwellTurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof MaxwellTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public MaxwellTurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(MaxwellTurretContent.MENU.get(),id,inventory,machine,pos);}
}
