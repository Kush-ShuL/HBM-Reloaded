package com.hbm.item.structurewand;

import com.hbm.world.structure.StructureAuthoringJobs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** ItemWandS: two clicked corners create the real editable save controller one block below the area. */
public final class StructureWandItem extends Item {
    public StructureWandItem(){super(new Properties().stacksTo(1));}
    private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();if(player==null)return InteractionResult.PASS;var level=context.getLevel();var pos=context.getClickedPos();var stack=context.getItemInHand();var tag=data(stack);
        if(player.isShiftKeyDown()){
            try{var key=StructureBlacklist.capture(level.getBlockState(pos));var keys=StructureBlacklist.read(tag);boolean removed=keys.remove(key);if(!removed)keys.add(key);StructureBlacklist.write(tag,keys);if(level.isClientSide)player.displayClientMessage(Component.translatable(removed?"message.hbm.structure_wand.removed":"message.hbm.structure_wand.added",key.actual().getName()),false);}catch(IllegalArgumentException error){if(level.isClientSide)player.displayClientMessage(Component.literal(error.getMessage()).withStyle(ChatFormatting.RED),false);}
        }else{
            int x=tag.getInt("x"),y=tag.getInt("y"),z=tag.getInt("z");
            if(x==0&&y==0&&z==0){tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());if(level.isClientSide)player.displayClientMessage(Component.translatable("message.hbm.structure_wand.first"),false);}
            else{
                tag.putInt("x",0);tag.putInt("y",0);tag.putInt("z",0); // Clear the source selection before any asynchronous world write.
                if(level instanceof ServerLevel server){var origin=new BlockPos(Math.min(x,pos.getX()),Math.min(y,pos.getY())-1,Math.min(z,pos.getZ()));StructureAuthoringJobs.configure(server,player.getUUID(),origin,Math.abs(x-pos.getX())+1,Math.abs(y-pos.getY())+1,Math.abs(z-pos.getZ())+1,StructureBlacklist.read(tag));}
            }
        }
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);var tag=data(stack);if(player.isShiftKeyDown()){StructureBlacklist.write(tag,java.util.Set.of());if(level.isClientSide)player.displayClientMessage(Component.translatable("message.hbm.structure_wand.cleared"),false);}stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        for(int i=0;i<4;i++)lines.add(Component.translatable("tooltip.hbm.structure_wand."+i));
        if(!stack.has(DataComponents.CUSTOM_DATA))return;var tag=data(stack);int x=tag.getInt("x"),y=tag.getInt("y"),z=tag.getInt("z");lines.add(Component.translatable(x!=0||y!=0||z!=0?"tooltip.hbm.structure_wand.from":"tooltip.hbm.structure_wand.no_start",x,y,z).withStyle(ChatFormatting.AQUA));
        var keys=StructureBlacklist.read(tag);if(!keys.isEmpty()){lines.add(Component.translatable("tooltip.hbm.structure_wand.blacklist"));for(var key:keys)lines.add(Component.literal("- ").append(key.actual().getName()).withStyle(ChatFormatting.RED));}
    }
}
