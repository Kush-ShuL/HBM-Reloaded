package com.hbm.machine.tungstencrate;

import api.hbm.block.ILaserable;
import com.hbm.machine.storage.*;
import com.hbm.ore.RawOreContent;
import com.hbm.weapon.crucible.CrucibleContent;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** The real TileEntityCrateTungsten: the sole lockable crate inventory, plus original incoming laser processing. */
public final class TungstenCrateBlockEntity extends StorageCrateBlockEntity implements ILaserable {
    public static final long SPECIAL_ENERGY_THRESHOLD=10_000_000L;
    // Deliberately not serialized: the original class does not save its five-tick heat timer.
    private int heatTimer;
    public TungstenCrateBlockEntity(BlockPos pos,BlockState state){super(StorageCrateKind.TUNGSTEN,pos,state);}

    private ItemStack smeltingResult(ItemStack stack){
        if(level==null||stack.isEmpty())return ItemStack.EMPTY;
        // The single existing modern raw/deepslate-to-source identity bridge; no second ore table or yield multiplier.
        var input=new SingleRecipeInput(RawOreContent.processingStack(stack));
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,input,level)
                .map(holder->holder.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    private static Item sourceItem(String name){
        var id=ResourceLocation.fromNamespaceAndPath("hbm",name);
        if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Missing true tungsten-crate source item "+id);
        return BuiltInRegistries.ITEM.get(id);
    }
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){
        if(isLocked())return false;
        if(stack.is(sourceItem("billet_polonium")))return false;
        if(stack.is(CrucibleContent.CRUCIBLE.get())&&stack.getDamageValue()>0)return false;
        return smeltingResult(stack).isEmpty();
    }
    @Override public void addEnergy(Level world,int x,int y,int z,long energy,Direction incoming){
        heatTimer=5;
        for(int slot=0;slot<getContainerSize();slot++){
            var previous=getItem(slot);if(previous.isEmpty())continue;
            var result=smeltingResult(previous);
            if(previous.is(sourceItem("billet_polonium"))&&energy>SPECIAL_ENERGY_THRESHOLD)result=new ItemStack(sourceItem("billet_yharonite"));
            if(previous.is(CrucibleContent.CRUCIBLE.get())&&previous.getDamageValue()>0&&energy>SPECIAL_ENERGY_THRESHOLD)result=new ItemStack(CrucibleContent.CRUCIBLE.get());
            int amount=previous.getCount();
            // Original all-or-nothing, whole-slot multiplication; no partial output, per-item laser deduction, or source-NBT merge.
            if(!result.isEmpty()&&result.getCount()*amount<=result.getMaxStackSize()){
                // Source assigns directly to its same slots[]; do not impose setItem's unrelated64 insertion clamp.
                slots.set(slot,result.copyWithCount(result.getCount()*amount));setChanged();
            }
        }
    }
    public static void tick(Level level,BlockPos pos,BlockState state,StorageCrateBlockEntity container){
        if(container instanceof TungstenCrateBlockEntity crate)crate.tick();
    }
    private void tick(){
        if(level==null)return;
        if(!level.isClientSide){
            if(heatTimer>0)heatTimer--;
            int metadata=heatTimer>0?1:0;var state=getBlockState();
            if(state.getValue(StorageCrateBlock.LEGACY_META)!=metadata)level.setBlock(worldPosition,state.setValue(StorageCrateBlock.LEGACY_META,metadata),3);
        }
        if(getBlockState().getValue(StorageCrateBlock.LEGACY_META)==1){
            double x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();var random=level.random;
            // Retain the source order and its twenty coordinate RNG calls on either logical side.
            level.addParticle(ParticleTypes.FLAME,x+random.nextDouble(),y+1.1,z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.SMOKE,x+random.nextDouble(),y+1.1,z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.FLAME,x-.1,y+random.nextDouble(),z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.SMOKE,x-.1,y+random.nextDouble(),z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.FLAME,x+1.1,y+random.nextDouble(),z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.SMOKE,x+1.1,y+random.nextDouble(),z+random.nextDouble(),0,0,0);
            level.addParticle(ParticleTypes.FLAME,x+random.nextDouble(),y+random.nextDouble(),z-.1,0,0,0);
            level.addParticle(ParticleTypes.SMOKE,x+random.nextDouble(),y+random.nextDouble(),z-.1,0,0,0);
            level.addParticle(ParticleTypes.FLAME,x+random.nextDouble(),y+random.nextDouble(),z+1.1,0,0,0);
            level.addParticle(ParticleTypes.SMOKE,x+random.nextDouble(),y+random.nextDouble(),z+1.1,0,0,0);
        }
    }
}
