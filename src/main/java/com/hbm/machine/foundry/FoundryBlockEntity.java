package com.hbm.machine.foundry;

import com.hbm.api.machine.*;
import com.hbm.material.*;
import com.hbm.machine.foundry.FoundryContent.Kind;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** X5846 FoundryBase/Channel/Tank/Casting/Outlet/Slagtap state and tick rules. */
public final class FoundryBlockEntity extends BlockEntity implements FoundryAcceptor,WorldlyContainer,ICopiable,MachineView {
    public NtmMaterial type,filter;public int amount,nextUpdate,lastFlow,cooloff=100;public boolean invertFilter,invertRedstone;
    NtmMaterial nodeType;boolean nodeReady;public final NonNullList<ItemStack> slots=NonNullList.withSize(2,ItemStack.EMPTY);
    private NtmMaterial lastType,lastFilter;private int lastAmount=-1;private boolean lastClosed;private long revision;
    public FoundryBlockEntity(BlockPos pos,BlockState state){super(FoundryContent.ENTITY.get(),pos,state);}
    public Kind kind(){return ((FoundryBlock)getBlockState().getBlock()).kind;}
    public Direction facing(){return getBlockState().getValue(FoundryBlock.FACING);}
    public int capacity(){return switch(kind()){case CHANNEL->HbmMaterials.shape("INGOT").q(2);case TANK->HbmMaterials.shape("BLOCK").q(4);case SLAG->HbmMaterials.shape("BLOCK").q(16);case MOLD,BASIN->{var mold=mold();yield mold==null?0:mold.requiredUnits();}default->0;};}
    public com.hbm.machine.anvil.MoldItem.Mold mold(){var mold=slots.getFirst().getItem() instanceof com.hbm.machine.anvil.MoldItem item?item.mold(slots.getFirst()):null;return mold!=null&&mold.size()==(kind()==Kind.BASIN?1:0)?mold:null;}
    public boolean closed(){return invertRedstone^(level!=null&&level.hasNeighborSignal(worldPosition));}
    public long revision(){return revision;}
    public void changed(){setChanged();revision++;if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
    public static void tick(Level level,BlockPos pos,BlockState state,FoundryBlockEntity self){
        if(!level.isClientSide){switch(self.kind()){case CHANNEL->self.tickChannel();case TANK->self.tickTank();case MOLD,BASIN->self.tickCasting();default->{}}
            if(self.lastType!=self.type||self.lastAmount!=self.amount||self.lastFilter!=self.filter||self.kind().outlet()&&self.lastClosed!=self.closed()){self.lastType=self.type;self.lastAmount=self.amount;self.lastFilter=self.filter;self.lastClosed=self.kind().outlet()&&self.closed();self.changed();}
        }
    }
    private List<Direction> shuffled(){var sides=new ArrayList<>(Arrays.asList(FoundryNetwork.HORIZONTAL));Collections.shuffle(sides);return sides;}
    private void tickChannel(){
        if(!nodeReady){nodeReady=true;nodeType=type;}
        if(nodeType!=null&&amount==0)nodeType=null;if(type==null&&amount!=0)amount=0;nextUpdate--;
        if(nextUpdate<=0&&amount>0&&type!=null){boolean operated=false;nextUpdate=5;List<Direction> sides=shuffled();if(lastFlow>0){Direction last=Direction.from3DDataValue(lastFlow);sides.remove(last);sides.add(last);}
            for(Direction side:sides){var target=level.getBlockEntity(worldPosition.relative(side));if(target instanceof FoundryAcceptor acceptor&&!(target instanceof FoundryBlockEntity foundry&&foundry.kind()==Kind.CHANNEL)&&acceptor.canAcceptPartialFlow(side.getOpposite(),new MaterialStack(type,amount))){MaterialStack left=acceptor.flow(side.getOpposite(),new MaterialStack(type,amount));if(left==null){type=null;amount=0;nodeType=null;}else amount=left.amount;operated=true;break;}}
            if(!operated)for(Direction side:sides){var target=level.getBlockEntity(worldPosition.relative(side));if(target instanceof FoundryBlockEntity other&&other.kind()==Kind.CHANNEL&&(other.type==null||other.type==type||other.amount==0)){other.type=type;other.nodeReady=true;other.nodeType=type;other.lastFlow=side.getOpposite().get3DDataValue();if(level.random.nextInt(5)==0||amount==1){int previous=amount;amount=other.amount;other.amount=previous;}else{int difference=amount-other.amount;if(difference>0){difference/=2;amount-=difference;other.amount+=difference;}}other.setChanged();}}
        }
        if(amount==0){lastFlow=0;nextUpdate=5;}
    }
    private void tickTank(){
        if(type==null&&amount!=0)amount=0;nextUpdate--;
        if(nextUpdate<=0&&amount>0&&type!=null){boolean operated=false;nextUpdate=level.random.nextInt(6)+5;
            if(level.getBlockEntity(worldPosition.below()) instanceof FoundryBlockEntity below&&below.kind()==Kind.TANK&&(below.type==null||below.type==type)&&below.amount<below.capacity()){below.type=type;int transfer=Math.min(amount,below.capacity()-below.amount);amount-=transfer;below.amount+=transfer;below.setChanged();operated=true;}
            List<Direction> sides=shuffled();
            if(!operated)for(Direction side:sides){var target=level.getBlockEntity(worldPosition.relative(side));if(target instanceof FoundryAcceptor acceptor&&!(target instanceof FoundryBlockEntity foundry&&foundry.kind()==Kind.CHANNEL)&&acceptor.canAcceptPartialFlow(side.getOpposite(),new MaterialStack(type,amount))){MaterialStack left=acceptor.flow(side.getOpposite(),new MaterialStack(type,amount));if(left==null){type=null;amount=0;}else amount=left.amount;operated=true;break;}}
            if(!operated)for(Direction side:sides){if(level.getBlockEntity(worldPosition.relative(side)) instanceof FoundryBlockEntity other&&other.kind()==Kind.TANK&&(other.type==null||other.type==type||other.amount==0)){other.type=type;if(level.random.nextInt(5)==0){int previous=amount;amount=other.amount;other.amount=previous;}else{int difference=amount-other.amount;if(difference>0){difference/=2;amount-=difference;other.amount+=difference;}}other.setChanged();}}
        }
    }
    private void tickCasting(){
        if(amount>capacity())amount=capacity();if(amount==0)type=null;var mold=mold();
        if(mold!=null&&amount==capacity()&&slots.get(1).isEmpty()){cooloff--;if(cooloff<=0){amount=0;ItemStack output=mold.output(type);if(!output.isEmpty())slots.set(1,output.copy());cooloff=200;changed();}else if(cooloff%5==0)changed();}else cooloff=200;
    }
    public boolean standardCheck(MaterialStack stack){if(type!=null&&type!=stack.material&&amount>0)return false;if(amount>=capacity())return false;if(kind().cast()){var mold=mold();return slots.get(1).isEmpty()&&mold!=null&&!mold.output(stack.material).isEmpty();}return true;}
    public MaterialStack standardAdd(MaterialStack stack){type=stack.material;if(stack.amount+amount<=capacity()){amount+=stack.amount;changed();return null;}int needed=capacity()-amount;amount=capacity();stack.amount-=needed;changed();return stack;}
    @Override public boolean canAcceptPartialFlow(Direction side,MaterialStack stack){
        if(kind()==Kind.TANK||kind()==Kind.BASIN||kind()==Kind.SLAG)return false;
        if(kind().outlet()){
            if(filter!=null&&((filter!=stack.material)^invertFilter)||closed()||side!=facing().getOpposite())return false;
            var hit=outletHit();if(kind()==Kind.SLAGTAP)return hit.getType()==HitResult.Type.BLOCK;
            var target=outletTarget();return target!=null&&target.acceptor().canAcceptPartialPour(target.hit().getLocation(),Direction.UP,stack);
        }
        return standardCheck(stack);
    }
    @Override public MaterialStack flow(Direction side,MaterialStack stack){if(kind()==Kind.TANK||kind()==Kind.BASIN||kind()==Kind.SLAG)return stack;if(kind().outlet())return pourOutlet(side,stack);if(kind()==Kind.CHANNEL)nodeType=stack.material;return standardAdd(stack);}
    @Override public boolean canAcceptPartialPour(Vec3 hit,Direction side,MaterialStack stack){if(kind().outlet()||kind()==Kind.SLAG||side!=Direction.UP)return false;if(kind()==Kind.CHANNEL&&(!nodeReady||!FoundryNetwork.accepts(level,worldPosition,stack.material)))return false;return standardCheck(stack);}
    @Override public MaterialStack pour(Vec3 hit,Direction side,MaterialStack stack){if(kind().outlet()||kind()==Kind.SLAG)return stack;if(kind()==Kind.CHANNEL)nodeType=stack.material;return standardAdd(stack);}
    private Vec3 outletStart(){return Vec3.atLowerCornerOf(worldPosition).add(.5,-.125,.5);}
    private Vec3 outletEnd(){return Vec3.atLowerCornerOf(worldPosition).add(.5,.125-(kind()==Kind.SLAGTAP?15:4),.5);}
    private BlockHitResult outletHit(){return FoundryPouring.hit(level,outletStart(),outletEnd());}
    private FoundryPouring.Target outletTarget(){return FoundryPouring.target(level,outletStart(),outletEnd());}
    private MaterialStack pourOutlet(Direction side,MaterialStack stack){
        if(kind()==Kind.SLAGTAP)return tapSlag(side,stack);
        var target=outletTarget();if(target==null)return stack;MaterialStack left=target.acceptor().pour(target.hit().getLocation(),Direction.UP,stack);
        if(stack!=null)stream(side,stack.material,Math.max(1F,worldPosition.getY()-(float)(Math.ceil(target.hit().getBlockPos().getY()+1)-.875)));return left;
    }
    private void stream(Direction side,NtmMaterial material,float length){Direction dir=side.getOpposite();FoundryStreamPacket.send((ServerLevel)level,Vec3.atLowerCornerOf(worldPosition).add(.5-dir.getStepX()*.125,.125,.5-dir.getStepZ()*.125),material.moltenColor(),dir,length,0,.375F);}
    private MaterialStack tapSlag(Direction side,MaterialStack stack){
        if(stack==null||stack.material==null||stack.amount<=0)return null;var hit=outletHit();if(hit.getType()!=HitResult.Type.BLOCK)return null;BlockPos pos=hit.getBlockPos();boolean didFlow=false;
        if(level.getBlockEntity(pos) instanceof FoundryBlockEntity slag&&slag.kind()==Kind.SLAG){if(slag.type==stack.material){int transfer=Math.min(slag.capacity()-slag.amount,stack.amount);slag.amount+=transfer;stack.amount-=transfer;didFlow|=transfer>0;slag.changed();level.scheduleTick(pos,FoundryContent.block(Kind.SLAG),1);}}
        else if(level.getBlockState(pos).canBeReplaced()){level.setBlock(pos,FoundryContent.block(Kind.SLAG).defaultBlockState(),3);var slag=(FoundryBlockEntity)level.getBlockEntity(pos);slag.type=stack.material;int transfer=Math.min(slag.capacity(),stack.amount);slag.amount+=transfer;stack.amount-=transfer;didFlow|=transfer>0;slag.changed();level.scheduleTick(pos,FoundryContent.block(Kind.SLAG),1);}
        if(stack.amount>0&&level.getBlockState(pos.above()).canBeReplaced()){BlockPos above=pos.above();level.setBlock(above,FoundryContent.block(Kind.SLAG).defaultBlockState(),3);var slag=(FoundryBlockEntity)level.getBlockEntity(above);slag.type=stack.material;int transfer=Math.min(slag.capacity(),stack.amount);slag.amount+=transfer;stack.amount-=transfer;didFlow|=transfer>0;slag.changed();level.scheduleTick(above,FoundryContent.block(Kind.SLAG),1);}
        if(didFlow)stream(side,stack.material,Math.max(1F,worldPosition.getY()-(float)Math.ceil(pos.getY())));return stack.amount<=0?null:stack;
    }
    public void tickSlag(){
        if(level.getBlockState(worldPosition.below()).canBeReplaced()&&worldPosition.getY()>level.getMinBuildHeight()){BlockPos below=worldPosition.below();level.setBlock(below,getBlockState(),3);var other=(FoundryBlockEntity)level.getBlockEntity(below);other.type=type;other.amount=amount;other.changed();level.setBlockAndUpdate(worldPosition,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());return;}
        if(level.getBlockEntity(worldPosition.below()) instanceof FoundryBlockEntity below&&below.kind()==Kind.SLAG&&below.type==type&&below.amount<capacity()){int transfer=Math.min(capacity()-below.amount,amount);below.amount+=transfer;amount-=transfer;if(amount<=0)level.setBlockAndUpdate(worldPosition,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());else changed();below.changed();level.scheduleTick(worldPosition.below(),getBlockState().getBlock(),1);return;}
        Direction[] sides={Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST};int count=0;for(Direction side:sides)if(level.getBlockState(worldPosition.relative(side)).canBeReplaced())count++;
        if(amount>=capacity()/5&&count>0){int spread=Math.max(amount/(count*2),1);for(Direction side:sides){BlockPos pos=worldPosition.relative(side);if(level.getBlockState(pos).canBeReplaced()){level.setBlock(pos,getBlockState(),3);var other=(FoundryBlockEntity)level.getBlockEntity(pos);other.type=type;other.amount=spread;other.changed();level.scheduleTick(pos,getBlockState().getBlock(),1);amount-=spread;changed();}}}
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);saveState(tag,lookup);}
    private void saveState(CompoundTag tag,HolderLookup.Provider lookup){tag.putInt(kind()==Kind.SLAG?"mat":"type",type==null?-1:type.id());tag.putInt("amount",amount);tag.putByte("flow",(byte)lastFlow);tag.putBoolean("invert",invertRedstone);tag.putBoolean("invertFilter",invertFilter);tag.putShort("filter",filter==null?-1:(short)filter.id());net.minecraft.world.ContainerHelper.saveAllItems(tag,slots,lookup);tag.putInt("clientCooloff",cooloff);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadState(tag,lookup);}
    private void loadState(CompoundTag tag,HolderLookup.Provider lookup){type=HbmMaterials.byId(tag.getInt(kind()==Kind.SLAG?"mat":"type"));amount=tag.getInt("amount");lastFlow=tag.getByte("flow");invertRedstone=tag.getBoolean("invert");invertFilter=tag.getBoolean("invertFilter");filter=HbmMaterials.byId(tag.getShort("filter"));net.minecraft.world.ContainerHelper.loadAllItems(tag,slots,lookup);if(level!=null&&level.isClientSide&&tag.contains("clientCooloff"))cooloff=tag.getInt("clientCooloff");revision++;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();saveState(tag,lookup);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){loadState(tag,lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();if(kind().outlet()){tag.putBoolean("invert",invertRedstone);tag.putBoolean("invertFilter",invertFilter);if(filter!=null)tag.putIntArray("matFilter",new int[]{filter.id()});}else if(type!=null)tag.putIntArray("matFilter",new int[]{type.id()});return tag;}
    @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){if(!kind().outlet())return;if(tag.contains("invert"))invertRedstone=tag.getBoolean("invert");if(tag.contains("invertFilter"))invertFilter=tag.getBoolean("invertFilter");if(tag.contains("matFilter")){int[] ids=tag.getIntArray("matFilter");if(index>=0&&index<ids.length)filter=HbmMaterials.byId(ids[index]);}changed();}
    @Override public String[] infoForDisplay(Level level,BlockPos pos){if(!kind().outlet())return null;var lines=new ArrayList<String>();lines.add("copytool.invertRedstone");lines.add("copytool.invertFilter");if(filter!=null)lines.add(filter.descriptionId());return lines.toArray(String[]::new);}
    @Override public boolean supportsSettingsCopy(){return kind()!=Kind.SLAG;}
    @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}
    @Override public List<ProgressView> progressBars(){var bars=new ArrayList<ProgressView>();if(capacity()>0)bars.add(new ProgressView(type==null?"gui.hbm.foundry.material":type.descriptionId(),amount,capacity(),type==null?0xFF8E42:type.moltenColor()));if(kind().cast()&&type!=null&&amount>0&&mold()!=null&&amount==capacity()&&slots.get(1).isEmpty())bars.add(new ProgressView("gui.hbm.foundry.cooling",200-cooloff,200,0x62A9DC));return bars;}
    @Override public int getContainerSize(){return kind().cast()?2:0;}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots.get(slot);}
    @Override public ItemStack removeItem(int slot,int count){var result=net.minecraft.world.ContainerHelper.removeItem(slots,slot,count);if(!result.isEmpty())changed();return result;}
    @Override public ItemStack removeItemNoUpdate(int slot){return net.minecraft.world.ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);stack.limitSize(getMaxStackSize());changed();}@Override public boolean stillValid(Player player){return false;}@Override public void clearContent(){slots.replaceAll(stack->ItemStack.EMPTY);changed();}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return kind().cast()?new int[]{1}:new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==1&&kind().cast();}
}
