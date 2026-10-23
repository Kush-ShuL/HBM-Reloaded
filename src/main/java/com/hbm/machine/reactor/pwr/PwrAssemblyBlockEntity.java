package com.hbm.machine.reactor.pwr;

import com.hbm.api.machine.MachineView;
import com.hbm.fluid.*;
import com.hbm.fluid.network.FluidReceiver;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Genuine BlockPWR.TileEntityBlockPWR inventory/fluid/ROR proxy, enabled only for original port cells. */
public final class PwrAssemblyBlockEntity extends BlockEntity implements WorldlyContainer,FluidReceiver,MachineView {
    public Block original;public BlockPos controllerPos=BlockPos.ZERO;private PwrControllerBlockEntity cached;private boolean loaded=true;
    public PwrAssemblyBlockEntity(BlockPos pos,BlockState state){super(PwrContent.PART.get(),pos,state);}
    public boolean isPort(){return getBlockState().getValue(PwrAssemblyBlock.PORT)&&original!=null;}
    public PwrControllerBlockEntity core(){if(cached!=null&&!cached.isRemoved())return cached;if(level!=null&&level.hasChunkAt(controllerPos)&&level.getBlockEntity(controllerPos) instanceof PwrControllerBlockEntity controller){cached=controller;return controller;}return null;}
    public static void tick(Level level,BlockPos pos,BlockState state,PwrAssemblyBlockEntity part){if(level.getGameTime()%20!=0||part.original==null)return;var controller=part.core();if(controller!=null?!controller.assembled:level.hasChunkAt(part.controllerPos))part.restore();}
    public void restore(){if(level==null||level.isClientSide||original==null)return;var source=original;var controller=core();boolean was=PwrAssemblyBlock.RESTORING.get();PwrAssemblyBlock.RESTORING.set(true);try{level.removeBlockEntity(worldPosition);level.setBlock(worldPosition,source.defaultBlockState(),3);}finally{PwrAssemblyBlock.RESTORING.set(was);}if(controller!=null){controller.assembled=false;controller.setChanged();}}
    @Override public boolean isLoaded(){return loaded&&!isRemoved();}@Override public void onChunkUnloaded(){loaded=false;super.onChunkUnloaded();}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return isPort();}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){var core=isPort()?core():null;return core==null?amount:core.transferFluid(type,pressure,amount);}
    @Override public long getDemand(HbmFluidType type,int pressure){var core=isPort()?core():null;return core==null?0:core.getDemand(type,pressure);}
    @Override public FluidTank[] getAllTanks(){var core=isPort()?core():null;return core==null?FluidTank.EMPTY_ARRAY:core.getAllTanks();}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){var core=isPort()?core():null;return core==null?List.of():core.tanks();}
    @Override public List<ProgressView> progressBars(){var core=isPort()?core():null;return core==null?List.of():core.progressBars();}
    @Override public int getContainerSize(){return isPort()&&core()!=null?3:0;}
    @Override public boolean isEmpty(){var core=isPort()?core():null;return core==null||core.isEmpty();}
    @Override public ItemStack getItem(int slot){var core=isPort()?core():null;return core==null?ItemStack.EMPTY:core.getItem(slot);}
    @Override public ItemStack removeItem(int slot,int count){var core=isPort()?core():null;return core==null?ItemStack.EMPTY:core.removeItem(slot,count);}
    @Override public ItemStack removeItemNoUpdate(int slot){var core=isPort()?core():null;return core==null?ItemStack.EMPTY:core.removeItemNoUpdate(slot);}
    @Override public void setItem(int slot,ItemStack stack){var core=isPort()?core():null;if(core!=null)core.setItem(slot,stack);}
    @Override public void clearContent(){var core=isPort()?core():null;if(core!=null)core.clearContent();}
    @Override public boolean stillValid(Player player){return false;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){var core=isPort()?core():null;return core!=null&&core.canPlaceItem(slot,stack);}
    @Override public int[] getSlotsForFace(Direction side){var core=isPort()?core():null;return core==null?new int[0]:core.getSlotsForFace(side);}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){var core=isPort()?core():null;return core!=null&&core.canPlaceItemThroughFace(slot,stack,side);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){var core=isPort()?core():null;return core!=null&&core.canTakeItemThroughFace(slot,stack,side);}
    public String[] getFunctionInfo(){return PwrControllerBlockEntity.ROR.clone();}
    public String provideRORValue(String name){if(!isPort())return "";var core=core();return core==null?null:core.provideRORValue(name);}
    public String runRORFunction(String name,String[] params){if(!isPort())return "";var core=core();return core==null?null:core.runRORFunction(name,params);}
    private void write(CompoundTag tag){if(original!=null){tag.putString("block",BuiltInRegistries.BLOCK.getKey(original).toString());tag.putInt("cX",controllerPos.getX());tag.putInt("cY",controllerPos.getY());tag.putInt("cZ",controllerPos.getZ());}}
    private void read(CompoundTag tag){original=tag.contains("block",8)?BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(tag.getString("block"))).orElse(null):null;if(original!=null)controllerPos=new BlockPos(tag.getInt("cX"),tag.getInt("cY"),tag.getInt("cZ"));cached=null;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);write(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);read(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();write(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){read(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){read(packet.getTag());}
}
