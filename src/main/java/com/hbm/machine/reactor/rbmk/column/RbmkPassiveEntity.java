package com.hbm.machine.reactor.rbmk.column;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.residue.RbmkDebrisEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Exact Blank/Moderator/Reflector/Absorber BEs; their active function is heat and neutron transport. */
public final class RbmkPassiveEntity extends RbmkColumnEntity implements RbmkFluxColumn.Absorber {
    public RbmkPassiveEntity(BlockPos pos, BlockState state) { super(RbmkColumnsContent.PASSIVE.get(), pos, state); }
    public RbmkColumnKind kind() { return ((RbmkPassiveBlock)getBlockState().getBlock()).kind; }
    @Override public RbmkFluxColumn.Type neutronType() { return kind().neutronType; }
    @Override public String consoleType() { return kind().consoleType; }
    @Override public void addHeat(double amount) { heat += amount; }
    public static void tick(Level level, BlockPos pos, BlockState state, RbmkPassiveEntity machine) { if (level instanceof ServerLevel world) machine.baseTick(world); }
    @Override public void onMelt(int reduce) {
        boolean graphite = kind() == RbmkColumnKind.MODERATOR;
        int count = (graphite ? 2 : 1) + level.random.nextInt(2);
        for (int i = 0; i < count; i++) spawnDebris(graphite ? RbmkDebrisEntity.Type.GRAPHITE : RbmkDebrisEntity.Type.BLANK);
        super.onMelt(reduce);
    }
}
