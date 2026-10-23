package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** BlockTNTBase and its original four registered subclasses. */
public class TntExplosiveBlock extends Block implements IFuckingExplode {
    public static final BooleanProperty IGNITE_ON_BREAK = BooleanProperty.create("ignite_on_break");
    private final float radius;
    public TntExplosiveBlock(float radius) {
        super(Properties.of().mapColor(MapColor.FIRE).strength(0).sound(SoundType.GRASS).ignitedByLava());
        this.radius = radius;
        registerDefaultState(defaultBlockState().setValue(IGNITE_ON_BREAK, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(IGNITE_ON_BREAK); }
    @Override public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 100; }
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) { return 15; }
    private void checkIgnition(Level level, BlockPos pos) {
        boolean fire = false;
        for (Direction side : Direction.values()) if (level.getBlockState(pos.relative(side)).is(Blocks.FIRE)) fire = true;
        if (level.hasNeighborSignal(pos) || fire) { prime(level, pos, null, 80, true); level.removeBlock(pos, false); }
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        if (!previous.is(this)) checkIgnition(level, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) { checkIgnition(level, pos); }
    @Override public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Source checks only metadata, including creative breaking.
        if (state.getValue(IGNITE_ON_BREAK)) prime(level, pos, null, 80, true);
        return super.playerWillDestroy(level, pos, state, player);
    }
    public void prime(Level level, BlockPos pos, LivingEntity igniter, int fuse, boolean sound) {
        if (level.isClientSide) return;
        var entity = new PrimedExplosive(level, pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D, igniter, this);
        entity.setFuse(fuse); level.addFreshEntity(entity);
        if (sound) level.playSound(null, entity.getX(), entity.legacyY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1F, 1F);
    }
    @Override public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        prime(level, pos, explosion == null ? null : explosion.getIndirectSourceEntity(), level.random.nextInt(20) + 10, false);
    }
    @Override public boolean dropFromExplosion(Explosion explosion) { return false; }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        String tool = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (tool.equals("hbm:defuser")) {
            if (!level.isClientSide) { level.removeBlock(pos, false); popResource(level, pos, new ItemStack(this)); if(stack.getItem() instanceof com.hbm.item.tool.ToolingItem tooling)tooling.onToolUsed(stack,player,hand); }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (tool.equals("hbm:screwdriver") || tool.equals("hbm:screwdriver_desh")) {
            if (!level.isClientSide) {
                boolean enabled = !state.getValue(IGNITE_ON_BREAK); level.setBlock(pos, state.setValue(IGNITE_ON_BREAK, enabled), 3);
                if(stack.getItem() instanceof com.hbm.item.tool.ToolingItem tooling)tooling.onToolUsed(stack,player,hand);
                player.displayClientMessage(Component.translatable(enabled ? "hbm.explosives.ignite_on_break.enabled" : "hbm.explosives.ignite_on_break.disabled").withStyle(enabled ? ChatFormatting.RED : ChatFormatting.GOLD), false);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(Items.FLINT_AND_STEEL)) {
            prime(level, pos, player, 80, true); level.removeBlock(pos, false);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide && projectile instanceof AbstractArrow arrow && arrow.isOnFire()) {
            prime(level, hit.getBlockPos(), arrow.getOwner() instanceof LivingEntity living ? living : null, 80, true);
            level.removeBlock(hit.getBlockPos(), false);
        }
    }
    @Override public void onCaughtFire(BlockState state, Level level, BlockPos pos, Direction face, LivingEntity igniter) {
        prime(level, pos, igniter, 80, true);
    }
    @Override public void explodeEntity(Level level, double x, double y, double z, PrimedExplosive entity) {
        // These four source classes explicitly use World.createExplosion, with no custom VNT flags.
        level.explode(entity, x, y, z, radius, Level.ExplosionInteraction.TNT);
    }
}
