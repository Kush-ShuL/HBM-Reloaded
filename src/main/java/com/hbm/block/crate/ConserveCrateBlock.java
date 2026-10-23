package com.hbm.block.crate;

import com.hbm.item.food.ConserveFoodType;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original BlockCanCrate's 5..8 draws, 36 equally likely contents, crowbar interaction and full collision. */
public final class ConserveCrateBlock extends Block {
    private static final List<String> CANS=List.of("can_smart","can_creature","can_redbomb","can_mrsugar","can_overcharge","can_luna","can_breen","can_bepis");
    public ConserveCrateBlock(){super(Properties.of().mapColor(MapColor.WOOD).strength(1,1.5F).sound(SoundType.WOOD).noOcclusion());}
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!held.is(CrateContent.CROWBAR.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(!level.isClientSide){dropContents(level,pos);level.removeBlock(pos,false);level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.crateBreak"),SoundSource.BLOCKS,.5F,1);}
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    public List<ItemStack> getContents(Level level,BlockPos pos){
        var result=new ArrayList<ItemStack>();int count=5+level.random.nextInt(4);
        for(int i=0;i<count;i++){
            int choice=level.random.nextInt(36);
            if(choice<27)result.add(ConserveContent.CONSERVE.get().stack(ConserveFoodType.values()[choice]));
            else result.add(new ItemStack(HbmContent.item(choice<35?CANS.get(choice-27):"pudding")));
        }
        return result;
    }
    public void dropContents(Level level,BlockPos pos){for(var stack:getContents(level,pos))Block.popResource(level,pos,stack);}
}
