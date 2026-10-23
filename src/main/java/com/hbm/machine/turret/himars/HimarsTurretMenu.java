package com.hbm.machine.turret.himars;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Actual ContainerTurretBase: chip0, nine pod slots1..9, battery10, native player slots. */
public final class HimarsTurretMenu extends TurretMenu {
 public HimarsTurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof HimarsTurretEntity turret?turret:new SimpleContainer(11),pos);}
 public HimarsTurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(HimarsTurretContent.MENU.get(),id,inventory,machine,pos);}
}
