package com.hbm.machine.conveyorgrabber;

import com.hbm.conveyor.crane.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Real CraneBase subtype: original pull/turn sprites and only the two real upgrades are dropped. */
public final class ConveyorGrabberBlock extends CraneBaseBlock {
    private static final String[] TEXTURES={"crane_top","crane_side","crane_pull","crane_side_pull","crane_out","crane_side_out","crane_grabber_top","crane_grabber_side_up","crane_grabber_side_down","crane_grabber_top_left","crane_grabber_top_right","crane_grabber_side_left_turn_up","crane_grabber_side_right_turn_up","crane_grabber_side_left_turn_down","crane_grabber_side_right_turn_down","crane_grabber_side_up_turn_left","crane_grabber_side_up_turn_right","crane_grabber_side_down_turn_left","crane_grabber_side_down_turn_right"};
    @Override public ConveyorGrabberEntity createCrane(BlockPos pos,BlockState state){return new ConveyorGrabberEntity(pos,state);}
    @Override public String[] sourceTextures(){return TEXTURES.clone();}
    @Override protected void dropContents(Level level,BlockPos pos,CraneBaseEntity crane,int start,int end){super.dropContents(level,pos,crane,9,11);}
}
