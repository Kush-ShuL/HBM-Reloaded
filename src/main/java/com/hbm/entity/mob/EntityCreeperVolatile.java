package com.hbm.entity.mob;
import com.hbm.entity.mob.sourcecreepers.SourceCreeper;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmContent;
import com.hbm.world.structure.dependencies.materials.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public final class EntityCreeperVolatile extends SourceCreeper {
 public EntityCreeperVolatile(EntityType<? extends EntityCreeperVolatile> type,Level level){super(type,level,30);}
 @Override public void sourceExplosion(){if(level().isClientSide)return;discard();new ExplosionVNT(level(),getX(),getY(),getZ(),isPowered()?14:7,this).setBlockAllocator(new BlockAllocatorBulkie(60,isPowered()?32:16)).setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBulkie(StructureMaterialsContent.SLAG.get().fromLegacy(1)))).setEntityProcessor(new EntityProcessorStandard().withRangeMod(.5F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectStandard()).explode();}
 @Override protected void sourceDrops(boolean byPlayer,int looting){spawnAtLocation(new ItemStack(HbmContent.item("sulfur"),2+random.nextInt(3)),0);spawnAtLocation(new ItemStack(HbmContent.item("stick_tnt"),1+random.nextInt(2)),0);}
}
