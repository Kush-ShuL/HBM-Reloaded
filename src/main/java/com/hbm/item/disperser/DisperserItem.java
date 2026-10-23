package com.hbm.item.disperser;

import com.hbm.entity.grenade.disperser.EntityDisperserCanister;
import com.hbm.fluid.*;
import com.hbm.item.fluid.*;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Original ItemDisperser extends the sole ItemFluidTank adapter. No fillable/standard-fluid capability is invented. */
public final class DisperserItem extends FluidContainerItem {
    private static final Random ITEM_RANDOM=new Random();
    private final boolean gland;
    public DisperserItem(boolean gland){super(Kind.TANK,gland?"glyphid_gland_empty":"disperser_canister_empty");this.gland=gland;}
    public boolean isGland(){return gland;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);int fluid=FluidItemContent.type(stack).getID();if(!player.getAbilities().instabuild)stack.shrink(1);
        level.playSound(player,player.getX(),player.getY(),player.getZ(),SoundEvents.ARROW_SHOOT,SoundSource.PLAYERS,.5F,.4F/(ITEM_RANDOM.nextFloat()*.4F+.8F));
        if(!level.isClientSide){var projectile=new EntityDisperserCanister(level,player);projectile.setSourceItem(this);projectile.setFluid(fluid);level.addFreshEntity(projectile);}
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public List<ItemStack> creativeStacks(){var result=new ArrayList<ItemStack>();var order=HbmFluids.getInNiceOrder();for(int i=1;i<order.length;i++){var type=order[i];if(type.isDispersable()&&!gland)result.add(FluidItemContent.stack(this,type));else if(type==HbmFluids.PHEROMONE||type==HbmFluids.SULFURIC_ACID&&gland)result.add(FluidItemContent.stack(this,type));}return result;}
    @Override public Component getName(ItemStack stack){var name=Component.translatable(getDescriptionId());var fluid=FluidItemContent.type(stack).displayName();return gland?fluid.copy().append(" ").append(name):name.append(" ").append(fluid);}
}
