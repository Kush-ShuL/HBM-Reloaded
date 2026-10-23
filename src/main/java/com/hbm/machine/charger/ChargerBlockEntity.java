package com.hbm.machine.charger;

import com.hbm.energy.*;
import com.hbm.item.battery.HbmBattery;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original bufferless equipment charger; all native network arithmetic stays exact. */
public final class ChargerBlockEntity extends BlockEntity implements EnergyReceiver,EnergyConductor, com.hbm.multiblock.MuffledMachine {
    private static final EquipmentSlot[] EQUIPMENT={EquipmentSlot.MAINHAND,EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final BigInteger FIVE=BigInteger.valueOf(5);
    private List<Player> players=List.of();
    private BigInteger demand=BigInteger.ZERO;
    private PowerNode node;
    private int lastOperation,usingTicks,lastUsingTicks;
    private Direction nodeDirection;
    private boolean particles,loaded=true;
    public boolean muffled,tilted;
    public ChargerBlockEntity(BlockPos pos,BlockState state){super(ChargerContent.ENTITY.get(),pos,state);}
    public int usingTicks(){return usingTicks;}
    public int lastUsingTicks(){return lastUsingTicks;}
    public static void tick(Level level,BlockPos pos,BlockState state,ChargerBlockEntity charger){
        if(level instanceof ServerLevel server)charger.serverTick(server);
        charger.lastUsingTicks=charger.usingTicks;
        if((charger.demand.signum()>0||charger.particles)&&charger.usingTicks<20){if(++charger.usingTicks==2)charger.sound(SoundEvents.PISTON_EXTEND,.5F,.5F);}
        if(charger.demand.signum()<=0&&!charger.particles&&charger.usingTicks>0){if(--charger.usingTicks==4)charger.sound(SoundEvents.PISTON_CONTRACT,.5F,.5F);}
        Direction direction=ChargerBlock.port(state);
        if(level.isClientSide&&charger.particles){int dx=direction==null?0:direction.getStepX(),dz=direction==null?0:direction.getStepZ();var random=level.random;
            level.addParticle(ParticleTypes.ENCHANTED_HIT,pos.getX()+.5+random.nextDouble()*.0625+dx*.75,pos.getY()+.1,pos.getZ()+.5+random.nextDouble()*.0625+dz*.75,-dx+random.nextGaussian()*.1,0,-dz+random.nextGaussian()*.1);}
    }
    private void serverTick(ServerLevel server){
        Direction direction=ChargerBlock.port(getBlockState());
        if(node!=null&&nodeDirection!=direction)removeNode();
        if(node==null||node.isExpired()){
            node=HbmEnergy.nodespace(server).getNode(worldPosition);
            if(node==null||node.isExpired()){
                node=new PowerNode(worldPosition).setConnections(direction==null?new NodeConnection[0]:new NodeConnection[]{new NodeConnection(worldPosition.relative(direction),direction)});
                HbmEnergy.nodespace(server).createNode(node);
            }
            nodeDirection=direction;
        }
        if(node.hasValidNet())node.addReceiver(this);
        players=server.getEntitiesOfClass(Player.class,new AABB(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),worldPosition.getX()+1,worldPosition.getY()+.5,worldPosition.getZ()+1));
        demand=BigInteger.ZERO;
        for(var player:players)for(var slot:EQUIPMENT){var stack=player.getItemBySlot(slot);if(stack.getItem() instanceof HbmBattery battery)demand=demand.add(BigInteger.valueOf(Math.min(battery.getMaxCharge(stack)-battery.getCharge(stack),battery.getChargeRate(stack))).max(BigInteger.ZERO));}
        particles=lastOperation>0;
        if(particles){lastOperation--;if(server.getGameTime()%20==0)sound(SoundEvents.FIRE_EXTINGUISH,.2F,.5F);}
        var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);
    }
    private void sound(net.minecraft.sounds.SoundEvent sound,float volume,float pitch){
        if(level==null)return;
        if(level.isClientSide)level.playLocalSound(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,sound,SoundSource.BLOCKS,volume,pitch,false);
        else level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,sound,SoundSource.BLOCKS,volume,pitch);
    }
    @Override public BigInteger transferPowerExact(BigInteger offered){
        if(offered.signum()<0)throw new IllegalArgumentException("Negative native HE");
        if(usingTicks<20||offered.signum()==0)return offered;
        BigInteger remaining=offered;
        for(var player:players)for(var slot:EQUIPMENT){
            if(remaining.signum()==0)return BigInteger.ZERO;
            var stack=player.getItemBySlot(slot);if(!(stack.getItem() instanceof HbmBattery battery))continue;
            long required=Math.max(0,Math.min(battery.getMaxCharge(stack)-battery.getCharge(stack),battery.getChargeRate(stack)));
            BigInteger amount=BigInteger.valueOf(required).min(remaining.divide(FIVE).max(BigInteger.ONE)).min(remaining);
            battery.chargeBattery(stack,amount.longValueExact());remaining=remaining.subtract(amount);lastOperation=4;
        }
        return remaining;
    }
    @Override public long transferPower(long offered){return transferPowerExact(BigInteger.valueOf(offered)).longValueExact();}
    @Override public long getPower(){return 0;}
    @Override public long getMaxPower(){return demand.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
    @Override public BigInteger getPowerExact(){return BigInteger.ZERO;}
    @Override public BigInteger getMaxPowerExact(){return demand;}
    @Override public BigInteger getReceiverSpeedExact(){return demand;}
    @Override public void setPower(long ignored){}
    @Override public void setPowerExact(BigInteger ignored){}
    @Override public PowerNode energyNode(){return node;}
    @Override public boolean canConnect(Direction side){return side!=null&&side==ChargerBlock.port(getBlockState());}
    @Override public boolean isLoaded(){return loaded&&!isRemoved()&&level!=null;}
    private void removeNode(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void onLoad(){super.onLoad();loaded=true;}
    @Override public void onChunkUnloaded(){loaded=false;removeNode();super.onChunkUnloaded();}
    @Override public void setRemoved(){loaded=false;removeNode();super.setRemoved();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putString("charge",demand.toString());tag.putBoolean("particles",particles);return tag;}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);demand=tag.contains("charge")?new BigInteger(tag.getString("charge")):BigInteger.ZERO;particles=tag.getBoolean("particles");}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
