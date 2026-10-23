package com.hbm.bomb.nuclear;

import com.google.gson.Gson;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** The nine original static assembly inventories, including the two-stage Mike/Tsar loading rules. */
public final class NuclearAssembly implements WorldlyContainer {
    public record Requirement(int slot,String item,Integer metadata){}
    public record SlotPosition(int index,int x,int y){}
    public record Gui(String texture,int width,int height,int playerX,int playerY,int titleY,String infoKey){}
    public record Definition(String kind,String symbol,String id,String originalClass,List<SlotPosition> slots,int maxStackSize,String nameKey,List<Requirement> ready,List<Requirement> filled,Gui gui){}
    private record Document(List<Definition> assemblies){}
    private static final Map<String,Definition> DEFINITIONS=loadDefinitions();
    private final Definition definition;
    private final ItemStack[] items;
    private final Runnable dirty;
    private final Predicate<Player> usable;
    private Component name;
    public NuclearAssembly(String kind,Runnable dirty,Predicate<Player> usable){definition=definition(kind);items=new ItemStack[definition.slots().size()];java.util.Arrays.fill(items,ItemStack.EMPTY);this.dirty=dirty;this.usable=usable;}
    public static Definition definition(String kind){var definition=DEFINITIONS.get(kind.toUpperCase(java.util.Locale.ROOT));if(definition==null)throw new IllegalArgumentException("Unknown original nuclear assembly "+kind);return definition;}
    private static Map<String,Definition> loadDefinitions(){
        try(var stream=NuclearAssembly.class.getResourceAsStream("/hbm/nuclear-assemblies.json")){
            if(stream==null)throw new IllegalStateException("Missing original nuclear assembly definitions");
            var document=new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Document.class);
            return document.assemblies().stream().collect(Collectors.toUnmodifiableMap(Definition::kind,value->value));
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
    }
    public Definition definition(){return definition;}
    public Component displayName(){return name==null?Component.translatable(definition.nameKey()):name;}
    public void setCustomName(Component name){this.name=name;setChanged();}
    public boolean matches(int slot,String symbol){
        ItemStack stack=getItem(slot);if(stack.isEmpty())return false;
        try{return stack.getItem()==HbmContent.item(symbol);}catch(IllegalArgumentException absent){return false;}
    }
    private boolean matches(List<Requirement> requirements){
        if(requirements==null)return false;
        for(var expected:requirements){
            if(!matches(expected.slot(),expected.item()))return false;
            if(expected.metadata()!=null&&getItem(expected.slot()).getOrDefault(HbmHazards.LEGACY_METADATA.get(),getItem(expected.slot()).getDamageValue()).intValue()!=expected.metadata().intValue())return false;
        }
        return true;
    }
    public boolean isReady(){return matches(definition.ready());}
    public boolean isFilled(){return matches(definition.filled());}
    @Override public int getContainerSize(){return items.length;}
    @Override public boolean isEmpty(){for(ItemStack item:items)if(!item.isEmpty())return false;return true;}
    @Override public ItemStack getItem(int slot){return slot>=0&&slot<items.length?items[slot]:ItemStack.EMPTY;}
    @Override public ItemStack removeItem(int slot,int amount){ItemStack stack=getItem(slot);if(stack.isEmpty())return ItemStack.EMPTY;if(stack.getCount()<=amount){items[slot]=ItemStack.EMPTY;return stack;}ItemStack removed=stack.split(amount);if(stack.isEmpty())items[slot]=ItemStack.EMPTY;return removed;}
    @Override public ItemStack removeItemNoUpdate(int slot){ItemStack stack=getItem(slot);if(!stack.isEmpty())items[slot]=ItemStack.EMPTY;return stack;}
    @Override public void setItem(int slot,ItemStack stack){items[slot]=stack;if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());}
    @Override public int getMaxStackSize(){return definition.maxStackSize();}
    @Override public void setChanged(){dirty.run();}
    @Override public boolean stillValid(Player player){return usable.test(player);}
    @Override public void clearContent(){java.util.Arrays.fill(items,ItemStack.EMPTY);}
    @Override public boolean canPlaceItem(int slot,ItemStack item){return false;}
    @Override public int[] getSlotsForFace(Direction side){return new int[0];}
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack item,Direction side){return false;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack item,Direction side){return side!=Direction.DOWN||slot!=1||item.is(net.minecraft.world.item.Items.BUCKET);}
    public void save(CompoundTag tag,HolderLookup.Provider lookup){
        var list=new ListTag();for(int slot=0;slot<items.length;slot++)if(!items[slot].isEmpty()){var entry=new CompoundTag();entry.putByte("slot",(byte)slot);items[slot].save(lookup,entry);list.add(entry);}tag.put("items",list);if(name!=null)tag.putString("name",Component.Serializer.toJson(name,lookup));
    }
    public void load(CompoundTag tag,HolderLookup.Provider lookup){
        clearContent();var list=tag.getList("items",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var entry=list.getCompound(i);int slot=entry.getByte("slot");if(slot>=0&&slot<items.length)items[slot]=ItemStack.parseOptional(lookup,entry);}
        name=tag.contains("name",Tag.TAG_STRING)&&!tag.getString("name").isEmpty()?Component.Serializer.fromJson(tag.getString("name"),lookup):null;
    }
}
