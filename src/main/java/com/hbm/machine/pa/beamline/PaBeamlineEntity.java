package com.hbm.machine.pa.beamline;
import com.hbm.machine.pa.physics.*;
import com.hbm.multiblock.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Real3-cell beamline: axis admission/exit, source window toggle and transient particle flash, no fake tanks/energy. */
public final class PaBeamlineEntity extends BlockEntity implements PaParticleUser,MuffledMachine{
    public boolean window,didPass;public float light,prevLight;private boolean muffled;
    public PaBeamlineEntity(BlockPos pos,BlockState state){super(PaBeamlineContent.ENTITY.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
    private ForgeDirection axis(){return ForgeDirection.getOrientation(getBlockState().getValue(DummyableBlock.LEGACY_META)-10).getRotation(ForgeDirection.DOWN);}
    public static void tick(Level level,BlockPos pos,BlockState state,PaBeamlineEntity beamline){if(level instanceof ServerLevel server){var snapshot=beamline.getUpdateTag(server.registryAccess());var packet=ClientboundBlockEntityDataPacket.create(beamline,(ignored,lookup)->snapshot);for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<22500)player.connection.send(packet);}else{beamline.prevLight=beamline.light;if(beamline.light>0)beamline.light-=.25F;if(beamline.light>beamline.prevLight)beamline.prevLight=beamline.light;}}
    @Override public boolean canParticleEnter(PaParticle particle,ForgeDirection dir,int x,int y,int z){var axis=axis();return worldPosition.getX()-axis.offsetX==x&&worldPosition.getY()==y&&worldPosition.getZ()-axis.offsetZ==z&&axis==dir;}
    @Override public void onEnter(PaParticle particle,ForgeDirection dir){particle.addDistance(3);didPass=true;}@Override public BlockPos getExitPos(PaParticle particle){var axis=axis();return worldPosition.offset(axis.offsetX*2,0,axis.offsetZ*2);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("window",window);tag.putBoolean("muffled",muffled);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);window=tag.getBoolean("window");muffled=tag.getBoolean("muffled");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("window",window);tag.putBoolean("didPass",didPass);tag.putBoolean("muffled",muffled);didPass=false;return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){window=tag.getBoolean("window");didPass=tag.getBoolean("didPass");muffled=tag.getBoolean("muffled");if(didPass)light=2F;}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
}
