package com.hbm.railway.cart;
import com.hbm.pneumatic.SourcePatternItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
public final class SourceDestroyerCart extends SourceInventoryCart {
    public SourceDestroyerCart(EntityType<?> type,Level level){super(type,level,SourceCartKind.DESTROYER,18);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public InteractionResult interact(Player player,InteractionHand hand){if(player instanceof ServerPlayer server)server.openMenu(this);return InteractionResult.sidedSuccess(level().isClientSide);}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new DestroyerCartMenu(id,inventory,this);}
    @Override protected void afterSourceTick(){
        if(tickCount%5!=0)return;
        if(level().isClientSide){level().addParticle(ParticleTypes.SMOKE,getX(),getY()+.75,getZ(),0,.01,0);return;}
        boolean sound=false;
        outer:for(var entity:level().getEntitiesOfClass(ItemEntity.class,new AABB(getX()-2.5,getY()-1.5,getZ()-2.5,getX()+2.5,getY()+2,getZ()+2.5))){
            var stack=entity.getItem();for(int i=0;i<9;i++)if(!items.get(i).isEmpty()&&SourcePatternItems.sameExact(items.get(i),stack)){entity.discard();sound=true;continue outer;}
            for(int i=9;i<18;i++)if(!items.get(i).isEmpty()&&SourcePatternItems.sameItem(items.get(i),stack)){entity.discard();sound=true;continue outer;}
        }
        if(sound)level().playSound(null,getX(),getY(),getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,SoundSource.BLOCKS,.5F,.5F+random.nextFloat()*.2F);
    }
}
