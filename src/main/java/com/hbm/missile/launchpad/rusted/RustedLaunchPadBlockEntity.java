package com.hbm.missile.launchpad.rusted;

import api.hbm.item.IDesignatorItem;
import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.entity.missile.EntityMissileTier4.EntityMissileDoomsdayRusted;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.missile.rusted.RustedMissileContent;
import com.hbm.multiblock.*;
import com.hbm.registry.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Full original four-slot one-shot rusted pad. It consumes launch codes, never the key/designator. */
public final class RustedLaunchPadBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,NamedMultiblock {
    private final NonNullList<ItemStack> slots=NonNullList.withSize(4,ItemStack.EMPTY);
    public int prevRedstonePower,redstonePower;
    public final Set<BlockPos> activatedBlocks=new HashSet<>(4);
    public boolean missileLoaded;
    private Component customName;
    public static Consumer<Smoke> smoke=ignored->{};
    public record Smoke(Level level,double x,double y,double z,double mx,double my,double mz){}
    public RustedLaunchPadBlockEntity(BlockPos pos,BlockState state){super(RustedLaunchPadContent.ENTITY.get(),pos,state);}
    @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    @Override public void setCustomName(Component name){customName=name;setChanged();}
    @Override public Component getDisplayName(){return customName==null?Component.translatable("container.launchPadRusted"):customName;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RustedLaunchPadMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,RustedLaunchPadBlockEntity pad){
        if(level instanceof ServerLevel server){if(pad.redstonePower>0&&pad.prevRedstonePower<=0)pad.launch();pad.prevRedstonePower=pad.redstonePower;var packet=pad.getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<250D*250D)player.connection.send(packet);dispatchScope.commit();}}
        else if(!level.getEntitiesOfClass(EntityMissileBaseNT.class,new AABB(pos.getX()-.5,pos.getY(),pos.getZ()-.5,pos.getX()+1.5,pos.getY()+10,pos.getZ()+1.5)).isEmpty()){
            for(int i=0;i<15;i++){Direction direction=DummyableBlock.facing(state);if(level.random.nextBoolean())direction=direction.getOpposite();if(level.random.nextBoolean())direction=direction.getCounterClockWise();float mx=(float)(level.random.nextGaussian()*.15F+.75)*direction.getStepX(),mz=(float)(level.random.nextGaussian()*.15F+.75)*direction.getStepZ();smoke.accept(new Smoke(level,pos.getX()+.5,pos.getY()+.25,pos.getZ()+.5,mx,0,mz));}
        }
    }
    public BombReturnCode launch(){
        ItemStack codes=slots.get(1),key=slots.get(2),target=slots.get(3);
        if(missileLoaded&&!codes.isEmpty()&&!key.isEmpty()&&!target.isEmpty()&&codes.is(HbmContent.item("launch_code"))&&key.is(HbmContent.item("launch_key"))&&target.getItem() instanceof IDesignatorItem designator){
            int x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();if(!designator.isReady(level,target,x,y,z))return BombReturnCode.ERROR_MISSING_COMPONENT;Vec3 coordinates=designator.getCoords(level,target,x,y,z);
            var missile=new EntityMissileDoomsdayRusted(level,x+.5F,y+1F,z+.5F,Mth.floor(coordinates.x),Mth.floor(coordinates.z));missile.trackingRange=500;level.addFreshEntity(missile);level.playSound(null,x+.5,y,z+.5,HbmSounds.get("weapon.missileTakeOff"),SoundSource.BLOCKS,2F,1F);missileLoaded=false;removeItem(1,1);setChanged();return BombReturnCode.LAUNCHED;
        }return BombReturnCode.ERROR_MISSING_COMPONENT;
    }
    public void releaseMissile(){if(missileLoaded&&slots.getFirst().isEmpty()){missileLoaded=false;slots.set(0,new ItemStack(RustedMissileContent.ITEM.get()));setChanged();}}
    public void setMissileLoaded(boolean loaded){missileLoaded=loaded;setChanged();}
    public void updateRedstonePower(BlockPos part){boolean powered=level.hasNeighborSignal(part),contained=activatedBlocks.contains(part);if(!contained&&powered){activatedBlocks.add(part.immutable());if(redstonePower==-1)redstonePower=0;redstonePower++;setChanged();}else if(contained&&!powered){activatedBlocks.remove(part);redstonePower--;if(redstonePower==0)redstonePower=-1;setChanged();}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);tag.putBoolean("missileLoaded",missileLoaded);tag.putInt("redstonePower",redstonePower);tag.putInt("prevRedstonePower",prevRedstonePower);var blocks=new CompoundTag();int i=0;for(BlockPos pos:activatedBlocks){blocks.putInt("x"+i,pos.getX());blocks.putInt("y"+i,pos.getY());blocks.putInt("z"+i,pos.getZ());i++;}tag.put("activatedBlocks",blocks);if(customName!=null)tag.putString("CustomName",Component.Serializer.toJson(customName,lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){if(tag.getBoolean("visualOnly")){missileLoaded=tag.getBoolean("missileLoaded");return;}super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,slots,lookup);missileLoaded=tag.getBoolean("missileLoaded");redstonePower=tag.getInt("redstonePower");prevRedstonePower=tag.getInt("prevRedstonePower");var blocks=tag.getCompound("activatedBlocks");activatedBlocks.clear();for(int i=0;i<blocks.getAllKeys().size()/3;i++)activatedBlocks.add(new BlockPos(blocks.getInt("x"+i),blocks.getInt("y"+i),blocks.getInt("z"+i)));customName=tag.contains("CustomName")?Component.Serializer.fromJson(tag.getString("CustomName"),lookup):tag.contains("name")&&!tag.getString("name").isEmpty()?Component.literal(tag.getString("name")):null;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("visualOnly",true);tag.putBoolean("missileLoaded",missileLoaded);return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public int getContainerSize(){return 4;}@Override public int getMaxStackSize(){return 64;}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int amount){var stack=ContainerHelper.removeItem(slots,slot,amount);if(!stack.isEmpty())setChanged();return stack;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){slots.clear();setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
}
