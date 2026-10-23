package com.hbm.door.generic;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original TileEntityDoorGeneric backend; lock/key/muffling state stays in the existing unique lockable base. */
public final class GenericDoorBlockEntity extends LockableBlockEntity {
    public static final byte CLOSED=0,OPEN=1,CLOSING=2,OPENING=3;
    public static Consumer<GenericDoorBlockEntity> CLIENT_TICK=door->{},CLIENT_REMOVE=door->{};
    public static BiConsumer<GenericDoorBlockEntity,Byte> CLIENT_STATE=(door,state)->{};
    public byte state=CLOSED,skinIndex;public int openTicks,redstonePower;public long animStartTime;public boolean shouldUseBB;
    public final Set<BlockPos> activatedBlocks=new HashSet<>(4);
    public GenericDoorBlockEntity(BlockPos pos,BlockState block){super(GenericDoorContent.ENTITY.get(),pos,block);}
    public GenericDoorDeclaration declaration(){return ((GenericDoorBlock)getBlockState().getBlock()).declaration;}
    public float volume(){return declaration().soundVolume()*(muffled?.1F:1F);}
    public static BlockPos rotate(BlockPos position,int turns){return switch(turns&3){case 1->new BlockPos(-position.getZ(),position.getY(),position.getX());case 2->new BlockPos(-position.getX(),position.getY(),-position.getZ());case 3->new BlockPos(position.getZ(),position.getY(),-position.getX());default->position;};}
    public static int sourceRotation(Direction facing){return switch(facing){case SOUTH->2;case EAST->3;case WEST->1;default->0;};}
    public static void tick(Level level,BlockPos pos,BlockState block,GenericDoorBlockEntity door){
        if(!DummyableBlock.isCore(block))return;
        var type=door.declaration();
        if(!level.isClientSide&&(type.family247()||type==GenericDoorDeclaration.VAULT_DOOR||type==GenericDoorDeclaration.TRANSITION_SEAL)&&(door.state==OPENING||door.state==CLOSING)&&!com.hbm.door.family.GenericDoorFamilyContent.animationReady(door))return;
        if(type==GenericDoorDeclaration.VAULT_DOOR)com.hbm.door.vault.VaultDoorContent.beforeTick(door);
        if(door.state==OPENING)door.openTicks=Math.min(door.openTicks+1,type.timeToOpen());else if(door.state==CLOSING)door.openTicks=Math.max(door.openTicks-1,0);
        if(level.isClientSide){CLIENT_TICK.accept(door);return;}
        if(door.state==OPENING||door.state==CLOSING){
            var facing=DummyableBlock.facing(block);int rotation=sourceRotation(facing);if(facing.getAxis()==Direction.Axis.X)rotation=(rotation+2)&3;
            for(int i=0;i<type.ranges().length;i++){
                var range=type.ranges()[i];var start=new BlockPos(range[0],range[1],range[2]);float time=type.rangeTime(door.openTicks,i);
                for(int j=door.state==OPENING?0:Math.abs(range[3])-1;door.state==OPENING?j<Math.abs(range[3]):j>=0;j+=door.state==OPENING?1:-1){
                    if(door.state==OPENING?(float)j/Math.abs(range[3]-1)>time:(float)j/Math.abs(range[3]-1)<time)break;
                    for(int k=0;k<range[4];k++){
                        var add=switch(range[5]){case 0->new BlockPos(0,k,(int)Math.signum(range[3])*j);case 1->new BlockPos(k,(int)Math.signum(range[3])*j,0);default->new BlockPos((int)Math.signum(range[3])*j,k,0);};
                        var cell=rotate(start.offset(add),rotation).offset(pos);
                        if(cell.equals(pos))door.shouldUseBB=false;
                        else {var part=level.getBlockState(cell);if(part.is(block.getBlock())){int meta=part.getValue(DummyableBlock.LEGACY_META);if(door.state==OPENING&&meta<=5||door.state==CLOSING&&meta>=6&&meta<12)level.setBlock(cell,part.setValue(DummyableBlock.LEGACY_META,meta+(door.state==OPENING?6:-6)),3);}}
                    }
                }
            }
            if(door.state==OPENING&&door.openTicks==type.timeToOpen())door.state=OPEN;
            if(door.state==CLOSING&&door.openTicks==0)door.state=CLOSED;
            door.setChanged();
        }
        GenericDoorStatePacket.send(door);
        if(door.redstonePower==-1&&door.state==OPEN||door.redstonePower>0&&door.state==CLOSED)door.tryToggle(-1);
        if(door.redstonePower==-1){door.redstonePower=0;door.setChanged();}
    }
    public boolean tryToggle(Player player){
        if(isLocked()&&player==null)return false;
        if(state==CLOSED&&redstonePower>0)return false;
        if(state==CLOSED){if(!level.isClientSide&&canAccess(player)){state=OPENING;setChanged();}return true;}
        if(state==OPEN){if(!level.isClientSide&&canAccess(player)){state=CLOSING;setChanged();}return true;}return false;
    }
    public boolean tryToggle(int passcode){if(isLocked()&&passcode!=getPins())return false;if(state==CLOSED){if(!level.isClientSide){state=OPENING;setChanged();}return true;}if(state==OPEN){if(!level.isClientSide){state=CLOSING;setChanged();}return true;}return false;}
    public void open(){if(state==CLOSED){state=OPENING;setChanged();}}public void close(){if(state==OPEN){state=CLOSING;setChanged();}}
    public boolean cycleSkinIndex(){if(declaration().skins<=0)return false;skinIndex++;skinIndex%=declaration().skins;setChanged();return true;}
    public void receive(byte next,byte skin,boolean bounding){if(state!=next){CLIENT_STATE.accept(this,next);state=next;if(next==OPENING||next==CLOSING)animStartTime=System.currentTimeMillis();}skinIndex=skin;shouldUseBB=bounding;}
    public void updateRedstonePower(BlockPos cell){
        boolean powered=level.hasNeighborSignal(cell),contained=activatedBlocks.contains(cell);
        if(!contained&&powered){activatedBlocks.add(cell.immutable());if(redstonePower==-1)redstonePower=0;redstonePower++;setChanged();}
        else if(contained&&!powered){activatedBlocks.remove(cell);redstonePower--;if(redstonePower==0)redstonePower=-1;setChanged();}
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putByte("state",state);tag.putInt("openTicks",openTicks);tag.putLong("animStartTime",animStartTime);tag.putInt("redstoned",redstonePower);tag.putBoolean("shouldUseBB",shouldUseBB);if(declaration().skins>0)tag.putByte("skin",skinIndex);var cells=new CompoundTag();int i=0;for(var p:activatedBlocks){cells.putInt("x"+i,p.getX());cells.putInt("y"+i,p.getY());cells.putInt("z"+i,p.getZ());i++;}tag.put("activatedBlocks",cells);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);state=tag.getByte("state");openTicks=tag.getInt("openTicks");animStartTime=tag.getInt("animStartTime");redstonePower=tag.getInt("redstoned");shouldUseBB=tag.getBoolean("shouldUseBB");skinIndex=tag.getByte("skin");var cells=tag.getCompound("activatedBlocks");activatedBlocks.clear();for(int i=0;i<cells.getAllKeys().size()/3;i++)activatedBlocks.add(new BlockPos(cells.getInt("x"+i),cells.getInt("y"+i),cells.getInt("z"+i)));}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){byte before=state;long clientClock=animStartTime;loadAdditional(tag,lookup);byte next=state;state=before;animStartTime=clientClock;receive(next,skinIndex,shouldUseBB);}
    @Override public void setRemoved(){if(level!=null&&level.isClientSide)CLIENT_REMOVE.accept(this);super.setRemoved();}
    @Override public void onChunkUnloaded(){if(level!=null&&level.isClientSide)CLIENT_REMOVE.accept(this);super.onChunkUnloaded();}
}
