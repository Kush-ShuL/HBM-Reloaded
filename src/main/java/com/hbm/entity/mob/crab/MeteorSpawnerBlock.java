package com.hbm.entity.mob.crab;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
/** Visible original BlockCybercrab and its legal, unitemized brick host for the original orphan-TE branch. */
public final class MeteorSpawnerBlock extends Block implements EntityBlock {
    public static final MapCodec<MeteorSpawnerBlock> CODEC=simpleCodec(MeteorSpawnerBlock::new);
    private final boolean hidden;
    public MeteorSpawnerBlock(BlockBehaviour.Properties properties){this(properties,false);}
    public MeteorSpawnerBlock(BlockBehaviour.Properties properties,boolean hidden){super(properties);this.hidden=hidden;}
    @Override protected MapCodec<? extends Block> codec(){return CODEC;}
    @Override public String getDescriptionId(){return hidden?"block.hbm.meteor_brick":super.getDescriptionId();}
    @Override public net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader level,BlockPos pos,BlockState state){return new net.minecraft.world.item.ItemStack(hidden?net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse("hbm:meteor_brick")):CyberCrabContent.SPAWNER_ITEM.get());}
    @Override public BlockEntity newBlockEntity(BlockPos position,BlockState state){return new MeteorSpawnerBlockEntity(position,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return!level.isClientSide&&type==CyberCrabContent.SPAWNER_ENTITY.get()?(world,pos,block,tile)->((MeteorSpawnerBlockEntity)tile).tick():null;}
}
