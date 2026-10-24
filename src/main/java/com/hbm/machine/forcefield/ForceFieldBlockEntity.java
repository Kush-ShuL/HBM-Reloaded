package com.hbm.machine.forcefield;
import com.hbm.api.machine.MachineView;import com.hbm.energy.*;import com.hbm.item.battery.*;import java.math.BigInteger;import java.util.*;import net.minecraft.core.*;import net.minecraft.nbt.*;import net.minecraft.network.chat.Component;import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.*;import net.minecraft.world.entity.player.*;import net.minecraft.world.inventory.AbstractContainerMenu;import net.minecraft.world.item.*;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;
/** Original3slots, regen/collapse/recharge and native privateHE receiver. No player owner/whitelist/OC exists in source. */
public final class ForceFieldBlockEntity extends BlockEntity implements WorldlyContainer,MenuProvider,EnergyReceiver,EnergyConductor,MachineView, com.hbm.multiblock.MuffledMachine {
 private final ItemStack[] slots={ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY};private final ForceFieldBoundary boundary=new ForceFieldBoundary();private PowerNode node;private Component customName;
 public BigInteger power=BigInteger.ZERO;public int health=100,maxHealth=100,powerCons,cooldown,blink,color=0x0000ff;public float radius=16;public boolean isOn,muffled,tilted;
 public ForceFieldBlockEntity(BlockPos pos,BlockState state){super(ForceFieldContent.ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,ForceFieldBlockEntity field){field.tickBothSides(level);}
 private void tickBothSides(Level world){
  if(world instanceof ServerLevel server){
   if(node==null||node.isExpired()){node=new PowerNode(worldPosition).setConnections(Arrays.stream(Direction.values()).filter(this::canConnect).map(side->new NodeConnection(worldPosition.relative(side),side)).toArray(NodeConnection[]::new));HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addReceiver(this);
   int rStack=0,hStack=0;radius=ForceFieldConfig.baseRadius;maxHealth=100;
   if(slots[1].is(ForceFieldContent.RADIUS.get())){rStack=slots[1].getCount();radius+=rStack*ForceFieldConfig.radUpgrade;}
   if(slots[2].is(ForceFieldContent.HEALTH.get())){hStack=slots[2].getCount();maxHealth+=hStack*ForceFieldConfig.shUpgrade;}
   powerCons=ForceFieldConfig.baseCon+rStack*ForceFieldConfig.radCon+hStack*ForceFieldConfig.shCon;
   if(slots[0].getItem() instanceof CreativeBatteryItem)power=ForceFieldConfig.maxPower;else if(slots[0].getItem() instanceof HbmBattery battery){long amount=ForceFieldConfig.maxPower.subtract(power).min(BigInteger.valueOf(battery.getDischargeRate(slots[0]))).min(BigInteger.valueOf(battery.getCharge(slots[0]))).longValueExact();battery.dischargeBattery(slots[0],amount);power=power.add(BigInteger.valueOf(amount));}
   if(blink>0){blink--;color=0xff0000;}else color=0x00ff00;
  }
  if(cooldown>0)cooldown--;else{if(health<maxHealth)health=(int)(health+(maxHealth/100)*ForceFieldConfig.healthRegenModif);if(health>maxHealth)health=maxHealth;}
  if(isOn&&cooldown==0&&health>0&&power.compareTo(BigInteger.valueOf(powerCons))>=0){boundary.update(world,worldPosition,radius,muffled,this::damage);if(!world.isClientSide)power=power.subtract(BigInteger.valueOf(powerCons));}else boundary.clear();
  if(world instanceof ServerLevel server){if(power.compareTo(BigInteger.valueOf(powerCons))<0)power=BigInteger.ZERO;setChanged();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<250000)player.connection.send(packet);dispatchScope.commit();}}
 }
 private void damage(int amount){health-=amount;if(amount>=maxHealth/250)blink=5;if(health<=0){health=0;cooldown=(int)(100+radius*(float)ForceFieldConfig.cooldownModif);}}
 public boolean fieldVisible(){return isOn&&health>0&&power.signum()>0&&cooldown==0;}
 public void toggle(){isOn=!isOn;setChanged();}
 @Override public Component getDisplayName(){return customName==null?Component.translatable("container.forceField"):customName;}
 public void setCustomName(Component name){customName=name;setChanged();}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ForceFieldMenu(id,inventory,this);}
 @Override public long getPower(){return legacy(power);}@Override public long getMaxPower(){return legacy(ForceFieldConfig.maxPower);}@Override public void setPower(long value){power=BigInteger.valueOf(value);setChanged();}
 @Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return ForceFieldConfig.maxPower;}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
 @Override public BigInteger getReceiverSpeedExact(){return ForceFieldConfig.maxPower;}
 @Override public BigInteger transferPowerExact(BigInteger offered){if(offered.signum()<0)throw new IllegalArgumentException("Negative HE");var accepted=offered.min(ForceFieldConfig.maxPower.subtract(power));power=power.add(accepted);return offered.subtract(accepted);}
 private static long legacy(BigInteger amount){return amount.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
 @Override public boolean canConnect(Direction side){return side!=null&&side!=Direction.UP;}@Override public PowerNode energyNode(){return node;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
 private void disconnect(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;boundary.clear();}
 @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
 private void writeState(CompoundTag tag){tag.putString("powerTime",power.toString());tag.putInt("health",health);tag.putInt("maxHealth",maxHealth);tag.putInt("cooldown",cooldown);tag.putInt("blink",blink);tag.putFloat("radius",radius);tag.putBoolean("isOn",isOn);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 private void readState(CompoundTag tag){try{power=new BigInteger(tag.getString("powerTime"));}catch(NumberFormatException ignored){power=BigInteger.valueOf(tag.getLong("powerTime"));}health=tag.getInt("health");maxHealth=tag.getInt("maxHealth");cooldown=tag.getInt("cooldown");blink=tag.getInt("blink");radius=tag.getFloat("radius");isOn=tag.getBoolean("isOn");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);writeState(tag);var items=new ListTag();for(int slot=0;slot<3;slot++)if(!slots[slot].isEmpty()){var item=(CompoundTag)slots[slot].save(lookup);item.putByte("slot",(byte)slot);items.add(item);}tag.put("items",items);if(customName!=null)tag.putString("name",Component.Serializer.toJson(customName,lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readState(tag);Arrays.fill(slots,ItemStack.EMPTY);for(var value:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("slot");if(slot>=0&&slot<3)slots[slot]=ItemStack.parseOptional(lookup,item);}if(tag.contains("name"))try{customName=Component.Serializer.fromJson(tag.getString("name"),lookup);}catch(Exception ignored){customName=Component.literal(tag.getString("name"));}}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();writeState(tag);tag.putInt("color",color);tag.putInt("powerCons",powerCons);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readState(tag);color=tag.getInt("color");powerCons=tag.getInt("powerCons");}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public int getContainerSize(){return 3;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int amount){var result=slots[slot].split(amount);if(!result.isEmpty())setChanged();return result;}@Override public ItemStack removeItemNoUpdate(int slot){var stack=slots[slot];slots[slot]=ItemStack.EMPTY;return stack;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;if(stack.getCount()>64)stack.setCount(64);setChanged();}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);setChanged();}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==1||slot==0&&stack.getItem() instanceof HbmBattery;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0};}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return canPlaceItem(slot,stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
