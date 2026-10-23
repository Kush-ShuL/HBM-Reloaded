package com.hbm.machine.siren;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Real subtype item: one cassette per stack, base icon plus source color overlay. */
public final class CassetteItem extends Item {
    public CassetteItem(){super(new Properties().stacksTo(1).component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public static SirenTrack track(ItemStack stack){return !stack.isEmpty()&&stack.getItem() instanceof CassetteItem?SirenTrack.getEnum(metadata(stack)):SirenTrack.NULL;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.siren_track.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var track=track(stack);lines.add(Component.translatable("tooltip.hbm.cassette.header"));lines.add(Component.translatable("tooltip.hbm.cassette.name",track.title()));lines.add(Component.translatable("tooltip.hbm.cassette.type",track.typeName()));lines.add(Component.translatable("tooltip.hbm.cassette.volume",track.volume));}
}
