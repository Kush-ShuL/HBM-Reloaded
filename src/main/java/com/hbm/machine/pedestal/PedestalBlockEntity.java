package com.hbm.machine.pedestal;

import com.hbm.bomb.conventional.ConventionalEffects;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class PedestalBlockEntity extends BlockEntity {
    private ItemStack item=ItemStack.EMPTY;
    public PedestalBlockEntity(BlockPos pos,BlockState state){super(PedestalContent.ENTITY.get(),pos,state);}
    public ItemStack item(){return item;}
    public void setItem(ItemStack stack){item=stack;setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    public void craft(){
        if(level==null||level.isClientSide)return;
        int[][] offsets={{-2,-2},{0,-3},{2,-2},{-3,0},{0,0},{3,0},{-2,2},{0,3},{2,2}};
        var pedestals=new PedestalBlockEntity[9];var stacks=new ArrayList<ItemStack>();
        for(int i=0;i<offsets.length;i++){var at=worldPosition.offset(offsets[i][0],0,offsets[i][1]);pedestals[i]=level.getBlockEntity(at) instanceof PedestalBlockEntity p?p:null;stacks.add(pedestals[i]==null?ItemStack.EMPTY:pedestals[i].item);}
        var input=new PedestalInput(stacks,worldPosition);
        for(var recipe:PedestalRecipeConfig.recipes(level.getRecipeManager())){
            if(!recipe.matches(input,level))continue;
            for(int i=0;i<9;i++)if(i!=4&&pedestals[i]!=null&&!pedestals[i].item.isEmpty())pedestals[i].setItem(ItemStack.EMPTY);
            setItem(recipe.result().copy());
            ConventionalEffects.compose(level,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,10,2.5F,1F);
            var center=new AABB(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5,worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5).inflate(50);
            for(var player:level.getEntitiesOfClass(Player.class,center))PedestalContent.legendary(player);
            return;
        }
    }
    public static void tick(Level level,BlockPos pos,BlockState state,PedestalBlockEntity pedestal){
        long time=level.getGameTime();if(time%20!=0||pedestal.item.isEmpty())return;
        String id=BuiltInRegistries.ITEM.getKey(pedestal.item.getItem()).toString();
        if(id.equals("hbm:protection_charm"))PedestalEntries.push(level,PedestalEntries.Kind.CHARM_OF_PROTECTION,pos);
        if(id.equals("hbm:meteor_charm"))PedestalEntries.push(level,PedestalEntries.Kind.METEORITE_CHARM,pos);
        if(time%60==0&&id.equals("hbm:defuser_gold"))for(var creeper:level.getEntitiesOfClass(Creeper.class,new AABB(pos).inflate(25)))PedestalDefuser.defuse(creeper,null,false);
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(!item.isEmpty())tag.put("item",item.save(lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);item=ItemStack.parseOptional(lookup,tag.getCompound("item"));}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){
        var tag=saveWithoutMetadata(lookup);
        // NeoForge ignores empty BE update packets. Explicit empty item resets
        // the existing client stack through the normal loadAdditional path.
        if(item.isEmpty())tag.put("item",new CompoundTag());
        return tag;
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
