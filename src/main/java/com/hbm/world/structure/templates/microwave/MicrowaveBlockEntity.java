package com.hbm.world.structure.templates.microwave;
import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original TileEntityMicrowave update order, actual smelting/food gate and entity-only self-destruction. */
public final class MicrowaveBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer,EnergyReceiver,MachineView,ComputerControl,ICopiable, com.hbm.multiblock.MuffledMachine {
    public static final long MAX_POWER=50000;public static final int CONSUMPTION=50,MAX_TIME=300,MAX_SPEED=5;
    private NonNullList<ItemStack> slots=NonNullList.withSize(3,ItemStack.EMPTY);private BigInteger power=BigInteger.ZERO;private PowerNode node;private String sourceName="";
    public int time,speed;public boolean muffled,tilted;private int lastTime,lastSpeed;private BigInteger lastPower;private boolean lastMuffled,lastTilted,packed;
    public MicrowaveBlockEntity(BlockPos pos,BlockState state){super(MicrowaveContent.ENTITY.get(),pos,state);}
    public void tickServer(){if(!(level instanceof ServerLevel server))return;ensureNode(server);var before=power;int oldTime=time;chargeFromItems();
        if(canProcess()){
            if(speed>=MAX_SPEED){server.destroyBlock(worldPosition,false);new ExplosionVNT(server,worldPosition.getX()+.5D,worldPosition.getY()+.5D,worldPosition.getZ()+.5D,5F).setEntityProcessor(new EntityProcessorCrossSmooth(1,50)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1F)).explode();return;}
            if(time>=MAX_TIME){process();time=0;}
            if(canProcess()){power=power.subtract(BigInteger.valueOf(CONSUMPTION));time+=speed*2;}
        }
        if(!before.equals(power)||oldTime!=time)setChanged();sync(server);
    }
    private void chargeFromItems(){var stack=slots.get(2);if(stack.getItem() instanceof CreativeBatteryItem){power=BigInteger.valueOf(MAX_POWER);return;}if(stack.getItem() instanceof HbmBattery battery){long amount=BigInteger.valueOf(MAX_POWER).subtract(power).min(BigInteger.valueOf(battery.getDischargeRate(stack))).min(BigInteger.valueOf(battery.getCharge(stack))).longValueExact();battery.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
    private ItemStack result(ItemStack input){if(level==null||input.isEmpty())return ItemStack.EMPTY;var recipe=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(input),level);return recipe.isEmpty()?ItemStack.EMPTY:recipe.get().value().assemble(new SingleRecipeInput(input),level.registryAccess());}
    private static boolean sameLegacyItem(ItemStack a,ItemStack b){return a.getItem()==b.getItem()&&a.getOrDefault(HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).equals(b.getOrDefault(HbmHazards.LEGACY_METADATA.get(),b.getDamageValue()));}
    public boolean canProcess(){if(speed==0||power.compareTo(BigInteger.valueOf(CONSUMPTION))<0)return false;var input=slots.get(0);var result=result(input);if(result.isEmpty()||!input.has(DataComponents.FOOD)&&!result.has(DataComponents.FOOD))return false;var output=slots.get(1);return output.isEmpty()||sameLegacyItem(result,output)&&result.getCount()+output.getCount()<=result.getMaxStackSize();}
    private void process(){var result=result(slots.get(0)).copy();if(slots.get(1).isEmpty())slots.set(1,result);else slots.get(1).grow(result.getCount());removeItem(0,1);setChanged();}
    public void button(int value){if(value==0)speed++;if(value==1)speed--;speed=Mth.clamp(speed,0,MAX_SPEED);setChanged();}
    private void ensureNode(ServerLevel server){if(node==null||node.isExpired()){node=new PowerNode(worldPosition);node.addReceiver(this);HbmEnergy.nodespace(server).createNode(node);}}
    private void disconnect(){if(node!=null&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void onLoad(){super.onLoad();if(level instanceof ServerLevel server)ensureNode(server);}
    @Override public void setRemoved(){disconnect();super.setRemoved();}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long getPower(){return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public BigInteger getPowerExact(){return power;}
    @Override public long getMaxPower(){return MAX_POWER;}
    @Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(MAX_POWER);}
    @Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}
    @Override public void setPowerExact(BigInteger value){power=value;setChanged();}
    @Override public long energyStored(){return getPower();}
    @Override public long energyCapacity(){return MAX_POWER;}
    @Override public List<ProgressView> progressBars(){return time>0?List.of(new ProgressView("jade.hbm.processing",time,MAX_TIME,0x71c78b)):List.of();}
    @Override public int getContainerSize(){return 3;}
    @Override public int getMaxStackSize(){return 64;}
    @Override protected NonNullList<ItemStack> getItems(){return slots;}
    @Override protected void setItems(NonNullList<ItemStack> items){slots=items;}
    @Override public ItemStack removeItem(int slot,int amount){var removed=ContainerHelper.removeItem(slots,slot,amount);if(!removed.isEmpty())setChanged();return removed;}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&!result(stack).isEmpty();}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public int[] getSlotsForFace(Direction face){return face==Direction.DOWN?new int[]{1}:new int[]{0};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return slot==1;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5D,worldPosition.getY()+.5D,worldPosition.getZ()+.5D)<=144D;}
    @Override public Component getCustomName(){return sourceName.isEmpty()?super.getCustomName():Component.literal(sourceName);}
    @Override public Component getName(){var custom=getCustomName();return custom!=null?custom:getDefaultName();}
    @Override protected Component getDefaultName(){return Component.translatable("container.microwave");}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new MicrowaveMenu(id,inventory,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);if(power.bitLength()<64)tag.putLong("power",power.longValue());else tag.putString("power",power.toString());tag.putInt("speed",speed);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(hasCustomName())tag.putString("name",getCustomName().getString());/* Source deliberately does not persist time. */}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("Items",Tag.TAG_LIST)){slots=NonNullList.withSize(3,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,slots,lookup);}power=tag.contains("power",Tag.TAG_STRING)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));speed=tag.getInt("speed");if(tag.contains("time"))time=tag.getInt("time");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(tag.contains("name",Tag.TAG_STRING))sourceName=tag.getString("name");}
    private CompoundTag stateTag(){var tag=new CompoundTag();tag.putString("power",power.toString());tag.putInt("time",time);tag.putInt("speed",speed);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(hasCustomName())tag.putString("name",getCustomName().getString());return tag;}
    private void sync(ServerLevel server){if(packed&&time==lastTime&&speed==lastSpeed&&power.equals(lastPower)&&muffled==lastMuffled&&tilted==lastTilted&&server.getGameTime()%20!=0)return;packed=true;lastTime=time;lastSpeed=speed;lastPower=power;lastMuffled=muffled;lastTilted=tilted;var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500D)player.connection.send(packet);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return stateTag();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public String computerType(){return "microwave";}
    @Override public String[] computerMethods(){return new String[]{"test","variableget","variableset"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "test"->new Object[]{"This is a testing device for everything CC:Tweaked."};case "variableget"->new Object[]{speed,"test of the `getter` callback function"};case "variableset"->{speed=Mth.clamp(args.checkInteger(0),0,5);setChanged();yield new Object[]{"test of the `setter` callback function"};}default->throw new ComputerException("Unknown method");};}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putInt("microSpeed",speed);return null;/* Actual source ICopiable returns null after populating its temporary compound. */}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){if(tag.contains("microSpeed")){speed=tag.getInt("microSpeed");setChanged();}}
    @Override public String[] infoForDisplay(Level level,BlockPos pos){return new String[]{"copyTool.speed"};}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
