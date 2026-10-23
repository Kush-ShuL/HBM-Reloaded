package com.hbm.railway.cart;
import com.hbm.machine.storage.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.ClickType;
/** Source steel crate slots/routing stay in the sole StorageCrateMenu. Only the provider is this real cart. */
public final class CrateCartMenu extends StorageCrateMenu {
    private static Container read(Inventory inventory,RegistryFriendlyByteBuf data){var entity=inventory.player.level().getEntity(data.readInt());return entity instanceof SourceCrateCart crate?crate:new SimpleContainer(54);}
    public CrateCartMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,read(inventory,data));}
    public CrateCartMenu(int id,Inventory inventory,Container container){super(SourceCartContent.CRATE_MENU.get(),id,inventory,container,StorageCrateKind.STEEL,null);}
    @Override public void clicked(int slot,int button,ClickType type,Player player){
        // Source ContainerCrateBase protects a held storage crate whenever the backing inventory is not a TileEntity.
        if(player.getMainHandItem().getItem() instanceof StorageCrateItem&&(slot==54+27+player.getInventory().selected||type==ClickType.SWAP&&button==player.getInventory().selected))return;
        super.clicked(slot,button,type,player);
    }
}
