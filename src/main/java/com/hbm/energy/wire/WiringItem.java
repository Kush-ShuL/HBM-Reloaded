package com.hbm.energy.wire;

import com.hbm.block.pylon.PylonBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

/** X5846 ItemWiring, including unlimited use, no dimension key and two-click x/y/z data. */
public final class WiringItem extends Item {
    public WiringItem() { super(new Properties()); }
    public static BlockPos start(ItemStack stack) {
        var data=stack.get(DataComponents.CUSTOM_DATA);
        if(data==null) return null;
        var tag=data.copyTag(); return new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        var player=context.getPlayer(); var level=context.getLevel(); BlockPos pos=context.getClickedPos(); var stack=context.getItemInHand();
        if(player==null||player.isShiftKeyDown()) return InteractionResult.PASS;
        if(level.getBlockState(pos).getBlock() instanceof DummyableBlock block) { BlockPos core=block.findCore(level,pos); if(core!=null) pos=core; }
        if(!(level.getBlockEntity(pos) instanceof PylonBlockEntity second)) return InteractionResult.PASS;
        if(level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos previous=start(stack);
        if(previous==null) {
            var tag=new net.minecraft.nbt.CompoundTag(); tag.putInt("x",pos.getX()); tag.putInt("y",pos.getY()); tag.putInt("z",pos.getZ()); stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
            player.displayClientMessage(Component.literal("Wire start"),false);
        } else {
            if(level.getBlockEntity(previous) instanceof PylonBlockEntity first) {
                int status=PylonBlockEntity.canLink(first,second);
                if(status==0) { first.addConnection(pos); second.addConnection(previous); }
                player.displayClientMessage(Component.literal(switch(status) { case 0->"Wire end"; case 1->"Wire error - Pylons are not the same type"; case 2->"Wire error - Cannot connect to the same pylon"; default->"Wire error - Pylon is too far away"; }),false);
            } else player.displayClientMessage(Component.literal("Wire error"),false);
            stack.remove(DataComponents.CUSTOM_DATA);
        }
        player.swing(context.getHand(),true); return InteractionResult.CONSUME;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags) {
        BlockPos pos=start(stack);
        if(pos==null) tooltip.add(Component.literal("Right-click poles to connect"));
        else { tooltip.add(Component.literal("Wire start x: "+pos.getX())); tooltip.add(Component.literal("Wire start y: "+pos.getY())); tooltip.add(Component.literal("Wire start z: "+pos.getZ())); }
    }
}
