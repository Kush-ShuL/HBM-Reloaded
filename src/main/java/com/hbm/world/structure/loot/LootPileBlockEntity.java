package com.hbm.world.structure.loot;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source BlockLoot.TileEntityLoot, including authored sub-block item offsets. */
public final class LootPileBlockEntity extends BlockEntity {
    public record PlacedItem(ItemStack stack,int quantity,double x,double y,double z){public ItemStack drop(){return stack.copyWithCount(quantity);}}
    private final List<PlacedItem> items=new ArrayList<>();
    public LootPileBlockEntity(BlockPos pos,BlockState state){super(StructureLootContent.PILE_ENTITY.get(),pos,state);}
    public List<PlacedItem> items(){return Collections.unmodifiableList(items);}
    public void add(ItemStack stack,double x,double y,double z){if(stack==null)throw new IllegalArgumentException("Missing real source loot item");items.add(new PlacedItem(stack.copy(),stack.getCount(),x,y,z));setChanged();}
    public void add(LegacyStructureLoot.Draw draw,double x,double y,double z){items.add(new PlacedItem(draw.item().copyWithCount(Math.max(1,draw.count())),draw.count(),x,y,z));setChanged();}
    public void sync(){if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("count",items.size());for(int i=0;i<items.size();i++){var entry=items.get(i);tag.put("item"+i,entry.stack.saveOptional(lookup));tag.putInt("quantity"+i,entry.quantity);tag.putDouble("x"+i,entry.x);tag.putDouble("y"+i,entry.y);tag.putDouble("z"+i,entry.z);}}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);items.clear();for(int i=0;i<tag.getInt("count");i++){var stack=ItemStack.parseOptional(lookup,tag.getCompound("item"+i));if(!stack.isEmpty())items.add(new PlacedItem(stack,tag.contains("quantity"+i)?tag.getInt("quantity"+i):stack.getCount(),tag.getDouble("x"+i),tag.getDouble("y"+i),tag.getDouble("z"+i)));}}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadAdditional(tag,lookup);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
}
