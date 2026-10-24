package com.hbm.world.oldlantern;

import com.hbm.api.machine.IRepairable;
import com.hbm.bobmazon.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.pedestal.PedestalReputation;
import com.hbm.recipe.generic.GenericIngredient;
import com.hbm.registry.HbmItemTags;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original repair/reputation, four timed horns and the real Bobmazon supply drop. */
public final class OldLanternEntity extends BlockEntity implements IRepairable,com.hbm.multiblock.MuffledMachine {
    public boolean isBroken,muffled,tilted;
    public int comTimer=-1,reputation;
    private Boolean lastPacked;
    public OldLanternEntity(BlockPos pos,BlockState state){super(OldLanternContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,OldLanternEntity lantern){
        if(level.isClientSide)return;
        switch(lantern.comTimer){
            case 360->lantern.horn("block.hornNearSingle",10);
            case 280->lantern.horn("block.hornFarSingle",10000);
            case 220->lantern.horn("block.hornNearDual",10);
            case 100->lantern.horn("block.hornFarDual",10000);
            case 0->lantern.deliver();
        }
        if(lantern.comTimer>=0){lantern.comTimer--;lantern.setChanged();}
        // networkPackNT is called each tick, but LoadedBase deduplicates unchanged
        // bytes except at worldTime %20==0. This override serializes only damage.
        if(lantern.lastPacked==null||lantern.lastPacked!=lantern.isBroken||level.getGameTime()%20==0){
            lantern.lastPacked=lantern.isBroken;
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersNear((net.minecraft.server.level.ServerLevel)level,null,pos.getX(),pos.getY(),pos.getZ(),250,new OldLanternPacket(pos,lantern.isBroken));
        }
    }
    private void horn(String name,float volume){level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),com.hbm.registry.HbmSounds.get(name),SoundSource.BLOCKS,volume,1);}
    private static ItemStack item(String name,int count){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Actual old-lantern supply dependency missing: "+id);return new ItemStack(BuiltInRegistries.ITEM.get(id),count);}
    private static ItemStack circuit(int metadata,int count){return com.hbm.bomb.nuclear.programmable.SourceCircuitItem.stack(metadata,count);}
    private void deliver(){
        var shuttle=BobmazonContent.DELIVERY.get().create(level);if(shuttle==null)throw new IllegalStateException("Actual Bobmazon delivery entity could not be created");
        double x=worldPosition.getX()+.5+level.random.nextGaussian()*10,z=worldPosition.getZ()+.5+level.random.nextGaussian()*10;
        shuttle.setPos(x,300,z);
        // Keep source RNG order: two Gaussian positions, basic quantity, advanced quantity,
        // then the diamond draw only when reputation is below the original bonus threshold.
        var payload=List.of(circuit(8,4+level.random.nextInt(4)),circuit(9,4+level.random.nextInt(2)),item("coin_token",Math.max(1,reputation)),reputation>=10?item("gem_alexandrite",1):new ItemStack(Items.DIAMOND,6+level.random.nextInt(6)),new ItemStack(Items.POPPY));
        shuttle.payload=CustomKitItem.create(level.registryAccess(),payload,"Supplies",List.of(),0xffffff,0x008000);
        level.addFreshEntity(shuttle);
    }
    public void penalizeRemoval(){for(var player:level.getEntitiesOfClass(Player.class,new AABB(worldPosition.getX()-50,worldPosition.getY()-50,worldPosition.getZ()-50,worldPosition.getX()+51,worldPosition.getY()+51,worldPosition.getZ()+51))){int value=PedestalReputation.get(player);if(value>-25)PedestalReputation.set(player,value-1);}}
    @Override public boolean isDamaged(){return isBroken;}
    @Override public boolean isMuffled(){return muffled;}
    @Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public List<GenericIngredient> getRepairMaterials(){return List.of(new GenericIngredient(HbmItemTags.ingredient("c:plates/steel"),2),new GenericIngredient(new com.hbm.machine.alloy.AlloyStackIngredient(circuit(8,1)).toVanilla(),1));}
    @Override public void repair(Player player){isBroken=false;comTimer=400;reputation=PedestalReputation.get(player);setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
    @Override public void tryExtinguish(Level level,BlockPos pos,EnumExtinguishType type){}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("isBroken",isBroken);tag.putInt("comTimer",comTimer);tag.putInt("reputation",reputation);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);isBroken=tag.getBoolean("isBroken");comTimer=tag.getInt("comTimer");reputation=tag.getInt("reputation");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("isBroken",isBroken);return tag;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){isBroken=tag.getBoolean("isBroken");}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
