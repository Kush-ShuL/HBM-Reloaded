package com.hbm.machine.foundry.client;

import java.awt.Color;
import net.minecraft.core.Direction;

/** One pure X5846 ParticleFoundry mesh shared by world particles and local tutorial actors. */
public final class FoundryStreamGeometry {
    @FunctionalInterface
    public interface Vertex {
        void vertex(double x, double y, double z, double u, double v);
    }

    public static int brightenedRgb(int source) {
        Color color = new Color(source).brighter();
        double brightener = 0.7D;
        int r = (int) (255D - (255D - color.getRed()) * brightener);
        int g = (int) (255D - (255D - color.getGreen()) * brightener);
        int b = (int) (255D - (255D - color.getBlue()) * brightener);
        return r << 16 | g << 8 | b;
    }

    /** Age is not clamped: the source still renders age20+partial before removal on tick21. */
    public static void emit(Direction dir, double length, double base, double offset,
            float age, int lifetime, double scroll, Vertex out) {
        Direction rot = dir.getClockWise();
        double width = 0.0625 + (age / lifetime) * 0.0625;
        double girth = 0.125 * (1 - (age / lifetime));
		double dirXG = dir.getStepX() * girth;
		double dirZG = dir.getStepZ() * girth;
		double rotXW = rot.getStepX() * width;
		double rotZW = rot.getStepZ() * width;
		
		double uMin = 0.5 - width;
		double uMax = 0.5 + width;
		double vMin = 0;
		double vMax = length;
		
		double add = scroll;

		//lower back
		out.vertex(rotXW,		girth,		rotZW,	uMax,	vMax + add + girth);
		out.vertex(-rotXW,	girth,		-rotZW,	uMin,	vMax + add + girth);
		out.vertex(-rotXW,	-length,	-rotZW,	uMin,	vMin + add);
		out.vertex(rotXW,		-length,	rotZW,	uMax,	vMin + add);

		//lower front
		out.vertex(dirXG + rotXW,	0,			dirZG + rotZW,	uMax,	vMax + add);
		out.vertex(dirXG - rotXW,	0,			dirZG - rotZW,	uMin,	vMax + add);
		out.vertex(dirXG - rotXW,	-length,	dirZG - rotZW,	uMin,	vMin + add);
		out.vertex(dirXG + rotXW,	-length,	dirZG + rotZW,	uMax,	vMin + add);
		
		double wMin = 0;
		double wMax = girth;
		
		//lower left
		out.vertex(rotXW,			girth,		rotZW,			wMin, vMax + add + girth);
		out.vertex(dirXG + rotXW,	0,			dirZG + rotZW,	wMax, vMax + add);
		out.vertex(dirXG + rotXW,	-length,	dirZG + rotZW,	wMax, vMin + add);
		out.vertex(rotXW,			-length,	rotZW,			wMin, vMin + add);
		
		//lower right
		out.vertex(-rotXW,		girth,		-rotZW,			wMin, vMax + add + girth);
		out.vertex(dirXG - rotXW,	0,			dirZG - rotZW,	wMax, vMax + add);
		out.vertex(dirXG - rotXW,	-length,	dirZG - rotZW,	wMax, vMin + add);
		out.vertex(-rotXW,		-length,	-rotZW,			wMin, vMin + add);

		double dirOX = dir.getStepX() * offset;
		double dirOZ = dir.getStepZ() * offset;
		
		vMax = offset;

		//upper back
		out.vertex(rotXW,				0,		rotZW,			uMax, vMax - add);
		out.vertex(-rotXW,			0,		-rotZW,			uMin, vMax - add);
		out.vertex(-rotXW - dirOX,	base,	-rotZW - dirOZ,	uMin, vMin - add);
		out.vertex(rotXW - dirOX,		base,	rotZW - dirOZ,	uMax, vMin - add);
		
		//upper front
		out.vertex(rotXW,				girth,			rotZW,			uMax, vMax - add + 0.25);
		out.vertex(-rotXW,			girth,			-rotZW,			uMin, vMax - add + 0.25);
		out.vertex(-rotXW - dirOX,	base + girth,	-rotZW - dirOZ,	uMin, vMin - add + 0.25);
		out.vertex(rotXW - dirOX,		base + girth,	rotZW - dirOZ,	uMax, vMin - add + 0.25);
		
		//upper left
		out.vertex(rotXW,			0,				rotZW,			wMax, vMax - add + 0.75);
		out.vertex(rotXW,			girth,			rotZW,			wMin, vMax - add + 0.75);
		out.vertex(rotXW - dirOX,	base + girth,	rotZW - dirOZ,	wMin, vMin - add + 0.75);
		out.vertex(rotXW - dirOX,	base,			rotZW - dirOZ,	wMax, vMin - add + 0.75);
		
		//upper right
		out.vertex(-rotXW,			0,				-rotZW,			wMax, vMax - add + 0.75);
		out.vertex(-rotXW,			girth,			-rotZW,			wMin, vMax - add + 0.75);
		out.vertex(-rotXW - dirOX,	base + girth,	-rotZW - dirOZ,	wMin, vMin - add + 0.75);
		out.vertex(-rotXW - dirOX,	base,			-rotZW - dirOZ, wMax, vMin - add + 0.75);
		
		vMax = 0.125F;
		
		//bend
		out.vertex(dirXG + rotXW,	0,		dirZG + rotZW,	uMax,	vMin + add + 0.75);
		out.vertex(dirXG - rotXW,	0,		dirZG - rotZW,	uMin,	vMin + add + 0.75);
		out.vertex(-rotXW,		girth,	-rotZW,			uMin,	vMax + add + 0.75);
		out.vertex(rotXW,			girth,	rotZW,			uMax,	vMax + add + 0.75);
 }
    private FoundryStreamGeometry() {}
}
