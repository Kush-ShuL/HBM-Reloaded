package com.hbm.world.structure.generated;
import java.util.Random;

/** The original GlyphidHive.generateSmall algorithm only. No block/entity/feature registration or fake backend. */
public final class GlyphidHiveLayout {
    private static final int[][][] SCHEMATIC={
		{
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
		},
		{
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,1,1,1,1,1,0,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,0,1,1,1,1,1,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
		},
		{
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,1,1,1,3,3,3,1,1,1,0},
			{0,1,1,1,3,3,3,1,1,1,0},
			{0,1,1,1,3,3,3,1,1,1,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
		},
		{
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,2,2,2,1,1,0,0},
			{0,1,1,2,2,2,2,2,1,1,0},
			{0,1,1,2,2,2,2,2,1,1,0},
			{0,1,1,2,2,2,2,2,1,1,0},
			{0,0,1,1,2,2,2,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
		},
		{
			{0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,1,1,1,1,1,1,1,1,1,0},
			{0,1,1,1,1,1,1,1,1,1,0},
			{0,1,1,1,1,1,1,1,1,1,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,1,1,1,1,1,1,1,0,0},
			{0,0,0,0,1,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0},
		}
	};
    /** Phase3 must implement these calls with the real sole compat.World/WorldGenLevel,
     * real glyphid spawner and existing LootPile presets. No no-op implementation is installed. */
    public interface Target {
        void base(int x,int y,int z,int metadata);
        void spawner(int x,int y,int z,int metadata);
        /** Source sets floor skull metadata1 first, then tests its actual Skull BE before drawing rotation. */
        boolean skull(int x,int y,int z);
        void skullRotation(int x,int y,int z,int rotation);
        /** Implement with actual LootGenerator bone/hive presets, using world.rand, not the layout RNG. */
        void bones(int x,int y,int z);
        void hiveLoot(int x,int y,int z);
    }
    public static void generateSmall(Target target,int x,int y,int z,Random random,boolean infected,boolean loot){
        int metadata=infected?1:0;
        for(int i=0;i<11;i++)for(int j=0;j<5;j++)for(int k=0;k<11;k++){
            int block=SCHEMATIC[4-j][i][k],px=x+i-5,py=y+j-2,pz=z+k-5;
            switch(block){
                // Source0 leaves old terrain untouched, never clears the schematic's surrounding box.
                case 1->target.base(px,py,pz,metadata);
                case 2->{if(random.nextInt(3)==0)target.spawner(px,py,pz,metadata);else target.base(px,py,pz,metadata);}
                case 3->{int choice=random.nextInt(3);if(choice==0){if(target.skull(px,py,pz))target.skullRotation(px,py,pz,random.nextInt(16));}
                         else if(choice==1)target.bones(px,py,pz);
                         else if(loot)target.hiveLoot(px,py,pz);
                         else target.base(px,py,pz,metadata);}
            }
        }
    }
    private GlyphidHiveLayout(){}
}
