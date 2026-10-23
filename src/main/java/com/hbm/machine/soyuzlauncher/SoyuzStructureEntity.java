package com.hbm.machine.soyuzlauncher;

import com.hbm.registry.HbmContent;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Exact source20tick construction check, material alternatives and deletion order. No replacement recipe. */
public final class SoyuzStructureEntity extends BlockEntity {
    private int age;
    public SoyuzStructureEntity(BlockPos pos, BlockState state) { super(SoyuzLauncherContent.STRUCTURE_ENTITY.get(),pos,state); }
    public void tick() {
        if (level == null || level.isClientSide || ++age < 20) return; age = 0;
        for (var cell : SoyuzLauncherLayout.MATERIALS) {
            var p=worldPosition.offset(cell.offset()); var state=level.getBlockState(p);
            boolean valid=switch(cell.material()) {
                case LAUNCHER -> state.is(HbmContent.block("struct_launcher"));
                case SCAFFOLD -> state.is(HbmContent.block("struct_scaffold"));
                case CONCRETE -> state.is(HbmContent.block("concrete")) || state.is(HbmContent.block("concrete_smooth"));
            };
            if(!valid)return;
        }
        // Source erases only these scaffold/cap/foot cells; the pad and other feet become actual proxies.
        DummyableBlock.safely(() -> {
            clear(-2,2,51,51,5,9); clear(-1,1,38,38,-8,-6); clear(-2,2,0,2,5,9);
            clear(-1,1,5,50,6,8); clear(0,0,5,37,-7,-7);
            level.setBlock(worldPosition,Blocks.AIR.defaultBlockState(),3);
            SoyuzLauncherContent.LAUNCHER.get().assemble(level,worldPosition.above(4));
        });
    }
    private void clear(int x0,int x1,int y0,int y1,int z0,int z1) { for(int x=x0;x<=x1;x++)for(int y=y0;y<=y1;y++)for(int z=z0;z<=z1;z++)level.setBlock(worldPosition.offset(x,y,z),Blocks.AIR.defaultBlockState(),3); }
}
