package com.hbm.machine.autocrafter;
import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.battery.*;
import com.hbm.pneumatic.PatternMatcher;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Real21-slot source algorithm; actual crafting recipes and the one shared source PatternMatcher. */
public final class AutocrafterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer,EnergyReceiver,MachineView,ICopiable,com.hbm.multiblock.MuffledMachine {
    public static final int CONSUMPTION=100,MAX_POWER=CONSUMPTION*100;
    private static final int[] ACCESS={10,11,12,13,14,15,16,17,18,19};
    private NonNullList<ItemStack> slots=NonNullList.withSize(21,ItemStack.EMPTY);
    public final PatternMatcher matcher=new PatternMatcher(9);
    private List<RecipeHolder<CraftingRecipe>> recipes=List.of();
    public int recipeIndex,recipeCount;public boolean muffled,tilted;
    private BigInteger power=BigInteger.ZERO;private PowerNode node;private String sourceName="";
    private boolean rebuildPending=true;private long recipeRevision=-1;private CompoundTag lastPacked;
    public AutocrafterBlockEntity(BlockPos pos,BlockState state){super(AutocrafterContent.ENTITY.get(),pos,state);}
    private CraftingInput grid(int start){var cells=new ArrayList<ItemStack>(9);for(int i=0;i<9;i++)cells.add(slots.get(start+i));return CraftingInput.of(3,3,cells);}
    private ItemStack preview(CraftingRecipe recipe){return recipe.assemble(grid(0),level.registryAccess());}
    private void rebuild(boolean keepSelection){
        if(level==null)return;int selected=keepSelection?recipeIndex:0;var matches=new ArrayList<RecipeHolder<CraftingRecipe>>();var template=grid(0);
        for(var holder:level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING))if(holder.value().matches(template,level))matches.add(holder);
        recipes=List.copyOf(matches);recipeCount=recipes.size();recipeIndex=selected>=0&&selected<recipeCount?selected:0;
        slots.set(9,recipes.isEmpty()?ItemStack.EMPTY:preview(recipes.get(recipeIndex).value()));rebuildPending=false;recipeRevision=AutocrafterRecipeEvents.revision();setChanged();
    }
    public void updateTemplateGrid(){rebuild(false);}
    public void nextMode(int index){if(index<0||index>8||level!=null&&level.isClientSide)return;matcher.nextMode(slots.get(index),index);setChanged();}
    public void nextTemplate(){if(level==null||level.isClientSide)return;if(++recipeIndex>=recipes.size())recipeIndex=0;slots.set(9,recipes.isEmpty()?ItemStack.EMPTY:preview(recipes.get(recipeIndex).value()));setChanged();}
    public void setTemplateFromMenu(int index,ItemStack stack){if(index<0||index>8)return;setItem(index,stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));if(level!=null&&!level.isClientSide)matcher.initPatternSmart(slots.get(index),index);updateTemplateGrid();}
    public void tickServer(){
        if(!(level instanceof ServerLevel server))return;ensureNode(server);
        if(rebuildPending||recipeRevision!=AutocrafterRecipeEvents.revision())rebuild(true);
        var previous=power;chargeFromItems();
        if(!recipes.isEmpty()&&power.compareTo(BigInteger.valueOf(CONSUMPTION))>=0){
            var recipe=recipes.get(recipeIndex).value();var input=grid(10);
            if(recipe.matches(input,server)){
                var result=recipe.assemble(input,server.registryAccess());var output=slots.get(19);
                if(!result.isEmpty()&&(output.isEmpty()||ItemStack.isSameItemSameComponents(output,result)&&output.getCount()+result.getCount()<=output.getMaxStackSize())){
                    if(output.isEmpty())slots.set(19,result.copy());else output.grow(result.getCount());
                    for(int i=10;i<19;i++){
                        var ingredient=slots.get(i);if(ingredient.isEmpty())continue;
                        // Source asks the consumed item for a container only after this physical slot becomes empty.
                        var before=ingredient.copy();removeItem(i,1);
                        if(slots.get(i).isEmpty()&&before.hasCraftingRemainingItem()){
                            var container=before.getCraftingRemainingItem();
                            if(!container.isEmpty()&&container.isDamageableItem()&&container.getDamageValue()>container.getMaxDamage())continue;
                            setItem(i,container);
                        }
                    }
                    power=power.subtract(BigInteger.valueOf(CONSUMPTION));setChanged();
                }
            }
        }
        if(!previous.equals(power))setChanged();sync(server);
    }
    private void chargeFromItems(){var stack=slots.get(20);if(stack.getItem() instanceof CreativeBatteryItem){power=BigInteger.valueOf(MAX_POWER);return;}if(stack.getItem() instanceof HbmBattery battery){long amount=BigInteger.valueOf(MAX_POWER).subtract(power).max(BigInteger.ZERO).min(BigInteger.valueOf(battery.getDischargeRate(stack))).min(BigInteger.valueOf(battery.getCharge(stack))).longValueExact();battery.dischargeBattery(stack,amount);power=power.add(BigInteger.valueOf(amount));}}
    public static boolean battery(ItemStack stack){return stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem;}
    private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    private static boolean sameItem(ItemStack a,ItemStack b){return com.hbm.pneumatic.SourcePatternItems.sameExact(a,b);}
    @Override public boolean canPlaceItem(int slot,ItemStack input){
        if(input.isEmpty()||input.getCount()>1&&input.hasCraftingRemainingItem())return false;
        if(slot<10||slot>18||slots.get(slot-10).isEmpty())return false;
        if(!slots.get(slot).isEmpty()&&slots.get(slot).getCount()+input.getCount()>4||input.getCount()>4)return false;
        var validSlots=new ArrayList<Integer>();
        for(int i=0;i<9;i++){var filter=slots.get(i);if(filter.isEmpty()||matcher.modes[i]==null||matcher.modes[i].isEmpty())continue;if(matcher.isValidForFilter(filter,i,input)){validSlots.add(i+10);if(i+10==slot&&slots.get(slot).isEmpty())return true;}}
        if(!validSlots.contains(slot))return false;int count=slots.get(slot).getCount();
        for(int index:validSlots){var valid=slots.get(index);if(valid.isEmpty())return false;if(!sameItem(valid,input))continue;if(valid.getCount()<count)return false;}
        return !input.hasCraftingRemainingItem();
    }
    @Override public int[] getSlotsForFace(Direction face){return ACCESS;}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){if(slot==19)return true;if(slot>9&&slot<19){var filter=slots.get(slot-10);if(filter.isEmpty()||matcher.modes[slot-10]==null||matcher.modes[slot-10].isEmpty())return true;return !matcher.isValidForFilter(filter,slot-10,stack);}return false;}
    @Override public int getContainerSize(){return 21;}
    @Override public int getMaxStackSize(){return 64;}
    @Override protected NonNullList<ItemStack> getItems(){return slots;}
    @Override protected void setItems(NonNullList<ItemStack> items){slots=items;}
    @Override public ItemStack removeItem(int slot,int amount){var result=ContainerHelper.removeItem(slots,slot,amount);if(!result.isEmpty())setChanged();return result;}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    @Override public Component getCustomName(){return sourceName.isEmpty()?super.getCustomName():Component.literal(sourceName);}
    @Override public Component getName(){var name=getCustomName();return name==null?getDefaultName():name;}
    @Override protected Component getDefaultName(){return Component.translatable("container.autocrafter");}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new AutocrafterMenu(id,inventory,this);}
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
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);var items=new ListTag();for(int i=0;i<21;i++)if(!slots.get(i).isEmpty()){var row=(CompoundTag)slots.get(i).save(lookup);row.putByte("slot",(byte)i);items.add(row);}tag.put("items",items);
        if(power.bitLength()<64)tag.putLong("power",power.longValue());else tag.putString("power",power.toString());tag.putInt("rec",recipeIndex);matcher.write(tag);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(hasCustomName())tag.putString("name",getCustomName().getString());
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);
        if(tag.contains("items",Tag.TAG_LIST)){slots=NonNullList.withSize(21,ItemStack.EMPTY);for(var entry:tag.getList("items",Tag.TAG_COMPOUND)){var row=(CompoundTag)entry;int slot=row.getByte("slot");if(slot>=0&&slot<21)slots.set(slot,ItemStack.parseOptional(lookup,row));}rebuildPending=true;}
        else if(tag.contains("Items",Tag.TAG_LIST)){slots=NonNullList.withSize(21,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,slots,lookup);rebuildPending=true;}
        if(tag.contains("power"))power=tag.contains("power",Tag.TAG_STRING)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));
        if(tag.contains("rec"))recipeIndex=tag.getInt("rec");if(tag.contains("recipeCount"))recipeCount=tag.getInt("recipeCount");matcher.read(tag);
        muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");if(tag.contains("name",Tag.TAG_STRING))sourceName=tag.getString("name");
    }
    private CompoundTag stateTag(){var tag=new CompoundTag();tag.putString("power",power.toString());matcher.write(tag);tag.putInt("recipeCount",recipeCount);tag.putInt("rec",recipeIndex);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);return tag;}
    private void sync(ServerLevel server){var tag=stateTag();if(tag.equals(lastPacked)&&server.getGameTime()%20!=0)return;lastPacked=tag.copy();var packet=ClientboundBlockEntityDataPacket.create(this,(machine,lookup)->tag);try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225)player.connection.send(packet);dispatchScope.commit();}}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return stateTag();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    public void setFilterContents(CompoundTag tag){int slot=tag.getInt("slot");if(slot<0||slot>8||level==null)return;setItem(slot,ItemStack.parseOptional(level.registryAccess(),tag.getCompound("stack")));matcher.initPatternSmart(slots.get(slot),slot);updateTemplateGrid();}
    public int[] getFilterSlots(){return new int[]{0,9};}
    @Override public CompoundTag getSettings(Level world,BlockPos pos){var tag=new CompoundTag();var items=new ListTag();for(int i=0;i<9;i++)if(!slots.get(i).isEmpty()){var row=(CompoundTag)slots.get(i).save(world.registryAccess());row.putByte("slot",(byte)i);items.add(row);}tag.put("items",items);return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level world,Player player,BlockPos pos){
        // Source default IControlReceiverFilter copies only listed nonempty slots, cycles their existing mode, and does NOT call updateTemplateGrid.
        var items=tag.getList("items",Tag.TAG_COMPOUND);int size=items.size();if(size<=0)return;
        for(int i=0,count=0;i<9;i++,count++)if(i<size){var row=items.getCompound(count);int slot=row.getByte("slot");var stack=ItemStack.parseOptional(world.registryAccess(),row);boolean router=tag.contains("modes")&&slot>index*5&&slot<index*+5;if(!stack.isEmpty()&&index<size&&(slot<9||router)&&slot>=0&&slot<21){setItem(slot,stack);nextMode(slot);setChanged();}}
    }
    @Override public String[] infoForDisplay(Level world,BlockPos pos){return new String[]{"copytool.filter"};}
}
