package com.hbm.client.cable;

import com.hbm.block.cable.BoxCableBlock;
import com.hbm.block.cable.CableBlockItem;
import com.hbm.block.cable.CableKind;
import com.hbm.block.cable.CableModelData;
import com.hbm.block.cable.FacingCableBlock;
import com.hbm.block.cable.NativeCableBlock;
import com.hbm.block.cable.PaintableCableBlock;
import com.hbm.block.cable.SwitchedCableBlock;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Dynamic source geometry; ordinary JSON models supply particle and display transforms. */
final class CableBakedModel extends BakedModelWrapper<BakedModel> {
    private static final ResourceLocation OBJ = ResourceLocation.fromNamespaceAndPath("hbm","models/blocks/cable_neo.obj");
    private static final String[] TEXTURES = {"red_wire_coated","red_wire_coated_ct","cable_neo","red_cable_classic",
        "red_cable_base","red_cable_overlay","deco_red_copper","cable_gauge","cable_switch_on","cable_switch_off",
        "cable_detector_on","cable_detector_off","cable_diode","hadron_coil_alloy","boxduct_cable_straight",
        "boxduct_cable_junction","boxduct_cable_curve_tl","boxduct_cable_curve_tr","boxduct_cable_curve_bl","boxduct_cable_curve_br",
        "boxduct_cable_end_0","boxduct_cable_end_1","boxduct_cable_end_2","boxduct_cable_end_3","boxduct_cable_end_4"};
    private final CableKind kind;
    private final int itemSize;
    private final Function<Material,TextureAtlasSprite> textures;
    private final Map<String,TextureAtlasSprite> sprites = new HashMap<>();
    private final Map<BlockState,List<BakedQuad>> blockCache = new ConcurrentHashMap<>();
    private final Map<Integer,CableBakedModel> itemVariants = new ConcurrentHashMap<>();
    private volatile List<BakedQuad> itemCache;
    private final ItemOverrides overrides = new ItemOverrides() {
        @Override public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
            if (kind == CableKind.BOX && stack.getItem() instanceof CableBlockItem item)
                return itemVariants.computeIfAbsent(item.size(stack), size -> new CableBakedModel(originalModel,kind,textures,size));
            return CableBakedModel.this;
        }
    };
    CableBakedModel(BakedModel original, CableKind kind, Function<Material,TextureAtlasSprite> textures, int itemSize) {
        super(original); this.kind=kind; this.textures=textures; this.itemSize=itemSize;
        for (String texture : TEXTURES) sprites.put(texture,textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,
                ResourceLocation.fromNamespaceAndPath("hbm","block/"+texture))));
    }
    private TextureAtlasSprite sprite(String name) { return sprites.get(name); }
    @Override public ItemOverrides getOverrides() { return overrides; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean left) {
        getTransforms().getTransform(context).apply(left,pose); return this;
    }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        if (state.getBlock() instanceof NativeCableBlock cable)
            data = data.derive().with(CableModelData.CONNECTION_STATE, cable.refreshConnections(state,level,pos)).build();
        if (kind == CableKind.COATED) return data.derive().with(CableModelData.CONNECTED_TEXTURES,CableConnectedTextures.context(level,pos,state)).build();
        return data;
    }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state,side,random,ModelData.EMPTY,null); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        // Original RenderCable queried its neighbours on every world render. A
        // persisted state may predate a machine's port BE or its neighbour chunk.
        if (state != null && data.has(CableModelData.CONNECTION_STATE)) state = data.get(CableModelData.CONNECTION_STATE);
        List<BakedQuad> all;
        if (state == null) {
            all = itemCache;
            if (all == null) itemCache = all = List.copyOf(build(null,random,ModelData.EMPTY));
        } else if (kind == CableKind.PAINTABLE || kind == CableKind.COATED) all = build(state,random,data);
        else all = blockCache.computeIfAbsent(state, key -> List.copyOf(build(key,random,ModelData.EMPTY)));
        boolean full = kind == CableKind.COATED || kind == CableKind.PAINTABLE || kind == CableKind.GAUGE || kind.switched();
        if (state == null || !full) return side == null ? all : List.of();
        return side == null ? List.of() : all.stream().filter(quad -> quad.getDirection() == side).toList();
    }
    private List<BakedQuad> build(BlockState state, RandomSource random, ModelData data) {
        List<BakedQuad> out = new ArrayList<>();
        switch (kind) {
            case NEO -> neo(out,state,false);
            case CLASSIC -> classic(out,state);
            case COATED -> out.addAll(CableConnectedTextures.quads(sprite("red_wire_coated"),sprite("red_wire_coated_ct"),data.get(CableModelData.CONNECTED_TEXTURES)));
            case BOX -> boxed(out,state);
            case PAINTABLE -> painted(out,state,random,data);
            case GAUGE -> {
                CableQuads.cube(out,sprite("deco_red_copper"),0,0,0,1,1,1);
                if (state != null) overlay(out,sprite("cable_gauge"),state.getValue(FacingCableBlock.FACING));
            }
            case SWITCH, DETECTOR -> {
                String name = kind == CableKind.SWITCH ? "cable_switch_" : "cable_detector_";
                CableQuads.cube(out,sprite(name+(state != null && state.getValue(SwitchedCableBlock.ENABLED) ? "on" : "off")),0,0,0,1,1,1);
            }
            case DIODE -> diode(out,state);
        }
        return out;
    }
    private boolean connected(BlockState state, Direction side) {
        if (state != null) return NativeCableBlock.connected(state,side);
        return side.getAxis() != Direction.Axis.Y;
    }
    private void neo(List<BakedQuad> out, BlockState state, boolean diode) {
        String straight = null;
        if (!diode && state != null) for (Direction.Axis axis : Direction.Axis.values()) {
            if (!NativeCableBlock.axisOnly(state,axis)) continue;
            int count = 0;
            for (Direction direction : Direction.values()) if (connected(state,direction)) count++;
            if (count == 2) straight = "C"+axis.getName().toUpperCase(java.util.Locale.ROOT);
        }
        List<String> parts = new ArrayList<>();
        if (straight != null) parts.add(straight);
        else {
            if (!diode) parts.add("Core");
            if (connected(state,Direction.EAST)) parts.add("posX");
            if (connected(state,Direction.WEST)) parts.add("negX");
            if (connected(state,Direction.UP)) parts.add("posY");
            if (connected(state,Direction.DOWN) || diode && state == null) parts.add("negY");
            // X5846's RenderCable intentionally selects reversed Z-labelled OBJ groups.
            if (connected(state,Direction.NORTH)) parts.add("posZ");
            if (connected(state,Direction.SOUTH)) parts.add("negZ");
        }
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),OBJ);
        out.addAll(CableQuads.obj(mesh,sprite("cable_neo"),face -> parts.stream().anyMatch(part -> face.legacyPart().equalsIgnoreCase(part)),state==null));
    }
    private void classic(List<BakedQuad> out, BlockState state) {
        float lo=.34375F, hi=.65625F;
        for (Direction side : Direction.values()) {
            if (!connected(state,side)) out.add(CableQuads.face(sprite("red_cable_classic"),side,CableQuads.face(side,lo,lo,lo,hi,hi,hi),0,0,5F/16,5F/16,0));
            else {
                float[] box = armBox(side,lo,hi,lo,hi);
                for (Direction face : Direction.values()) if (face.getAxis()!=side.getAxis())
                    out.add(CableQuads.face(sprite("red_cable_classic"),face,CableQuads.face(face,box[0],box[1],box[2],box[3],box[4],box[5]),5F/16,0,10F/16,5F/16,classicRotation(side,face)));
            }
        }
    }
    private static int classicRotation(Direction arm, Direction face) {
        // Longitudinal UV runs from the core towards the joined block.
        return switch (arm) {
            case UP -> 0;
            case DOWN -> 2;
            case EAST -> face == Direction.UP || face == Direction.NORTH ? 1 : 3;
            case WEST -> face == Direction.UP || face == Direction.NORTH ? 3 : 1;
            case SOUTH -> face == Direction.UP || face == Direction.WEST ? 1 : 3;
            case NORTH -> face == Direction.UP || face == Direction.WEST ? 3 : 1;
        };
    }
    private void painted(List<BakedQuad> out, BlockState state, RandomSource random, ModelData data) {
        var paint = data.get(CableModelData.PAINT);
        if (paint == null) CableQuads.cube(out,sprite("red_cable_base"),0,0,0,1,1,1);
        else {
            var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(paint);
            out.addAll(model.getQuads(paint,null,random,ModelData.EMPTY,null));
            for (Direction side : Direction.values()) out.addAll(model.getQuads(paint,side,random,ModelData.EMPTY,null));
        }
        // Source item rendering uses only pass zero; metadata one hides the overlay when painted.
        if (state != null && (paint == null || !state.getValue(PaintableCableBlock.HIDE_OVERLAY)))
            for (Direction side : Direction.values()) overlay(out,sprite("red_cable_overlay"),side);
    }
    private static void overlay(List<BakedQuad> out, TextureAtlasSprite sprite, Direction side) {
        var points = CableQuads.face(side,0,0,0,1,1,1);
        for (float[] point : points) { point[0]+=side.getStepX()*.0001F; point[1]+=side.getStepY()*.0001F; point[2]+=side.getStepZ()*.0001F; }
        out.add(CableQuads.face(sprite,side,points,0,0,1,1,0));
    }
    private void diode(List<BakedQuad> out, BlockState state) {
        Direction facing = state == null ? Direction.DOWN : state.getValue(FacingCableBlock.FACING);
        float[] box = {0,0,0,1,1,1};
        switch (facing) {
            case DOWN -> box[1]=.875F; case UP -> box[4]=.125F;
            case NORTH -> box[2]=.875F; case SOUTH -> box[5]=.125F;
            case WEST -> box[0]=.875F; case EAST -> box[3]=.125F;
        }
        CableQuads.cube(out,sprite("cable_diode"),box[0],box[1],box[2],box[3],box[4],box[5]);
        CableQuads.cube(out,sprite("hadron_coil_alloy"),.125F,.125F,.125F,.875F,.875F,.875F);
        neo(out,state,true);
    }
    private void boxed(List<BakedQuad> out, BlockState state) {
        int size = state == null ? itemSize : state.getValue(BoxCableBlock.SIZE);
        float lo = (2+size)/16F, hi=1-lo;
        int mask = boxMask(state), count=Integer.bitCount(mask);
        List<float[]> boxes = new ArrayList<>();
        if (state == null || mask>0 && (mask&0b111100)==0) boxes.add(new float[]{lo,lo,0,hi,hi,1});
        else if (mask>0 && (mask&0b001111)==0) boxes.add(new float[]{0,lo,lo,1,hi,hi});
        else if (mask>0 && (mask&0b110011)==0) boxes.add(new float[]{lo,0,lo,hi,1,hi});
        else {
            boxes.add(new float[]{lo,lo,lo,hi,hi,hi});
            for (Direction direction : Direction.values()) if (connected(state,direction)) boxes.add(armBox(direction,lo,hi,lo,hi));
        }
        for (float[] box : boxes) for (Direction side : Direction.values()) {
            String icon = state == null ? (side.getAxis()==Direction.Axis.Z ? "boxduct_cable_end_"+size : "boxduct_cable_straight") : boxTexture(mask,count,side,size);
            int rotation = boxRotation(mask,side);
            CableQuads.croppedFace(out,sprite(icon),side,box[0],box[1],box[2],box[3],box[4],box[5],rotation);
        }
    }
    private int boxMask(BlockState state) {
        if (state == null) return 3;
        return (connected(state,Direction.EAST)?32:0)|(connected(state,Direction.WEST)?16:0)|
               (connected(state,Direction.UP)?8:0)|(connected(state,Direction.DOWN)?4:0)|
               (connected(state,Direction.SOUTH)?2:0)|(connected(state,Direction.NORTH)?1:0);
    }
    private static boolean has(int mask, Direction side) { return (mask & switch(side){case EAST->32;case WEST->16;case UP->8;case DOWN->4;case SOUTH->2;case NORTH->1;})!=0; }
    private static String boxTexture(int mask, int count, Direction side, int size) {
        String end="boxduct_cable_end_"+size, straight="boxduct_cable_straight";
        if ((mask&0b001111)==0 && mask>0) return side.getAxis()==Direction.Axis.X ? end : straight;
        if ((mask&0b111100)==0 && mask>0) return side.getAxis()==Direction.Axis.Z ? end : straight;
        if ((mask&0b110011)==0 && mask>0) return side.getAxis()==Direction.Axis.Y ? end : straight;
        if (has(mask,side)) return end;
        if (count==2) {
            if (has(mask,side.getOpposite())) return straight;
            boolean nY=(mask&4)!=0,pY=(mask&8)!=0,nX=(mask&16)!=0,pX=(mask&32)!=0,nZ=(mask&1)!=0,pZ=(mask&2)!=0;
            String curve=null;
            if(nY&&pZ) curve=side==Direction.WEST?"br":"bl";
            else if(nY&&nZ) curve=side==Direction.EAST?"br":"bl";
            else if(nY&&pX) curve=side==Direction.SOUTH?"br":"bl";
            else if(nY&&nX) curve=side==Direction.NORTH?"br":"bl";
            else if(pY&&pZ) curve=side==Direction.WEST?"tr":"tl";
            else if(pY&&nZ) curve=side==Direction.EAST?"tr":"tl";
            else if(pY&&pX) curve=side==Direction.SOUTH?"tr":"tl";
            else if(pY&&nX) curve=side==Direction.NORTH?"tr":"tl";
            else if(pX&&nZ) curve="tr"; else if(pX&&pZ) curve="br";
            else if(nX&&nZ) curve="tl"; else if(nX&&pZ) curve="bl";
            if(curve!=null)return "boxduct_cable_curve_"+curve;
        }
        return "boxduct_cable_junction";
    }
    private static int boxRotation(int mask, Direction side) {
        boolean xOnly=mask>0&&(mask&15)==0, zOnly=mask>0&&(mask&60)==0;
        boolean horizontal=Integer.bitCount(mask)==2&&(mask&12)==0;
        if (xOnly || horizontal) {
            if(side==Direction.NORTH)return 2;
            if(side==Direction.SOUTH)return 1;
        }
        if (zOnly || horizontal) {
            if(side==Direction.WEST)return 1;
            if(side==Direction.EAST)return 2;
        }
        if ((xOnly || Integer.bitCount(mask)==2&&(mask&12)!=0&&(mask&48)!=0) && side.getAxis()==Direction.Axis.Y)return 1;
        return 0;
    }
    private static float[] armBox(Direction side, float lo, float hi, float coreLo, float coreHi) {
        return switch(side) {
            case DOWN -> new float[]{lo,0,lo,hi,coreLo,hi}; case UP -> new float[]{lo,coreHi,lo,hi,1,hi};
            case NORTH -> new float[]{lo,lo,0,hi,hi,coreLo}; case SOUTH -> new float[]{lo,lo,coreHi,hi,hi,1};
            case WEST -> new float[]{0,lo,lo,coreLo,hi,hi}; case EAST -> new float[]{coreHi,lo,lo,1,hi,hi};
        };
    }
}
