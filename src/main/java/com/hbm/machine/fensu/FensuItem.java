package com.hbm.machine.fensu;

import java.math.BigInteger;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** IPersistentInfoProvider's actual saved unbounded HE, with no narrowing to a fixed machine capacity. */
public final class FensuItem extends BlockItem {
    public FensuItem(FensuBlock block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompound("persistent");if(data.contains("power")){byte[] bytes=data.getByteArray("power");if(bytes.length>0)lines.add(Component.literal(FensuNumbers.format(new BigInteger(bytes))+" HE").withStyle(ChatFormatting.YELLOW));}}
}
