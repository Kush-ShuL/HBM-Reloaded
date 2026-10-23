package com.hbm.weapon.repairkits;

import com.hbm.armor.LegacyArmorDurabilityRules;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

/** Complete source ItemRepairKit: one use repairs all9 hotbar slots/all modified configs by25% of their actual maximum. */
public final class ItemRepairKit extends Item {
    public final int sourceUses;
    private final String sound;
    public ItemRepairKit(int uses,String sound){super(new Properties().durability(uses-1));this.sourceUses=uses;this.sound=sound;}
    @Override public InteractionResultHolder<ItemStack> use(Level world,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!(world instanceof ServerLevel server))return InteractionResultHolder.pass(stack);
        boolean repaired=false;
        for(int i=0;i<9;i++){
            var item=player.getInventory().getItem(i);if(!(item.getItem() instanceof ItemGunBaseNT gun))continue;
            for(int index=0;index<gun.getConfigCount();index++){
                var config=gun.getConfig(item,index);float maximum=config.getDurability(item);
                float wear=Math.min(ItemGunBaseNT.getWear(item,index),maximum);
                if(wear>0){ItemGunBaseNT.setWear(item,index,Math.max(0F,ItemGunBaseNT.getWear(item,index)-maximum*.25F));repaired=true;}
            }
        }
        if(repaired){world.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(sound),SoundSource.PLAYERS,1,1);wearOnce(server,player,stack,hand);}
        return InteractionResultHolder.pass(stack);
    }
    private void wearOnce(ServerLevel world,Player player,ItemStack stack,InteractionHand hand){
        if(player.getAbilities().instabuild||!stack.isDamageableItem())return;
        int consumed=LegacyArmorDurabilityRules.damageAfterUnbreaking(world,stack,1,player.getRandom());
        // Compute strict source >max BEFORE modern setDamageValue clamps to MAX_DAMAGE.
        int next=stack.getDamageValue()+consumed;
        if(next>stack.getMaxDamage()){
            var before=stack.copy();player.onEquippedItemBroken(this,LivingEntity.getSlotForHand(hand));stack.shrink(1);
            player.awardStat(Stats.ITEM_BROKEN.get(this));EventHooks.onPlayerDestroyItem(player,before,hand);stack.setDamageValue(0);
        }else stack.setDamageValue(next);
    }
}
