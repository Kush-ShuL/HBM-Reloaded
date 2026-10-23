package com.hbm.railway.cart;
import com.hbm.machine.storage.*;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
public final class SourceCrateCart extends SourceInventoryCart {
    public SourceCrateCart(EntityType<?> type,Level level){super(type,level,SourceCartKind.CRATE,54);}
    public void loadSourcePayload(ItemStack stack){CrateStackData.loadSlots(CrateStackData.data(stack),this,registryAccess());}
    @Override public ItemStack getCartItem(){return SourceCartItem.stack(SourceCartKind.Base.VANILLA,SourceCartKind.CRATE);}
    @Override public BlockState getDefaultDisplayBlockState(){return StorageCrateContent.BLOCK.get(StorageCrateKind.STEEL).get().defaultBlockState();}
    @Override public InteractionResult interact(Player player,InteractionHand hand){if(player instanceof ServerPlayer server)server.openMenu(this);return InteractionResult.sidedSuccess(level().isClientSide);}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CrateCartMenu(id,inventory,this);}
    @Override protected void destroy(DamageSource source){
        discard();var result=getCartItem();var payload=new CompoundTag();CrateStackData.saveSlots(payload,this,registryAccess());
        if(!payload.isEmpty())CrateStackData.data(result,payload);
        if(hasCustomName())result.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,getCustomName());
        // Source empty nbt cannot exceed6000. With slots it aliases the cart's full tag, including custom name.
        try{if(!payload.isEmpty()&&CrateStackData.compressedSize(result,registryAccess())>6000){level().explode(this,getX(),getY(),getZ(),2F,true,Level.ExplosionInteraction.BLOCK);spawnAtLocation(getCartItem(),0);}}catch(IOException ignored){}
        // Source quirk: even after the capacity explosion/extra empty drop, still drop the packed cart.
        spawnAtLocation(result,0);
    }
}
