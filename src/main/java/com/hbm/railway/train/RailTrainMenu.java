package com.hbm.railway.train;
import com.hbm.item.battery.HbmBattery;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source29/45topologies, with each real MenuType carrying its own topology and provider-written entityID. */
public final class RailTrainMenu extends AbstractContainerMenu {
    public final boolean trailer;private final Container inventory;public final int entityId;private final Player player;
    public RailTrainMenu(int id,Inventory player,RegistryFriendlyByteBuf data,boolean trailer){this(id,player,read(player,data,trailer),trailer);}
    private record ClientInit(Container inventory,int entityId){}
    private static ClientInit read(Inventory player,RegistryFriendlyByteBuf data,boolean trailer){int entity=data.readInt();var actual=player.player.level().getEntity(entity);return new ClientInit(actual instanceof RailCarEntity train&&train.trailer==trailer?train:new SimpleContainer(trailer?45:29),entity);}
    private RailTrainMenu(int id,Inventory player,ClientInit init,boolean trailer){this(id,player,init.inventory,init.entityId,trailer);}
    public RailTrainMenu(int id,Inventory player,RailCarEntity train){this(id,player,train,train.getId(),train.trailer);}
    private RailTrainMenu(int id,Inventory player,Container inventory,int entityId,boolean trailer){super(trailer?RailTrainContent.TRAILER_MENU.get():RailTrainContent.TRAM_MENU.get(),id);this.inventory=inventory;this.trailer=trailer;this.entityId=entityId;this.player=player.player;
        int rows=trailer?5:4,columns=trailer?9:7;
        for(int y=0;y<rows;y++)for(int x=0;x<columns;x++)addSlot(new Slot(inventory,y*columns+x,8+x*18,18+y*18));
        if(!trailer)addSlot(new Slot(inventory,28,152,72)); // Source ordinary Slot accepts manual non-battery items too.
        for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(player,9+y*9+x,8+x*18,(trailer?140:122)+y*18));
        for(int x=0;x<9;x++)addSlot(new Slot(player,x,8+x*18,trailer?198:180));
    }
    public RailCarEntity entity(){var entity=player.level().getEntity(entityId);return entity instanceof RailCarEntity train&&train.trailer==trailer?train:null;}
    @Override public boolean stillValid(Player player){return inventory.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var item=slot.getItem();var result=item.copy();int count=trailer?45:29;if(index<count){if(!moveItemStackTo(item,count,slots.size(),true))return ItemStack.EMPTY;}else if(!trailer&&result.getItem() instanceof HbmBattery){if(!moveItemStackTo(item,28,29,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(item,0,trailer?45:28,false))return ItemStack.EMPTY;if(item.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return result;}
}
