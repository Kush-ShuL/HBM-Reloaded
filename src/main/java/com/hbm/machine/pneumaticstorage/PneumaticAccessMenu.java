package com.hbm.machine.pneumaticstorage;

import api.hbm.ntl.StackCache;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/** Source48 virtual slots are read-only projections; all transfers consume the real server-side cache. */
public final class PneumaticAccessMenu extends AbstractContainerMenu {
    public static final int GRID_SIZE=48;
    public final PneumaticAccess machine;
    private final Player player;
    public final LinkedHashMap<Long,Entry> cachedEntries=new LinkedHashMap<>();
    public final Entry[] visible=new Entry[GRID_SIZE];
    public int listingStart,listingSize,sort;
    public String search="";
    public Predicate<ItemStack> detailedMatch;
    public static final class Entry {
        public final long hash;public final ItemStack display;public long amount;
        public Entry(long hash,ItemStack display,long amount){this.hash=hash;this.display=display.copyWithCount(1);this.amount=amount;}
        public int itemId(){return BuiltInRegistries.ITEM.getId(display.getItem());}
        public int meta(){return display.getOrDefault(HbmHazards.LEGACY_METADATA.get(),display.getDamageValue());}
    }
    public PneumaticAccessMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,(PneumaticAccess)inventory.player.level().getBlockEntity(data.readBlockPos()));}
    public PneumaticAccessMenu(int id,Inventory inventory,PneumaticAccess machine){super(PneumaticStorageContent.ACCESS_MENU.get(),id);this.machine=machine;player=inventory.player;var projection=new SimpleContainer(GRID_SIZE);
        for(int i=0;i<GRID_SIZE;i++){final int index=i;addSlot(new Slot(projection,i,42+i%8*18,17+i/8*18){@Override public ItemStack getItem(){return player.level().isClientSide&&visible[index]!=null?visible[index].display:ItemStack.EMPTY;}@Override public void set(ItemStack stack){}@Override public boolean mayPlace(ItemStack stack){return false;}@Override public boolean mayPickup(Player who){return false;}});}
        for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,42+i%9*18,169+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,42+i*18,227));
    }
    @Override public boolean stillValid(Player who){return machine.stillValid(who);}
    @Override public void clicked(int index,int button,ClickType type,Player who){if(type==ClickType.PICKUP_ALL||index>=0&&index<GRID_SIZE)return;super.clicked(index,button,type,who);}
    @Override public boolean canDragTo(Slot slot){return slot.index>=GRID_SIZE&&super.canDragTo(slot);}
    @Override public ItemStack quickMoveStack(Player who,int index){if(index<GRID_SIZE||index>=slots.size()||who.level().isClientSide||machine.cache==null||machine.cache.hasExpired)return ItemStack.EMPTY;var slot=slots.get(index);var stack=slot.getItem();if(stack.isEmpty())return ItemStack.EMPTY;var original=stack.copy();int remaining=(int)machine.cache.addItemsAndReturnQuantity(stack,stack.getCount());stack.shrink(stack.getCount()-remaining);slot.setChanged();broadcastChanges();return original;}
    @Override public void broadcastChanges(){super.broadcastChanges();if(!(player instanceof ServerPlayer server)||machine.cache==null||machine.cache.hasExpired)return;var deltas=new ArrayList<PneumaticStoragePackets.DeltaLine>();for(var pair:machine.cache.cacheSlots.entrySet()){if(pair.getKey()==StackCache.getNullIdentity())continue;var current=pair.getValue();var old=cachedEntries.get(pair.getKey());if(old==null){old=new Entry(pair.getKey(),current.displayStack,current.stacksize);cachedEntries.put(pair.getKey(),old);deltas.add(new PneumaticStoragePackets.DeltaLine(pair.getKey(),current.stacksize,current.displayStack));}else if(old.amount!=current.stacksize){old.amount=current.stacksize;deltas.add(new PneumaticStoragePackets.DeltaLine(pair.getKey(),current.stacksize,ItemStack.EMPTY));}}
        for(int i=0;i<deltas.size();i+=48)PacketDistributor.sendToPlayer(server,new PneumaticStoragePackets.Deltas(containerId,List.copyOf(deltas.subList(i,Math.min(i+48,deltas.size())))));
    }
    public void receive(List<PneumaticStoragePackets.DeltaLine> lines){for(var line:lines){if(!line.identity().isEmpty())cachedEntries.put(line.hash(),new Entry(line.hash(),line.identity(),line.amount()));else{var entry=cachedEntries.get(line.hash());if(entry!=null)entry.amount=line.amount();}}rebuild();}
    public void rebuild(){var values=new ArrayList<Entry>();String term=search.toLowerCase(Locale.US);for(var entry:cachedEntries.values())if(entry.amount>0&&(term.isEmpty()||entry.display.getHoverName().getString().toLowerCase(Locale.US).contains(term)||detailedMatch!=null&&detailedMatch.test(entry.display)))values.add(entry);Comparator<Entry> id=Comparator.comparingInt(Entry::itemId).thenComparingInt(Entry::meta).thenComparing(Comparator.comparingLong((Entry entry)->entry.amount).reversed()).thenComparingInt(entry->entry.display.getComponentsPatch().isEmpty()?0:1);Comparator<Entry> sorter=switch(sort){case 1->id;case 2->Comparator.comparing((Entry entry)->entry.display.getHoverName().getString(),String.CASE_INSENSITIVE_ORDER).thenComparing(id);case 3->Comparator.comparing((Entry entry)->entry.display.getDescriptionId(),String.CASE_INSENSITIVE_ORDER).thenComparing(id);default->Comparator.comparingLong((Entry entry)->entry.amount).reversed().thenComparingInt(Entry::itemId).thenComparingInt(Entry::meta).thenComparingInt(entry->entry.display.getComponentsPatch().isEmpty()?0:1);};values.sort(sorter);listingSize=values.size();listingStart=Math.max(0,Math.min(listingStart,Math.max(1,(int)Math.ceil(listingSize/8D-6))));for(int i=0;i<GRID_SIZE;i++){int index=listingStart*8+i;visible[i]=index<values.size()?values.get(index):null;}}
    public void transfer(long hash,int type){
        if(player.level().isClientSide||!stillValid(player)||machine.cache==null||machine.cache.hasExpired||type<0||type>2)return;
        var held=getCarried();long heldHash=StackCache.getStackIdentity(held);var entry=cachedEntries.get(hash);
        if(type<2&&!held.isEmpty()&&heldHash!=hash){int quantity=type==0?held.getCount():1;var identity=held.copyWithCount(1);int remaining=(int)machine.cache.addItemsAndReturnQuantity(identity,quantity);held.shrink(quantity-remaining);setCarried(ItemStack.EMPTY);give(held);broadcastChanges();return;}
        if(entry==null)return;var live=machine.cache.cacheSlots.get(hash);if(live==null||live.stacksize<=0)return;
        if(type==2){int capacity=Math.min(entry.display.getMaxStackSize(),inventorySpace(entry.display));int consumed=(int)machine.cache.consumeItemsAndReturnQuantity(entry.display,Math.min(capacity,live.stacksize));give(entry.display.copyWithCount(consumed));}
        else if(held.isEmpty()||heldHash==hash){int capacity=entry.display.getMaxStackSize()-held.getCount();if(type==1)capacity=Math.min(capacity,1);if(capacity>0){int consumed=(int)machine.cache.consumeItemsAndReturnQuantity(entry.display,Math.min(capacity,live.stacksize));setCarried(entry.display.copyWithCount(held.getCount()+consumed));}}
        broadcastChanges();
    }
    private int inventorySpace(ItemStack stack){int total=0;var inventory=player.getInventory();for(int i=0;i<36;i++){var present=inventory.getItem(i);if(present.isEmpty())total+=Math.min(stack.getMaxStackSize(),inventory.getMaxStackSize());else if(ItemStack.isSameItemSameComponents(stack,present))total+=Math.max(0,Math.min(stack.getMaxStackSize(),inventory.getMaxStackSize())-present.getCount());}return total;}
    private void give(ItemStack stack){if(stack.isEmpty())return;player.getInventory().add(stack);if(!stack.isEmpty())player.drop(stack,false);}
}
