package com.hbm.machine.turret.fritz;
import com.hbm.machine.turret.TurretMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
/** Original shared11-slot turret container; slot9 is also the identifier/container output. */
public final class FritzTurretMenu extends TurretMenu {
    public FritzTurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof FritzTurretEntity turret?turret:new SimpleContainer(11),pos);}
    public FritzTurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(FritzTurretContent.MENU.get(),id,inventory,machine,pos);}
}
