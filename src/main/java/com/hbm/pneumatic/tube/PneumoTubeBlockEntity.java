package com.hbm.pneumatic.tube;

import api.hbm.ntl.IPneumaticConnector;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.pneumatic.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 compressed-air tube, real endpoints/filters/5tick transfers, no external fluid storage. */
public final class PneumoTubeBlockEntity extends BlockEntity implements IPneumaticConnector,PneumaticEndpoint,StandardFluidReceiver,WorldlyContainer,MenuProvider,ICopiable,MachineView, com.hbm.multiblock.MuffledMachine {
 private final ItemStack[] slots=new ItemStack[15];public final PatternMatcher pattern=new PatternMatcher(15);public Direction insertionDir,ejectionDir;public boolean whitelist,redstone,muffled,tilted;public byte sendOrder,receiveOrder;public int soundDelay,sendCounter;public final FluidTank compair=new FluidTank(HbmFluids.AIR,4000).withPressure(1);private PneumaticNode node;private FluidPorts fluidPorts;private boolean chunkUnloaded;private Component customName;public BlockState paint;
 public PneumoTubeBlockEntity(BlockPos pos,BlockState state){super(PneumaticTubeContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
 @Override public BlockPos pneumaticPosition(){return worldPosition;}@Override public boolean whitelist(){return whitelist;}@Override public boolean matchesFilter(ItemStack stack){for(int i=0;i<15;i++)if(!slots[i].isEmpty()&&pattern.isValidForFilter(slots[i],i,stack))return true;return false;}public boolean isCompressor(){return insertionDir!=null;}public boolean isEndpoint(){return ejectionDir!=null;}public static int getRangeFromPressure(int p){return PneumaticNetwork.getRangeFromPressure(p);}
 public static void tick(Level level,BlockPos pos,BlockState state,PneumoTubeBlockEntity tube){if(level instanceof ServerLevel server)tube.serverTick(server);}
 private void serverTick(ServerLevel server){if(soundDelay>0)soundDelay--;node=PneumaticNetworks.connect(this);if(isCompressor()){if(fluidPorts==null)fluidPorts=new FluidPorts(worldPosition);fluidPorts.update(server,this);}else if(fluidPorts!=null){fluidPorts.remove(server);fluidPorts=null;}
  if(isCompressor()&&(!server.hasNeighborSignal(worldPosition)^redstone)){int time=Math.abs((int)(server.getGameTime()+PneumaticNetwork.getIdentifier(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())));if(time%5==0&&node!=null&&node.network()!=null&&compair.getFill()>=50&&server.getBlockEntity(worldPosition.relative(insertionDir)) instanceof Container source){if(node.network().send(source,this,insertionDir.getOpposite(),sendOrder,receiveOrder,getRangeFromPressure(compair.getPressure()),sendCounter)){compair.setFill(compair.getFill()-50);setChanged();if(soundDelay<=0&&!muffled){server.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("weapon.reload.tubeFwoomp"),SoundSource.BLOCKS,.25F,.9F+server.random.nextFloat()*.2F);soundDelay=20;}}sendCounter++;}}
  if(isEndpoint()&&node!=null&&node.network()!=null&&server.getGameTime()%10==0&&server.getBlockEntity(worldPosition.relative(ejectionDir)) instanceof Container target)node.network().addReceiver(target,ejectionDir,this);
  if(server.getGameTime()%1==0){var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<225)player.connection.send(packet);dispatchScope.commit();}}
 }
 public void directions(Direction input,Direction output){insertionDir=input;ejectionDir=output;if(level instanceof ServerLevel server&&fluidPorts!=null){fluidPorts.remove(server);fluidPorts=null;}changed();}
 public void changed(){setChanged();if(level!=null){requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}}
 public void control(int button){switch(button){case 0->redstone=!redstone;case 1->{int p=compair.getPressure()+1;if(p>5)p=1;compair.setTankType(HbmFluids.AIR);compair.withPressure(p);}case 2->{receiveOrder++;if(receiveOrder>1)receiveOrder=0;}case 3->{sendOrder++;if(sendOrder>2)sendOrder=0;}case 4->whitelist=!whitelist;default->{return;}}changed();}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{compair};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{compair};}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side!=insertionDir&&side!=ejectionDir&&type==HbmFluids.AIR&&isCompressor();}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return Math.clamp((compair.getMaxFill()-compair.getFill())/25,1,100);}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long remainder=StandardFluidReceiver.super.transferFluid(type,pressure,amount);if(remainder!=amount)setChanged();return remainder;}
 private void disconnect(){PneumaticNetworks.disconnect(this);node=null;if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;}
 @Override public void onLoad(){super.onLoad();chunkUnloaded=false;node=PneumaticNetworks.loaded(this);}
 @Override public void setRemoved(){if(!chunkUnloaded)disconnect();super.setRemoved();}
 @Override public void onChunkUnloaded(){chunkUnloaded=true;PneumaticNetworks.unloaded(this);if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;super.onChunkUnloaded();}
 @Override public Component getDisplayName(){return customName==null?Component.translatable("container.pneumoTube"):customName;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new PneumoTubeMenu(id,inventory,this,isEndpoint()&&!isCompressor());}
 public void setName(Component name){customName=name;setChanged();}
 @Override public boolean supportsSettingsCopy(){return getBlockState().getBlock() instanceof PneumoTubeBlock block&&block.paintable;}
 @Override public CompoundTag getSettings(Level world,BlockPos pos){var tag=new CompoundTag();if(paint!=null)tag.put("paintblock",NbtUtils.writeBlockState(paint));return tag;}
 @Override public void pasteSettings(CompoundTag tag,int index,Level world,Player player,BlockPos pos){if(tag.contains("paintblock",Tag.TAG_COMPOUND)){paint=NbtUtils.readBlockState(world.holderLookup(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("paintblock"));changed();}}
 private void state(CompoundTag tag){tag.putByte("insertionDir",(byte)(insertionDir==null?6:insertionDir.get3DDataValue()));tag.putByte("ejectionDir",(byte)(ejectionDir==null?6:ejectionDir.get3DDataValue()));compair.writeToNBT(tag,"tank");pattern.write(tag);tag.putByte("sendOrder",sendOrder);tag.putByte("receiveOrder",receiveOrder);tag.putInt("sendCounter",sendCounter);tag.putBoolean("whitelist",whitelist);tag.putBoolean("redstone",redstone);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(paint!=null)tag.put("paint",NbtUtils.writeBlockState(paint));}
 private static Direction dir(int value){return value>=0&&value<6?Direction.from3DDataValue(value):null;}
 private void loadState(CompoundTag tag,HolderLookup.Provider lookup){insertionDir=dir(tag.getByte("insertionDir"));ejectionDir=dir(tag.getByte("ejectionDir"));compair.readFromNBT(tag,"tank");pattern.read(tag);sendOrder=tag.getByte("sendOrder");receiveOrder=tag.getByte("receiveOrder");sendCounter=tag.getInt("sendCounter");whitelist=tag.getBoolean("whitelist");redstone=tag.getBoolean("redstone");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");paint=tag.contains("paint",Tag.TAG_COMPOUND)?NbtUtils.readBlockState(lookup.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("paint")):null;}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,NonNullList.of(ItemStack.EMPTY,slots),lookup);state(tag);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var items=NonNullList.withSize(15,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,items,lookup);for(int i=0;i<15;i++)slots[i]=items.get(i);loadState(tag,lookup);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();ContainerHelper.saveAllItems(tag,NonNullList.of(ItemStack.EMPTY,slots),lookup);state(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);requestModelDataUpdate();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
 @Override public List<TankView> tanks(){return isCompressor()?List.of(new TankView(compair.getTankType(),compair.getFill(),compair.getMaxFill())):List.of();}
 @Override public int getContainerSize(){return 15;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int amount){var result=slots[slot].split(amount);setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
