package com.hbm.entity.mob.glyphid.ai;

import com.hbm.block.fluid.LegacyMaterials;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/** Official1.7.10 PathFinder/Path/PathPoint/PathEntity algorithm, with modern loaded-world and height access only. */
public final class LegacyMobPath {
    public final Node[] nodes;public int index;
    private LegacyMobPath(Node[] nodes){this.nodes=nodes;}
    public boolean done(){return index>=nodes.length;}
    public Vec3 next(Entity entity){var node=nodes[index];double half=(int)(entity.getBbWidth()+1F)*.5D;return new Vec3(node.x+half,node.y,node.z+half);}
    public static LegacyMobPath toEntity(LegacyCreatureMob mob,Entity target,float maximum,boolean doors,boolean blocked,boolean avoidWater,boolean swim){return new Finder(mob,doors,blocked,avoidWater,swim).find(target.getX(),target.getBoundingBox().minY,target.getZ(),maximum);}
    public static LegacyMobPath toPosition(LegacyCreatureMob mob,int x,int y,int z,float maximum,boolean doors,boolean blocked,boolean avoidWater,boolean swim){return toPosition((Mob)mob,x,y,z,maximum,doors,blocked,avoidWater,swim);}
    public static LegacyMobPath toPosition(Mob mob,int x,int y,int z,float maximum,boolean doors,boolean blocked,boolean avoidWater,boolean swim){return new Finder(mob,doors,blocked,avoidWater,swim).find((float)x+.5F,(float)y+.5F,(float)z+.5F,maximum);}
    public static final class Node {
        public final int x,y,z;int heap=-1;float total,distance,priority;Node previous;boolean visited;
        Node(int x,int y,int z){this.x=x;this.y=y;this.z=z;}
        float squared(Node node){float dx=node.x-x,dy=node.y-y,dz=node.z-z;return dx*dx+dy*dy+dz*dz;}
        float distance(Node node){return Mth.sqrt(squared(node));}
    }
    private static final class Heap {
        Node[] points=new Node[1024];int size;
        void add(Node node){if(node.heap>=0)throw new IllegalStateException("Source path node already queued");if(size==points.length)points=Arrays.copyOf(points,size<<1);points[size]=node;node.heap=size;up(size++);}
        Node pop(){Node node=points[0];points[0]=points[--size];points[size]=null;if(size>0)down(0);node.heap=-1;return node;}
        void change(Node node,float priority){float previous=node.priority;node.priority=priority;if(priority<previous)up(node.heap);else down(node.heap);}
        void up(int index){Node node=points[index];float priority=node.priority;while(index>0){int parent=(index-1)>>1;Node other=points[parent];if(!(priority<other.priority))break;points[index]=other;other.heap=index;index=parent;}points[index]=node;node.heap=index;}
        void down(int index){Node node=points[index];float priority=node.priority;while(true){int left=1+(index<<1),right=left+1;if(left>=size)break;Node a=points[left],b=right>=size?null:points[right];float av=a.priority,bv=b==null?Float.POSITIVE_INFINITY:b.priority;if(av<bv){if(!(av<priority))break;points[index]=a;a.heap=index;index=left;}else{if(!(bv<priority))break;points[index]=b;b.heap=index;index=right;}}points[index]=node;node.heap=index;}
    }
    private static final class Finder {
        final Mob mob;final ServerLevel world;final boolean doors,blocked,swim;boolean avoidWater;
        final Map<BlockPos,Node> points=new HashMap<>();final Heap heap=new Heap();final Node[] neighbors=new Node[32];
        Finder(Mob mob,boolean doors,boolean blocked,boolean avoidWater,boolean swim){this.mob=mob;world=(ServerLevel)mob.level();this.doors=doors;this.blocked=blocked;this.avoidWater=avoidWater;this.swim=swim;}
        Node point(int x,int y,int z){return points.computeIfAbsent(new BlockPos(x,y,z),pos->new Node(x,y,z));}
        LegacyMobPath find(double x,double y,double z,float maximum){
            int startY=Mth.floor(mob.getBoundingBox().minY+.5D);
            if(swim&&mob.isInWater()){
                startY=(int)mob.getBoundingBox().minY;
                while(water(state(Mth.floor(mob.getX()),startY,Mth.floor(mob.getZ()))))startY++;
                avoidWater=false;
            }
            Node start=point(Mth.floor(mob.getBoundingBox().minX),startY,Mth.floor(mob.getBoundingBox().minZ));
            Node end=point(Mth.floor(x-mob.getBbWidth()/2F),Mth.floor(y),Mth.floor(z-mob.getBbWidth()/2F));
            Node size=new Node(Mth.floor(mob.getBbWidth()+1F),Mth.floor(mob.getBbHeight()+1F),Mth.floor(mob.getBbWidth()+1F));
            start.total=0;start.distance=start.squared(end);start.priority=start.distance;heap.add(start);Node best=start;
            while(heap.size>0){Node current=heap.pop();if(current.x==end.x&&current.y==end.y&&current.z==end.z)return build(current);if(current.squared(end)<best.squared(end))best=current;current.visited=true;
                int count=neighbors(current,size,end,maximum);
                for(int i=0;i<count;i++){Node next=neighbors[i];float distance=current.total+current.squared(next);if(next.heap<0||distance<next.total){next.previous=current;next.total=distance;next.distance=next.squared(end);if(next.heap>=0)heap.change(next,next.total+next.distance);else{next.priority=next.total+next.distance;heap.add(next);}}}
            }
            return best==start?null:build(best);
        }
        LegacyMobPath build(Node end){int size=1;for(Node node=end;node.previous!=null;node=node.previous)size++;Node[] result=new Node[size];for(Node node=end;node!=null;node=node.previous)result[--size]=node;return new LegacyMobPath(result);}
        int neighbors(Node current,Node size,Node end,float maximum){
            int count=0,step=vertical(current.x,current.y+1,current.z,size)==1?1:0;
            Node south=safe(current.x,current.y,current.z+1,size,step),west=safe(current.x-1,current.y,current.z,size,step),east=safe(current.x+1,current.y,current.z,size,step),north=safe(current.x,current.y,current.z-1,size,step);
            if(south!=null&&!south.visited&&south.distance(end)<maximum)neighbors[count++]=south;
            if(west!=null&&!west.visited&&west.distance(end)<maximum)neighbors[count++]=west;
            if(east!=null&&!east.visited&&east.distance(end)<maximum)neighbors[count++]=east;
            if(north!=null&&!north.visited&&north.distance(end)<maximum)neighbors[count++]=north;
            return count;
        }
        Node safe(int x,int y,int z,Node size,int step){
            Node result=null;int vertical=vertical(x,y,z,size);if(vertical==2)return point(x,y,z);if(vertical==1)result=point(x,y,z);
            if(result==null&&step>0&&vertical!=-3&&vertical!=-4&&vertical(x,y+step,z,size)==1){result=point(x,y+step,z);y+=step;}
            if(result!=null){int falls=0,last=0;while(y>world.getMinBuildHeight()){last=vertical(x,y-1,z,size);if(avoidWater&&last==-1)return null;if(last!=1)break;if(falls++>=(mob instanceof LegacyCreatureMob creature?creature.legacyMaxFall():3))return null;if(--y>world.getMinBuildHeight())result=point(x,y,z);}if(last==-2)return null;}
            return result;
        }
        /** Source getVerticalOffset return codes1/2/0/-1/-2/-3/-4 and original XYZ order. */
        int vertical(int x,int y,int z,Node size){
            boolean special=false;
            for(int ix=x;ix<x+size.x;ix++)for(int iy=y;iy<y+size.y;iy++)for(int iz=z;iz<z+size.z;iz++){
                BlockState state=state(ix,iy,iz);if(state==null)return 0;var material=LegacyMaterials.of(state);if(material.name().equals("air"))continue;
                boolean trap=state.getBlock() instanceof TrapDoorBlock,woodDoor=state.getBlock() instanceof DoorBlock&&material.name().equals("wood");
                if(trap)special=true;else if(!water(state)){if(!doors&&woodDoor)return 0;}else{if(avoidWater)return-1;special=true;}
                if(state.getBlock() instanceof BaseRailBlock){BlockState at=state(Mth.floor(mob.getX()),Mth.floor(mob.getY()),Mth.floor(mob.getZ())),below=state(Mth.floor(mob.getX()),Mth.floor(mob.getY())-1,Mth.floor(mob.getZ()));if((at==null||!(at.getBlock() instanceof BaseRailBlock))&&(below==null||!(below.getBlock() instanceof BaseRailBlock)))return-3;}
                else if(!passable(state,material.isSolid())&&(!blocked||!woodDoor)){
                    if(state.getBlock() instanceof FenceBlock||state.getBlock() instanceof FenceGateBlock||state.getBlock() instanceof WallBlock)return-3;
                    if(trap)return-4;if(!material.name().equals("lava"))return 0;if(!mob.isInLava())return-2;
                }
            }
            return special?2:1;
        }
        boolean passable(BlockState state,boolean solid){if(state.getBlock() instanceof DoorBlock||state.getBlock() instanceof FenceGateBlock)return state.getValue(BlockStateProperties.OPEN);if(state.getBlock() instanceof TrapDoorBlock)return!state.getValue(BlockStateProperties.OPEN);if(state.is(Blocks.LAVA))return false;return!solid;}
        BlockState state(int x,int y,int z){if(world.isOutsideBuildHeight(y))return Blocks.AIR.defaultBlockState();var chunk=world.getChunkSource().getChunkNow(x>>4,z>>4);return chunk==null?null:chunk.getBlockState(new BlockPos(x,y,z));}
        boolean water(BlockState state){return state!=null&&state.is(Blocks.WATER);}
    }
}
