package com.hbm.machine.reactor.rbmk.boiler;
import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import com.hbm.multiblock.NamedMultiblock;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Original boiler: exact heat/water arithmetic, type-compression, overflow vent and adaptive unified private port groups. */
public final class RbmkBoilerEntity extends RbmkColumnEntity implements StandardFluidTransceiver,ComputerControl,IRORValueProvider,MenuProvider,NamedMultiblock {
    public final FluidTank feed=new FluidTank(HbmFluids.WATER,10000),steam=new FluidTank(HbmFluids.STEAM,1000000);
    public int consumption,output,ventDelay;private Component customName;
    private record Shape(List<BlockPos> positions,List<NodeConnection> connections){}
    private Shape lastShape;private final Map<HbmFluidType,FluidNode> nodes=new IdentityHashMap<>();
    public RbmkBoilerEntity(BlockPos pos,BlockState state){super(RbmkBoilerContent.ENTITY.get(),pos,state);}
    @Override public RbmkFluxColumn.Type neutronType(){return RbmkFluxColumn.Type.OTHER;}@Override public String consoleType(){return "BOILER";}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkBoilerEntity machine){if(level instanceof ServerLevel world)machine.serverTick(world);}
    private void serverTick(ServerLevel world){
        updatePorts(world);consumption=output=0;if(ventDelay>0)ventDelay--;
        double heatProvided=heat-heatFromSteam(steam.getTankType());
        if(heatProvided>0){double heatPerMb=RbmkDials.getBoilerHeatConsumption(world),factor=factorFromSteam(steam.getTankType());int waterUsed,produced;
            if(steam.getTankType()==HbmFluids.ULTRAHOTSTEAM){produced=(int)Math.floor((heatProvided/heatPerMb)*100D/factor);waterUsed=(int)Math.floor(produced/100D*factor);if(feed.getFill()<waterUsed){produced=(int)Math.floor(feed.getFill()*100D/factor);waterUsed=(int)Math.floor(produced/100D*factor);}}
            else{waterUsed=(int)Math.floor(heatProvided/heatPerMb);waterUsed=Math.min(waterUsed,feed.getFill());produced=(int)Math.floor(waterUsed*100D/factor);}
            consumption=waterUsed;output=produced;feed.setFill(feed.getFill()-waterUsed);steam.setFill(steam.getFill()+produced);
            if(steam.getFill()>steam.getMaxFill()){steam.setFill(steam.getMaxFill());if(ventDelay<=0){double x=worldPosition.getX()+.25+world.random.nextInt(2)*.5,z=worldPosition.getZ()+.25+world.random.nextInt(2)*.5;RbmkBoilerEffects.steam(world,x,worldPosition.getY()+RbmkDials.getColumnHeight(world),z,worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5);ventDelay=20+world.random.nextInt(10);world.playSound(null,worldPosition.getX(),worldPosition.getY()+RbmkDials.getColumnHeight(world),worldPosition.getZ(),HbmSounds.get("block.steamEngineOperate"),SoundSource.BLOCKS,2,1+world.random.nextFloat()*.25F);}}
            heat-=waterUsed*heatPerMb;
        }
        baseTick(world);setChanged();
    }
    public static double heatFromSteam(HbmFluidType type){return type==HbmFluids.STEAM?100:type==HbmFluids.HOTSTEAM?300:type==HbmFluids.SUPERHOTSTEAM?450:type==HbmFluids.ULTRAHOTSTEAM?600:0;}
    public static double factorFromSteam(HbmFluidType type){return type==HbmFluids.STEAM?1:type==HbmFluids.HOTSTEAM?10:type==HbmFluids.SUPERHOTSTEAM?100:type==HbmFluids.ULTRAHOTSTEAM?1000:0;}
    private int loaderMode(){if(level!=null&&level.getBlockState(worldPosition.below(2)).is(RbmkBoilerContent.LOADER.get()))return 2;return level!=null&&level.getBlockState(worldPosition.below()).is(RbmkBoilerContent.LOADER.get())?1:0;}
    private Shape shape(){int mode=loaderMode();var top=worldPosition.above(RbmkDials.getColumnHeight(level));var positions=new ArrayList<BlockPos>();var connections=new ArrayList<NodeConnection>();positions.add(top);connections.add(new NodeConnection(top.above(),Direction.UP));if(mode!=1){positions.add(worldPosition);connections.add(new NodeConnection(worldPosition.below(),Direction.DOWN));}if(mode!=0){var loader=worldPosition.below(mode);positions.add(loader);for(var side:Direction.values())connections.add(new NodeConnection(loader.relative(side),side));}return new Shape(List.copyOf(positions),List.copyOf(connections));}
    private void updatePorts(ServerLevel world){var shape=shape();if(!shape.equals(lastShape)||nodes.values().stream().anyMatch(FluidNode::isExpired)){disconnect();lastShape=shape;}
        var active=Set.of(feed.getTankType(),steam.getTankType());for(var entry:List.copyOf(nodes.entrySet()))if(!active.contains(entry.getKey())){FluidNetworks.nodespace(world,entry.getKey()).destroyNode(entry.getValue());nodes.remove(entry.getKey());}
        for(var type:active){var node=nodes.get(type);if(node==null){node=new FluidNode(shape.positions.toArray(BlockPos[]::new)).setConnections(shape.connections.toArray(NodeConnection[]::new));FluidNetworks.nodespace(world,type).createNode(node);nodes.put(type,node);}if(node.hasValidNet()){if(type==feed.getTankType())node.addReceiver(this);if(type==steam.getTankType())node.addProvider(this);}}
    }
    private void disconnect(){if(level instanceof ServerLevel world)nodes.forEach((type,node)->FluidNetworks.nodespace(world,type).destroyNode(node));nodes.clear();lastShape=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    /** Capability query is at the actual core; unified remote port-node connections are constructed separately. */
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side==Direction.DOWN&&loaderMode()!=1&&(type==feed.getTankType()||type==steam.getTankType());}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{feed,steam};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{feed};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{steam};}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    public boolean hasPermission(Player player){return player.position().distanceToSqr(Vec3.atLowerCornerOf(worldPosition))<400;}
    public void cycleCompressor(){if(heat>50&&feed.getFill()>0)return;var type=steam.getTankType();if(type==HbmFluids.STEAM){steam.setTankType(HbmFluids.HOTSTEAM);steam.setFill(steam.getFill()/10);}if(type==HbmFluids.HOTSTEAM){steam.setTankType(HbmFluids.SUPERHOTSTEAM);steam.setFill(steam.getFill()/10);}if(type==HbmFluids.SUPERHOTSTEAM){steam.setTankType(HbmFluids.ULTRAHOTSTEAM);steam.setFill(steam.getFill()/10);}if(type==HbmFluids.ULTRAHOTSTEAM){steam.setTankType(HbmFluids.STEAM);steam.setFill(Math.min(steam.getFill()*1000,steam.getMaxFill()));}setChanged();}
    @Override protected void collectOverpressureNetworks(Set<FluidNetMK2> networks){if(RbmkDials.getOverpressure(level)){var node=nodes.get(steam.getTankType());if(node!=null&&node.hasValidNet())networks.add(node.network());}}
    @Override public void onMelt(int reduce){int count=1+level.random.nextInt(2);for(int i=0;i<count;i++)spawnDebris(RbmkDebrisEntity.Type.BLANK);super.onMelt(reduce);}
    @Override protected void saveState(CompoundTag tag){super.saveState(tag);feed.writeToNBT(tag,"feed");steam.writeToNBT(tag,"steam");}@Override protected void loadState(CompoundTag tag){super.loadState(tag);feed.readFromNBT(tag,"feed");steam.readFromNBT(tag,"steam");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("name"))customName=Component.Serializer.fromJson(tag.getString("name"),lookup);}
    @Override public CompoundTag consoleData(){var tag=new CompoundTag();tag.putInt("water",feed.getFill());tag.putInt("maxWater",feed.getMaxFill());tag.putInt("steam",steam.getFill());tag.putInt("maxSteam",steam.getMaxFill());tag.putShort("type",(short)steam.getTankType().getID());return tag;}
    public static int steamIndex(HbmFluidType type){return type==HbmFluids.HOTSTEAM?1:type==HbmFluids.SUPERHOTSTEAM?2:type==HbmFluids.ULTRAHOTSTEAM?3:0;}public static HbmFluidType steamType(int index){return switch(index){case 1->HbmFluids.HOTSTEAM;case 2->HbmFluids.SUPERHOTSTEAM;case 3->HbmFluids.ULTRAHOTSTEAM;default->HbmFluids.STEAM;};}
    @Override public String computerType(){return "rbmk_boiler";}@Override public String[] computerMethods(){return new String[]{"getHeat","getSteam","getSteamMax","getWater","getWaterMax","getCoordinates","getInfo","getSteamType","setSteamType"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{return switch(method){case "getHeat"->new Object[]{heat};case "getSteam"->new Object[]{steam.getFill()};case "getSteamMax"->new Object[]{steam.getMaxFill()};case "getWater"->new Object[]{feed.getFill()};case "getWaterMax"->new Object[]{feed.getMaxFill()};case "getCoordinates"->new Object[]{worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "getInfo"->new Object[]{heat,steam.getFill(),steam.getMaxFill(),feed.getFill(),feed.getMaxFill(),steamIndex(steam.getTankType()),worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "getSteamType"->new Object[]{steamIndex(steam.getTankType())};case "setSteamType"->{steam.setTankType(steamType(args.checkInteger(0)));setChanged();yield new Object[]{true};}default->throw new ComputerException("No such method: "+method);};}
    public void provideExtraInfo(CompoundTag tag){tag.putDouble("consumption",consumption);tag.putDouble("outputmb",output);}@Override public String[] getFunctionInfo(){return new String[]{PREFIX_VALUE+"feed",PREFIX_VALUE+"steam",PREFIX_VALUE+"consumption"};}@Override public String provideRORValue(String name){if(name.equals(PREFIX_VALUE+"feed"))return ""+feed.getFill();if(name.equals(PREFIX_VALUE+"steam"))return ""+steam.getFill();if(name.equals(PREFIX_VALUE+"consumption"))return ""+consumption;return null;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.rbmkBoiler"):customName;}@Override public void setCustomName(Component name){customName=name;setChanged();}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RbmkBoilerMenu(id,inventory,worldPosition);}
    @Override public List<TankView> tanks(){return List.of(new TankView(feed.getTankType(),feed.getFill(),feed.getMaxFill()),new TankView(steam.getTankType(),steam.getFill(),steam.getMaxFill()));}
}
