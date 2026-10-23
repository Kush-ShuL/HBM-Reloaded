package com.hbm.machine.reactor.rbmk.console;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.context.UseOnContext;
/** Original real RBMK linker: stores source core coordinates and links any actual console/crane/display role. */
public final class RbmkLinkerItem extends Item {
    public RbmkLinkerItem(){super(new Properties().stacksTo(1).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,2,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).build()));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.rbmk_tool.name");}
    @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();var pos=context.getClickedPos();var block=level.getBlockState(pos).getBlock();var stack=context.getItemInHand();var player=context.getPlayer();if(player==null)return InteractionResult.PASS;if(block instanceof RbmkColumnBlock column){var core=column.findCore(level,pos);if(core!=null&&!level.isClientSide){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putInt("posX",core.getX());tag.putInt("posY",core.getY());tag.putInt("posZ",core.getZ());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));player.displayClientMessage(Component.translatable("item.rbmk_tool.linked").withStyle(ChatFormatting.YELLOW),false);}return InteractionResult.sidedSuccess(level.isClientSide);}
        if(stack.has(DataComponents.CUSTOM_DATA)){var core=block instanceof DummyableBlock dummy?dummy.findCore(level,pos):pos;if(core!=null&&level.getBlockEntity(core) instanceof RbmkTargetConsole console){if(!level.isClientSide){var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();console.setTarget(new BlockPos(tag.getInt("posX"),tag.getInt("posY"),tag.getInt("posZ")));player.displayClientMessage(Component.translatable("item.rbmk_tool.set").withStyle(ChatFormatting.YELLOW),false);}return InteractionResult.sidedSuccess(level.isClientSide);}}return InteractionResult.PASS;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(String line:Component.translatable("item.rbmk_tool.desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}
}
