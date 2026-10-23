package com.hbm.machine.electricpress;

import com.hbm.conveyor.MovingItem;
import com.hbm.item.StampItem;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Actual source moving-item batch press: one stamp wear per strike,not per entity,with source no-GUI interaction. */
public final class ConveyorPressEntity extends PoweredPressEntity {
    public int usage=100;
    public double speed=.125,press,renderPress,lastPress;
    private double syncPress;
    private int turnProgress,delay;
    private boolean retracting;
    public ItemStack renderedStamp=ItemStack.EMPTY;
    public ConveyorPressEntity(BlockPos pos,BlockState state){super(ElectricPressContent.CONVEYOR_ENTITY.get(),pos,state,1);}
    @Override protected String sourceName(){return "";}
    @Override protected void serverTick(ServerLevel server){if(delay<=0){if(retracting){if(power.compareTo(BigInteger.valueOf(usage))>=0){press-=speed;power=power.subtract(BigInteger.valueOf(usage));if(press<=0){press=0;retracting=false;delay=0;}setChanged();}}else if(canExtend(server)){press+=speed;power=power.subtract(BigInteger.valueOf(usage));if(press>=1){press=1;retracting=true;delay=5;process(server);}setChanged();}}else delay--;}
    private List<MovingItem> items(ServerLevel server){var p=worldPosition;return server.getEntitiesOfClass(MovingItem.class,new AABB(p.getX(),p.getY()+1,p.getZ(),p.getX()+1,p.getY()+1.5,p.getZ()+1));}
    private boolean canExtend(ServerLevel server){if(power.compareTo(BigInteger.valueOf(usage))<0||slots.get(0).isEmpty())return false;for(var item:items(server)){var stack=item.getItemStack();if(!ElectricPressRecipes.output(server,stack,slots.get(0)).isEmpty()&&stack.getCount()==1){if(item.getX()>worldPosition.getX()+.35&&item.getX()<worldPosition.getX()+.65&&item.getZ()>worldPosition.getZ()+.35&&item.getZ()<worldPosition.getZ()+.65)item.setPos(worldPosition.getX()+.5,item.getY(),worldPosition.getZ()+.5);return true;}}return false;}
    private void process(ServerLevel server){for(var item:items(server)){var stack=item.getItemStack();var output=ElectricPressRecipes.output(server,stack,slots.get(0));if(!output.isEmpty()&&stack.getCount()==1){item.discard();var out=new MovingItem(server);out.setPos(item.getX(),item.getY(),item.getZ());out.setItemStack(output.copy());server.addFreshEntity(out);}}server.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("block.pressOperate"),SoundSource.BLOCKS,muffled?.15F:1.5F,1);ElectricPressRecipes.wearStamp(slots.get(0));if(slots.get(0).isEmpty())slots.set(0,ItemStack.EMPTY);}
    @Override protected void clientTick(){lastPress=renderPress;if(turnProgress>0){renderPress+=(syncPress-renderPress)/(double)turnProgress;turnProgress--;}else renderPress=syncPress;}
    public double renderedPress(float partial){return lastPress+(renderPress-lastPress)*partial;}
    @Override protected void savePress(CompoundTag tag){tag.putDouble("press",press);}
    @Override protected void loadPress(CompoundTag tag){press=tag.getDouble("press");}
    @Override protected void writeSync(CompoundTag tag,HolderLookup.Provider lookup){tag.putDouble("press",press);if(!slots.get(0).isEmpty())tag.put("stamp",slots.get(0).save(lookup));}
    @Override protected void readSync(CompoundTag tag,HolderLookup.Provider lookup){syncPress=tag.getDouble("press");press=syncPress;renderedStamp=tag.contains("stamp")?ItemStack.parseOptional(lookup,tag.getCompound("stamp")):ItemStack.EMPTY;turnProgress=2;}
    @Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.DOWN;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return stack.getItem() instanceof StampItem;}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{0};}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
    @Override public List<ProgressView> progressBars(){return press>0||retracting?List.of(new ProgressView("gui.hbm.electricpress.progress",press,1,0x71c78b)):List.of();}
}
