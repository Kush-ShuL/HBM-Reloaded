package com.hbm.bomb.nuclear.programmable;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityNukeCustom's actual 27-slot inventory and per-tick local payload calculation. */
public final class CustomNuclearBlockEntity extends ProgrammableBombInventory {
 public CustomBombPayload payload=CustomBombPayload.ZERO;
 public CustomNuclearBlockEntity(BlockPos pos,BlockState state){super(ProgrammableNuclearContent.CUSTOM_ENTITY.get(),pos,state,27,"container.nukeCustom",8);}
 public static void tick(Level level,BlockPos pos,BlockState state,CustomNuclearBlockEntity bomb){bomb.payload=CustomNuclearIngredients.calculate(bomb);}
 public boolean isFalling(){for(var stack:slots)if(stack.is(HbmContent.item("custom_fall")))return true;return false;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CustomNuclearMenu(id,inventory,this);}
}
