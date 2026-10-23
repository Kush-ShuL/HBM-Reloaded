package com.hbm.recipe.generic;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original paper-copying semantics, including consuming paper and dropping a copy in creative mode. */
public final class BlueprintItem extends Item {
    public BlueprintItem(){super(new Properties());}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.blueprints.name");}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(level.isClientSide)return InteractionResultHolder.pass(stack);if(!stack.has(DataComponents.CUSTOM_DATA))return InteractionResultHolder.pass(stack);String pool=Blueprints.pool(stack);if(pool==null||pool.startsWith(Blueprints.SECRET))return InteractionResultHolder.pass(stack);int paper=-1;for(int i=0;i<player.getInventory().getContainerSize();i++)if(player.getInventory().getItem(i).is(Items.PAPER)){paper=i;break;}if(paper<0)return InteractionResultHolder.pass(stack);player.getInventory().getItem(paper).shrink(1);player.swing(hand,true);var copy=stack.copyWithCount(1);if(!player.getAbilities().instabuild){if(stack.getCount()<stack.getMaxStackSize()){stack.grow(1);return InteractionResultHolder.success(stack);}if(!player.getInventory().add(copy))player.drop(copy,false);player.inventoryMenu.broadcastChanges();}else player.drop(copy,false);return InteractionResultHolder.success(stack);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){String pool=Blueprints.pool(stack);if(pool==null)return;var names=Blueprints.pools().get(pool);if(names==null||names.isEmpty())return;tooltip.add(Component.translatable(pool.startsWith(Blueprints.SECRET)?"tooltip.hbm.blueprint.secret":"tooltip.hbm.blueprint.copy").withStyle(pool.startsWith(Blueprints.SECRET)?ChatFormatting.RED:ChatFormatting.YELLOW));for(String name:names){var recipe=Blueprints.recipe(name);tooltip.add(recipe==null?Component.translatable(name):recipe.displayName());}}
}
