package com.hbm.machine.reactor.rbmk.residue;

import com.hbm.machine.reactor.rbmk.core.RbmkNeutronField;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;

/** Original ItemRBMKLid; ordinary/glass cover consumes one even in creative, exactly as the source. */
public final class RbmkLidItem extends Item {
    public final boolean glass;
    public RbmkLidItem(boolean glass) { super(new Properties()); this.glass = glass; }
    @Override public InteractionResult useOn(UseOnContext context) {
        var world = context.getLevel(); var clicked = context.getClickedPos();
        if (!(world.getBlockState(clicked).getBlock() instanceof RbmkLidTarget block)) return InteractionResult.PASS;
        var column = block.rbmkCore(world, clicked);
        if (column == null || column.hasLid()) return InteractionResult.PASS;
        RbmkNeutronField.lid(world, column.corePosition(), true);
        var sound = glass ? SoundType.GLASS : SoundType.STONE; // Original concrete_smooth inherits the stone step sound.
        world.playSound(context.getPlayer(), clicked, sound.getStepSound(), SoundSource.BLOCKS, (sound.getVolume() + 1F) / 2F, sound.getPitch() * .8F);
        column.installLid(glass);
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
