package com.hbm.client.render;

import com.hbm.client.render.mixin.WorldEffectMeshAccess;
import com.mojang.blaze3d.vertex.*;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;

/** Finite source-VFX material adapter to the sole168 queue. Never capture ordinary models or171 gun types. */
public final class WorldEffectMeshes {
    private static final Map<RenderType,String> MATERIALS=new IdentityHashMap<>();
    private static final java.util.Set<RenderType> OPAQUE_OCCLUDERS=java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private WorldEffectMeshes(){}
    /** Registration occurs at the actual owned material definition, not through inferred names/formats. */
    public static <T extends RenderType>T register(T type,String source){MATERIALS.put(type,source);return type;}
    /** Only genuine opaque bodies supply early coverage; additive outer shells keep their original order. */
    public static <T extends RenderType>T occluder(T type){
        if(!MATERIALS.containsKey(type))throw new IllegalArgumentException("Unregistered source mesh material");
        OPAQUE_OCCLUDERS.add(type);return type;
    }
    public static boolean capture(RenderType type,MeshData submitted){
        if(!MATERIALS.containsKey(type)||WorldEffectsDepth.earlyDepthPass()||!WorldEffectsForward.acceptsSourceDraw()||submitted.drawState().vertexCount()==0)return false;
        Copy copy=null;boolean retained=false;
        try{
            copy=new Copy(submitted);var ready=copy;
            retained=WorldEffectsForward.defer(()->type.draw(ready.mesh),ready::close,
                    OPAQUE_OCCLUDERS.contains(type)?()->{
                        // Native drawing closes MeshData: retain the colour mesh and draw an exact temporary copy.
                        try(var depth=new Copy(ready.mesh)){type.draw(depth.mesh);}
                    }:null);
            if(retained)submitted.close();
            return retained;
        }catch(RuntimeException|Error failure){submitted.close();throw failure;}
        finally{if(!retained&&copy!=null)copy.close();}
    }
    /** Exact original vertex/index bytes and DrawState: no float unpack/repack, quad triangulation or RNG replay. */
    private static final class Copy implements AutoCloseable {
        final ByteBufferBuilder vertices;
        ByteBufferBuilder indices;
        MeshData mesh;
        boolean closed;
        Copy(MeshData original){
            var vertexBytes=original.vertexBuffer();vertices=new ByteBufferBuilder(Math.max(vertexBytes.remaining(),1));
            try{
                copy(vertexBytes,vertices);mesh=new MeshData(vertices.build(),original.drawState());
                var indexBytes=original.indexBuffer();
                if(indexBytes!=null){indices=new ByteBufferBuilder(Math.max(indexBytes.remaining(),1));copy(indexBytes,indices);((WorldEffectMeshAccess)(Object)mesh).hbm$sourceIndices(indices.build());}
            }catch(RuntimeException|Error failure){close();throw failure;}
        }
        static void copy(ByteBuffer source,ByteBufferBuilder target){int count=source.remaining();long destination=target.reserve(count);MemoryUtil.memCopy(MemoryUtil.memAddress(source),destination,count);}
        @Override public void close(){
            if(closed)return;closed=true;
            // Native BufferUploader closes MeshData. Its Result.close is itself idempotent;
            // this path additionally closes backing builders after success, failure or an abandoned frame.
            try{if(mesh!=null)mesh.close();}finally{try{if(indices!=null)indices.close();}finally{vertices.close();}}
        }
    }
}
