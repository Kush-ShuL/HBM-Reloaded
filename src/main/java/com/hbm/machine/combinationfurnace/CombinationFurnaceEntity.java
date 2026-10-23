package com.hbm.machine.combinationfurnace;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.machine.electricfurnace.ElectricFurnaceBlockEntity;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.ore.OreProcessing;
import com.hbm.pollution.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Exact source private TU and optional solid/fluid operation, with its real three-type exhaust buffers. */
public final class CombinationFurnaceEntity extends ThermalMachine implements StandardFluidSender,IFluidCopiable {
    public static final int PROCESS_TIME=20000,MAX_HEAT=100000;
    public static Consumer<CombinationFurnaceEntity> CLIENT_TICK=machine->{};
    public final FluidTank tank=new FluidTank(HbmFluids.NONE,24000),smoke=new FluidTank(HbmFluids.fromName("SMOKE"),50),smokeLeaded=new FluidTank(HbmFluids.fromName("SMOKE_LEADED"),50),smokePoison=new FluidTank(HbmFluids.fromName("SMOKE_POISON"),50);
    public int heat,progress;public boolean wasOn,tilted;
    private FluidPorts ports;private List<BlockPos> boundPorts=List.of();
    private final ContainerData data=new ContainerData(){public int getCount(){return 10;}public int get(int index){return switch(index){case 0->progress&65535;case 1->progress>>>16;case 2->heat&65535;case 3->heat>>>16;case 4->tank.getFill()&65535;case 5->tank.getFill()>>>16;case 6->tank.getMaxFill()&65535;case 7->tank.getMaxFill()>>>16;case 8->tank.getTankType().getID();case 9->tank.getPressure();default->0;};}public void set(int index,int value){}};
    public CombinationFurnaceEntity(BlockPos pos,BlockState state){super(CombinationFurnaceContent.ENTITY.get(),pos,state,4);}
    @Override protected String title(){return "container.furnaceCombination";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CombinationFurnaceMenu(id,inventory,this,data);}
    @Override protected void saveMachine(CompoundTag tag){tank.writeToNBT(tag,"tank");smoke.writeToNBT(tag,"smoke0");smokeLeaded.writeToNBT(tag,"smoke1");smokePoison.writeToNBT(tag,"smoke2");tag.putInt("prog",progress);tag.putInt("heat",heat);tag.putBoolean("tilted",tilted);}
    @Override protected void loadMachine(CompoundTag tag){tank.readFromNBT(tag,"tank");smoke.readFromNBT(tag,"smoke0");smokeLeaded.readFromNBT(tag,"smoke1");smokePoison.readFromNBT(tag,"smoke2");progress=tag.getInt("prog");heat=tag.getInt("heat");tilted=tag.getBoolean("tilted");}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("items",Tag.TAG_LIST)){slots.replaceAll(stack->ItemStack.EMPTY);for(Tag value:tag.getList("items",Tag.TAG_COMPOUND)){CompoundTag item=(CompoundTag)value;int index=item.getByte("slot")&255;if(index<4)slots.set(index,ItemStack.parseOptional(lookup,item));}}if(tag.contains("name",Tag.TAG_STRING)&&!tag.getString("name").isEmpty())setCustomName(Component.literal(tag.getString("name")));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){CompoundTag tag=super.getUpdateTag(lookup);tag.putBoolean("wasOn",wasOn);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);wasOn=tag.getBoolean("wasOn");}
    public static List<BlockPos> portPositions(BlockPos core){var positions=new ArrayList<BlockPos>();for(int y=0;y<2;y++)for(int[] pair:new int[][]{{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}})positions.add(core.offset(pair[0],y,pair[1]));return positions;}
    public static List<Direction> portSides(BlockPos core,BlockPos pos){var sides=new ArrayList<Direction>();if(pos.getX()==core.getX()-1)sides.add(Direction.WEST);if(pos.getX()==core.getX()+1)sides.add(Direction.EAST);if(pos.getZ()==core.getZ()-1)sides.add(Direction.NORTH);if(pos.getZ()==core.getZ()+1)sides.add(Direction.SOUTH);return sides;}
    private void updatePorts(ServerLevel server){
        var active=new ArrayList<BlockPos>();var block=(CombinationFurnaceBlock)getBlockState().getBlock();for(BlockPos pos:portPositions(worldPosition))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(block)){block.setPort(server,pos,true);active.add(pos);}
        if(!active.equals(boundPorts)){disconnect();boundPorts=List.copyOf(active);if(!active.isEmpty()){var connections=new ArrayList<NodeConnection>();for(BlockPos pos:active)for(Direction side:portSides(worldPosition,pos))connections.add(new NodeConnection(pos.relative(side),side));ports=new FluidPorts(active.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}
        if(ports!=null)ports.update(server,this);
    }
    private void pullHeat(){if(heat>=MAX_HEAT)return;if(level.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int difference=source.getHeatStored()-heat;if(difference==0)return;if(difference>0){difference=(int)Math.ceil(difference*.25D);source.useUpHeat(difference);heat+=difference;if(heat>MAX_HEAT)heat=MAX_HEAT;return;}}heat=Math.max(heat-Math.max(heat/1000,1),0);}
    public CombinationFurnaceRecipe recipe(){return level==null?null:CombinationFurnaceRecipes.find(level.getRecipeManager(),slots.get(0));}
    public boolean canSmelt(){
        var recipe=recipe();if(recipe==null)return false;double rate=OreProcessing.rate(slots.get(0));ItemStack output=recipe.solid(),current=slots.get(1);
        if(!output.isEmpty()){int maximum=OreProcessing.maximumCount(output.getCount(),rate);if(current.isEmpty()){if(maximum>output.getMaxStackSize())return false;}else if(!ElectricFurnaceBlockEntity.sameLegacyItem(output,current)||(long)maximum+current.getCount()>current.getMaxStackSize())return false;}
        FluidStack fluid=recipe.fluid();if(fluid!=null){if(tank.getTankType()!=fluid.type&&tank.getFill()>0)return false;if(tank.getTankType()==fluid.type&&(long)tank.getFill()+OreProcessing.maximumCount(fluid.fill,rate)>tank.getMaxFill())return false;}
        return true;
    }
    private void pollute(ServerLevel server,PollutionType type,float amount){FluidTank target=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?smokeLeaded:smokePoison;target.setFill(target.getFill()+(int)Math.ceil(amount*100));if(target.getFill()>target.getMaxFill()){int overflow=target.getFill()-target.getMaxFill();target.setFill(target.getMaxFill());PollutionSystem.increment(server,worldPosition,type,overflow/100F);if(server.random.nextInt(3)==0)server.playSound(null,worldPosition,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}}
    public static void tick(Level level,BlockPos pos,BlockState state,CombinationFurnaceEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){
        updatePorts(server);pullHeat();wasOn=false;var array=slots.toArray(ItemStack[]::new);tank.unloadTank(2,3,array);slots.set(2,array[2]);slots.set(3,array[3]);
        if(canSmelt()){int burn=heat/100;if(burn>0){wasOn=true;progress+=burn;heat-=burn;
            if(progress>=PROCESS_TIME){setChanged();progress-=PROCESS_TIME;var recipe=recipe();double rate=OreProcessing.rate(slots.get(0));ItemStack output=OreProcessing.output(recipe.solid(),rate,server.random);if(!output.isEmpty()){if(slots.get(1).isEmpty())slots.set(1,output);else slots.get(1).grow(output.getCount());}FluidStack fluid=recipe.fluid();if(fluid!=null){if(tank.getTankType()!=fluid.type)tank.setTankType(fluid.type);tank.setFill(tank.getFill()+OreProcessing.count(fluid.fill,rate,server.random));}removeItem(0,1);}
            for(Entity entity:server.getEntities((Entity)null,new AABB(worldPosition.getX()-.5,worldPosition.getY()+2,worldPosition.getZ()-.5,worldPosition.getX()+1.5,worldPosition.getY()+4,worldPosition.getZ()+1.5)))entity.igniteForSeconds(5);
            if(server.getGameTime()%10==0)server.playSound(null,worldPosition.getX(),worldPosition.getY()+1,worldPosition.getZ(),HbmSounds.get("weapon.flamethrowerShoot"),SoundSource.BLOCKS,.25F,.5F);
            if(server.getGameTime()%20==0)pollute(server,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*3);
        }}else progress=0;sync(50);
    }
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tank,smoke,smokeLeaded,smokePoison};}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&level!=null&&CombinationFurnaceRecipes.find(level.getRecipeManager(),stack)!=null;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1;}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.combination_furnace.progress",progress,PROCESS_TIME,0xD08020),new ProgressView("gui.hbm.combination_furnace.heat",heat,MAX_HEAT,0xD05010));}
    private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;boundPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
