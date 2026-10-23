package com.hbm.world.structure.generated.algorithm.legacy;
import com.hbm.world.structure.generated.*;
import com.hbm.world.structure.generated.compat.World;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
/** The original Barrel sets only its actual Sellafield core radius to 2.5. */
public final class LegacyBuildingAttachments {
 public static boolean isSellafield(World world,int x,int y,int z){var tile=world.getTileEntity(x,y,z);return tile!=null&&net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(tile.getBlockState().getBlock()).toString().equals("hbm:sellafield");}
 public static void sellafield(World world,int x,int y,int z,double radius){var data=new CompoundTag();data.putDouble("radius",radius);GeneratedAttachments.applySource("sellafield",world,new BlockPos(x,y,z),data);}
}
