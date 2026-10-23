package com.hbm.client.structure.woodfixtures46;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/** Ordered original ISBRH cuboids and RenderBlocks/RenderBlocksNT UVs. No proxy cube or OBJ replacement. */
public final class LegacyWoodGeometry {
    private record Box(double x0, double y0, double z0, double x1, double y1, double z1) {}
    private static Box box(double x0, double y0, double z0, double x1, double y1, double z1) { return new Box(x0, y0, z0, x1, y1, z1); }

    static List<BakedQuad> bake(boolean barrier, int type, int joins, boolean inventory, TextureAtlasSprite sprite) {
        var bounds = new ArrayList<Box>();
        if (barrier) barrier(bounds, joins, inventory);
        else structure(bounds, type, joins, inventory);
        double itemY = !inventory || barrier ? 0 : type == 0 ? .125 : type == 2 ? .625 : 0;
        var quads = new ArrayList<BakedQuad>(bounds.size() * 6);
        for (var b : bounds) cube(quads, b, inventory, itemY, sprite);
        return List.copyOf(quads);
    }
    /** Shared original RenderBlocksNT/vanilla inventory cuboid UV kernel, without changing existing wood geometry. */
    public static List<BakedQuad> cuboid(double x0, double y0, double z0, double x1, double y1, double z1, boolean inventory, TextureAtlasSprite sprite) {
        var result = new ArrayList<BakedQuad>(6);
        cube(result, box(x0, y0, z0, x1, y1, z1), inventory, 0, sprite);
        return List.copyOf(result);
    }
    private static void barrier(List<Box> boxes, int key, boolean inventory) {
        if (inventory) {
            boxes.add(box(.4375, 0, .4375, .5625, 1, .5625));
            boxes.add(box(.5, .0625, 0, .5625, .4725, 1)); // Actual source .4725, not .4375.
            boxes.add(box(.5, .5625, 0, .5625, .9375, 1)); return;
        }
        boolean nx = (key & 1) != 0, nz = (key & 2) != 0, px = (key & 4) != 0, pz = (key & 8) != 0, py = (key & 16) != 0;
        if (nx) {
            boxes.add(box(0, 0, .4375, .125, 1, .5625));
            boxes.add(box(0, .0625, nz ? .125 : 0, .0625, .4375, pz ? .875 : 1));
            boxes.add(box(0, .5625, nz ? .125 : 0, .0625, .9375, pz ? .875 : 1));
        }
        if (nz) {
            boxes.add(box(.4375, 0, 0, .5625, 1, .125));
            boxes.add(box(nx ? .125 : 0, .0625, 0, px ? .875 : 1, .4375, .0625));
            boxes.add(box(nx ? .125 : 0, .5625, 0, px ? .875 : 1, .9375, .0625));
        }
        if (px) {
            boxes.add(box(.875, 0, .4375, 1, 1, .5625));
            boxes.add(box(.9375, .0625, nz ? .125 : 0, 1, .4375, pz ? .875 : 1));
            boxes.add(box(.9375, .5625, nz ? .125 : 0, 1, .9375, pz ? .875 : 1));
        }
        if (pz) {
            boxes.add(box(.4375, 0, .875, .5625, 1, 1));
            boxes.add(box(nx ? .125 : 0, .0625, .9375, px ? .875 : 1, .4375, 1));
            boxes.add(box(nx ? .125 : 0, .5625, .9375, px ? .875 : 1, .9375, 1));
        }
        if (py) ceiling(boxes, false);
    }
    private static void structure(List<Box> boxes, int type, int key, boolean inventory) {
        boolean nx = !inventory && (key & 1) != 0, nz = !inventory && (key & 2) != 0;
        boolean px = !inventory && (key & 4) != 0, pz = !inventory && (key & 8) != 0, py = !inventory && (key & 16) != 0;
        if (type == 0) {
            boxes.add(box(0, 0, 0, .125, .125, 1));
            boxes.add(box(.875, 0, 0, 1, .125, 1));
            double x0 = inventory || nx ? 0 : .0625, x1 = inventory || px ? 1 : .9375;
            boxes.add(box(x0, .125, .0625, x1, .1875, .4375));
            boxes.add(box(x0, .125, .5625, x1, .1875, .9375));
        } else if (type == 1) {
            double top = py ? 1 : .875;
            boxes.add(box(.0625, 0, .0625, .1875, top, .1875));
            boxes.add(box(.8125, 0, .0625, .9375, top, .1875));
            boxes.add(box(.0625, 0, .8125, .1875, top, .9375));
            Box current = box(.8125, 0, .8125, .9375, top, .9375); boxes.add(current);
            // Original unbraced ifs only guard setRenderBounds, not renderStandardBlock.
            // A joined side therefore repeats the previous cuboid; keep the exact source draw sequence.
            if (!nx) current = box(0, .125, 0, .0625, .375, 1); boxes.add(current);
            if (!px) current = box(.9375, .125, 0, 1, .375, 1); boxes.add(current);
            if (!nz) current = box(0, .5, 0, 1, .75, .0625); boxes.add(current);
            if (!pz) current = box(0, .5, .9375, 1, .75, 1); boxes.add(current);
            if (!py) current = box(0, .875, 0, 1, 1, 1); boxes.add(current);
        } else ceiling(boxes, inventory);
    }
    private static void ceiling(List<Box> boxes, boolean inventory) {
        double bottom = inventory ? .0625 : .875, middle = inventory ? .125 : .9375, top = inventory ? .1875 : 1;
        boxes.add(box(0, bottom, 0, .125, middle, 1)); boxes.add(box(.875, bottom, 0, 1, middle, 1));
        boxes.add(box(0, middle, .0625, 1, top, .4375)); boxes.add(box(0, middle, .5625, 1, top, .9375));
    }
    private static void cube(List<BakedQuad> out, Box b, boolean inventory, double dy, TextureAtlasSprite sprite) {
        double x = b.x0, X = b.x1, y = b.y0, Y = b.y1, z = b.z0, Z = b.z1;
        face(out, Direction.DOWN, inventory, dy, sprite, new double[][]{{x,y,Z,x,Z},{x,y,z,x,z},{X,y,z,X,z},{X,y,Z,X,Z}});
        face(out, Direction.UP, inventory, dy, sprite, new double[][]{{X,Y,Z,X,Z},{X,Y,z,X,z},{x,Y,z,x,z},{x,Y,Z,x,Z}});
        // Inventory uses vanilla RenderBlocks' mirrored north U. World uses the source RenderBlocksNT correction.
        double northLeft = inventory ? X : 1 - x, northRight = inventory ? x : 1 - X;
        face(out, Direction.NORTH, inventory, dy, sprite, new double[][]{{x,Y,z,northLeft,1-Y},{X,Y,z,northRight,1-Y},{X,y,z,northRight,1-y},{x,y,z,northLeft,1-y}});
        face(out, Direction.SOUTH, inventory, dy, sprite, new double[][]{{x,Y,Z,x,1-Y},{x,y,Z,x,1-y},{X,y,Z,X,1-y},{X,Y,Z,X,1-Y}});
        face(out, Direction.WEST, inventory, dy, sprite, new double[][]{{x,Y,Z,Z,1-Y},{x,Y,z,z,1-Y},{x,y,z,z,1-y},{x,y,Z,Z,1-y}});
        // Keep RenderBlocksNT's actual east vertex/UV assignment, including the different upper-pair assignment.
        double e0 = inventory ? z : 1-Z, e1 = inventory ? Z : 1-z;
        double e2 = inventory ? Z : 1-Z, e3 = inventory ? z : 1-z;
        face(out, Direction.EAST, inventory, dy, sprite, new double[][]{{X,y,Z,e0,1-y},{X,y,z,e1,1-y},{X,Y,z,e2,1-Y},{X,Y,Z,e3,1-Y}});
    }
    private static void face(List<BakedQuad> result, Direction normal, boolean inventory, double dy, TextureAtlasSprite sprite, double[][] vertices) {
        Direction rotated = inventory ? switch (normal) { case NORTH -> Direction.WEST; case SOUTH -> Direction.EAST; case WEST -> Direction.SOUTH; case EAST -> Direction.NORTH; default -> normal; } : normal;
        var out = new QuadBakingVertexConsumer(); out.setSprite(sprite); out.setDirection(rotated); out.setShade(true); out.setHasAmbientOcclusion(!inventory);
        for (var v : vertices) {
            // Original inventory RY90 * T(-.5), plus source per-variant Y offset; modern item renderer later subtracts .5.
            double px = inventory ? v[2] : v[0], py = v[1] + dy, pz = inventory ? 1-v[0] : v[2];
            out.addVertex((float)px, (float)py, (float)pz).setColor(255,255,255,255)
                    .setUv(sprite.getU((float)v[3]), sprite.getV((float)v[4])).setLight(0)
                    .setNormal(rotated.getStepX(), rotated.getStepY(), rotated.getStepZ());
        }
        result.add(out.bakeQuad());
    }
    private LegacyWoodGeometry() {}
}
