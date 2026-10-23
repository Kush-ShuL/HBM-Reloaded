package com.hbm.item.casingbag;

import com.hbm.hazard.HbmHazards;
import com.hbm.machine.storage.CrateStackData;
import com.hbm.weapon.sedna.gun.CasingRecovery;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Actual ItemCasingBag. The existing unique magazine recovery hook calls this item directly. */
public final class CasingBagItem extends Item implements CasingRecovery {
    public CasingBagItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.casing_bag.name"); }
    @Override public int getUseDuration(ItemStack stack,LivingEntity holder) { return 1; }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        var stack=player.getItemInHand(hand);
        if(player instanceof ServerPlayer server) server.openMenu(new SimpleMenuProvider(
                (id,inventory,viewer)->new CasingBagMenu(id,inventory,hand),
                stack.has(DataComponents.CUSTOM_NAME)?stack.getHoverName():Component.translatable("container.casingBag")),
                buffer->buffer.writeEnum(hand));
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }

    public static int sourceDamage(ItemStack stack) {
        if(stack.getItem() instanceof com.hbm.item.casing.FlatCasingItem item) return item.sourceOrdinal();
        return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());
    }
    /** Modern enum naming is the exact source name plus namespace; retain the original NBT key spelling. */
    public static String recoveryKey(ItemStack casing) {
        if(casing.getItem() instanceof com.hbm.item.casing.FlatCasingItem item) return item.sourceRecoveryKey();
        String name=casing.getItem().getDescriptionId(casing);
        if(name.startsWith("item.hbm.")) name="item."+name.substring("item.hbm.".length());
        else if(name.startsWith("block.hbm.")) name="tile."+name.substring("block.hbm.".length());
        return name+"@"+sourceDamage(casing);
    }
    private static float recovered(ItemStack bag,String key) { return CrateStackData.data(bag).getFloat(key); }
    private static void recovered(ItemStack bag,String key,float value) {
        // Read the current component so a preceding inventory save is never overwritten by an old tag snapshot.
        var tag=CrateStackData.data(bag);tag.putFloat(key,value);CrateStackData.data(bag,tag);
    }

    /** Literal original deterministic fractional accumulation; not a new randomized drop probability. */
    @Override public boolean pushCasing(ItemStack bag,ItemStack casing,float amount,HolderLookup.Provider lookup) {
        if(!bag.has(DataComponents.CUSTOM_DATA)) bag.set(DataComponents.CUSTOM_DATA,CustomData.EMPTY);
        String name=recoveryKey(casing);
        boolean result=false;
        if(recovered(bag,name)<1F) {
            result=true;
            recovered(bag,name,recovered(bag,name)+amount);
        }
        if(recovered(bag,name)>=1F) {
            var inventory=new CasingBagInventory(bag,lookup);
            ItemStack toAdd=casing.copy(); // SOURCE QUIRK: only once, outside the while loop; do not reset its count.
            while(recovered(bag,name)>=1F) {
                boolean didSomething=false;
                for(int i=0;i<inventory.getContainerSize();i++) {
                    if(toAdd.getCount()<=0) break;
                    var slot=inventory.getItem(i);
                    if(!slot.isEmpty() && slot.getItem()==toAdd.getItem() && sourceDamage(slot)==sourceDamage(toAdd)) {
                        int transfer=Math.min(toAdd.getCount(),slot.getMaxStackSize()-slot.getCount());
                        // Keep both operations even when slot and toAdd are the SAME source reference.
                        toAdd.shrink(transfer);
                        slot.grow(transfer);
                        if(transfer>0) didSomething=true;
                    }
                }
                for(int i=0;i<inventory.getContainerSize();i++) {
                    if(toAdd.getCount()<=0) break;
                    if(inventory.getItem(i).isEmpty()) {
                        inventory.setItem(i,toAdd); // source stores this reference, not a fresh copy and not a consumed split
                        didSomething=true;
                        break;
                    }
                }
                if(didSomething) {
                    recovered(bag,name,recovered(bag,name)-1F);
                    result=true;
                } else break;
            }
            inventory.setChanged();
        }
        return result;
    }
}
