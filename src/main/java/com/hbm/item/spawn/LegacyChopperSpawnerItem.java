package com.hbm.item.spawn;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** X5846 ItemChopper's shared placement/liquid-use path, bound only by actual migrated callers. */
public class LegacyChopperSpawnerItem extends Item {
    private final Supplier<? extends EntityType<? extends Mob>> type;
    public LegacyChopperSpawnerItem(Supplier<? extends EntityType<? extends Mob>> type,int maximumStack){
        super(new Properties().stacksTo(maximumStack));this.type=type;
    }
    /** The original UFO branch alone overrides this with +35; ordinary source spawners use zero. */
    protected double spawnHeightOffset(){return 0;}
    /** Original per-kind initialization runs before position and finalizeSpawn. */
    protected void initialize(Mob entity){}
    protected Mob spawn(ServerLevel level,double x,double y,double z){
        Mob entity=type.get().create(level);if(entity==null)return null;
        initialize(entity);
        float yaw=Mth.wrapDegrees(level.random.nextFloat()*360F);
        entity.moveTo(x,y+spawnHeightOffset(),z,yaw,0);
        entity.setYHeadRot(yaw);entity.setYBodyRot(yaw);
        entity.finalizeSpawn(level,level.getCurrentDifficultyAt(entity.blockPosition()),MobSpawnType.SPAWN_EGG,null);
        level.addFreshEntity(entity);return entity;
    }
    private void finish(ItemStack stack,Player player,Mob entity){
        if(entity==null)return;
        var name=stack.get(DataComponents.CUSTOM_NAME);if(name!=null)entity.setCustomName(name);
        if(!player.getAbilities().instabuild)stack.shrink(1);
    }
    @Override public InteractionResult useOn(UseOnContext context){
        if(context.getLevel().isClientSide)return InteractionResult.SUCCESS;
        if(!(context.getLevel() instanceof ServerLevel level))return InteractionResult.PASS;
        var clicked=context.getClickedPos();var side=context.getClickedFace();var at=clicked.relative(side);
        double offset=side==net.minecraft.core.Direction.UP&&level.getBlockState(clicked).getBlock() instanceof FenceBlock?.5D:0;
        var entity=spawn(level,at.getX()+.5D,at.getY()+offset,at.getZ()+.5D);
        var player=context.getPlayer();
        if(player!=null)finish(context.getItemInHand(),player,entity);
        return InteractionResult.CONSUME;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(level.isClientSide)return InteractionResultHolder.success(stack);
        BlockHitResult hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.SOURCE_ONLY);
        if(hit.getType()!=HitResult.Type.BLOCK)return InteractionResultHolder.pass(stack);
        var pos=hit.getBlockPos();
        if(!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,hit.getDirection(),stack))return InteractionResultHolder.pass(stack);
        if(level instanceof ServerLevel server&&level.getBlockState(pos).getBlock() instanceof LiquidBlock){
            finish(stack,player,spawn(server,pos.getX(),pos.getY(),pos.getZ()));
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
