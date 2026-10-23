package com.hbm.block.junglemechanics;
import com.hbm.entity.mob.botprime.BotPrimeContent;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
/** Actual BlockBallsSpawner: one source mech_key, real BOT head at Y300 with74-body onSpawn, then cracked brick. */
public final class JungleCircleBlock extends Block {
 public JungleCircleBlock(){super(JungleMechanicsContent.properties(0));}@Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var sourceHeld=player.getMainHandItem();if(hand==InteractionHand.MAIN_HAND&&sourceHeld.is(HbmContent.item("mech_key"))){sourceHeld.shrink(1);if(level instanceof ServerLevel server){var bot=BotPrimeContent.HEAD.get().create(server);if(bot==null)throw new IllegalStateException("Real BOTPrime head could not be created");bot.moveTo(pos.getX()+.5,300,pos.getZ()+.5,0,0);bot.setDeltaMovement(0,-1,0);bot.finalizeSpawn(server,server.getCurrentDifficultyAt(bot.blockPosition()),MobSpawnType.TRIGGERED,null);server.addFreshEntity(bot);server.setBlock(pos,HbmContent.block("brick_jungle_cracked").defaultBlockState(),3);}}return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return JungleDrops.drops(this,state,params,0,true);}
}
