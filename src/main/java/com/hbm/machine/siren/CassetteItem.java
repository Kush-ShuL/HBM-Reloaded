package com.hbm.machine.siren;

import java.util.List;
import net.minecraft.network.chat.Component;
import java.util.Objects;
import net.minecraft.world.item.*;

/** One immutable source track per cassette; original base icon and tinted overlay. */
public final class CassetteItem extends Item {
    private final SirenTrack track;
    public CassetteItem(SirenTrack track){super(new Properties().stacksTo(1));this.track=Objects.requireNonNull(track);}
    public SirenTrack track(){return track;}
    public static SirenTrack track(ItemStack stack){return !stack.isEmpty()&&stack.getItem() instanceof CassetteItem cassette?cassette.track:SirenTrack.NULL;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var track=track(stack);lines.add(Component.translatable("tooltip.hbm.cassette.header"));lines.add(Component.translatable("tooltip.hbm.cassette.name",track.title()));lines.add(Component.translatable("tooltip.hbm.cassette.type",track.typeName()));lines.add(Component.translatable("tooltip.hbm.cassette.volume",track.volume));}
}
