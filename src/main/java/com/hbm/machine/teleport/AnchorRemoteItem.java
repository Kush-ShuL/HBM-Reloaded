package com.hbm.machine.teleport;

import com.hbm.item.battery.BatteryItem;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Source ItemAnchorRemote: actual chargeable HE battery, same-dimension anchor lookup and pre-teleport entity-only blast. */
public final class AnchorRemoteItem extends BatteryItem {
    public AnchorRemoteItem(){super(1_000_000,10_000,0);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.anchor_remote.name");}
    @Override public InteractionResult useOn(UseOnContext context){
        if(!context.getLevel().getBlockState(context.getClickedPos()).is(TeleportContent.ANCHOR.get()))return InteractionResult.PASS;
        var stack=context.getItemInHand();var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();var pos=context.getClickedPos();
        tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());
        // Deliberately no dimension or synthetic charge initialization; binding an untagged battery therefore leaves charge0.
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack stack=player.getItemInHand(hand);
        if(player.isShiftKeyDown()||level.isClientSide)return InteractionResultHolder.pass(stack);
        var data=stack.get(DataComponents.CUSTOM_DATA);
        if(data==null||getCharge(stack)<10_000){failure(level,player);return InteractionResultHolder.consume(stack);}
        var tag=data.copyTag();int x=tag.getInt("x"),y=tag.getInt("y"),z=tag.getInt("z");
        level.getChunk(x>>4,z>>4);
        if(!level.getBlockState(new net.minecraft.core.BlockPos(x,y,z)).is(TeleportContent.ANCHOR.get())){failure(level,player);return InteractionResultHolder.consume(stack);}
        if(player.isPassenger())player.stopRiding();
        level.explode(player,x+.5,y+1+player.getBbHeight()/2,z+.5,2F,false,Level.ExplosionInteraction.NONE);
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.ENDERMAN_TELEPORT,SoundSource.PLAYERS,1F,1F);
        if(player instanceof ServerPlayer server)server.connection.teleport(x+.5,y+1,z+.5,player.getYRot(),player.getXRot());
        else player.teleportTo(x+.5,y+1,z+.5);
        player.fallDistance=0;
        for(int i=0;i<32;i++)level.addParticle(ParticleTypes.PORTAL,player.getX(),player.getY()+player.getRandom().nextDouble()*2,player.getZ(),player.getRandom().nextGaussian(),0,player.getRandom().nextGaussian());
        dischargeBattery(stack,10_000);
        return InteractionResultHolder.consume(stack);
    }
    private static void failure(Level level,Player player){level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.EXPERIENCE_ORB_PICKUP,SoundSource.PLAYERS,.25F,.75F);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        long charge=stack.has(DataComponents.CUSTOM_DATA)?getCharge(stack):1_000_000;
        tooltip.add(Component.translatable("tooltip.hbm.battery.energy_stored",shortNumber(charge),shortNumber(1_000_000)));
        tooltip.add(Component.translatable("tooltip.hbm.battery.charge_rate",shortNumber(10_000)));
    }
}
