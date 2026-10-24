package com.hbm.machine.blastfurnace;

import com.hbm.api.machine.MachineView;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.fuel.ModuleBurnTime;
import com.hbm.multiblock.*;
import com.hbm.ore.OreProcessing;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 five-slot fuel processor, private airblast/flue tanks and source float arithmetic. */
public final class BlastFurnaceBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,StandardFluidTransceiver,MachineView, com.hbm.multiblock.MuffledMachine {
    public static final int FUEL_COAL=1600,FUEL_RATE=800,MAX_FUEL=FUEL_COAL*24,FLUE_GAS=8;
    public static Consumer<BlastFurnaceBlockEntity> CLIENT_TICK=machine->{};
    private final ItemStack[] slots=new ItemStack[5];
    public final FluidTank[] tanks={new FluidTank(HbmFluids.AIRBLAST,4000),new FluidTank(HbmFluids.FLUE,1000)};
    public final ModuleBurnTime burnModule=new ModuleBurnTime().setWoodHeatMod(0D);
    public boolean isProgressing,muffled,tilted;public float progress,speed;public int fuel;
    private int tiltBlocksChecked,tiltBlocksValid;private Component customName;private FluidPorts fluidPorts;private List<BlockPos> currentPorts=List.of();private CompoundTag lastSent;
    public BlastFurnaceBlockEntity(BlockPos pos,BlockState state){super(BlastFurnaceContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.blastFurnace"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory player,Player owner){return new BlastFurnaceMenu(id,player,this,menuData,worldPosition);}
    public List<BlastFurnaceRecipe> recipes(){return level==null?List.of():BlastFurnaceRecipes.recipes(level.getRecipeManager());}
    public static BlockPos[] portPositions(BlockPos core,Direction facing){return new BlockPos[]{core.east(),core.west(),core.south(),core.north(),core.relative(facing).above(3),core.relative(facing).above(5),core.above(6)};}
    public static Direction[] portSides(Direction facing){return new Direction[]{Direction.EAST,Direction.WEST,Direction.SOUTH,Direction.NORTH,facing,facing,Direction.UP};}
    public static void tick(Level level,BlockPos pos,BlockState state,BlastFurnaceBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
    private void serverTick(ServerLevel server){
        updatePorts(server);checkFoundation(server);
        if(!slots[0].isEmpty()){int capacity=MAX_FUEL-fuel,burn=getBurnTime(slots[0]);if(burn>0&&burn<=capacity){fuel+=burn;slots[0].shrink(1);setChanged();}}
        speed=0F;var recipe=recipes().stream().filter(value->value.accepts(slots[1],slots[2])).findFirst().orElse(null);int[] consumed=recipe==null?null:recipe.consumption(slots[1],slots[2]);double rate=consumed==null?1D:OreProcessing.rate(new ItemStack[]{slots[1],slots[2]},consumed);
        if(!tilted&&recipe!=null&&fuel>=FUEL_RATE&&consumed!=null&&canOutput(recipe,rate)){
            speed=net.minecraft.util.Mth.clamp(1F+tanks[0].getFill()*8F/tanks[0].getMaxFill(),1F,5F);isProgressing=true;progress+=speed/recipe.duration();
            if(progress>=1F){process(recipe,consumed,rate);progress=0F;fuel-=FUEL_RATE;tanks[1].setFill((int)(tanks[1].getFill()+FLUE_GAS*(recipe.duration()/speed)));if(tanks[1].getFill()>tanks[1].getMaxFill()){int spill=tanks[1].getFill()-tanks[1].getMaxFill();tanks[1].getTankType().release(server,worldPosition,tanks[1],spill,FluidTraits.Release.SPILL);tanks[1].setFill(tanks[1].getMaxFill());}setChanged();}
            if(server.random.nextInt(10)==0&&!muffled)server.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SoundEvents.FIRE_AMBIENT,SoundSource.BLOCKS,1F,.5F+server.random.nextFloat()*.25F);
        }else{isProgressing=false;progress=0F;}
        if(tanks[0].getFill()>0)tanks[0].setFill((int)(tanks[0].getFill()*.95));
        if(isProgressing||tanks[0].getFill()>0)setChanged();var tag=getUpdateTag(server.registryAccess());if(!tag.equals(lastSent)||server.getGameTime()%20==0){lastSent=tag;var packet=ClientboundBlockEntityDataPacket.create(this,(ignored,lookup)->tag);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<10000)player.connection.send(packet);dispatchScope.commit();}}
    }
    /** Original hasQuantities and unordered consumption; fixes its config-only one-input index crash. */
    private boolean canOutput(BlastFurnaceRecipe recipe,double rate){for(int i=0;i<recipe.outputItems().size();i++){var existing=slots[3+i];if(existing.isEmpty())continue;var output=recipe.outputItems().get(i);if(output.possibleMultiOutput())return false;var stack=output.single();if(!sameLegacy(stack,existing)||OreProcessing.maximumCount(stack.getCount(),rate)+existing.getCount()>stack.getMaxStackSize())return false;}return true;}
    private void process(BlastFurnaceRecipe recipe,int[] consumed,double rate){for(int i=0;i<recipe.outputItems().size();i++){var output=OreProcessing.output(recipe.outputItems().get(i).collapse(level.random),rate,level.random);if(output.isEmpty())continue;if(slots[3+i].isEmpty())slots[3+i]=output;else slots[3+i].grow(output.getCount());}slots[1].shrink(consumed[0]);slots[2].shrink(consumed[1]);}
    private static boolean sameLegacy(ItemStack a,ItemStack b){return ItemStack.isSameItem(a,b)&&a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
    public int getBurnTime(ItemStack stack){return stack.isEmpty()||stack.hasCraftingRemainingItem()?0:burnModule.getBurnHeat(burnModule.getBurnTime(stack,0D),stack,0D);}
    private void updatePorts(ServerLevel server){var facing=DummyableBlock.facing(getBlockState());var positions=portPositions(worldPosition,facing);var sides=portSides(facing);var loaded=new ArrayList<BlockPos>();var connections=new ArrayList<com.hbm.energy.NodeConnection>();for(int i=0;i<positions.length;i++){var pos=positions[i];if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos))){loaded.add(pos);connections.add(new com.hbm.energy.NodeConnection(pos.relative(sides[i]),sides[i]));}}if(!currentPorts.equals(loaded)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty())fluidPorts=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(com.hbm.energy.NodeConnection[]::new));}if(fluidPorts!=null)fluidPorts.update(server,this);}
    private void disconnect(){if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;currentPorts=List.of();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    /** Original normal-weight standardFloor3x3, four corners and one probe each20ticks. */
    private void checkFoundation(ServerLevel server){if(!com.hbm.machine.reactor.zirnox.ZirnoxConfig.gravity()){tilted=false;return;}int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;if(tiltBlocksChecked>=4){boolean next=tiltBlocksValid<tiltBlocksChecked*.95;if(next&&!tilted)server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,com.hbm.registry.HbmSounds.get("block.metalImpact"),SoundSource.BLOCKS,3,1);tilted=next;setChanged();tiltBlocksChecked=tiltBlocksValid=0;}var pos=worldPosition.offset(-1+(tiltBlocksChecked/2)*2,-1,-1+(tiltBlocksChecked%2)*2);tiltBlocksChecked++;var ground=server.getBlockState(pos);var id=BuiltInRegistries.BLOCK.getKey(ground.getBlock()).toString();if(ground.isFaceSturdy(server,pos,Direction.UP)&&!com.hbm.block.fluid.LegacyMaterials.of(ground).name().equals("sand")&&!Set.of("hbm:dirt_dead","hbm:dirt_oily","hbm:stone_cracked").contains(id))tiltBlocksValid++;}
    @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return Math.max(tanks[1].getFill()*50/tanks[1].getMaxFill(),8);}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return isProgressing?List.of(new ProgressView("jade.hbm.processing",progress,1,0x71C78B)):List.of();}
    private void writeState(CompoundTag tag){tag.putFloat("progress",progress);tag.putFloat("speed",speed);tag.putInt("fuel",fuel);tag.putBoolean("isProgressing",isProgressing);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tanks[0].writeToNBT(tag,"t0");tanks[1].writeToNBT(tag,"t1");}
    private void readState(CompoundTag tag){progress=tag.getFloat("progress");speed=tag.getFloat("speed");fuel=tag.getInt("fuel");isProgressing=tag.getBoolean("isProgressing");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");tanks[0].readFromNBT(tag,"t0");tanks[1].readFromNBT(tag,"t1");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=NonNullList.withSize(5,ItemStack.EMPTY);for(int i=0;i<5;i++)items.set(i,slots[i]);ContainerHelper.saveAllItems(tag,items,lookup);writeState(tag);tag.remove("speed");tag.remove("isProgressing");if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var items=NonNullList.withSize(5,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,items,lookup);for(int i=0;i<5;i++)slots[i]=items.get(i);readState(tag);if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 5;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}
    @Override public ItemStack removeItem(int slot,int count){var result=slots[slot].split(count);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var result=slots[slot];slots[slot]=ItemStack.EMPTY;return result;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>stack.getMaxStackSize())stack.setCount(stack.getMaxStackSize());setChanged();}
    @Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}@Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0?getBurnTime(stack)>0:slot==1||slot==2;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{1,2,0,3,4};}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){if(slot==1||slot==2){if(sameLegacy(stack,slots[slot==1?2:1]))return false;for(var recipe:recipes())for(var input:recipe.inputItems())if(input.matchesProcessing(stack,true))return true;return false;}return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=3;}
    private final ContainerData menuData=new ContainerData(){public int getCount(){return 7;}public int get(int index){if(index==6)return isProgressing?1:0;int value=index<2?fuel:index<4?Float.floatToRawIntBits(progress):Float.floatToRawIntBits(speed);return(value>>>((index%2)*16))&65535;}public void set(int index,int value){}};

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
