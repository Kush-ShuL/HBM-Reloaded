package com.hbm.machine.oilfracking;
import com.hbm.fluid.*;
import com.hbm.machine.oilextraction.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Actual source subclass differences only; the true OilDrillBase loop remains shared with well/pumpjack. */
public final class FrackingEntity extends OilDrillEntity {
    public FrackingEntity(BlockPos pos,BlockState state){super(FrackingContent.ENTITY.get(),pos,state);tanks=new FluidTank[]{new FluidTank(HbmFluids.OIL,64000),new FluidTank(HbmFluids.GAS,64000),new FluidTank(HbmFluids.fromName("FRACKSOL"),64000)};}
    @Override protected String title(){return "container.frackingTower";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new FrackingMenu(id,inventory,this,worldPosition);}
    @Override public OilDrillConfig.Values config(){var c=FrackingConfig.get();return new OilDrillConfig.Values(c.power(),c.consumption(),c.delay(),c.oil(),c.gasMin(),c.gasMax(),c.drain());}
    @Override protected BlockPos[] sourcePorts(){return new BlockPos[]{worldPosition};}
    @Override protected Direction[] sourceSides(BlockPos port){return new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST,Direction.DOWN};}
    @Override public boolean canConnect(Direction side){return side!=null&&(side.getAxis().isHorizontal()||side==Direction.DOWN);}
    public static void tick(Level level,BlockPos pos,BlockState state,FrackingEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);}
    @Override protected int drillDepth(ServerLevel server){return server.getMinBuildHeight();}
    @Override protected void onDrill(BlockPos pos,BlockState state){}
    @Override protected boolean canSuckBlock(BlockState state){return super.canSuckBlock(state)||state.is(block("ore_bedrock_oil"));}
    @Override protected boolean canPump(){boolean enough=tanks[2].getFill()>=FrackingConfig.get().solution();if(!enough)indicator=3;return enough;}
    @Override protected void doSuck(BlockPos pos){super.doSuck(pos);if(level.getBlockState(pos).is(block("ore_bedrock_oil")))onSuck(pos);}
    @Override protected void onSuck(BlockPos pos){var c=FrackingConfig.get();var state=level.getBlockState(pos);int oil=0,gas=0;if(state.is(block("ore_oil"))){oil=c.oil();gas=c.gasMin()+level.random.nextInt(c.gasMax()-c.gasMin()+1);if(level.random.nextDouble()<c.drain())level.setBlock(pos,block("ore_oil_empty").defaultBlockState(),3);}if(state.is(block("ore_bedrock_oil"))){oil=c.bedrockOil();gas=c.bedrockGasMin()+level.random.nextInt(c.bedrockGasMax()-c.bedrockGasMin()+1);}tanks[0].setFill(Math.min(tanks[0].getMaxFill(),tanks[0].getFill()+oil));tanks[1].setFill(Math.min(tanks[1].getMaxFill(),tanks[1].getFill()+gas));tanks[2].setFill(tanks[2].getFill()-c.solution());SurfaceOilSpot.generate((ServerLevel)level,worldPosition.getX(),worldPosition.getZ(),c.destructionRange(),10,false);}
    public static boolean sourceUpgrade(net.minecraft.world.item.ItemStack stack){var id=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&java.util.Set.of("upgrade_speed_1","upgrade_speed_2","upgrade_speed_3","upgrade_effect_1","upgrade_effect_2","upgrade_effect_3","upgrade_power_1","upgrade_power_2","upgrade_power_3","upgrade_fortune_1","upgrade_fortune_2","upgrade_fortune_3","upgrade_afterburn_1","upgrade_afterburn_2","upgrade_afterburn_3","upgrade_overdrive_1","upgrade_overdrive_2","upgrade_overdrive_3","upgrade_radius","upgrade_health","upgrade_smelter","upgrade_shredder","upgrade_centrifuge","upgrade_crystallizer","upgrade_nullifier","upgrade_screm","upgrade_gc_speed","upgrade_5g").contains(id.getPath());}
    @Override public void setItem(int slot,net.minecraft.world.item.ItemStack stack){super.setItem(slot,stack);if(level!=null&&slot>=5&&slot<=7&&!OilDrillEntity.upgrade(stack)&&sourceUpgrade(stack))level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1,1);}
    @Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[0],tanks[1]};}
    @Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[2]};}
}
