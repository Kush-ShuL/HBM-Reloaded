package com.hbm.machine.reactorcontroller;

import com.hbm.api.computer.*;
import com.hbm.api.machine.MachineView;
import com.hbm.machine.reactor.*;
import com.hbm.multiblock.MuffledMachine;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original old research-reactor remote, not an RBMK console or an autonomous energy machine. */
public final class ReactorControllerEntity extends BlockEntity implements WorldlyContainer,MenuProvider,Nameable,ComputerControl,MachineView,MuffledMachine {
    private ItemStack sensor=ItemStack.EMPTY;private Component customName;private CompoundTag lastSnapshot;
    public ResearchReactorBlockEntity reactor;
    public boolean isLinked,muffled,tilted;
    public int flux,heat;
    public double level,levelLower,levelUpper,heatLower,heatUpper;
    public RodFunction function=RodFunction.LINEAR;
    public enum RodFunction{LINEAR,QUAD,LOG}
    public ReactorControllerEntity(BlockPos pos,BlockState state){super(ReactorControllerContent.ENTITY.get(),pos,state);}
    public void tickServer(){
        if(getLevel()==null||getLevel().isClientSide)return;
        isLinked=establishLink();
        if(isLinked){double value;double lower=Math.min(heatLower,heatUpper),upper=Math.max(heatLower,heatUpper);if(heat<lower)value=levelLower;else if(heat>upper)value=levelUpper;else value=getTargetLevel(function,heat);double target=Mth.clamp(value*.01D,0D,1D);if(target!=this.level)reactor.setTarget(target);}
        var tag=snapshot();if(!tag.equals(lastSnapshot)||getLevel().getGameTime()%20==0){lastSnapshot=tag.copy();PacketDistributor.sendToPlayersNear((ServerLevel)getLevel(),null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),150,new ReactorControllerPackets.Snapshot(worldPosition,tag));}
    }
    private boolean establishLink(){
        if(getLevel()==null||sensor.isEmpty()||!(sensor.getItem() instanceof com.hbm.item.reactorsensor.ReactorSensorItem))return false;
        var data=sensor.get(DataComponents.CUSTOM_DATA);if(data==null)return false;var tag=data.copyTag();var selected=new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
        if(!getLevel().getBlockState(selected).is(ReactorContent.RESEARCH.get()))return false;var core=ReactorContent.RESEARCH.get().findCore(getLevel(),selected);
        if(core!=null&&getLevel().getBlockEntity(core) instanceof ResearchReactorBlockEntity linked){reactor=linked;flux=linked.totalFlux;this.level=linked.rodLevel;heat=linked.heat;return true;}return false;
    }
    public double getTargetLevel(RodFunction curve,int atHeat){return switch(curve){case LINEAR->(atHeat-heatLower)*((levelUpper-levelLower)/(heatUpper-heatLower))+levelLower;case QUAD->Math.pow((atHeat-heatLower)/(heatUpper-heatLower),2)*(levelUpper-levelLower)+levelLower;case LOG->Math.pow((atHeat-heatUpper)/(heatLower-heatUpper),2)*(levelLower-levelUpper)+levelUpper;};}
    public int[] displayData(){return isLinked?new int[]{(int)(this.level*100),flux,(int)Math.round(heat*.00002*980+20)}:new int[]{0,0,0};}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    public void receiveControl(CompoundTag tag){if(tag.contains("function")){int ordinal=tag.getInt("function");if(ordinal<0||ordinal>=RodFunction.values().length)return;function=RodFunction.values()[ordinal];}else{double lo=tag.getDouble("levelLower"),hi=tag.getDouble("levelUpper"),cold=tag.getDouble("heatLower"),hot=tag.getDouble("heatUpper");if(!Double.isFinite(lo)||!Double.isFinite(hi)||!Double.isFinite(cold)||!Double.isFinite(hot))return;levelLower=lo;levelUpper=hi;heatLower=cold;heatUpper=hot;}setChanged();}
    @Override public String computerType(){return "reactor_control";}
    @Override public String[] computerMethods(){return new String[]{"isLinked","getReactor","setParams","getParams"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "isLinked"->new Object[]{isLinked};case "getReactor"->new Object[]{displayData()};case "getParams"->new Object[]{function.ordinal(),heatUpper,heatLower,levelUpper,levelLower};case "setParams"->{int fn=args.checkInteger(0);double maxHeat=args.checkDouble(1),minHeat=args.checkDouble(2),maxLevel=args.checkDouble(3)/100D,minLevel=args.checkDouble(4)/100D;function=RodFunction.values()[Mth.clamp(fn,0,2)];heatUpper=Mth.clamp(maxHeat,0,9999);heatLower=Mth.clamp(minHeat,0,9999);levelUpper=Mth.clamp(maxLevel,0,1);levelLower=Mth.clamp(minLevel,0,1);setChanged();yield new Object[0];}default->throw new ComputerException("No such source reactor_control method: "+method);};}
    @Override public int getContainerSize(){return 1;}
    @Override public boolean isEmpty(){return sensor.isEmpty();}
    @Override public ItemStack getItem(int slot){return slot==0?sensor:ItemStack.EMPTY;}
    @Override public ItemStack removeItem(int slot,int count){if(slot!=0||count<=0)return ItemStack.EMPTY;var taken=sensor.split(count);if(sensor.isEmpty())sensor=ItemStack.EMPTY;setChanged();return taken;}
    @Override public ItemStack removeItemNoUpdate(int slot){if(slot!=0)return ItemStack.EMPTY;var taken=sensor;sensor=ItemStack.EMPTY;return taken;}
    @Override public void setItem(int slot,ItemStack stack){if(slot!=0)return;sensor=stack;sensor.limitSize(64);setChanged();}
    @Override public void clearContent(){sensor=ItemStack.EMPTY;setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public boolean stillValid(Player player){return getLevel()!=null&&getLevel().getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    public void setCustomName(Component name){customName=name;setChanged();}
    @Override public boolean hasCustomName(){return customName!=null&&!customName.getString().isEmpty();}
    @Override public Component getCustomName(){return customName;}
    @Override public Component getName(){return hasCustomName()?customName:Component.translatable("container.reactorControl");}
    @Override public Component getDisplayName(){return getName();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ReactorControllerMenu(id,inventory,this,worldPosition);}
    /** The provider supplies the complete schema, also for vanilla spectator opens. */
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    private void writeSettings(CompoundTag tag){tag.putBoolean("isLinked",isLinked);tag.putDouble("levelLower",levelLower);tag.putDouble("levelUpper",levelUpper);tag.putDouble("heatLower",heatLower);tag.putDouble("heatUpper",heatUpper);tag.putInt("function",function.ordinal());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    public CompoundTag snapshot(){var tag=new CompoundTag();writeSettings(tag);tag.putInt("heat",heat);tag.putInt("flux",flux);tag.putDouble("level",this.level);return tag;}
    public void readSnapshot(CompoundTag tag){readSettings(tag);heat=tag.getInt("heat");flux=tag.getInt("flux");this.level=tag.getDouble("level");}
    private void readSettings(CompoundTag tag){isLinked=tag.getBoolean("isLinked");levelLower=tag.getDouble("levelLower");levelUpper=tag.getDouble("levelUpper");heatLower=tag.getDouble("heatLower");heatUpper=tag.getDouble("heatUpper");int ordinal=tag.getInt("function");function=ordinal>=0&&ordinal<RodFunction.values().length?RodFunction.values()[ordinal]:RodFunction.LINEAR;muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);writeSettings(tag);var list=new ListTag();if(!sensor.isEmpty()){var item=(CompoundTag)sensor.save(lookup);item.putByte("slot",(byte)0);list.add(item);}tag.put("items",list);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readSettings(tag);if(tag.contains("items",Tag.TAG_LIST)){sensor=ItemStack.EMPTY;for(var entry:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)entry;if(item.getByte("slot")==0)sensor=ItemStack.parseOptional(lookup,item);}}if(tag.contains("name")){String raw=tag.getString("name");try{customName=Component.Serializer.fromJson(raw,lookup);}catch(com.google.gson.JsonParseException error){customName=Component.literal(raw);}if(customName==null&&!raw.isEmpty())customName=Component.literal(raw);}if(tag.contains("heat"))readSnapshot(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=snapshot();if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    @Override public List<ProgressView> progressBars(){return isLinked?List.of(new ProgressView("gui.hbm.reactor.control",this.level,1,0x08ff00),new ProgressView("gui.hbm.reactor.heat",heat,50000,0xff8500)):List.of();}
}
