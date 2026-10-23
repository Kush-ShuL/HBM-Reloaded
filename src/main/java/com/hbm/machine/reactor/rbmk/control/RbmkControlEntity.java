package com.hbm.machine.reactor.rbmk.control;

import api.hbm.redstoneoverradio.*;
import com.hbm.api.computer.*;
import com.hbm.api.machine.ICopiable;
import com.hbm.energy.*;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import com.hbm.multiblock.NamedMultiblock;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Exact source manual/auto control laws, source-gated native HE drive, original OC/ROR/settings interfaces. */
public final class RbmkControlEntity extends RbmkColumnEntity implements RbmkFluxColumn.Control, EnergyReceiver, SidedEnergyConnector, ComputerControl, IRORValueProvider, IRORInteractive, ICopiable, MenuProvider, NamedMultiblock {
    public enum Color {RED,YELLOW,GREEN,BLUE,PURPLE}
    public enum Function {LINEAR,QUAD_UP,QUAD_DOWN}
    public double lastExtraction, extraction, targetLevel, startingLevel, levelLower, levelUpper, heatLower, heatUpper;
    public boolean hasPower;public Color color;public Function function=Function.LINEAR;
    private BigInteger power=BigInteger.ZERO;private static final BigInteger CONSUMPTION=BigInteger.valueOf(5000),CAPACITY=BigInteger.valueOf(50000);
    private PowerNode powerNode;private Component customName;
    public RbmkControlEntity(BlockPos pos,BlockState state){super(RbmkControlsContent.ENTITY.get(),pos,state);}
    public RbmkControlKind kind(){return ((RbmkControlBlock)getBlockState().getBlock()).kind;}
    public boolean automatic(){return kind().automatic;}public boolean powered(){return kind().powered;}
    @Override public boolean isLidRemovable(){return false;}@Override public boolean isModerated(){return kind().moderated;}
    @Override public int trackingRange(){return 100;}@Override public RbmkFluxColumn.Type neutronType(){return RbmkFluxColumn.Type.CONTROL_ROD;}
    @Override public String consoleType(){return automatic()?"CONTROL_AUTO":"CONTROL";}
    @Override public double controlLevel(){return extraction;}@Override public double controlMultiplier(){return multiplier();}
    public double multiplier(){double surge=0;if(!automatic()&&targetLevel<startingLevel&&Math.abs(extraction-targetLevel)>.01)surge=Math.sin(Math.pow(1-extraction,15)*Math.PI)*(startingLevel-targetLevel)*RbmkDials.getSurgeMod(level);return extraction+surge;}
    public void setTarget(double target){targetLevel=target;if(!automatic())startingLevel=extraction;}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkControlEntity control){if(level instanceof ServerLevel world)control.serverTick(world);else control.lastExtraction=control.extraction;}
    private void serverTick(ServerLevel world){
        if(automatic()){
            double faux=0,lower=Math.min(heatLower,heatUpper),upper=Math.max(heatLower,heatUpper);
            if(heat<lower)faux=levelLower;else if(heat>upper)faux=levelUpper;else faux=switch(function){case LINEAR->(heat-heatLower)*((levelUpper-levelLower)/(heatUpper-heatLower))+levelLower;case QUAD_UP->Math.pow((heat-heatLower)/(heatUpper-heatLower),2)*(levelUpper-levelLower)+levelLower;case QUAD_DOWN->Math.pow((heat-heatUpper)/(heatLower-heatUpper),2)*(levelLower-levelUpper)+levelUpper;};
            targetLevel=Mth.clamp(faux*.01,0,1);
        }
        hasPower=true;if(powered()){updatePower(world);if(power.compareTo(CONSUMPTION)<0)hasPower=false;}
        lastExtraction=extraction;
        if(hasPower){double speed=.00277D*RbmkDials.getControlSpeed(world);if(extraction<targetLevel){extraction+=speed;if(extraction>targetLevel)extraction=targetLevel;}if(extraction>targetLevel){extraction-=speed;if(extraction<targetLevel)extraction=targetLevel;}if(powered()&&extraction!=lastExtraction)power=power.subtract(CONSUMPTION);}
        baseTick(world);setChanged();
    }
    private void updatePower(ServerLevel world){if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(worldPosition).setConnections(new NodeConnection(worldPosition.below(),Direction.DOWN));HbmEnergy.nodespace(world).createNode(powerNode);}if(powerNode.hasValidNet())powerNode.addReceiver(this);}
    private void disconnect(){if(level instanceof ServerLevel world&&powerNode!=null)HbmEnergy.nodespace(world).destroyNode(powerNode);powerNode=null;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean canConnect(Direction side){return powered()&&side==Direction.DOWN;}@Override public ConnectionPriority getPriority(){return ConnectionPriority.LOW;}
    @Override public long getPower(){return power.longValueExact();}@Override public long getMaxPower(){return powered()?50000:0;}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return powered()?CAPACITY:BigInteger.ZERO;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
    @Override public void onMelt(int reduce){if(isModerated()){int count=2+level.random.nextInt(2);for(int i=0;i<count;i++)spawnDebris(RbmkDebrisEntity.Type.GRAPHITE);}int count=2+level.random.nextInt(2);for(int i=0;i<count;i++)spawnDebris(RbmkDebrisEntity.Type.ROD);standardMelt(reduce);}
    @Override protected void saveState(CompoundTag tag){super.saveState(tag);tag.putDouble("level",extraction);tag.putDouble("targetLevel",targetLevel);if(automatic()){tag.putDouble("levelLower",levelLower);tag.putDouble("levelUpper",levelUpper);tag.putDouble("heatLower",heatLower);tag.putDouble("heatUpper",heatUpper);tag.putInt("function",function.ordinal());}else{tag.putDouble("startingLevel",startingLevel);tag.putDouble("mult",multiplier());if(color!=null)tag.putInt("color",color.ordinal());}}
    @Override protected void loadState(CompoundTag tag){super.loadState(tag);extraction=tag.getDouble("level");targetLevel=tag.getDouble("targetLevel");if(automatic()){levelLower=tag.getDouble("levelLower");levelUpper=tag.getDouble("levelUpper");heatLower=tag.getDouble("heatLower");heatUpper=tag.getDouble("heatUpper");function=Function.values()[Mth.clamp(tag.getInt("function"),0,2)];}else{if(tag.contains("startingLevel"))startingLevel=tag.getDouble("startingLevel");color=tag.contains("color")?Color.values()[Mth.clamp(tag.getInt("color"),0,4)]:null;}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("name"))customName=Component.Serializer.fromJson(tag.getString("name"),lookup);power=BigInteger.ZERO;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putString("powerExact",power.toString());tag.putBoolean("hasPower",hasPower);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);power=new BigInteger(tag.getString("powerExact"));hasPower=tag.getBoolean("hasPower");}
    @Override public CompoundTag consoleData(){var tag=new CompoundTag();tag.putDouble("level",extraction);tag.putDouble("targetLevel",targetLevel);if(!automatic())tag.putShort("color",(short)(color==null?-1:color.ordinal()));return tag;}
    public boolean hasPermission(Player player){return player.position().distanceToSqr(Vec3.atLowerCornerOf(worldPosition))<400;}
    public void receiveControl(Player player,CompoundTag data){if(!hasPermission(player))return;if(automatic()){if(data.contains("function")){int index=data.getInt("function");if(index<0||index>=Function.values().length)return;function=Function.values()[index];}else{levelLower=data.getDouble("levelLower");levelUpper=data.getDouble("levelUpper");heatLower=data.getDouble("heatLower");heatUpper=data.getDouble("heatUpper");}}else{if(data.contains("level"))setTarget(data.getDouble("level"));if(data.contains("color")){var next=Color.values()[Math.abs(data.getInt("color")%5)];color=next==color?null:next;}}setChanged();}
    @Override public CompoundTag getSettings(Level world,BlockPos pos){var tag=new CompoundTag();if(automatic()){tag.putDouble("levelLower",levelLower);tag.putDouble("levelUpper",levelUpper);tag.putDouble("heatLower",heatLower);tag.putDouble("heatUpper",heatUpper);tag.putInt("function",function.ordinal());}else if(color!=null)tag.putInt("color",color.ordinal());return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level world,Player player,BlockPos pos){if(automatic()){if(tag.contains("levelLower"))levelLower=tag.getDouble("levelLower");if(tag.contains("levelUpper"))levelUpper=tag.getDouble("levelUpper");if(tag.contains("heatLower"))heatLower=tag.getDouble("heatLower");if(tag.contains("heatUpper"))heatUpper=tag.getDouble("heatUpper");if(tag.contains("function"))function=Function.values()[Math.floorMod(tag.getInt("function"),3)];}else color=tag.contains("color")?Color.values()[Math.floorMod(tag.getInt("color"),5)]:null;setChanged();}
    @Override public String computerType(){return "rbmk_control_rod";}@Override public String[] computerMethods(){return automatic()?new String[]{"getLevel","getTargetLevel","getCoordinates","getHeat","getInfo","setLevel"}:new String[]{"getLevel","getTargetLevel","getCoordinates","getHeat","getInfo","setLevel","getColor","setColor"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{return switch(method){case "getLevel"->new Object[]{multiplier()*100};case "getTargetLevel"->new Object[]{targetLevel*100};case "getCoordinates"->new Object[]{worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "getHeat"->new Object[]{heat};case "getInfo"->new Object[]{heat,multiplier()*100,targetLevel*100,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "setLevel"->{targetLevel=Mth.clamp(args.checkDouble(0)/100,0,1);setChanged();yield new Object[]{};}case "getColor"->{if(automatic()||color==null)throw new ComputerException("No color assigned");yield new Object[]{color.ordinal()};}case "setColor"->{if(automatic())throw new ComputerException("No such method: "+method);color=Color.values()[Mth.clamp(args.checkInteger(0),0,4)];setChanged();yield new Object[]{true};}default->throw new ComputerException("No such method: "+method);};}
    @Override public String[] getFunctionInfo(){return automatic()?new String[]{PREFIX_VALUE+"extraction"}:new String[]{PREFIX_VALUE+"extraction",PREFIX_FUNCTION+"setrods"+NAME_SEPARATOR+"percent",PREFIX_FUNCTION+"extendrods"+NAME_SEPARATOR+"percent"};}
    @Override public String provideRORValue(String name){return name.equals(PREFIX_VALUE+"extraction")?""+(int)(extraction*100):null;}
    @Override public String runRORFunction(String name,String[] params){if(automatic()||params.length==0)return null;if(name.equals(PREFIX_FUNCTION+"setrods")){setTarget(IRORInteractive.parseInt(params[0],0,100)/100D);setChanged();}if(name.equals(PREFIX_FUNCTION+"extendrods")){setTarget(Mth.clamp(targetLevel+IRORInteractive.parseInt(params[0],-100,100)/100D,0,1));setChanged();}return null;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable(automatic()?"container.rbmkControlAuto":"container.rbmkControl"):customName;}@Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RbmkControlMenu(id,inventory,worldPosition);}
    @Override public List<ProgressView> progressBars(){var bars=new ArrayList<>(super.progressBars());bars.add(new ProgressView("tile.rbmk.dodd.level",extraction,1,0xDFC748));return List.copyOf(bars);}
}
