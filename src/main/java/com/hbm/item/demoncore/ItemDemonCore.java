package com.hbm.item.demoncore;

import com.hbm.item.tool.ToolingContent;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** X5846 suspended core: the screwdriver slips only when the actual drop is grounded. */
public final class ItemDemonCore extends Item {
    public ItemDemonCore(){super(new Properties());}
    @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){
        if(entity!=null&&!entity.level().isClientSide&&entity.onGround()){
            // Source intentionally replaces the entire old stack with one fresh
            // closed core, discarding old count/components. Do not preserve them.
            entity.setItem(new ItemStack(HbmContent.item("demon_core_closed")));
            entity.level().addFreshEntity(new ItemEntity(entity.level(),entity.getX(),entity.getY(),entity.getZ(),new ItemStack(ToolingContent.SCREWDRIVER.get())));
            return true;
        }
        return false;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        lines.add(Component.literal("[").append(Component.translatable("trait.drop")).append("]").withStyle(ChatFormatting.RED));
    }
}
