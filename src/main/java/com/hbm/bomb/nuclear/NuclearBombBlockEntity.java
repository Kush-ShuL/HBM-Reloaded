package com.hbm.bomb.nuclear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The original static nuclear bomb TileEntities, with one unchanged assembly inventory per placed bomb. */
public final class NuclearBombBlockEntity extends BlockEntity implements MenuProvider {
 private final NuclearAssembly assembly;
 public NuclearBombBlockEntity(BlockPos pos,BlockState state){super(NuclearContent.BOMB_ENTITY.get(),pos,state);assembly=new NuclearAssembly(((NuclearBombBlock)state.getBlock()).kind(),this::setChanged,player->level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64);}
 public NuclearAssembly assembly(){return assembly;}
 @Override public Component getDisplayName(){return assembly.displayName();}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new NuclearAssemblyMenu(NuclearContent.MENUS.get(assembly.definition().kind().toLowerCase(java.util.Locale.ROOT)).get(),id,inventory,assembly);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);assembly.save(tag,lookup);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);assembly.load(tag,lookup);}
}
