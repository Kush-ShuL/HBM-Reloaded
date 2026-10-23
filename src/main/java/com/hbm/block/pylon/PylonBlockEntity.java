package com.hbm.block.pylon;

import com.hbm.energy.EnergyConductor;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.NodeConnection;
import com.hbm.energy.PowerNode;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmOreDictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Original pylon endpoint metadata, mount positions and explicit remote native-HE links. */
public final class PylonBlockEntity extends BlockEntity implements EnergyConductor, com.hbm.multiblock.MuffledMachine {
    private final List<BlockPos> connected=new ArrayList<>();
    private PowerNode node;
    private int color;
    private boolean loaded=true;
    private boolean chunkUnloaded;
    public boolean muffled,tilted;
    private static final Map<String,Integer> DYES=Map.ofEntries(Map.entry("black",1973019),Map.entry("red",11743532),Map.entry("green",3887386),Map.entry("brown",5320730),Map.entry("blue",2437522),Map.entry("purple",8073150),Map.entry("cyan",2651799),Map.entry("silver",11250603),Map.entry("lightgray",11250603),Map.entry("gray",4408131),Map.entry("pink",14188952),Map.entry("lime",4312372),Map.entry("yellow",14602026),Map.entry("lightblue",6719955),Map.entry("magenta",12801229),Map.entry("orange",15435844),Map.entry("white",15790320));
    public PylonBlockEntity(BlockPos pos,BlockState state) { super(PylonContent.ENTITY.get(),pos,state); }
    public PylonKind kind() { return getBlockState().getBlock() instanceof ConnectorBlock block?block.kind:((PylonBlock)getBlockState().getBlock()).kind; }
    public int legacyMetadata() { return kind().connector()?getBlockState().getValue(ConnectorBlock.FACING).get3DDataValue():getBlockState().getValue(DummyableBlock.LEGACY_META); }
    public Direction facing() { return kind().connector()?getBlockState().getValue(ConnectorBlock.FACING):DummyableBlock.facing(getBlockState()); }
    public int color() { return color; }
    public List<BlockPos> connected() { return List.copyOf(connected); }
    @Override public PowerNode energyNode() { return node; }
    @Override public boolean isLoaded() { return loaded&&level!=null&&!isRemoved(); }
    @Override public boolean canConnect(Direction side) {
        if(side==null) return false;
        var kind=kind();
        if(kind.connector()) return side==facing().getOpposite();
        if(kind.medium()) return kind.transformer()&&side==facing().getOpposite();
        if(kind==PylonKind.WOOD||kind==PylonKind.STEEL) return side!=Direction.DOWN;
        // Original large/substation inherit TileEntityCableBaseNT.canConnect; node ports still restrict topology.
        return true;
    }
    public Vec3[] mounts() {
        var kind=kind(); var direction=facing();
        if(kind==PylonKind.CONNECTOR) return new Vec3[]{new Vec3(.5,.5,.5)};
        if(kind==PylonKind.SUPER_CONNECTOR) return new Vec3[]{new Vec3(.5+direction.getStepX()*.375,.5+direction.getStepY()*.375,.5+direction.getStepZ()*.375)};
        if(kind==PylonKind.WOOD||kind==PylonKind.STEEL) return new Vec3[]{new Vec3(.5,5.5,.5)};
        if(kind.medium()) return new Vec3[]{new Vec3(.5,7.5,.5),new Vec3(.5+direction.getStepX(),7.5,.5+direction.getStepZ()),new Vec3(.5+direction.getStepX()*2,7.5,.5+direction.getStepZ()*2)};
        double angle=kind==PylonKind.SUBSTATION?(direction.getAxis()==Direction.Axis.X?(float)Math.PI*.5F:0):switch(direction) { case WEST->(float)Math.PI*.25F; case SOUTH->(float)Math.PI*.5F; case EAST->(float)Math.PI*.75F; default->0; };
        // 1.7.10 Vec3.rotateAroundY uses x'=x*cos+z*sin, z'=z*cos-x*sin.
        double length=kind==PylonKind.SUBSTATION?1:3.375;
        double x=length*Mth.cos((float)angle),z=-length*Mth.sin((float)angle);
        if(kind==PylonKind.SUBSTATION) return new Vec3[]{new Vec3(.5+x*.5,5.25,.5+z*.5),new Vec3(.5+x*1.5,5.25,.5+z*1.5),new Vec3(.5-x*.5,5.25,.5-z*.5),new Vec3(.5-x*1.5,5.25,.5-z*1.5)};
        double top=.75+.0625;
        return new Vec3[]{new Vec3(.5+x,11.5+top,.5+z),new Vec3(.5+x,11.5-top,.5+z),new Vec3(.5-x,11.5+top,.5-z),new Vec3(.5-x,11.5-top,.5-z)};
    }
    public Vec3 connectionPoint() { return (kind()==PylonKind.SUBSTATION?new Vec3(.5,5.25,.5):mounts()[0]).add(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()); }
    public static int canLink(PylonBlockEntity first,PylonBlockEntity second) {
        if(first.kind().wires!=second.kind().wires) return 1;
        if(first==second) return 2;
        return Math.min(first.kind().range,second.kind().range)>=first.connectionPoint().distanceTo(second.connectionPoint())?0:3;
    }
    public void addConnection(BlockPos target) {
        connected.add(target.immutable());
        ensureNode(); if(node!=null) node.addRemoteConnection(target);
        sync();
    }
    public static int dyeColor(ItemStack stack) {
        if(stack.isEmpty()) return 0;
        int value=0;
        for(String name:HbmOreDictionary.names(stack)) if(name.length()>3&&name.startsWith("dye")) { value=DYES.getOrDefault(name.substring(3).toLowerCase(java.util.Locale.US),0); if(value!=0) break; }
        if(value==0&&stack.getItem() instanceof DyeItem dye) value=DYES.getOrDefault(dye.getDyeColor().getName().replace("_",""),0);
        return value;
    }
    public boolean setColor(ItemStack stack) {
        int value=dyeColor(stack);
        if(value==0||value==color) return false;
        stack.shrink(1); color=value; sync(); return true;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,PylonBlockEntity pylon) { pylon.ensureNode(); }
    public void ensureNode() {
        if(!(level instanceof ServerLevel server)||!isLoaded()||node!=null&&!node.isExpired()) return;
        var space=HbmEnergy.nodespace(server);
        node=space.getNode(worldPosition);
        if(node!=null&&!node.isExpired()) return;
        var positions=new ArrayList<BlockPos>(); positions.add(worldPosition);
        var points=new ArrayList<NodeConnection>(); var kind=kind();
        if(kind==PylonKind.SUBSTATION) {
            for(int x:new int[]{-1,1}) for(int z:new int[]{-1,1}) {
                BlockPos port=worldPosition.offset(x,0,z); positions.add(port);
                points.add(new NodeConnection(port.relative(x>0?Direction.EAST:Direction.WEST),x>0?Direction.EAST:Direction.WEST));
                points.add(new NodeConnection(port.relative(z>0?Direction.SOUTH:Direction.NORTH),z>0?Direction.SOUTH:Direction.NORTH));
            }
        } else if(kind.connector()||kind.transformer()) {
            Direction back=facing().getOpposite(); points.add(new NodeConnection(worldPosition.relative(back),back));
        } else if(kind==PylonKind.WOOD||kind==PylonKind.STEEL) {
            for(Direction direction:Direction.Plane.HORIZONTAL) points.add(new NodeConnection(worldPosition.relative(direction),direction));
        }
        node=new PowerNode(positions.toArray(BlockPos[]::new)).setConnections(points.toArray(NodeConnection[]::new)).setAcceptsRemote(true);
        for(BlockPos target:connected) node.addRemoteConnection(target);
        space.createNode(node);
    }
    private void removeNode() {
        if(node!=null&&level instanceof ServerLevel server&&!node.isExpired()) HbmEnergy.nodespace(server).destroyNode(node);
        node=null;
    }
    public void disconnectAll() {
        if(!(level instanceof ServerLevel)) return;
        for(BlockPos target:connected) {
            if(target.equals(worldPosition)) continue;
            if(level.getBlockEntity(target) instanceof PylonBlockEntity other) {
                other.removeNode(); other.connected.removeIf(worldPosition::equals); other.sync();
            }
        }
        removeNode();
    }
    private void sync() { setChanged(); if(level!=null) level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),Block.UPDATE_CLIENTS); }
    @Override public void onLoad() { super.onLoad(); loaded=true; chunkUnloaded=false; ensureNode(); }
    // Original UNINOS keeps conductor "souls" through unloaded intermediate chunks.
    // NeoForge then invokes setRemoved, so distinguish that call from actual block removal.
    @Override public void onChunkUnloaded() { loaded=false; chunkUnloaded=true; super.onChunkUnloaded(); }
    @Override public void setRemoved() { loaded=false; if(!chunkUnloaded) removeNode(); super.setRemoved(); }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) {
        super.saveAdditional(tag,lookup); tag.putInt("color",color); tag.putInt("conCount",connected.size());
        tag.putBoolean("muffled",muffled); tag.putBoolean("tilted",tilted);
        for(int i=0;i<connected.size();i++) { BlockPos pos=connected.get(i); tag.putIntArray("con"+i,new int[]{pos.getX(),pos.getY(),pos.getZ()}); }
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) {
        super.loadAdditional(tag,lookup); color=tag.getInt("color"); muffled=tag.getBoolean("muffled"); tilted=tag.getBoolean("tilted"); connected.clear();
        for(int i=0;i<tag.getInt("conCount");i++) { int[] pos=tag.getIntArray("con"+i); if(pos.length==3) connected.add(new BlockPos(pos[0],pos[1],pos[2])); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
