package com.hbm.machine.conveyorpackaging;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** True ItemMetaUpgrade(SPECIAL,3), stack1, no damage; invalid raw metadata keeps source name/first-icon split. */
public final class CraneMetaUpgradeItem extends Item {
    private final String sourceId;
    public CraneMetaUpgradeItem(String id){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));sourceId=id;}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static int icon(ItemStack stack){int meta=metadata(stack);return meta>=0&&meta<3?meta:0;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceId+"_"+(metadata(stack)+1)+".name");}
}
