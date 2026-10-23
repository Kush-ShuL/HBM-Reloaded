package com.hbm.machine.reactor.rbmk.irradiation;
import com.hbm.api.computer.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.console.RbmkConsoleExtra;
import com.hbm.machine.reactor.rbmk.core.*;
import com.hbm.machine.reactor.rbmk.fluid.RbmkAdaptivePorts;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import com.hbm.multiblock.NamedMultiblock;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Real two-slot neutron irradiation, strict>10000 threshold and native64000gas output. */
public final class RbmkOutgasserEntity extends RbmkColumnEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,StandardFluidSender,RbmkFluxColumn.Outgasser,ComputerControl,RbmkConsoleExtra {
    public static final int DURATION=10000;public double progress;public final FluidTank gas=new FluidTank(HbmFluids.TRITIUM,64000);private final NonNullList<ItemStack> items=NonNullList.withSize(2,ItemStack.EMPTY);private final RbmkAdaptivePorts ports;private Component customName;
    public RbmkOutgasserEntity(BlockPos pos,BlockState state){super(RbmkOutgasserContent.ENTITY.get(),pos,state);ports=new RbmkAdaptivePorts(pos);}
    @Override public RbmkFluxColumn.Type neutronType(){return RbmkFluxColumn.Type.OUTGASSER;}@Override public String consoleType(){return "OUTGASSER";}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkOutgasserEntity machine){if(level instanceof ServerLevel world){machine.ports.update(world,machine);if(!machine.canProcess())machine.progress=0;machine.baseTick(world);}}
    private RbmkIrradiationRecipe recipe(){return level==null?null:RbmkIrradiationRecipes.find(level.getRecipeManager(),items.getFirst());}
    @Override public boolean canProcess(){if(items.getFirst().isEmpty())return false;var recipe=recipe();if(recipe==null||recipe.fusionOnly())return false;if(recipe.gas().isPresent()){var fluid=recipe.gas().get();if(gas.getTankType()!=fluid.type&&gas.getFill()>0)return false;gas.setTankType(fluid.type);if(gas.getFill()+fluid.fill>gas.getMaxFill())return false;}var out=recipe.solid();var current=items.get(1);if(current.isEmpty()||out.isEmpty())return true;return current.getItem()==out.getItem()&&metadata(current)==metadata(out)&&current.getCount()+out.getCount()<=current.getMaxStackSize();}
    private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    @Override public void receiveFlux(RbmkNeutronField.Stream stream){if(canProcess()){double efficiency=Math.min(1-stream.fluxRatio*.8,1);progress+=stream.fluxQuantity*efficiency*RbmkDials.getOutgasserMod(level);if(progress>DURATION){process();setChanged();}}}
    private void process(){var recipe=recipe();removeItem(0,1);progress=0;recipe.gas().ifPresent(fluid->gas.setFill(gas.getFill()+fluid.fill));var out=recipe.solid();if(!out.isEmpty()){if(items.get(1).isEmpty())items.set(1,out.copy());else items.get(1).grow(out.getCount());}}
    @Override public void onMelt(int reduce){int count=4+level.random.nextInt(2);for(int i=0;i<count;i++)spawnDebris(RbmkDebrisEntity.Type.BLANK);super.onMelt(reduce);}
    private void disconnect(){if(level instanceof ServerLevel world)ports.remove(world);}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(HbmFluidType type,Direction side){return level!=null&&side==Direction.DOWN&&ports.loaderMode(level)!=1&&type==gas.getTankType();}@Override public FluidTank[] getAllTanks(){return new FluidTank[]{gas};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{gas};}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override protected void saveState(CompoundTag tag){super.saveState(tag);tag.putDouble("progress",progress);gas.writeToNBT(tag,"gas");}@Override protected void loadState(CompoundTag tag){super.loadState(tag);progress=tag.getDouble("progress");gas.readFromNBT(tag,"gas");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,items,lookup);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,items,lookup);if(tag.contains("name"))customName=Component.Serializer.fromJson(tag.getString("name"),lookup);}
    @Override public CompoundTag consoleData(){var tag=new CompoundTag();tag.putInt("gas",gas.getFill());tag.putInt("maxGas",gas.getMaxFill());tag.putShort("type",(short)gas.getTankType().getID());tag.putDouble("progress",progress);return tag;}
    public static String legacyName(ItemStack stack){String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();return switch(id){case "minecraft:brown_mushroom","minecraft:red_mushroom"->"tile.mushroom";case "minecraft:mushroom_stew"->"item.mushroomStew";default->stack.getItem().getDescriptionId(stack).replace("item.hbm.","item.").replace("block.hbm.","tile.");};}
    @Override public void consoleComputerData(Map<String,Object> data){var input=items.getFirst();data.put("fluxProgress",progress);data.put("requiredFlux",DURATION);data.put("craftingName",input.isEmpty()?"":legacyName(input));data.put("craftingNumber",input.getCount());}
    @Override public String computerType(){return "rbmk_outgasser";}@Override public String[] computerMethods(){return new String[]{"getGas","getGasMax","getGasType","getProgress","getCrafting","getCoordinates","getInfo"};}@Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{var input=items.getFirst();String name=input.isEmpty()?"":legacyName(input);return switch(method){case "getGas"->new Object[]{gas.getFill()};case "getGasMax"->new Object[]{gas.getMaxFill()};case "getGasType"->new Object[]{gas.getTankType().getName()};case "getProgress"->new Object[]{progress};case "getCrafting"->new Object[]{name,input.getCount()};case "getCoordinates"->new Object[]{worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()};case "getInfo"->new Object[]{gas.getFill(),gas.getMaxFill(),progress,gas.getTankType().getID(),worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),name,input.getCount()};default->throw new ComputerException("No such method: "+method);};}
    @Override public int getContainerSize(){return 2;}@Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return items.get(slot);}@Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(items,slot,amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}@Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){items.clear();setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=128;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&level!=null&&RbmkIrradiationRecipes.find(level.getRecipeManager(),stack)!=null;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.rbmkOutgasser"):customName;}@Override public void setCustomName(Component name){customName=name;setChanged();}@Override public AbstractContainerMenu createMenu(int id,Inventory player,Player entity){return new RbmkOutgasserMenu(id,player,this,worldPosition);}
    @Override public List<TankView> tanks(){return List.of(new TankView(gas.getTankType(),gas.getFill(),gas.getMaxFill()));}@Override public List<ProgressView> progressBars(){var bars=new ArrayList<>(super.progressBars());bars.add(new ProgressView("jade.hbm.rbmk.irradiation.flux",progress,DURATION,0x80bc62));return bars;}
}
