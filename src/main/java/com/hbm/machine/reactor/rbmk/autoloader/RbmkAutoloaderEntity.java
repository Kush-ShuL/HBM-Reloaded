package com.hbm.machine.reactor.rbmk.autoloader;
import com.hbm.api.machine.*;
import com.hbm.machine.reactor.rbmk.channel.RbmkFuelChannelEntity;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.machine.reactor.rbmk.fuel.RbmkFuelRodItem;
import com.hbm.multiblock.NamedMultiblock;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Actual18-slot automatic refueling cycle, .005 piston and40-tick pauses; no source HE/OC fabricated. */
public final class RbmkAutoloaderEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock,MachineView,ICopiable, com.hbm.multiblock.MuffledMachine {
    public static Consumer<RbmkAutoloaderEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};public static final double SPEED=.005;
    private final NonNullList<ItemStack> items=NonNullList.withSize(18,ItemStack.EMPTY);public double piston,renderPiston,lastPiston,syncPiston;public int turnProgress,cycle=50;private boolean retracting=true;private int delay;public boolean muffled;private Component customName;
    public RbmkAutoloaderEntity(BlockPos pos,BlockState state){super(RbmkAutoloaderContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,RbmkAutoloaderEntity machine){if(level instanceof ServerLevel world)machine.serverTick(world);else CLIENT_TICK.accept(machine);}
    private RbmkFuelChannelEntity channel(){if(level==null)return null;var pos=worldPosition.below();if(level.getBlockState(pos).getBlock() instanceof RbmkColumnBlock block&&block.core(level,pos) instanceof RbmkFuelChannelEntity rod)return rod;return null;}
    private void serverTick(ServerLevel world){if(delay>0)delay--;if(delay<=0&&retracting&&piston>0){piston-=SPEED;if(piston<=0){piston=0;delay=40;}}
        if(retracting&&world.getGameTime()%20==0&&hasFuel()&&hasSpace()){var rod=channel();if(rod!=null&&rod.coldEnoughForAutoloader()){var stack=rod.getItem(0);if(stack.isEmpty()||stack.getItem() instanceof RbmkFuelRodItem&&RbmkFuelRodItem.getEnrichment(stack)*100<cycle)retracting=false;}}
        if(delay<=0&&!retracting&&piston<1){piston+=SPEED;if(piston>=1){piston=1;delay=40;}}
        if(!retracting&&piston>=1){piston=1;var rod=channel();if(rod!=null){if(!rod.getItem(0).isEmpty()&&hasSpace())for(int i=9;i<18;i++)if(items.get(i).isEmpty()){items.set(i,rod.getItem(0).copy());rod.unload();break;}if(rod.getItem(0).isEmpty())for(int i=0;i<9;i++){var stack=items.get(i);if(eligible(stack)){rod.load(stack);items.set(i,ItemStack.EMPTY);break;}}retracting=true;delay=40;}}
        setChanged();var packet=ClientboundBlockEntityDataPacket.create(this);for(var player:world.players())if(player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<10000)player.connection.send(packet);
    }
    private boolean eligible(ItemStack stack){return stack.getItem() instanceof RbmkFuelRodItem&&RbmkFuelRodItem.getEnrichment(stack)*100>=cycle;}
    public boolean hasFuel(){for(int i=0;i<9;i++)if(eligible(items.get(i)))return true;return false;}public boolean hasSpace(){for(int i=9;i<18;i++)if(items.get(i).isEmpty())return true;return false;}
    public void control(boolean plus){if(plus&&cycle<95)cycle+=5;if(!plus&&cycle>5)cycle-=5;cycle=net.minecraft.util.Mth.clamp(cycle,5,95);setChanged();}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putInt("cycle",cycle);return tag;}@Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){if(tag.contains("cycle")){cycle=net.minecraft.util.Mth.clamp(tag.getInt("cycle"),5,95);setChanged();}}
    public float volume(float base){return muffled?base*.1F:base;}
    @Override public void setRemoved(){if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,items,lookup);tag.putDouble("piston",piston);tag.putBoolean("ret",retracting);tag.putInt("delay",delay);tag.putInt("cycle",cycle);tag.putBoolean("muffled",muffled);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,items,lookup);piston=tag.getDouble("piston");retracting=tag.getBoolean("ret");delay=tag.getInt("delay");cycle=tag.getInt("cycle");muffled=tag.getBoolean("muffled");if(tag.contains("CustomName"))customName=Component.Serializer.fromJson(tag.getString("CustomName"),lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putDouble("piston",piston);tag.putInt("cycle",cycle);tag.putBoolean("muffled",muffled);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){syncPiston=tag.getDouble("piston");cycle=tag.getInt("cycle");muffled=tag.getBoolean("muffled");turnProgress=2;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public int getContainerSize(){return 18;}@Override public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return items.get(slot);}@Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(items,slot,amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}@Override public void setItem(int slot,ItemStack stack){items.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){items.clear();setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<9&&eligible(stack);}@Override public int[] getSlotsForFace(Direction side){return piston<=0?java.util.stream.IntStream.range(0,18).toArray():new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=9;}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.rbmkAutoloader"):customName;}@Override public void setCustomName(Component name){customName=name;setChanged();}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RbmkAutoloaderMenu(id,inventory,this,worldPosition);}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.rbmk.autoloader.piston",level!=null&&level.isClientSide?renderPiston:piston,1,0x90c899),new ProgressView("jade.hbm.rbmk.autoloader.cycle",cycle,100,0x54c254));}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
