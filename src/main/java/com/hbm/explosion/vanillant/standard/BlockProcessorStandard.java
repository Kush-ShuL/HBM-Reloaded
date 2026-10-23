package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.*;
import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class BlockProcessorStandard implements IBlockProcessor {
    protected IDropChanceMutator chance;
    protected IFortuneMutator fortune;
    protected IBlockMutator convert;
    public BlockProcessorStandard withChance(IDropChanceMutator value){chance=value;return this;}
    public BlockProcessorStandard withFortune(IFortuneMutator value){fortune=value;return this;}
    public BlockProcessorStandard withBlockEffect(IBlockMutator value){convert=value;return this;}
    @Override public void process(ExplosionVNT explosion,Level world,double x,double y,double z,HashSet<BlockPos> affected){
        float dropChance=1F/explosion.size;
        var iterator=affected.iterator();
        while(iterator.hasNext()){
            BlockPos pos=iterator.next();var block=world.getBlockState(pos);
            if(block.isAir()){iterator.remove();continue;}
            if(block.canDropFromExplosion(world,pos,explosion.compat)&&world instanceof ServerLevel server&&world.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS)){
                // This value intentionally carries between blocks, matching the original mutable local.
                if(chance!=null)dropChance=chance.mutateDropChance(explosion,block,pos.getX(),pos.getY(),pos.getZ(),dropChance);
                int level=fortune==null?0:fortune.mutateFortune(explosion,block,pos.getX(),pos.getY(),pos.getZ());
                ItemStack tool=ItemStack.EMPTY;
                if(level!=0){tool=new ItemStack(Items.DIAMOND_PICKAXE);tool.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE),level);}
                var params=new LootParams.Builder(server).withParameter(LootContextParams.ORIGIN,Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL,tool).withOptionalParameter(LootContextParams.BLOCK_ENTITY,world.getBlockEntity(pos)).withOptionalParameter(LootContextParams.THIS_ENTITY,explosion.exploder);
                for(ItemStack stack:block.getDrops(params))if(world.random.nextFloat()<=dropChance)Block.popResource(world,pos,stack);
            }
            block.onBlockExploded(world,pos,explosion.compat);
            // Source obtains metadata after onBlockExploded, usually air / zero.
            if(convert!=null)convert.mutatePre(explosion,block,com.hbm.explosion.LegacyExplosionMath.metadata(world.getBlockState(pos)),pos.getX(),pos.getY(),pos.getZ());
        }
        if(convert!=null)for(BlockPos pos:affected)if(world.getBlockState(pos).isAir())convert.mutatePost(explosion,pos.getX(),pos.getY(),pos.getZ());
    }
    public BlockProcessorStandard setNoDrop(){chance=new DropChanceMutatorStandard(0F);return this;}
    public BlockProcessorStandard setAllDrop(){chance=new DropChanceMutatorStandard(1F);return this;}
    public BlockProcessorStandard setFortune(int level){fortune=(explosion,block,x,y,z)->level;return this;}
}
