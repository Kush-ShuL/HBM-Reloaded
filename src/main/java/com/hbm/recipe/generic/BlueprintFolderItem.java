package com.hbm.recipe.generic;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class BlueprintFolderItem extends Item {
    public BlueprintFolderItem(){super(new Properties().stacksTo(1).component(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.blueprint_folder.name");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(level.isClientSide)return InteractionResultHolder.pass(stack);
        int meta=Blueprints.metadata(stack);
        String prefix=meta==0?Blueprints.ALT:meta==1?Blueprints.DISCOVER:meta==2?Blueprints.SECRET:null;
        if(prefix==null)return InteractionResultHolder.pass(stack);
        var pools=Blueprints.pools().keySet().stream().filter(pool->pool.startsWith(prefix)).toList();
        if(pools.isEmpty())return InteractionResultHolder.pass(stack);
        var blueprint=Blueprints.make(pools.get(player.getRandom().nextInt(pools.size())));
        // Keep the hand occupied during insertion. Otherwise add() puts the
        // blueprint in the just-emptied hand, then ServerPlayerGameMode replaces
        // it with this use result (or restores the folder in creative mode).
        if(!player.getInventory().add(blueprint))player.drop(blueprint,false);
        stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }
}
