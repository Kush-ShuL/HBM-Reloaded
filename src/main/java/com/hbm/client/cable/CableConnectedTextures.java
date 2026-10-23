package com.hbm.client.cable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/** Exact CTContext quadrant selection and IconCT source atlas subdivisions. */
final class CableConnectedTextures {
    private static final Direction[][] ORIENTATION = {
        {Direction.SOUTH,Direction.WEST},{Direction.NORTH,Direction.WEST},
        {Direction.UP,Direction.EAST},{Direction.UP,Direction.WEST},
        {Direction.UP,Direction.NORTH},{Direction.UP,Direction.SOUTH}};
    private CableConnectedTextures() {}
    static int[] context(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        int[] result = new int[24];
        for (Direction side : Direction.values()) {
            Direction up = ORIENTATION[side.ordinal()][0], left = ORIENTATION[side.ordinal()][1];
            boolean t = same(level,pos.relative(up),state), b = same(level,pos.relative(up.getOpposite()),state);
            boolean l = same(level,pos.relative(left),state), r = same(level,pos.relative(left.getOpposite()),state);
            int at = side.ordinal()*4;
            result[at] = type(l,same(level,pos.relative(up).relative(left),state),t);
            result[at+1] = 1|type(r,same(level,pos.relative(up).relative(left.getOpposite()),state),t);
            result[at+2] = 2|type(l,same(level,pos.relative(up.getOpposite()).relative(left),state),b);
            result[at+3] = 3|type(r,same(level,pos.relative(up.getOpposite()).relative(left.getOpposite()),state),b);
        }
        return result;
    }
    private static boolean same(BlockAndTintGetter level, BlockPos pos, BlockState state) { return level.getBlockState(pos).is(state.getBlock()); }
    private static int type(boolean horizontal, boolean corner, boolean vertical) {
        if (vertical && horizontal && corner) return 4;
        if (vertical && horizontal) return 8;
        if (vertical) return 16;
        return horizontal ? 12 : 0;
    }
    static List<BakedQuad> quads(TextureAtlasSprite full, TextureAtlasSprite connected, int[] context) {
        List<BakedQuad> out = new ArrayList<>(24);
        for (Direction side : Direction.values()) {
            float[][] face = CableQuads.face(side,0,0,0,1,1,1);
            for (int quadrant = 0; quadrant < 4; quadrant++) {
                int type = context == null ? quadrant : context[side.ordinal()*4+quadrant];
                float x = (quadrant&1)*.5F, y = ((quadrant&2)>>1)*.5F;
                float[][] patch = {CableQuads.interpolate(face,x,y),CableQuads.interpolate(face,x+.5F,y),
                        CableQuads.interpolate(face,x,y+.5F),CableQuads.interpolate(face,x+.5F,y+.5F)};
                float subdivision = type < 4 ? .5F : .25F;
                float u = (type&1)*subdivision + ((type>=16 || type>=8 && type<12) ? subdivision*2 : 0);
                float v = ((type&2)>>1)*subdivision + ((type>=8 && type<16) ? subdivision*2 : 0);
                out.add(CableQuads.face(type<4?full:connected,side,patch,u,v,u+subdivision,v+subdivision,0));
            }
        }
        return out;
    }
}
