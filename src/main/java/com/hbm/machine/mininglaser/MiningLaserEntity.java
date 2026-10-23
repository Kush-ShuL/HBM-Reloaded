package com.hbm.machine.mininglaser;

import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.*;
import com.hbm.machine.crystallizer.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.excavator.ExcavatorContent;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.machine.shredder.ShredderRecipes;
import com.hbm.item.recipedependencies.FortuneUpgradeItem;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original scanning, power ordering, processing precedence and physical drop collection. No independent process recipe table or computer API. */
public final class MiningLaserEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector,StandardFluidSender {
    public static final long MAX_POWER=100_000_000L;
    public static final int BASE_CONSUMPTION=10_000;
    public long power;
    public final FluidTank tank=new FluidTank(HbmFluids.OIL,64_000);
    public boolean isOn,beam,tilted;
    private boolean redstonePowered;
    public int targetX,targetY,targetZ,lastTargetX,lastTargetY,lastTargetZ;
    public double breakProgress;
    private double clientBreakProgress;
    private PowerNode powerNode;
    private FluidPorts fluidPorts;

    public MiningLaserEntity(BlockPos pos,BlockState state){super(MiningLaserContent.ENTITY.get(),pos,state,30);}
    @Override protected String title(){return "container.miningLaser";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new MiningLaserMenu(id,inv,this,worldPosition);}
    @Override public void writeClientSideData(net.minecraft.world.inventory.AbstractContainerMenu menu,RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,MiningLaserEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}

    private void serverTick(ServerLevel server){
        setupPorts(server);
        charge();
        // Source performs this reset once per tick, not for each overdrive cycle.
        if(lastTargetX!=targetX||lastTargetY!=targetY||lastTargetZ!=targetZ)breakProgress=0;
        lastTargetX=targetX;lastTargetY=targetY;lastTargetZ=targetZ;
        redstonePowered=false;
        for(var side:Direction.Plane.HORIZONTAL)if(server.hasNeighborSignal(worldPosition.relative(side))){redstonePowered=true;break;}
        if(isOn&&!redstonePowered){
            int speed=1+level("speed",12),cycles=1+level("overdrive",9),range=1+level("effect",12)*2,fortune=level("fortune",3);
            int consumption=BASE_CONSUMPTION-BASE_CONSUMPTION*level("power",12)/16+BASE_CONSUMPTION*level("speed",12)/16;
            for(int i=0;i<cycles;i++){
                if(power<consumption){beam=false;break;}
                power-=consumption;
                // Legacy y<=0 restarts. Translate the old world floor to this dimension's real minimum.
                if(targetY<=server.getMinBuildHeight())targetY=worldPosition.getY()-2;
                scan(range);
                BlockPos target=target();BlockState state=server.getBlockState(target);
                // Deliberately after scan: the source's stale X/Z liquid handling remains reachable.
                if(state.liquid()){server.removeBlock(target,false);buildDam();continue;}
                if(beam&&canBreak(state,target)){
                    breakProgress+=getBreakSpeed(speed);clientBreakProgress=Math.min(breakProgress,1);
                    if(breakProgress<1)server.destroyBlockProgress(-1,target,(int)Math.floor(breakProgress*10));
                    else{breakBlock(fortune);buildDam();}
                }
            }
        }else{targetY=worldPosition.getY()-2;beam=false;}
        for(Direction side:List.of(Direction.EAST,Direction.WEST,Direction.SOUTH,Direction.NORTH))tryFillContainer(worldPosition.relative(side,2));
        sync(250);
    }
    private void charge(){
        if(power<0){power=0;return;}if(power>MAX_POWER){power=MAX_POWER;return;}
        ItemStack battery=slots.get(0);
        if(battery.getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}
        if(battery.getItem() instanceof HbmBattery item){long taken=Math.min(MAX_POWER-power,Math.min(item.getCharge(battery),item.getDischargeRate(battery)));power+=taken;item.dischargeBattery(battery,taken);}
    }
    public int level(String name,int maximum){int sum=0;for(int i=1;i<=8;i++){
        Item item=slots.get(i).getItem();int tier=0;
        if(item instanceof FurnaceUpgradeItem upgrade&&((name.equals("speed")&&upgrade.type()==FurnaceUpgradeItem.Type.SPEED)||(name.equals("power")&&upgrade.type()==FurnaceUpgradeItem.Type.POWER)))tier=upgrade.tier();
        else if(name.equals("effect")&&item instanceof EffectUpgradeItem upgrade)tier=upgrade.tier();
        else if(name.equals("overdrive")&&item instanceof OverdriveUpgradeItem upgrade)tier=upgrade.tier();
        else if(name.equals("fortune")&&item instanceof FortuneUpgradeItem upgrade)tier=upgrade.tier();
        sum+=tier;
    }return Math.min(maximum,sum);}
    public int range(){return 1+level("effect",12)*2;}
    public int width(){return 1+range()*2;}
    public boolean has(String name){for(int i=1;i<=8;i++)if(BuiltInRegistries.ITEM.getKey(slots.get(i).getItem()).toString().equals("hbm:upgrade_"+name))return true;return false;}
    public BlockPos target(){return new BlockPos(targetX,targetY,targetZ);}
    public boolean isRedstonePowered(){return redstonePowered;}
    public void scan(int range){
        for(int x=-range;x<=range;x++)for(int z=-range;z<=range;z++){
            BlockPos pos=new BlockPos(worldPosition.getX()+x,targetY,worldPosition.getZ()+z);BlockState state=level.getBlockState(pos);
            if(state.liquid())continue;
            if(canBreak(state,pos)){targetX=pos.getX();targetZ=pos.getZ();beam=true;return;}
        }
        beam=false;targetY--;
    }
    private boolean canBreak(BlockState state,BlockPos pos){return !state.isAir()&&!(state.getBlock() instanceof com.hbm.block.gas.GasBlock)&&state.getDestroySpeed(level,pos)>=0&&!state.liquid()&&!state.is(Blocks.BEDROCK);}
    public double getBreakSpeed(int speed){float hardness=level.getBlockState(target()).getDestroySpeed(level,target())*15/speed;return hardness==0?1:1D/hardness;}
    private void buildDam(){for(Direction side:Direction.values()){BlockPos pos=target().relative(side);if(level.getBlockState(pos).liquid())level.setBlockAndUpdate(pos,ExcavatorContent.BARRICADE.get().defaultBlockState());}}

