package com.hbm.machine.woodprocessing;
import com.hbm.api.machine.MachineView;
import com.hbm.heat.HeatSource;
import com.hbm.multiblock.*;
import com.hbm.registry.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Source three-slot heat-driven sawmill; original output overwrite, all-input consumption and dust RNG are retained. */
public final class SawmillEntity extends BlockEntity implements WorldlyContainer,MuffledMachine,NamedMultiblock,MachineView {
 public static final int PROCESSING_TIME=600;
 public final NonNullList<ItemStack> slots=NonNullList.withSize(3,ItemStack.EMPTY);
 public int heat,displayHeat,progress;private int warnCooldown,overspeed;private CompoundTag lastSnapshot;public boolean hasBlade=true,muffled,tilted;public float spin,lastSpin;private net.minecraft.network.chat.Component customName;
 public SawmillEntity(BlockPos pos,BlockState state){super(WoodProcessingContent.SAWMILL_ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 public static void tick(Level level,BlockPos pos,BlockState state,SawmillEntity saw){if(level instanceof ServerLevel server)saw.serverTick(server);else{ saw.lastSpin=saw.spin;saw.spin+=saw.heat*25F/300F;if(saw.spin>=360){saw.spin-=360;saw.lastSpin-=360;}}}
 private void serverTick(ServerLevel server){
  if(hasBlade){pullHeat();if(warnCooldown>0)warnCooldown--;
   if(heat>=100){var result=getOutput(slots.get(0));if(!result.isEmpty()){
    progress+=heat/10;if(progress>=PROCESSING_TIME){progress=0;slots.set(0,ItemStack.EMPTY);slots.set(1,result);if(!result.is(WoodProcessingContent.item("powder_sawdust"))&&server.random.nextFloat()<(result.is(Items.STICK)?.1F:.5F))slots.set(2,new ItemStack(WoodProcessingContent.item("powder_sawdust")));setChanged();}
   }else progress=0;
    var box=SawmillBlock.rotateBox(new AABB(-1,.375,-1,-.875,2.375,1),DummyableBlock.facing(getBlockState())).move(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5);WoodEffects.cut(server,box);
   }else progress=0;
   if(heat>300){overspeed++;if(overspeed>60&&warnCooldown==0){warnCooldown=100;server.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5,HbmSounds.get("block.warnOverspeed"),SoundSource.BLOCKS,2,1);}if(overspeed>300){hasBlade=false;server.explode(null,worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5,5,false,Level.ExplosionInteraction.NONE);var dir=DummyableBlock.facing(getBlockState());var rot=dir.getCounterClockWise();var blade=new SawbladeEntity(server,worldPosition.getX()+.5+dir.getStepX(),worldPosition.getY()+1,worldPosition.getZ()+.5+dir.getStepZ()).orientation(dir.get3DDataValue());blade.setDeltaMovement(rot.getStepX(),1+(heat-100)*.0001D,rot.getStepZ());server.addFreshEntity(blade);setChanged();}}else overspeed=0;
  }else{overspeed=0;warnCooldown=0;}
  displayHeat=heat;var packet=getUpdatePacket();var snapshot=packet.getTag();if(!snapshot.equals(lastSnapshot)||server.getGameTime()%20==0){lastSnapshot=snapshot.copy();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);}heat=0;
 }
 private void pullHeat(){if(level.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int amount=(int)(source.getHeatStored()*.1D);if(amount>0){source.useUpHeat(amount);heat+=amount;return;}}heat=Math.max(heat-Math.max(heat/1000,1),0);}
 public ItemStack getOutput(ItemStack input){if(input.isEmpty()||level==null)return ItemStack.EMPTY;var names=HbmOreDictionary.names(input);if(names.contains("stickWood"))return new ItemStack(WoodProcessingContent.item("powder_sawdust"));if(names.contains("logWood")){var crafting=CraftingInput.of(1,1,List.of(input));for(var recipe:level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING))if(recipe.value().matches(crafting,level)){var output=recipe.value().assemble(crafting,level.registryAccess());if(!output.isEmpty()){output=output.copy();output.setCount(output.getCount()*6/4);return output;}}}if(names.contains("plankWood"))return new ItemStack(Items.STICK,6);else if(names.contains("treeSapling"))return new ItemStack(Items.STICK);return ItemStack.EMPTY;}
 @Override public int getContainerSize(){return 3;}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots.get(slot);}@Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(slots,slot,amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}@Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);stack.limitSize(64);setChanged();}@Override public void clearContent(){slots.clear();setChanged();}@Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getCenter())<=144;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0&&isEmpty()&&stack.getCount()==1&&!getOutput(stack).isEmpty();}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>0;}
 private void saveItems(CompoundTag tag,HolderLookup.Provider lookup){var list=new ListTag();for(int i=0;i<3;i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);}
 private void loadItems(CompoundTag tag,HolderLookup.Provider lookup){slots.clear();for(var entry:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)entry;int slot=item.getByte("slot");if(slot>=0&&slot<3)slots.set(slot,ItemStack.parseOptional(lookup,item));}}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);saveItems(tag,lookup);tag.putBoolean("hasBlade",hasBlade);tag.putInt("progress",progress);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null)tag.putString("name",net.minecraft.network.chat.Component.Serializer.toJson(customName,lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadItems(tag,lookup);hasBlade=tag.getBoolean("hasBlade");progress=tag.getInt("progress");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(tag.contains("name"))customName=net.minecraft.network.chat.Component.Serializer.fromJson(tag.getString("name"),lookup);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveItems(tag,lookup);tag.putBoolean("hasBlade",hasBlade);tag.putInt("progress",progress);tag.putInt("heat",displayHeat);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadItems(tag,lookup);hasBlade=tag.getBoolean("hasBlade");progress=tag.getInt("progress");heat=displayHeat=tag.getInt("heat");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}@Override public void setCustomName(net.minecraft.network.chat.Component name){customName=name;setChanged();}@Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.woodprocessing.heat",displayHeat,300,0xff9900),new ProgressView("gui.hbm.woodprocessing.progress",progress,600,0x55ff55));}
}
