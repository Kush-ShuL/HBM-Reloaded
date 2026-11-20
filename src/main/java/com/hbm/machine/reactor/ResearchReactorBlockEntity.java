package com.hbm.machine.reactor;
import com.hbm.api.computer.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Retired research reactor: retain its model, inventory and source computer readouts. */
public final class ResearchReactorBlockEntity extends ReactorInventory {
    public double lastLevel,rodLevel,targetLevel;
    public int heat,totalFlux;public byte water;public final int[] slotFlux=new int[12];
    public static final int MAX_HEAT=50000;private static final int[] ACCESS={0,1,2,3,4,5,6,7,8,9,10,11};
    public ResearchReactorBlockEntity(BlockPos pos,BlockState state){super(ReactorContent.RESEARCH_ENTITY.get(),pos,state,12);}
    @Override protected String nameKey(){return "container.reactorResearch";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return null;}
    public static void tick(Level level,BlockPos pos,BlockState state,ResearchReactorBlockEntity reactor){
        if(level.isClientSide){reactor.lastLevel=reactor.rodLevel;return;}
        reactor.serverTick((ServerLevel)level);
    }
    private void serverTick(ServerLevel server){
        var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<150*150)player.connection.send(packet);dispatchScope.commit();}
    }
    public void setTarget(double value){targetLevel=Mth.clamp(value,0,1);setChanged();}
    public int temperature(){return (int)Math.round(heat*.00002*980+20);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false; /* Original fuelMap.containsValue uses ItemStack reference identity; the stored output is always copied. */}
    @Override protected void writeState(CompoundTag tag){tag.putInt("heat",heat);tag.putByte("water",water);tag.putDouble("level",rodLevel);tag.putDouble("targetLevel",targetLevel);tag.putInt("totalFlux",totalFlux);tag.putIntArray("slotFlux",slotFlux);}
    @Override protected void readState(CompoundTag tag){heat=tag.getInt("heat");water=tag.getByte("water");rodLevel=tag.getDouble("level");targetLevel=tag.getDouble("targetLevel");totalFlux=tag.getInt("totalFlux");var values=tag.getIntArray("slotFlux");Arrays.fill(slotFlux,0);System.arraycopy(values,0,slotFlux,0,Math.min(12,values.length));}
    @Override public java.util.List<ProgressView> progressBars(){return java.util.List.of(new ProgressView("gui.hbm.reactor.heat",heat,MAX_HEAT,0xED7139),new ProgressView("gui.hbm.reactor.control",rodLevel,1,0x08FF00));}
    @Override public String computerType(){return "research_reactor";}
    @Override public String[] computerMethods(){return new String[]{"getTemp","getLevel","getTargetLevel","getFlux","getInfo","setLevel"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments){return switch(method){case "getTemp"->new Object[]{heat};case "getLevel"->new Object[]{rodLevel*100};case "getTargetLevel"->new Object[]{targetLevel};case "getFlux"->new Object[]{totalFlux};case "getInfo"->new Object[]{heat,rodLevel,targetLevel,totalFlux};case "setLevel"->{setTarget(arguments.checkDouble(0)/100);yield new Object[]{};}default->throw new ComputerException("No such method");};}
}
