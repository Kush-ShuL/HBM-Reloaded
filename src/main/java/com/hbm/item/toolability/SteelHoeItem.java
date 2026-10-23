package com.hbm.item.toolability;

import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Source ModHoe only: old till conditions/sound/durability, no recursion, preset GUI, modern plant mining or root/path conversion. */
public class SteelHoeItem extends HoeItem implements SourceAbilityCombat {
    public SteelHoeItem(){this(SteelTier.INSTANCE);}
    public SteelHoeItem(Tier material){super(material,new ItemToolAbility.SourceProperties());}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){return 1;}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return false;}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){return false;}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){}
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity miner){return false;}
    @Override public InteractionResult useOn(UseOnContext context){
        Player player=context.getPlayer();if(player==null)return InteractionResult.PASS;
        var level=context.getLevel();var pos=context.getClickedPos();var stack=context.getItemInHand();var face=context.getClickedFace();
        if(!player.mayUseItemAt(pos,face,stack))return InteractionResult.PASS;
        var state=level.getBlockState(pos);var above=level.getBlockState(pos.above());
        boolean sourceSoil=state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.DIRT)||state.is(Blocks.COARSE_DIRT)||state.is(Blocks.PODZOL);
        BlockState result=face!=Direction.DOWN&&(above.isAir()||LegacyMaterials.of(above).name().equals("air"))&&sourceSoil?Blocks.FARMLAND.defaultBlockState():null;
        // Modern counterpart to original Forge UseHoeEvent, preserving source default result and actual cancellation/override.
        var event=new BlockEvent.BlockToolModificationEvent(state,context,ItemAbilities.HOE_TILL,false);event.setFinalState(result);NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()||event.getFinalState()==null)return InteractionResult.PASS;
        var sound=Blocks.FARMLAND.defaultBlockState().getSoundType(level,pos,player);
        level.playSound(player,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,sound.getBreakSound(),SoundSource.BLOCKS,(sound.getVolume()+1)/2,sound.getPitch()*.8F);
        if(!level.isClientSide){level.setBlock(pos,event.getFinalState(),3);stack.hurtAndBreak(1,player,LivingEntity.getSlotForHand(context.getHand()));}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
