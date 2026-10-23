package com.hbm.item.structuretandem;

import com.hbm.world.structure.tandem.TandemPlacement;
import com.hbm.world.structure.templates.TemplateStateMappings;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source settings/armed condition; only the selected modern cold operation is additional current-runtime state. */
public final class TandemEntity extends BlockEntity {
    public String pool="default",target="default",condition="";
    public BlockState replacement=Blocks.AIR.defaultBlockState();
    public boolean rollable=true,armed;
    public CompoundTag pending;
    public transient TandemPlacement.Work work;
    public transient boolean executing;
    public transient long lastAttempt=Long.MIN_VALUE;
    private CompoundTag lastSynced;
    public TandemEntity(BlockPos pos,BlockState state){super(TandemContent.ENTITY.get(),pos,state);}
    public CompoundTag settings(){var tag=new CompoundTag();tag.putString("pool",pool);tag.putString("target",target);tag.put("replacement",NbtUtils.writeBlockState(replacement));tag.putBoolean("roll",rollable);return tag;}
    private void readSettings(CompoundTag tag){pool=tag.getString("pool");target=tag.getString("target");if(tag.contains("replacement",Tag.TAG_COMPOUND))replacement=TemplateStateMappings.parse(tag.getCompound("replacement"));rollable=tag.getBoolean("roll");}
    public void edit(CompoundTag tag){TandemPlacement.cancel(this);readSettings(tag);setChanged();}
    public void replaceWith(BlockState state){TandemPlacement.cancel(this);replacement=state;setChanged();}
    public void arm(String condition){this.condition=condition==null?"":condition;armed=condition!=null;setChanged();}
    public static void tick(Level level,BlockPos pos,BlockState state,TandemEntity tile){
        if(!(level instanceof ServerLevel server))return;
        TandemPlacement.advance(server,tile);
        var current=tile.settings();
        if(!current.equals(tile.lastSynced)||level.getGameTime()%20==0){
            tile.lastSynced=current.copy();var packet=tile.getUpdatePacket();
            // Source networkPackNT(15) is the audience radius, not a fifteen-tick interval.
            for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=225)player.connection.send(packet);
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.merge(settings());tag.putBoolean("armed",armed);tag.putString("condition",condition);if(pending!=null)tag.put("pending",pending.copy());}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);readSettings(tag);armed=tag.getBoolean("armed");condition=tag.getString("condition");pending=tag.contains("pending",Tag.TAG_COMPOUND)?tag.getCompound("pending").copy():null;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return settings();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){readSettings(tag);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public void setRemoved(){super.setRemoved();TandemPlacement.unloaded(this);}
    @Override public void onChunkUnloaded(){super.onChunkUnloaded();TandemPlacement.unloaded(this);}
}
