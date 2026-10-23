package com.hbm.machine.custommachine;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source BlockEnumMulti subtype name and true metadata, not one item per material surrogate. */
public final class CMComponentItem extends BlockItem {
    private final CMComponentBlock.Kind kind;
    public CMComponentItem(CMComponentBlock block){super(block,properties(block.kind));kind=block.kind;}
    private static Properties properties(CMComponentBlock.Kind kind){var result=new Properties();return kind.variants.length>1?result.component(HbmHazards.LEGACY_METADATA.get(),0):result;}
    @Override public Component getName(ItemStack stack){int metadata=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return Component.translatable("tile."+kind.id+(kind.variants.length>1?"."+kind.variants[Math.abs(metadata%kind.variants.length)]:"")+".name");}
}
