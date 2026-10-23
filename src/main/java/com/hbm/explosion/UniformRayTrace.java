package com.hbm.explosion;

/** Exact source arithmetic inside one immutable, proven single-state section.
 * No world reads, palette lookups, coordinate rounding or prefix sums occur in
 * the solid inner loop: every double loss still rounds into float separately.
 */
final class UniformRayTrace {
    static final class Result {
        int nextStep,lastPositive;
        float resistance,x,y,z;
        long chunk;
    }

    static int sectionEnd(int first,int limit,int ox,int oy,int oz,double dx,double dy,double dz,int x,int y,int z){
        int sectionY=y>>4;
        return OriginalRayReplay.skipAir(first+1,limit,ox,oy,oz,dx,dy,dz,x>>4,z>>4,sectionY<<4,(sectionY+1)<<4,true);
    }

    /** Returns false only when its deadline requires a resumable partial span. */
    static boolean advance(Result out,int first,int end,int limit,float resistance,double[] losses,
            int ox,int oy,int oz,double dx,double dy,double dz,long deadline){
        int step=first,last=-1;
        if(step<end&&System.nanoTime()<deadline){
            if(losses.length==0&&resistance>0){
                // Source liquid subtraction is +0D. Positive float bits cannot
                // change, but every nonair sample still contributes its real tip.
                last=end-1;step=end;
            }else{
                int operations=0;
                while(step<end){
                    if((operations++&63)==0&&System.nanoTime()>=deadline)break;
                    int i=step++;
                    resistance-=losses.length==0?0D:losses[i];
                    if(resistance>0)last=i;
                    if(resistance<=0){step=limit;break;}
                }
            }
        }
        out.nextStep=step;out.resistance=resistance;out.lastPositive=last;
        if(last>=0){
            out.x=(float)(ox+dx*last);out.y=(float)(oy+dy*last);out.z=(float)(oz+dz*last);
            int x=(int)Math.floor(out.x),z=(int)Math.floor(out.z);
            out.chunk=((long)(z>>4)<<32)|((x>>4)&0xffffffffL);
        }
        return step>=end;
    }

    private UniformRayTrace(){}
}
