package com.hbm.item.artilleryremote;

import com.hbm.machine.turret.greg.ArtilleryTurretBase;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

/** Actual ItemDesignatorArtyRange: a core-coordinate link and the existing artillery target queue. */
public final class ArtilleryRemoteItem extends Item {
    public ArtilleryRemoteItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        if(player==null||!(level.getBlockState(pos).getBlock() instanceof DummyableBlock block))return InteractionResult.PASS;
        var core=block.findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof ArtilleryTurretBase))return InteractionResult.PASS;
        if(!level.isClientSide){CustomData.update(DataComponents.CUSTOM_DATA,context.getItemInHand(),tag->{tag.putInt("x",core.getX());tag.putInt("y",core.getY());tag.putInt("z",core.getZ());});sound(level,player,"item.techBleep");}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!stack.has(DataComponents.CUSTOM_DATA))return InteractionResultHolder.pass(stack);
        var eye=player.getEyePosition(1F);var end=eye.add(player.getViewVector(1F).scale(500D));
        // Source Library.rayTrace flags false,false,true: outlines including noncolliding blocks, no fluid hit, retain MISS coordinates.
        var hit=level.clip(new ClipContext(eye,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,player));var target=hit.getBlockPos();
        if(!level.isClientSide){var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();var linked=new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
            if(level.getBlockEntity(linked) instanceof ArtilleryTurretBase artillery){artillery.enqueueTarget(target.getX()+.5D,target.getY()+.5D,target.getZ()+.5D);sound(level,player,"item.techBoop");}}
        // No source use duration, swing, cooldown or consumption; consume prevents a second offhand activation.
        return InteractionResultHolder.consume(stack);
    }
    private static void sound(Level level,Player player,String name){level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(name),SoundSource.PLAYERS,1F,1F);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(!stack.has(DataComponents.CUSTOM_DATA))tooltip.add(Component.translatable("tooltip.hbm.artillery_remote.unlinked").withStyle(ChatFormatting.RED));
        else{var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();tooltip.add(Component.translatable("tooltip.hbm.artillery_remote.linked",tag.getInt("x"),tag.getInt("y"),tag.getInt("z")).withStyle(ChatFormatting.YELLOW));}
    }
}
