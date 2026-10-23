package com.hbm.machine.reactor;
import com.hbm.api.computer.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.radiation.HbmRadiation;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** Exact X5846 research-reactor step order: rod motion, directed slot flux, water cooling, meltdown and shield leakage. */
public final class ResearchReactorBlockEntity extends ReactorInventory {
    public double lastLevel,rodLevel,targetLevel;
    public int heat,totalFlux;public byte water;public final int[] slotFlux=new int[12];
    public static final int MAX_HEAT=50000;private static final int[] ACCESS={0,1,2,3,4,5,6,7,8,9,10,11};
    private static final int[][] NEIGHBORS={{1,5},{0,6},{3,7},{2,4,8},{3,9},{0,6,10},{1,5,11},{2,8},{3,7,9},{4,8},{5,11},{6,10}};
    public ResearchReactorBlockEntity(BlockPos pos,BlockState state){super(ReactorContent.RESEARCH_ENTITY.get(),pos,state,12);}
    @Override protected String nameKey(){return "container.reactorResearch";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ResearchReactorMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,ResearchReactorBlockEntity reactor){
        if(level.isClientSide){reactor.lastLevel=reactor.rodLevel;return;}
        reactor.serverTick((ServerLevel)level);
    }
    private void serverTick(ServerLevel server){
        if(rodLevel<targetLevel){rodLevel+=.04;if(rodLevel>=targetLevel)rodLevel=targetLevel;}
        if(rodLevel>targetLevel){rodLevel-=.04;if(rodLevel<=targetLevel)rodLevel=targetLevel;}
        totalFlux=0;if(rodLevel>0)reaction();
        if(heat>0){water=countWater();if(water>0)heat-=(heat*.07F*water/12);else heat--;if(heat<0)heat=0;}
        if(heat>MAX_HEAT){explode(server);return;}
        if(rodLevel>0&&heat>0&&!(blocksRad(worldPosition.offset(1,1,0))&&blocksRad(worldPosition.offset(-1,1,0))&&blocksRad(worldPosition.offset(0,1,1))&&blocksRad(worldPosition.offset(0,1,-1))))HbmRadiation.incrementRad(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),(float)heat/MAX_HEAT*50F);
        setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<150*150)player.connection.send(packet);
    }
    private void reaction(){
        for(int i=0;i<12;i++){
            var stack=slots[i];if(stack.isEmpty()){slotFlux[i]=0;continue;}
            if(stack.getItem() instanceof PlateFuelItem plate){
                int output=plate.react(stack,slotFlux[i]);heat+=output*2;slotFlux[i]=0;totalFlux+=output;
                if(PlateFuelItem.life(stack)>plate.lifeTime)slots[i]=ReactorContent.spent(plate);
                for(int neighbor:NEIGHBORS[i])slotFlux[neighbor]+=(int)(output*rodLevel);
            }else{
                var source=id("meteorite_sword_bred");var result=id("meteorite_sword_irradiated");
                if(BuiltInRegistries.ITEM.containsKey(source)&&BuiltInRegistries.ITEM.containsKey(result)&&stack.is(BuiltInRegistries.ITEM.get(source)))slots[i]=new ItemStack(BuiltInRegistries.ITEM.get(result));
                slotFlux[i]=0;
            }
        }
    }
    private static ResourceLocation id(String id){return ResourceLocation.fromNamespaceAndPath("hbm",id);}
    private boolean water(BlockPos pos){return level!=null&&level.getFluidState(pos).is(FluidTags.WATER);}
    public byte countWater(){byte result=0;for(var side:Direction.values()){if(side.getAxis()==Direction.Axis.Y){if(water(worldPosition.offset(0,1+side.getStepY()*2,0)))result++;}else for(int i=0;i<3;i++)if(water(worldPosition.offset(side.getStepX(),i,side.getStepZ())))result++;}return result;}
    public boolean isSubmerged(){return water(worldPosition.offset(1,1,0))||water(worldPosition.offset(-1,1,0))||water(worldPosition.offset(0,1,1))||water(worldPosition.offset(0,1,-1));}
    private boolean blocksRad(BlockPos pos){var state=level.getBlockState(pos);var fluid=state.getFluidState();if(fluid.is(FluidTags.WATER)&&fluid.isSource())return true;String key=BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();return key.equals("hbm:block_lead")||key.equals("hbm:block_desh")||key.equals("hbm:machine_reactor_small")||key.equals("hbm:machine_reactor")||state.getBlock().getExplosionResistance()>=100;}
    private void explode(ServerLevel server){
        Arrays.fill(slots,ItemStack.EMPTY);server.removeBlock(worldPosition,false);
        for(var side:Direction.values())if(side.getAxis()==Direction.Axis.Y){var pos=worldPosition.offset(0,1+side.getStepY()*2,0);if(water(pos))server.removeBlock(pos,false);}else for(int i=0;i<3;i++){var pos=worldPosition.offset(side.getStepX(),i,side.getStepZ());if(water(pos))server.removeBlock(pos,false);}
        server.explode(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),18F,Level.ExplosionInteraction.BLOCK);
        // Required real source blocks; absence is a dependency failure, never a substitute material.
        var steel=BuiltInRegistries.BLOCK.getOptional(id("deco_steel")).orElseThrow(()->new IllegalStateException("Research reactor requires original deco_steel"));
        var corium=BuiltInRegistries.BLOCK.getOptional(id("corium_block")).orElseThrow(()->new IllegalStateException("Research reactor requires original corium_block"));
        server.setBlock(worldPosition,steel.defaultBlockState(),3);server.setBlock(worldPosition.above(),corium.defaultBlockState(),3);server.setBlock(worldPosition.above(2),steel.defaultBlockState(),3);
        HbmRadiation.incrementRad(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),50);
        if(ReactorConfig.ELEMENTALS.get())for(var player:server.getEntitiesOfClass(Player.class,new net.minecraft.world.phys.AABB(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5).inflate(100))){var tag=player.getPersistentData();var persistent=tag.getCompound(Player.PERSISTED_NBT_TAG);persistent.putBoolean("radMark",true);tag.put(Player.PERSISTED_NBT_TAG,persistent);}
    }
    public void setTarget(double value){targetLevel=Mth.clamp(value,0,1);setChanged();}
    public int temperature(){return (int)Math.round(heat*.00002*980+20);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot>=0&&slot<=0&&stack.getItem() instanceof PlateFuelItem;}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false; /* Original fuelMap.containsValue uses ItemStack reference identity; the stored output is always copied. */}
    @Override protected void writeState(CompoundTag tag){tag.putInt("heat",heat);tag.putByte("water",water);tag.putDouble("level",rodLevel);tag.putDouble("targetLevel",targetLevel);tag.putInt("totalFlux",totalFlux);tag.putIntArray("slotFlux",slotFlux);}
    @Override protected void readState(CompoundTag tag){heat=tag.getInt("heat");water=tag.getByte("water");rodLevel=tag.getDouble("level");targetLevel=tag.getDouble("targetLevel");totalFlux=tag.getInt("totalFlux");var values=tag.getIntArray("slotFlux");Arrays.fill(slotFlux,0);System.arraycopy(values,0,slotFlux,0,Math.min(12,values.length));}
    @Override public java.util.List<ProgressView> progressBars(){return java.util.List.of(new ProgressView("gui.hbm.reactor.heat",heat,MAX_HEAT,0xED7139),new ProgressView("gui.hbm.reactor.control",rodLevel,1,0x08FF00));}
    @Override public String computerType(){return "research_reactor";}
    @Override public String[] computerMethods(){return new String[]{"getTemp","getLevel","getTargetLevel","getFlux","getInfo","setLevel"};}
    @Override public Object[] callComputer(String method,ComputerArguments arguments){return switch(method){case "getTemp"->new Object[]{heat};case "getLevel"->new Object[]{rodLevel*100};case "getTargetLevel"->new Object[]{targetLevel};case "getFlux"->new Object[]{totalFlux};case "getInfo"->new Object[]{heat,rodLevel,targetLevel,totalFlux};case "setLevel"->{setTarget(arguments.checkDouble(0)/100);yield new Object[]{};}default->throw new ComputerException("No such method");};}
}
