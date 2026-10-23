package com.hbm.machine.arcfurnace;
import com.hbm.energy.*;
import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.foundry.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.material.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.pollution.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
/** Original30-slot multi-batch furnace, electrode life, powered lid/delay, molten quanta and true foundry pouring. */
public final class ArcFurnaceBlockEntity extends ThermalMachine implements EnergyReceiver,SidedEnergyConnector, com.hbm.multiblock.MuffledMachine {
 public static Consumer<ArcFurnaceBlockEntity> CLIENT_TICK=machine->{},CLIENT_REMOVED=machine->{};
 public static final long MAX_POWER=2_500_000;public static final int MAX_LIQUID=HbmMaterials.shape("BLOCK").q(128);
 public static final int[] ACCESSIBLE=java.util.stream.IntStream.range(0,30).filter(i->i!=3&&i!=4).toArray();
 public long power;public boolean liquidMode,isProgressing,hasMaterial,muffled,tilted;public float progress,lid,prevLid,syncLid;public int delay,upgrade,approachNum;public final byte[] electrodes=new byte[3];public final List<MaterialStack> liquids=new ArrayList<>();private boolean applyingSync;
 private final Map<BlockPos,PowerNode> nodes=new LinkedHashMap<>();
 public ArcFurnaceBlockEntity(BlockPos pos,BlockState state){super(ArcFurnaceContent.ENTITY.get(),pos,state,30);}
 @Override protected String title(){return "container.machineArcFurnaceLarge";}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ArcFurnaceMenu(id,inventory,this);}
 public static void tick(Level level,BlockPos pos,BlockState state,ArcFurnaceBlockEntity machine){machine.updateUpgrade();if(level instanceof ServerLevel server)machine.serverTick(server);else CLIENT_TICK.accept(machine);}
 private void updateUpgrade(){upgrade=slots.get(4).getItem() instanceof FurnaceUpgradeItem item&&item.type()==FurnaceUpgradeItem.Type.SPEED?Math.min(3,item.tier()):0;}
 public int getMaxInputSize(){return upgrade==0?1:upgrade==1?4:upgrade==2?8:16;}
 public float volume(float original){return muffled?original*.1F:original;}
 public static record Port(BlockPos pos,Direction side){}
 public static List<Port> ports(BlockPos core,Direction dir){Direction rot=dir.getCounterClockWise();return List.of(new Port(core.relative(dir,2).relative(rot),dir),new Port(core.relative(dir,2).relative(rot,-1),dir),new Port(core.relative(rot,2).relative(dir),rot),new Port(core.relative(rot,2).relative(dir,-1),rot),new Port(core.relative(rot,-2).relative(dir),rot.getOpposite()),new Port(core.relative(rot,-2).relative(dir,-1),rot.getOpposite()));}
 private void updatePorts(ServerLevel server){var actual=ports(worldPosition,DummyableBlock.facing(getBlockState())).stream().filter(port->server.hasChunkAt(port.pos)&&server.getBlockState(port.pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port.pos))).toList();
  if(!nodes.keySet().equals(new LinkedHashSet<>(actual.stream().map(Port::pos).toList()))||nodes.values().stream().anyMatch(PowerNode::isExpired)){disconnect();for(var port:actual){var node=new PowerNode(port.pos).setConnections(new NodeConnection(port.pos.relative(port.side),port.side));HbmEnergy.nodespace(server).createNode(node);nodes.put(port.pos,node);}}for(var node:nodes.values())if(node.hasValidNet())node.addReceiver(this);
 }
 private void disconnect(){if(level instanceof ServerLevel server)for(var node:nodes.values())if(!node.isExpired())HbmEnergy.nodespace(server).destroyNode(node);nodes.clear();}
 @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)CLIENT_REMOVED.accept(this);super.onChunkUnloaded();}
 private void chargeBattery(){if(power<0){power=0;return;}if(power>MAX_POWER){power=MAX_POWER;return;}if(slots.get(3).getItem() instanceof CreativeBatteryItem){power=MAX_POWER;return;}if(slots.get(3).getItem() instanceof HbmBattery battery){long amount=Math.min(Math.min(MAX_POWER-power,battery.getDischargeRate(slots.get(3))),battery.getCharge(slots.get(3)));battery.dischargeBattery(slots.get(3),amount);power+=amount;}}
 private void serverTick(ServerLevel server){updatePorts(server);chargeBattery();isProgressing=false;if(lid>0)loadIngredients();
  if(power>0){boolean ingredients=hasIngredients(),electrodes=hasElectrodes();int consumption=(int)(1000*Math.pow(5,upgrade));
   if(ingredients&&electrodes&&delay<=0&&liquids.isEmpty()){
    if(lid>0){lid-=1F/(60F/(upgrade*.5+1));if(lid<0)lid=0;progress=0;}
    else if(power>=consumption){int duration=400/(upgrade*2+1);progress+=1F/duration;isProgressing=true;power-=consumption;if(progress>=1){process();progress=0;setChanged();delay=(int)(120/(upgrade*.5+1));PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,10);}}
   }else{if(delay>0)delay--;progress=0;if(lid<1){lid+=1F/(60F/(upgrade*.5+1));if(lid>1)lid=1;}}
   hasMaterial=ingredients;
  }
  decideElectrodeState();if(!hasMaterial)hasMaterial=hasIngredients();
  if(!liquids.isEmpty()&&lid>0){var dir=DummyableBlock.facing(getBlockState());double x=worldPosition.getX()+.5+dir.getStepX()*2.875,z=worldPosition.getZ()+.5+dir.getStepZ()*2.875;Vec3[] impact={Vec3.ZERO};var poured=FoundryPouring.pourFullStack(server,x,worldPosition.getY()+1.25,z,6,true,liquids,HbmMaterials.shape("INGOT").q(1),impact);if(poured!=null)FoundryStreamPacket.send(server,new Vec3(x,worldPosition.getY()+1,z),poured.material.moltenColor(),dir,Math.max(1F,worldPosition.getY()+1-(float)(Math.ceil(impact[0].y)-.875)),.625F,.625F);}
  liquids.removeIf(material->material.amount<=0);sync(150);
 }
 public ArcFurnaceRecipe recipe(ItemStack stack){return level==null?null:ArcFurnaceRecipes.output(level.getRecipeManager(),stack,liquidMode);}
 public void loadIngredients(){boolean changed=false;for(int queue=25;queue<30;queue++){if(slots.get(queue).isEmpty())continue;var recipe=recipe(slots.get(queue));if(recipe==null)continue;int max=maxInput(slots.get(queue),recipe);if(max==0)continue;
  for(int i=5;i<25;i++){if(slots.get(i).isEmpty()||!ItemStack.isSameItemSameComponents(slots.get(queue),slots.get(i)))continue;int move=Math.min(Math.min(slots.get(i).getMaxStackSize()-slots.get(i).getCount(),slots.get(queue).getCount()),max-slots.get(i).getCount());if(move>0){removeItem(queue,move);slots.get(i).grow(move);changed=true;}if(slots.get(queue).isEmpty())break;}
  if(!slots.get(queue).isEmpty())for(int i=5;i<25;i++){if(!slots.get(i).isEmpty())continue;int move=Math.min(max,slots.get(queue).getCount());slots.set(i,slots.get(queue).copyWithCount(move));removeItem(queue,move);changed=true;if(slots.get(queue).isEmpty())break;}
 }if(changed)setChanged();}
 private int maxInput(ItemStack input,ArcFurnaceRecipe recipe){int max=Math.min(input.getMaxStackSize(),getMaxInputSize());if(liquidMode)return max;double rate=com.hbm.ore.OreProcessing.rate(input);while(max>0&&com.hbm.ore.OreProcessing.maximumCount(recipe.solid().getCount()*max,rate)>recipe.solid().getMaxStackSize())max--;return max;}
 public void decideElectrodeState(){for(int i=0;i<3;i++){var stack=slots.get(i);electrodes[i]=(byte)(stack.is(ArcFurnaceContent.BURNT.get())?3:stack.is(ArcFurnaceContent.ELECTRODE.get())?(isProgressing||ArcElectrodeItem.durability(stack)>0?2:1):0);}}
 public void process(){for(int i=5;i<25;i++){if(slots.get(i).isEmpty())continue;var recipe=recipe(slots.get(i));if(recipe==null)continue;if(!liquidMode&&!recipe.solid().isEmpty()){double rate=com.hbm.ore.OreProcessing.rate(slots.get(i));int base=recipe.solid().getCount()*slots.get(i).getCount();if(com.hbm.ore.OreProcessing.maximumCount(base,rate)>recipe.solid().getMaxStackSize())continue;slots.set(i,recipe.solid().copyWithCount(com.hbm.ore.OreProcessing.count(base,rate,level.random)));}if(liquidMode&&!recipe.molten().isEmpty())while(!slots.get(i).isEmpty()){double rate=com.hbm.ore.OreProcessing.rate(slots.get(i));long amount=recipe.molten().stream().mapToLong(stack->com.hbm.ore.OreProcessing.maximumCount(stack.amount,rate)).sum();if(stackAmount()+amount>MAX_LIQUID)break;removeItem(i,1);for(var material:recipe.molten())addToStack(new MaterialStack(material.material,com.hbm.ore.OreProcessing.count(material.amount,rate,level.random)));}}
  for(int i=0;i<3;i++)if(ArcElectrodeItem.damage(slots.get(i)))slots.set(i,ArcFurnaceContent.BURNT.get().stack(ArcElectrodeItem.variant(slots.get(i))));
 }
 public void addToStack(MaterialStack stack){for(var existing:liquids)if(existing.material==stack.material){existing.amount+=stack.amount;return;}liquids.add(stack.copy());}
 public int stackAmount(){return liquids.stream().mapToInt(stack->stack.amount).sum();}
 public boolean hasIngredients(){for(int i=5;i<25;i++)if(!slots.get(i).isEmpty()){var recipe=recipe(slots.get(i));if(recipe!=null&&recipe.inMode(liquidMode)&&(liquidMode||com.hbm.ore.OreProcessing.maximumCount(recipe.solid().getCount()*slots.get(i).getCount(),com.hbm.ore.OreProcessing.rate(slots.get(i)))<=recipe.solid().getMaxStackSize()))return true;}return false;}
 public boolean hasElectrodes(){for(int i=0;i<3;i++)if(!slots.get(i).is(ArcFurnaceContent.ELECTRODE.get()))return false;return true;}
 @Override public void setItem(int slot,ItemStack stack){super.setItem(slot,stack);if(slot==4&&level!=null&&(stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem))level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1,1);}
 @Override public int[] getSlotsForFace(Direction side){return ACCESSIBLE.clone();}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){if(slot<3)return stack.is(ArcFurnaceContent.ELECTRODE.get());if(slot>=25)return recipe(stack)!=null;return false;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot<3)return stack.is(ArcFurnaceContent.ELECTRODE.get());if(slot>4){var recipe=recipe(stack);return recipe!=null&&recipe.inMode(liquidMode);}return false;}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){if(slot<3)return lid>=1&&!stack.is(ArcFurnaceContent.ELECTRODE.get());if(slot>4&&slot<25)return lid>0&&recipe(stack)==null;if(slot>=25)return recipe(stack)==null;return false;}
 /** Source AE2-facing operations retain their own simulation and extraction rules. */
 public ItemStack distributeInput(ItemStack input,boolean modulate){
  if(input.isEmpty())return ItemStack.EMPTY;
  if(input.is(ArcFurnaceContent.ELECTRODE.get())){for(int i=0;i<3;i++){if(slots.get(i).isEmpty()){var split=input.split(1);if(modulate)setItem(i,split);}if(input.isEmpty())return ItemStack.EMPTY;}return input;}
  var recipe=recipe(input);if(recipe!=null){int max=maxInput(input,recipe);if(max==0)return input;for(int i=5;i<25;i++){
   var current=slots.get(i);if(current.isEmpty()){if(input.getCount()>max){var split=input.split(max);if(modulate)slots.set(i,split);}else{if(modulate)slots.set(i,input);return ItemStack.EMPTY;}}
   else if(ItemStack.isSameItemSameComponents(input,current)&&current.getCount()<max){if(input.getCount()>max-current.getCount()){input.split(max-current.getCount());if(modulate)current.setCount(max);}else{if(modulate)current.grow(input.getCount());return ItemStack.EMPTY;}}
  }}if(modulate)setChanged();return input;
 }
 public ItemStack collectRequested(ItemStack request,boolean modulate){int remaining=request.getCount();if(remaining==0)return ItemStack.EMPTY;for(int i=0;i<25;i++){
  if(i>=3&&i<=4||i<3&&request.is(ArcFurnaceContent.ELECTRODE.get())||i>=5&&recipe(request)!=null)continue;
  var current=slots.get(i);if(current.isEmpty()||!ItemStack.isSameItemSameComponents(current,request))continue;
  if(remaining>current.getCount()){remaining-=current.getCount();if(modulate)slots.set(i,ItemStack.EMPTY);}else{if(modulate){current.shrink(remaining);if(current.isEmpty())slots.set(i,ItemStack.EMPTY);setChanged();}return request;}
 }request.shrink(remaining);if(modulate)setChanged();return request.isEmpty()?ItemStack.EMPTY:request;
 }
 public ItemStack getAvailableItemFromSlot(int slot){var stack=slots.get(slot);if(stack.isEmpty()||slot<3&&stack.is(ArcFurnaceContent.ELECTRODE.get())||slot>4&&recipe(stack)!=null||slot==3||slot==4)return ItemStack.EMPTY;return stack;}
 public void toggleMode(){liquidMode=!liquidMode;setChanged();}
 @Override public long getPower(){return power;}@Override public void setPower(long amount){power=amount;setChanged();}@Override public long getMaxPower(){return MAX_POWER;}@Override public boolean canConnect(Direction side){return false;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null;}@Override public long energyStored(){return power;}@Override public long energyCapacity(){return MAX_POWER;}@Override public List<ProgressView> progressBars(){return hasMaterial?List.of(new ProgressView("gui.hbm.arc_furnace.progress",progress,1,0xc989d0)):List.of();}
 @Override protected void saveMachine(CompoundTag tag){tag.putLong("power",power);tag.putBoolean("liquidMode",liquidMode);tag.putFloat("progress",progress);tag.putFloat("lid",lid);tag.putInt("delay",delay);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putBoolean("isProgressing",isProgressing);tag.putBoolean("hasMaterial",hasMaterial);for(int i=0;i<3;i++)tag.putByte("electrode"+i,electrodes[i]);tag.putShort("count",(short)liquids.size());for(int i=0;i<liquids.size();i++){tag.putInt("m"+i,liquids.get(i).material.id());tag.putInt("a"+i,liquids.get(i).amount);}}
 @Override protected void loadMachine(CompoundTag tag){power=tag.getLong("power");liquidMode=tag.getBoolean("liquidMode");progress=tag.getFloat("progress");if(applyingSync){syncLid=tag.getFloat("lid");if(syncLid!=0&&syncLid!=1)approachNum=2;}else lid=tag.getFloat("lid");delay=tag.getInt("delay");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");isProgressing=tag.getBoolean("isProgressing");hasMaterial=tag.getBoolean("hasMaterial");for(int i=0;i<3;i++)electrodes[i]=tag.getByte("electrode"+i);liquids.clear();for(int i=0;i<tag.getShort("count");i++){var material=HbmMaterials.byId(tag.getInt("m"+i));if(material==null)throw new IllegalArgumentException("Unknown saved original arc furnace material "+tag.getInt("m"+i));liquids.add(new MaterialStack(material,tag.getInt("a"+i)));}}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){applyingSync=true;try{super.handleUpdateTag(tag,lookup);}finally{applyingSync=false;}}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
