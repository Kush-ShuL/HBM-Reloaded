package com.hbm.block.crate;

import com.google.gson.Gson;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

/** X5846 BlockAmmoCrate: all original independent random rolls and amounts. */
public final class AmmoCrateBlock extends Block {
    private record Ammo(String name,int metadata,int minimum,int randomRange){}
    private record Definition(List<Ammo> ammo){}
    private static final List<Ammo> AMMO=definitions();
    //The source crate owns a java.util.Random independent of world.rand.
    private final Random lootRandom=new Random();
    public AmmoCrateBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(1,1.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());}
    private static List<Ammo> definitions(){
        try(var input=AmmoCrateBlock.class.getResourceAsStream("/hbm/crate-ammo.json")){
            if(input==null)throw new IllegalStateException("Run tools/port_crates.py");
            return List.copyOf(new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definition.class).ammo());
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack held,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!held.is(CrateContent.CROWBAR.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(!level.isClientSide){dropContents(level,pos);level.removeBlock(pos,false);level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("block.crateBreak"),SoundSource.BLOCKS,.5F,1);}
        //Opening never damages or consumes the source crowbar, including creative mode.
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    public List<ItemStack> getContents(Level level,BlockPos pos){
        var result=new ArrayList<ItemStack>();
        synchronized(lootRandom){
            result.add(new ItemStack(HbmContent.item("cap_nuka"),12+lootRandom.nextInt(21)));
            result.add(new ItemStack(HbmContent.item("syringe_metal_stimpak"),1+lootRandom.nextInt(3)));
            for(var ammo:AMMO)if(lootRandom.nextBoolean()){
                var stack=com.hbm.weapon.sedna.ammo.FlatAmmoContent.standardStack(ammo.metadata(),ammo.minimum()+lootRandom.nextInt(ammo.randomRange()));
                result.add(stack);
            }
            if(lootRandom.nextInt(10)==0)result.add(new ItemStack(HbmContent.item("syringe_metal_super"),2));
        }
        return result;
    }
    public void dropContents(Level level,BlockPos pos){for(var stack:getContents(level,pos))Block.popResource(level,pos,stack);}
}
