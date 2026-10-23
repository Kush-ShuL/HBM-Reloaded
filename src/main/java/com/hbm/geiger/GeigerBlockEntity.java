package com.hbm.geiger;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** TileEntityGeiger's transient timer/sample, comparator refresh, sound bands and computer callback. */
public final class GeigerBlockEntity extends BlockEntity implements ComputerControl {
    private int timer;
    private float ticker;
    public GeigerBlockEntity(BlockPos pos,BlockState state) { super(GeigerContent.ENTITY.get(),pos,state); }
    public float sample() { return ticker; }
    public float check() { return level instanceof ServerLevel server?HbmRadiation.getRadiation(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ()):ticker; }
    public static void tick(Level level,BlockPos pos,BlockState state,GeigerBlockEntity geiger) {
        if(!(level instanceof ServerLevel server)) return;
        geiger.timer++;
        if(geiger.timer==10) {
            geiger.timer=0; geiger.ticker=geiger.check();
            level.updateNeighborsAt(pos,state.getBlock()); level.updateNeighbourForOutputSignal(pos,state.getBlock());
            level.sendBlockUpdated(pos,state,state,Block.UPDATE_CLIENTS);
        }
        if(geiger.timer%5==0) {
            int sound=GeigerClicks.block(geiger.ticker,server.random::nextInt);
            // X5846 sounds.json assigns geiger1..6 to the player category, including fixed instruments.
            if(sound>0) level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("item.geiger"+sound),SoundSource.PLAYERS,1,1);
        }
    }
    @Override public String computerType() { return "ntm_geiger"; }
    @Override public String[] computerMethods() { return new String[]{"getRads"}; }
    @Override public Object[] callComputer(String method,ComputerArguments arguments) throws ComputerException {
        if(method.equals("getRads")) return new Object[]{check()};
        throw new ComputerException("Unknown method: "+method);
    }
    /** Original ROR introspection keys, ready for the actual ROR dispatcher when that module is ported. */
    public String[] getFunctionInfo() { return new String[]{"VAL:rad"}; }
    public String provideRORValue(String name) { return name.equals("VAL:rad")?""+(int)Math.ceil(ticker):null; }
    /** Equivalent data for the original Energy Control sensor, without a dependency on that unported mod. */
    public String radiationInfo() { int rads=(int)Math.ceil(ticker);return GeigerReadings.rateColor(rads).toString()+rads+" RAD/s"; }
    // Source timer/ticker were never persisted. Only the client update payload carries the sample.
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag=super.getUpdateTag(lookup);tag.putFloat("ticker",ticker);return tag; }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { super.handleUpdateTag(tag,lookup);ticker=tag.getFloat("ticker"); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
}
