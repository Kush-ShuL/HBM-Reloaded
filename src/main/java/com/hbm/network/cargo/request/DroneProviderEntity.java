package com.hbm.network.cargo.request;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
public final class DroneProviderEntity extends RequestContainerEntity {
 private static final int[] ACCESS={0,1,2,3,4,5,6,7,8};public DroneProviderEntity(BlockPos pos,BlockState state){super(CargoRequestContent.PROVIDER_ENTITY.get(),pos,state,9);}@Override public String sourceName(){return "container.droneProvider";}@Override public boolean canPlaceItem(int i,ItemStack stack){return true;}@Override public int[] getSlotsForFace(Direction side){return ACCESS;}
 @Override public RequestNetwork.PathNode createNode(BlockPos pos){var offer=new ArrayList<ItemStack>();for(var stack:slots)if(!stack.isEmpty())offer.add(stack.copy());return new RequestNetwork.OfferNode(pos,reachableNodes,offer);}@Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new CargoRequestMenu(id,inv,this,CargoRequestContent.Kind.PROVIDER);}
}
