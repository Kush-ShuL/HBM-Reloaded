package com.hbm.machine.reactor.zirnox.client;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.zirnox.*;
import java.util.*;
import net.minecraft.world.item.ItemStack;
/** Exact eleven source TileEntityReactorZirnox.fuelMap transitions used by source NEI. */
record ZirnoxFuelRecipes(ItemStack input,ItemStack output){
    static List<ZirnoxFuelRecipes> all(){var result=new ArrayList<ZirnoxFuelRecipes>();for(var type:ZirnoxRod.Type.values()){var rod=new ItemStack(ZirnoxContent.ROD.get());rod.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());result.add(new ZirnoxFuelRecipes(rod,ZirnoxContent.spent(type)));}return result;}
}
