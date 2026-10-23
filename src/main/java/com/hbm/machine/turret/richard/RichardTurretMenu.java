package com.hbm.machine.turret.richard;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Original Richard uses the unchanged eleven-slot ContainerTurretBase, with chip-first shift routing. */
public final class RichardTurretMenu extends TurretMenu {
    public RichardTurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof RichardTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public RichardTurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(RichardTurretContent.MENU.get(),id,inventory,machine,pos);}
}