    private void breakBlock(int fortune){
        ServerLevel server=(ServerLevel)level;BlockPos pos=target();BlockState state=server.getBlockState(pos);
        ItemStack original=state.getBlock().getCloneItemStack(server,pos,state);
        boolean normal=true;
        if(!original.isEmpty()){
            // Presence (not a new mutex selector) chooses a single branch. Failure in a stronger branch does not fall through.
            if(has("crystallizer")){
                var result=CrystallizerRecipeConfig.find(server.getRecipeManager(),original,HbmFluids.PEROXIDE);
                if(result==null)result=CrystallizerRecipeConfig.find(server.getRecipeManager(),original,HbmFluids.SULFURIC_ACID);
                if(result!=null){spawn(result.getResultItem(server.registryAccess()).copy());normal=false;}
            }else if(has("centrifuge")){
                var available=server.getRecipeManager().getAllRecipesFor(CentrifugeContent.TYPE.get());var input=new SingleRecipeInput(original);CentrifugeRecipe result=null;
                for(var holder:available)if(holder.value().exactInput()&&holder.value().matches(input,server)){result=holder.value();break;}
                if(result==null)for(var holder:available)if(holder.value().matches(input,server)){result=holder.value();break;}
                if(result!=null)for(ItemStack stack:result.outputs())if(!stack.isEmpty()){spawn(stack.copy());normal=false;}
            }else if(has("shredder")){
                ItemStack result=ShredderRecipes.result(server,original);
                if(!result.isEmpty()&&!BuiltInRegistries.ITEM.getKey(result.getItem()).toString().equals("hbm:scrap")){spawn(result.copy());normal=false;}
            }else if(has("smelter")){
                var result=server.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(original),server);
                if(result.isPresent()){spawn(result.get().value().getResultItem(server.registryAccess()).copy());normal=false;}
            }
        }
        if(normal&&server.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)){ItemStack tool=new ItemStack(Items.DIAMOND_PICKAXE);if(fortune>0)tool.enchant(server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE),fortune);Block.dropResources(state,server,pos,server.getBlockEntity(pos),null,tool);}
        server.destroyBlock(pos,false);
        suckDrops();
        if(has("screm"))server.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("block.screm"),SoundSource.BLOCKS,2000,1);
        breakProgress=0;
    }
    private void spawn(ItemStack stack){if(!stack.isEmpty())level.addFreshEntity(new ItemEntity(level,targetX+.5,targetY+.5,targetZ+.5,stack));}
    private static final Set<String> NULLIFIED=Set.of("minecraft:dirt","minecraft:stone","minecraft:cobblestone","minecraft:sand","minecraft:sandstone","minecraft:gravel","hbm:basalt","hbm:stone_gneiss","minecraft:flint","minecraft:snowball","minecraft:wheat_seeds");
    private void suckDrops(){
        boolean nullifier=has("nullifier");
        for(var item:level.getEntitiesOfClass(ItemEntity.class,new AABB(targetX+.5-3,targetY+.5-1,targetZ+.5-3,targetX+.5+3,targetY+.5+1,targetZ+.5+3))){
            if(!item.isAlive())continue;
            String id=BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString();
            if(nullifier&&NULLIFIED.contains(id)){item.discard();item.setPickUpDelay(60);continue;}
            if(id.equals("hbm:ore_oil")){
                // Original bug/behavior: one500mB award per entire EntityItem, even if its stack contains more than one oil ore.
                tank.setTankType(HbmFluids.OIL);tank.setFill(Math.min(tank.getFill()+500,tank.getMaxFill()));item.discard();item.setPickUpDelay(60);continue;
            }
            ItemStack remainder=insert(this,9,29,item.getItem().copy());
            if(remainder.isEmpty()){item.discard();item.setPickUpDelay(60);}else item.setItem(remainder.copy());
        }
        for(var entity:level.getEntitiesOfClass(LivingEntity.class,new AABB(targetX-.5,targetY-.5,targetZ-.5,targetX+1.5,targetY+1.5,targetZ+1.5)))entity.igniteForSeconds(5);
    }
    /** Source InventoryUtil merges existing components first, then transfers the entire remainder to the first empty slot, without sided validity filtering. */
    private static ItemStack insert(Container inventory,int start,int end,ItemStack stack){
        for(int i=start;i<=end;i++){ItemStack present=inventory.getItem(i);if(ItemStack.isSameItemSameComponents(present,stack)){int count=Math.min(stack.getCount(),present.getMaxStackSize()-present.getCount());if(count>0){present.grow(count);stack.shrink(count);inventory.setChanged();if(stack.isEmpty())return ItemStack.EMPTY;}}}
        for(int i=start;i<=end;i++)if(inventory.getItem(i).isEmpty()){inventory.setItem(i,stack);inventory.setChanged();return ItemStack.EMPTY;}
        return stack;
    }
    private static final Set<String> OUTPUTS=Set.of("minecraft:chest","minecraft:trapped_chest","hbm:crate_iron","hbm:crate_steel","hbm:crate_desh","hbm:safe","minecraft:hopper");
    private void tryFillContainer(BlockPos pos){
        if(!OUTPUTS.contains(BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString())||!(level.getBlockEntity(pos) instanceof Container inventory))return;
        for(int i=9;i<=29;i++)if(!slots.get(i).isEmpty()){int previous=slots.get(i).getCount();ItemStack remainder=insert(inventory,0,inventory.getContainerSize()-1,slots.get(i));slots.set(i,remainder);if(remainder.isEmpty()||remainder.getCount()<previous)return;}
    }

    private void setupPorts(ServerLevel server){
        if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(worldPosition.above()).setConnections(new NodeConnection(worldPosition.above(2),Direction.UP));HbmEnergy.nodespace(server).createNode(powerNode);}
        if(powerNode.hasValidNet())powerNode.addReceiver(this);
        if(fluidPorts==null){BlockPos[] positions={worldPosition.north(),worldPosition.east(),worldPosition.south(),worldPosition.west()};NodeConnection[] edges={new NodeConnection(worldPosition.north(2),Direction.NORTH),new NodeConnection(worldPosition.east(2),Direction.EAST),new NodeConnection(worldPosition.south(2),Direction.SOUTH),new NodeConnection(worldPosition.west(2),Direction.WEST)};fluidPorts=new FluidPorts(positions,edges);}
        fluidPorts.update(server,this);
    }
    private void disconnect(){if(level instanceof ServerLevel server){if(powerNode!=null)HbmEnergy.nodespace(server).destroyNode(powerNode);if(fluidPorts!=null)fluidPorts.remove(server);}powerNode=null;fluidPorts=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return level!=null&&!isRemoved()&&level.hasChunkAt(worldPosition);}
    @Override public boolean canConnect(Direction side){return side==Direction.UP;}
    @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null&&side.getAxis()!=Direction.Axis.Y;}
    @Override public long getPower(){return power;}
    @Override public long getMaxPower(){return MAX_POWER;}
    @Override public void setPower(long value){power=value;setChanged();}
    @Override public long energyStored(){return power;}
    @Override public long energyCapacity(){return MAX_POWER;}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}
    @Override public FluidTank[] getSendingTanks(){return getAllTanks();}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);setChanged();}
    @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
    @Override public List<ProgressView> progressBars(){return beam?List.of(new ProgressView("gui.hbm.mining_laser.progress",breakProgress,1,0xA00000)):List.of();}
    @Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.rangeClosed(9,29).toArray();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=9&&slot<=29;}
    @Override public void setItem(int slot,ItemStack stack){super.setItem(slot,stack);if(!stack.isEmpty()&&slot>=1&&slot<=8&&level instanceof ServerLevel server&&com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack))server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1,1);}

    @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",power);tag.putBoolean("isOn",isOn);tag.putBoolean("tilted",tilted);tank.writeToNBT(tag,"oil");}
    @Override protected void loadMachine(CompoundTag tag){power=tag.getLong("power");isOn=tag.getBoolean("isOn");tilted=tag.getBoolean("tilted");redstonePowered=false;tank.readFromNBT(tag,"oil");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("tx",targetX);tag.putInt("ty",targetY);tag.putInt("tz",targetZ);tag.putInt("lx",lastTargetX);tag.putInt("ly",lastTargetY);tag.putInt("lz",lastTargetZ);tag.putBoolean("beam",beam);tag.putDouble("progress",clientBreakProgress);tag.putBoolean("redstone",redstonePowered);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);targetX=tag.getInt("tx");targetY=tag.getInt("ty");targetZ=tag.getInt("tz");lastTargetX=tag.getInt("lx");lastTargetY=tag.getInt("ly");lastTargetZ=tag.getInt("lz");beam=tag.getBoolean("beam");breakProgress=tag.getDouble("progress");redstonePowered=tag.getBoolean("redstone");}
}
