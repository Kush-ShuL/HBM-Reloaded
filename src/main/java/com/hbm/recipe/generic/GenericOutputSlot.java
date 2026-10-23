package com.hbm.recipe.generic;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
/** Actual SlotCraftingOutput order and count; achievement definitions can bind once without replacing machine slots. */
public final class GenericOutputSlot extends Slot {
    private static final CopyOnWriteArrayList<BiConsumer<Player,ItemStack>> ACHIEVEMENTS=new CopyOnWriteArrayList<>();
    private final Player owner;private int craftBuffer;
    public GenericOutputSlot(Player owner,Container container,int slot,int x,int y){super(container,slot,x,y);this.owner=owner;}
    public static void registerAchievementHook(BiConsumer<Player,ItemStack> hook){ACHIEVEMENTS.add(hook);}
    public static void checkAchievements(Player player,ItemStack stack){ACHIEVEMENTS.forEach(hook->hook.accept(player,stack));}
    @Override public boolean mayPlace(ItemStack stack){return false;}
    @Override public ItemStack remove(int amount){if(hasItem())craftBuffer+=Math.min(amount,getItem().getCount());return super.remove(amount);}
    private void crafted(ItemStack stack){stack.onCraftedBy(owner.level(),owner,craftBuffer);checkAchievements(owner,stack);craftBuffer=0;}
    @Override protected void onQuickCraft(ItemStack stack,int count){craftBuffer+=count;crafted(stack);}
    @Override public void onTake(Player player,ItemStack stack){crafted(stack);super.onTake(player,stack);}
}
