package com.hbm.machine.storage;

import com.hbm.item.lock.KeyPinItem;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source lock state and per-access key/lock-pick checks; a successful pick does not unlock the container. */
public abstract class LockableBlockEntity extends BlockEntity implements com.hbm.multiblock.MuffledMachine {
    protected int lockPins;
    private boolean locked;
    protected double lockMod=.1;
    public boolean cheesable=true;
    public boolean muffled,tilted;
    protected LockableBlockEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    public boolean isLocked(){return locked;}
    public void lock(){if(lockPins==0)com.mojang.logging.LogUtils.getLogger().error("A block has been set to locked state before setting pins, this should not happen and may cause errors! {}",this);locked=true;changedLock();}
    public void unlock(){locked=false;changedLock();}
    public void setPins(int pins){lockPins=pins;changedLock();}
    public int getPins(){return lockPins;}
    public void setMod(double value){lockMod=value;changedLock();}
    public double getMod(){return lockMod;}
    protected void changedLock(){setChanged();if(level!=null){level.invalidateCapabilities(worldPosition);if(!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}}
    public static boolean isItem(ItemStack stack,String name){return !stack.isEmpty()&&BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:"+name);}
    private static boolean has(Player player,String name){for(var stack:player.getInventory().items)if(isItem(stack,name))return true;return false;}
    /** Read-only source key identity check; unlike canAccess this never attempts lock-picking or plays sounds. */
    public boolean matchesKey(ItemStack stack){
        if(isItem(stack,"key_red"))return true;
        if(stack.isEmpty()||!(stack.getItem() instanceof KeyPinItem key)||!key.isKey())return false;
        var data=stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return (data==null?0:data.copyTag().getInt("pins"))==lockPins;
    }
    /** The actual carried inventory, including the offhand; no permission/game-mode or remote-inventory bypass. */
    public boolean hasMatchingKey(Player player){
        if(player==null)return false;
        var inventory=player.getInventory();
        for(int slot=0;slot<inventory.getContainerSize();slot++)if(matchesKey(inventory.getItem(slot)))return true;
        return false;
    }
    public boolean canAccess(Player player){
        if(!locked)return true;if(player==null||level==null)return false;var stack=player.getMainHandItem();
        if(matchesKey(stack)){sound(player,"block.lockOpen",1);return true;}
        return tryPick(player);
    }
    private boolean tryPick(Player player){
        var held=player.getMainHandItem();boolean canPick=false;double chance=lockMod*100;
        if(isItem(held,"pin")&&(has(player,"screwdriver")||has(player,"screwdriver_desh"))){held.shrink(1);canPick=true;}
        if((isItem(held,"screwdriver")||isItem(held,"screwdriver_desh"))&&has(player,"pin")){for(var stack:player.getInventory().items)if(isItem(stack,"pin")){stack.shrink(1);break;}player.inventoryMenu.broadcastChanges();canPick=true;}
        if(canPick){var chest=player.getItemBySlot(EquipmentSlot.CHEST);if(isItem(chest,"jackt")||isItem(chest,"jackt2"))chance*=100;if(chance>level.random.nextDouble()*100){sound(player,"item.pinUnlock",1);return true;}sound(player,"item.pinBreak",.8F+level.random.nextFloat()*.2F);}
        return false;
    }
    private void sound(Player player,String event,float pitch){level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get(event),SoundSource.PLAYERS,1,pitch);}
    protected void saveLock(CompoundTag tag){tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);tag.putInt("lock",lockPins);tag.putBoolean("isLocked",locked);tag.putDouble("lockMod",lockMod);tag.putBoolean("cheesable",cheesable);}
    protected void loadLock(CompoundTag tag){muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");lockPins=tag.getInt("lock");locked=tag.getBoolean("isLocked");lockMod=tag.getDouble("lockMod");cheesable=tag.getBoolean("cheesable");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);saveLock(tag);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadLock(tag);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
