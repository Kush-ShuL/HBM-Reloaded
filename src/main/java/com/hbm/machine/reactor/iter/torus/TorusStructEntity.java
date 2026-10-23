package com.hbm.machine.reactor.iter.torus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original20worldtick exactmeta validation, ignores sourceair and its owncorecell. */
public final class TorusStructEntity extends BlockEntity {
    public TorusStructEntity(BlockPos pos,BlockState state){super(TorusContent.STRUCT_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos core,BlockState state,TorusStructEntity entity){if(level.isClientSide||level.getGameTime()%20!=0)return;for(var part:TorusLayout.PARTS){if(part.x()==0&&part.y()==0&&part.z()==0)continue;var pos=part.at(core);if(!level.hasChunkAt(pos))return;var found=level.getBlockState(pos);if(!found.is(TorusContent.COMPONENT.get())||found.getValue(FusionComponent.META)!=part.material())return;}TorusContent.BLOCK.get().form(level,core,net.minecraft.core.Direction.NORTH);}
}
