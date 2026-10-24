package com.hbm.world.structure.templates.funnel;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original18-slot TileEntityMachineFunnel, including first-match recipe caching and NBT-blind output merging. */
public final class FunnelBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, com.hbm.multiblock.MuffledMachine {
    public static final int MODE_ALL=0,MODE_3X3=1,MODE_2X2=2;
    private static final int[] INPUT={0,1,2,3,4,5,6,7,8},OUTPUT={9,10,11,12,13,14,15,16,17};
    private static final Map<IngredientKey,ItemStack> FROM4=new HashMap<>(),FROM9=new HashMap<>();
    private NonNullList<ItemStack> slots=NonNullList.withSize(18,ItemStack.EMPTY);
    public int mode;public boolean muffled,tilted;private String sourceName="";
    private boolean packed,lastMuffled,lastTilted;private int lastMode;
    public FunnelBlockEntity(BlockPos pos,BlockState state){super(FunnelContent.ENTITY.get(),pos,state);}
    public static void clearRecipeCaches(){FROM4.clear();FROM9.clear();}
    private record IngredientKey(Item item,int damage,Object potion,Object map){
        static IngredientKey of(ItemStack stack){return new IngredientKey(stack.getItem(),stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).potion(),stack.get(DataComponents.MAP_ID));}
    }
    public void tickServer(){if(!(level instanceof ServerLevel server))return;boolean changed=false;
        for(int i=0;i<9;i++){var input=slots.get(i);if(input.isEmpty())continue;int count=9;var result=mode==MODE_2X2||input.getCount()<9?ItemStack.EMPTY:getFrom9(input);if(result.isEmpty()){result=mode==MODE_3X3||input.getCount()<4?ItemStack.EMPTY:getFrom4(input);count=4;}
            if(!result.isEmpty()&&input.getCount()>=count){var output=slots.get(i+9);if(output.isEmpty()){slots.set(i+9,result.copy());input.shrink(count);changed=true;}else if(IngredientKey.of(output).equals(IngredientKey.of(result))&&output.getCount()+result.getCount()<=result.getMaxStackSize()){output.grow(result.getCount());input.shrink(count);changed=true;}}
        }
        if(changed)setChanged();
        if(packed&&mode==lastMode&&muffled==lastMuffled&&tilted==lastTilted&&server.getGameTime()%20!=0)return;
        packed=true;lastMode=mode;lastMuffled=muffled;lastTilted=tilted;var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225D)player.connection.send(packet);dispatchScope.commit();}
    }
    public ItemStack getFrom4(ItemStack ingredient){return find(ingredient,false);}
    public ItemStack getFrom9(ItemStack ingredient){return find(ingredient,true);}
    private ItemStack find(ItemStack ingredient,boolean nine){if(ingredient.isEmpty()||level==null)return ItemStack.EMPTY;var key=IngredientKey.of(ingredient);var cache=nine?FROM9:FROM4;if(cache.containsKey(key))return cache.get(key);var cells=new ArrayList<ItemStack>(Collections.nCopies(9,ItemStack.EMPTY));if(nine)for(int i=0;i<9;i++)cells.set(i,ingredient.copy());else for(int i:new int[]{0,1,3,4})cells.set(i,ingredient.copy());var input=CraftingInput.of(3,3,cells);var match=ItemStack.EMPTY;
        for(var holder:level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)){var recipe=holder.value();if(recipe.matches(input,level)){match=recipe.assemble(input,level.registryAccess());break;}}
        cache.put(key,match.copy());return match;
    }
    public void cycleMode(){if(++mode>2)mode=0;setChanged();}
    @Override public int getContainerSize(){return 18;}
    @Override public int getMaxStackSize(){return 64;}
    @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var result=ContainerHelper.removeItem(slots,slot,count);if(!result.isEmpty())setChanged();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(!stack.isEmpty()&&stack.getCount()>64)stack.setCount(64);setChanged();}
    @Override public void clearContent(){slots.clear();setChanged();}
    @Override protected NonNullList<ItemStack> getItems(){return slots;}
    @Override protected void setItems(NonNullList<ItemStack> items){slots=items;}
    @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5D,worldPosition.getY()+.5D,worldPosition.getZ()+.5D)<=144D;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<9&&(!slots.get(slot).isEmpty()||!getFrom9(stack).isEmpty()||!getFrom4(stack).isEmpty());}
    @Override public int[] getSlotsForFace(Direction face){return face==Direction.DOWN?OUTPUT:INPUT;}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction face){return canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction face){return face==Direction.DOWN?slot>8:face!=Direction.UP&&slot<9;}
    @Override public Component getCustomName(){return sourceName.isEmpty()?super.getCustomName():Component.literal(sourceName);}
    @Override public Component getName(){var custom=getCustomName();return custom!=null?custom:getDefaultName();}
    @Override protected Component getDefaultName(){return Component.translatable("container.machineFunnel");}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inventory){return new FunnelMenu(id,inventory,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,slots,lookup);if(hasCustomName())tag.putString("name",getCustomName().getString());writeSync(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("Items",Tag.TAG_LIST)){slots=NonNullList.withSize(18,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,slots,lookup);}if(tag.contains("name",Tag.TAG_STRING))sourceName=tag.getString("name");mode=tag.getInt("mode");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    private CompoundTag writeSync(CompoundTag tag){tag.putInt("mode",mode);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(hasCustomName())tag.putString("name",getCustomName().getString());return tag;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this,(machine,registries)->writeSync(new CompoundTag()));}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
