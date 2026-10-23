package com.hbm.armor;

import com.hbm.registry.HbmSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** ItemFilter's original swap/install action, count1 and maxDamage20000. */
public final class GasMaskFilterItem extends Item {
    public GasMaskFilterItem(){super(new Properties().durability(20000));}
    @Override public int getDamage(ItemStack stack){return stack.getOrDefault(DataComponents.DAMAGE,0);}
    @Override public void setDamage(ItemStack stack,int damage){stack.set(DataComponents.DAMAGE,Math.max(0,damage));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack filter=player.getItemInHand(hand),helmet=player.getItemBySlot(EquipmentSlot.HEAD);
        if(helmet.isEmpty())return InteractionResultHolder.pass(filter);
        if(!(helmet.getItem() instanceof GasMask)&&ArmorModStorage.hasMods(helmet)){
            ItemStack attachment=ArmorModStorage.pryMod(helmet,ArmorModStorage.HELMET,level.registryAccess());
            if(!attachment.isEmpty()){
                ItemStack result=install(attachment,filter,level,player);ArmorModStorage.applyMod(helmet,attachment,level.registryAccess());return InteractionResultHolder.sidedSuccess(result,level.isClientSide);
            }
        }
        if(!(helmet.getItem() instanceof GasMask))return InteractionResultHolder.pass(filter);
        return InteractionResultHolder.sidedSuccess(install(helmet,filter,level,player),level.isClientSide);
    }
    private static ItemStack install(ItemStack helmet,ItemStack filter,Level level,Player player){
        if(!(helmet.getItem() instanceof GasMask mask)||!mask.acceptsFilter(helmet,player,filter))return filter;
        ItemStack copy=filter.copy(),current=GasMaskFilters.filter(helmet,level.registryAccess());
        if(current.isEmpty())filter.setCount(0);else filter=current;
        GasMaskFilters.install(helmet,copy,level.registryAccess());
        level.playSound(player,player.blockPosition(),HbmSounds.get("item.gasmaskScrew"),SoundSource.PLAYERS,1,1);
        return filter;
    }
}
