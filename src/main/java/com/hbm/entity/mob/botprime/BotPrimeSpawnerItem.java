package com.hbm.entity.mob.botprime;
import com.hbm.item.spawn.LegacyChopperSpawnerItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source ItemChopper WORM branch and all5 original lore lines,now translatable. */
public final class BotPrimeSpawnerItem extends LegacyChopperSpawnerItem {
 public BotPrimeSpawnerItem(){super(BotPrimeContent.HEAD,1);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){for(int i=0;i<5;i++)lines.add(i==2?Component.empty():Component.translatable("desc.spawn_worm."+i));}
}
