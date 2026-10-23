package com.hbm.machine.reactor.iter.torus;
import java.util.*;
import net.minecraft.core.BlockPos;
/** The exact3 authored15×15source materialarrays, mirrored across5Ylayers. */
public final class TorusLayout {
    public static final int[][][] LAYOUT = {

		new int[][] {
			new int[] {0,0,0,0,3,3,3,3,3,3,3,0,0,0,0},
			new int[] {0,0,0,3,1,1,1,1,1,1,1,3,0,0,0},
			new int[] {0,0,3,1,1,1,1,1,1,1,1,1,3,0,0},
			new int[] {0,3,1,1,1,1,1,1,1,1,1,1,1,3,0},
			new int[] {3,1,1,1,1,3,3,3,3,3,1,1,1,1,3},
			new int[] {3,1,1,1,3,3,3,3,3,3,3,1,1,1,3},
			new int[] {3,1,1,1,3,3,3,3,3,3,3,1,1,1,3},
			new int[] {3,1,1,1,3,3,3,3,3,3,3,1,1,1,3},
			new int[] {3,1,1,1,3,3,3,3,3,3,3,1,1,1,3},
			new int[] {3,1,1,1,3,3,3,3,3,3,3,1,1,1,3},
			new int[] {3,1,1,1,1,3,3,3,3,3,1,1,1,1,3},
			new int[] {0,3,1,1,1,1,1,1,1,1,1,1,1,3,0},
			new int[] {0,0,3,1,1,1,1,1,1,1,1,1,3,0,0},
			new int[] {0,0,0,3,1,1,1,1,1,1,1,3,0,0,0},
			new int[] {0,0,0,0,3,3,3,3,3,3,3,0,0,0,0},
		},
		new int[][] {
			new int[] {0,0,0,0,1,1,3,3,3,1,1,0,0,0,0},
			new int[] {0,0,0,1,1,1,1,1,1,1,1,1,0,0,0},
			new int[] {0,0,1,1,2,2,2,2,2,2,2,1,1,0,0},
			new int[] {0,1,1,2,1,1,1,1,1,1,1,2,1,1,0},
			new int[] {1,1,2,1,1,1,1,1,1,1,1,1,2,1,1},
			new int[] {1,1,2,1,1,3,3,3,3,3,1,1,2,1,1},
			new int[] {3,1,2,1,1,3,3,3,3,3,1,1,2,1,3},
			new int[] {3,1,2,1,1,3,3,3,3,3,1,1,2,1,3},
			new int[] {3,1,2,1,1,3,3,3,3,3,1,1,2,1,3},
			new int[] {1,1,2,1,1,3,3,3,3,3,1,1,2,1,1},
			new int[] {1,1,2,1,1,1,1,1,1,1,1,1,2,1,1},
			new int[] {0,1,1,2,1,1,1,1,1,1,1,2,1,1,0},
			new int[] {0,0,1,1,2,2,2,2,2,2,2,1,1,0,0},
			new int[] {0,0,0,1,1,1,1,1,1,1,1,1,0,0,0},
			new int[] {0,0,0,0,1,1,3,3,3,1,1,0,0,0,0},
		},
		new int[][] {
			new int[] {0,0,0,0,1,1,3,3,3,1,1,0,0,0,0},
			new int[] {0,0,0,1,2,2,2,2,2,2,2,1,0,0,0},
			new int[] {0,0,1,2,2,2,2,2,2,2,2,2,1,0,0},
			new int[] {0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
			new int[] {1,2,2,2,1,1,1,1,1,1,1,2,2,2,1},
			new int[] {1,2,2,2,1,3,3,3,3,3,1,2,2,2,1},
			new int[] {3,2,2,2,1,3,3,3,3,3,1,2,2,2,3},
			new int[] {3,2,2,2,1,3,3,3,3,3,1,2,2,2,3},
			new int[] {3,2,2,2,1,3,3,3,3,3,1,2,2,2,3},
			new int[] {1,2,2,2,1,3,3,3,3,3,1,2,2,2,1},
			new int[] {1,2,2,2,1,1,1,1,1,1,1,2,2,2,1},
			new int[] {0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
			new int[] {0,0,1,2,2,2,2,2,2,2,2,2,1,0,0},
			new int[] {0,0,0,1,2,2,2,2,2,2,2,1,0,0,0},
			new int[] {0,0,0,0,1,1,3,3,3,1,1,0,0,0,0},
		}
	};
    public record Part(int x,int y,int z,int material){public BlockPos at(BlockPos core){return core.offset(x,y,z);}}
    public static final List<Part> PARTS;
    static{var parts=new ArrayList<Part>();for(int y=0;y<5;y++)for(int x=0;x<15;x++)for(int z=0;z<15;z++){int material=LAYOUT[y>2?4-y:y][x][z];if(material>0)parts.add(new Part(x-7,y,z-7,material));}PARTS=List.copyOf(parts);}
    private TorusLayout(){}
}
