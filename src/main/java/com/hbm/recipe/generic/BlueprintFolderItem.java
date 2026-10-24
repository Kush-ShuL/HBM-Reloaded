package com.hbm.recipe.generic;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class BlueprintFolderItem extends Item {
    public enum Kind {
        ALT(Blueprints.ALT),DISCOVER(Blueprints.DISCOVER),SECRET(Blueprints.SECRET);
        private final String prefix;
        Kind(String prefix){this.prefix=prefix;}
    }
    private final Kind kind;
    public BlueprintFolderItem(Kind kind){super(new Properties().stacksTo(1));this.kind=java.util.Objects.requireNonNull(kind);}
    public Kind kind(){return kind;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(level.isClientSide)return InteractionResultHolder.pass(stack);
        String prefix=kind.prefix;
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
