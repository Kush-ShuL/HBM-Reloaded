package com.hbm.entity.mob;
import com.hbm.entity.mob.sourcecreepers.SourceCreeper;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmContent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
public final class EntityCreeperGold extends SourceCreeper {
 public EntityCreeperGold(EntityType<? extends EntityCreeperGold> type,Level level){super(type,level,30);}
 @Override public void sourceExplosion(){if(level().isClientSide)return;discard();new ExplosionVNT(level(),getX(),getY(),getZ(),isPowered()?14:7,this).setBlockAllocator(new BlockAllocatorBulkie(60,isPowered()?32:16)).setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBulkie(Blocks.GOLD_ORE.defaultBlockState()))).setEntityProcessor(new EntityProcessorStandard().withRangeMod(.5F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectStandard()).explode();}
 @Override protected void sourceDrops(boolean byPlayer,int looting){int amount=byPlayer?5+random.nextInt(6+looting*2):3;for(int i=0;i<amount;i++)spawnAtLocation(new ItemStack(HbmContent.item("crystal_gold")),0);}
}
