package com.hbm.world.structure.generated.compat;
import com.hbm.world.structure.generated.GeneratedLoot;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
/** Source geometry object; each instance is persisted and placed by a real native GeneratedPiece. */
public abstract class StructureComponent {
    protected boolean containsSourcePosition(StructureBoundingBox box,int x,int y,int z){return box.isVecInside(getXWithOffset(x,z),getYWithOffset(y),getZWithOffset(x,z));}
    protected StructureBoundingBox boundingBox;protected int coordBaseMode=-1;private int componentType;
    protected StructureComponent(int componentType){this.componentType=componentType;}
    public abstract boolean addComponentParts(World world,Random random,StructureBoundingBox clip);
    protected abstract void func_143012_a(CompoundTag tag);protected abstract void func_143011_b(CompoundTag tag);
    public int getComponentType(){return componentType;}public StructureBoundingBox getBoundingBox(){return boundingBox;}
    public int getXWithOffset(int x,int z){return x;}public int getZWithOffset(int x,int z){return z;}public int getYWithOffset(int y){return coordBaseMode==-1?y:y+boundingBox.minY;}
    protected void placeBlockAtCurrentPosition(World world,Block block,int metadata,int x,int y,int z,StructureBoundingBox clip){int wx=getXWithOffset(x,z),wy=getYWithOffset(y),wz=getZWithOffset(x,z);if(clip.isVecInside(wx,wy,wz))world.setBlock(wx,wy,wz,block,metadata,2);}
    protected Block getBlockAtCurrentPosition(World world,int x,int y,int z,StructureBoundingBox clip){int wx=getXWithOffset(x,z),wy=getYWithOffset(y),wz=getZWithOffset(x,z);return clip.isVecInside(wx,wy,wz)?world.getBlock(wx,wy,wz):Blocks.air;}
    /** Exact branches called by these X5846 components, from supplied-era avk.a(aji,int). */
    protected int getMetadataWithOffset(Block block,int metadata){
        String id=block.sourceId;
        if(id.equals("minecraft:wooden_door")||id.equals("minecraft:iron_door")){
            if(coordBaseMode==0){if(metadata==0)return 2;if(metadata==2)return 0;}
            else if(coordBaseMode==1)return(metadata+1)&3;
            else if(coordBaseMode==3)return(metadata+3)&3;
        }else if(Set.of("minecraft:stone_stairs","minecraft:oak_stairs","minecraft:nether_brick_stairs","minecraft:stone_brick_stairs","minecraft:sandstone_stairs").contains(id)){
            if(coordBaseMode==0){if(metadata==2)return 3;if(metadata==3)return 2;}
            else if(coordBaseMode==1){if(metadata>=0&&metadata<4)return new int[]{2,3,0,1}[metadata];}
            else if(coordBaseMode==3){if(metadata>=0&&metadata<4)return new int[]{2,3,1,0}[metadata];}
        }
        // In particular the original helper has no chest branch.
        return metadata;
    }
    protected boolean generateStructureChestContents(World world,StructureBoundingBox clip,Random random,int x,int y,int z,GeneratedLoot.Pool pool,int amount){int wx=getXWithOffset(x,z),wy=getYWithOffset(y),wz=getZWithOffset(x,z);if(!clip.isVecInside(wx,wy,wz)||world.sameBlock(wx,wy,wz,Blocks.chest))return false;world.setBlock(wx,wy,wz,Blocks.chest,0,2);world.sourceChestFacing(wx,wy,wz);if(world.getTileEntity(wx,wy,wz) instanceof net.minecraft.world.Container inventory){GeneratedLoot.fill(world,random,pool,inventory,amount);return true;}return false;}
    protected void fillWithAir(World world,StructureBoundingBox clip,int x0,int y0,int z0,int x1,int y1,int z1){throw new UnsupportedOperationException("Source Component must supply exact implementation");}
    protected void fillWithBlocks(World world,StructureBoundingBox clip,int x0,int y0,int z0,int x1,int y1,int z1,Block outer,Block inner,boolean replaceOnly){throw new UnsupportedOperationException("Source Component must supply exact implementation");}
    protected void fillWithMetadataBlocks(World world,StructureBoundingBox clip,int x0,int y0,int z0,int x1,int y1,int z1,Block outer,int outerMetadata,Block inner,int innerMetadata,boolean replaceOnly){throw new UnsupportedOperationException("Source Component must supply exact implementation");}
    protected void fillWithRandomizedBlocks(World world,StructureBoundingBox clip,int x0,int y0,int z0,int x1,int y1,int z1,boolean replaceOnly,Random random,BlockSelector selector){throw new UnsupportedOperationException("Source Component must supply exact implementation");}
    protected void randomlyFillWithBlocks(World world,StructureBoundingBox clip,Random random,float chance,int x0,int y0,int z0,int x1,int y1,int z1,Block outer,Block inner,boolean replaceOnly){throw new UnsupportedOperationException("Source Component must supply exact implementation");}
    public static StructureComponent findIntersecting(List<?> components,StructureBoundingBox box){for(var raw:components)if(raw instanceof StructureComponent component&&component.boundingBox.intersectsWith(box))return component;return null;}
    public CompoundTag save(){var tag=new CompoundTag();tag.putInt("SourceOrientation",coordBaseMode);tag.putInt("SourceComponentType",componentType);tag.putIntArray("SourceBox",boundingBox.coordinates());func_143012_a(tag);return tag;}
    public void load(CompoundTag tag){coordBaseMode=tag.getInt("SourceOrientation");componentType=tag.getInt("SourceComponentType");int[] box=tag.getIntArray("SourceBox");boundingBox=new StructureBoundingBox(box[0],box[1],box[2],box[3],box[4],box[5]);func_143011_b(tag);}
    public abstract static class BlockSelector {protected Block field_151562_a;protected int selectedBlockMetaData;public abstract void selectBlocks(Random random,int x,int y,int z,boolean outer);public Block func_151561_a(){return field_151562_a;}public int getSelectedBlockMetaData(){return selectedBlockMetaData;}}
}
