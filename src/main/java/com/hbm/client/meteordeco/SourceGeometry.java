package com.hbm.client.meteordeco;
import com.hbm.block.meteordeco.*;
import java.util.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
/** Statements generated directly from original RenderFence/RenderSteelWall/RenderSteelCorner. */
final class SourceGeometry {
 static List<SourceFaces.Face> faces(BlockState state,boolean inventory,boolean corner){var context=new Context(state);var renderer=new SourceFaces(0);renderer.setRenderBounds(0,0,0,1,1,1);if(state!=null&&state.getBlock() instanceof MetalFenceBlock)worldFence(context,context,renderer);else if(corner){if(inventory)inventorySteelCorner(context,context,renderer);else worldSteelCorner(context,context,renderer);}else{if(inventory)inventorySteelWall(context,context,renderer);else worldSteelWall(context,context,renderer);}return List.copyOf(renderer.faces);}
 static final class Context {
  final BlockState state;final String postIcon="fence_metal_post";Context(BlockState state){this.state=state;}
  int metadata(){return state==null?0:state.hasProperty(SteelPanelBlock.META)?state.getValue(SteelPanelBlock.META):state.getValue(MetalFenceBlock.META);}
  int getBlockMetadata(int x,int y,int z){return metadata();}
  String getIcon(int side,int meta){return state!=null&&state.getBlock() instanceof MetalFenceBlock?(meta==0?"fence_metal":"fence_metal_post"):"steel_wall";}
  boolean canConnectFenceTo(Context world,int x,int y,int z){Direction side=x<0?Direction.WEST:x>0?Direction.EAST:z<0?Direction.NORTH:Direction.SOUTH;return state.getValue(MetalFenceBlock.property(side));}
 }
private static void worldFence(Context world, Context block, SourceFaces renderer) {
 int x=0,y=0,z=0;
		
		Context fence=block;
		int meta = world.getBlockMetadata(x, y, z);

		boolean xNeg = fence.canConnectFenceTo(world, x - 1, y, z);
		boolean xPos = fence.canConnectFenceTo(world, x + 1, y, z);
		boolean zNeg = fence.canConnectFenceTo(world, x, y, z - 1);
		boolean zPos = fence.canConnectFenceTo(world, x, y, z + 1);

		boolean hasX = xNeg || xPos;
		boolean hasZ = zNeg || zPos;
		
		boolean straightX = !hasZ && xNeg && xPos;
		boolean straightZ = !hasX && zNeg && zPos;
		boolean showPost = meta == 1 || (!straightX && !straightZ);

		if (!hasX && !hasZ) {
			hasX = true;
		}

		float f = 0.4375F;
		float f1 = 0.5625F;
		float f4 = xNeg ? 0.0F : f;
		float f5 = xPos ? 1.0F : f1;
		float f6 = zNeg ? 0.0F : f;
		float f7 = zPos ? 1.0F : f1;
		renderer.f = true;

		if (hasX) {
			renderer.setRenderBounds((double)f4, (double)0, (double)0.5, (double)f5, (double)1, (double)0.5);
			renderer.renderStandardBlock(fence, x, y, z);
		}

		if (hasZ) {
			renderer.setRenderBounds((double)0.5, (double)0, (double)f6, (double)0.5, (double)1, (double)f7);
			renderer.renderStandardBlock(fence, x, y, z);
		}

		if(showPost) {
			f = 0.375F;
			f1 = 0.625F;
			renderer.setOverrideBlockTexture(fence.postIcon);
			renderer.setRenderBounds((double)f, 0.0D, (double)f, (double)f1, 1.0D, (double)f1);
			renderer.renderStandardBlock(fence, x, y, z);
			renderer.clearOverrideBlockTexture();
		}

		renderer.f = false;
		
		return;
	}
private static void worldSteelWall(Context world, Context block, SourceFaces renderer) {
 int x=0,y=0,z=0;
		SourceFaces tessellator = renderer;
		tessellator.setBrightness(0);
		int meta = world.getBlockMetadata(x, y, z);
		
		switch(meta) {
		case 2: renderer.setRenderBounds(0D, 0D, 0.875D, 1D, 1D, 1D); break;
		case 3: renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0.125D); break;
		case 4: renderer.setRenderBounds(0.875D, 0D, 0D, 1D, 1D, 1D); break;
		case 5: renderer.setRenderBounds(0D, 0D, 0D, 0.125D, 1D, 1D); break;
		}

		renderer.renderStandardBlock(block, x, y, z);
		
		return;
	}
private static void inventorySteelWall(Context world, Context block, SourceFaces renderer) {

		SourceFaces tessellator = renderer;
		
		
		renderer.setRenderBounds(0D, 0D, 0.875D, 1D, 1D, 1D);
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);	renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, -1F, 0F);	renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(1F, 0F, 0F);	renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(-1F, 0F, 0F);	renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, 1F);	renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, -1F);	renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.draw();
	}
private static void worldSteelCorner(Context world, Context block, SourceFaces renderer) {
 int x=0,y=0,z=0;
		SourceFaces tessellator = renderer;
		tessellator.setBrightness(0);
		int meta = world.getBlockMetadata(x, y, z);
		
		renderer.nt=true;
		
		switch(meta) {
		case 2:
			renderer.setRenderBounds(0.25D, 0D, 0.875D, 1D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0D, 0D, 0.75D, 0.25D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0D, 0D, 0D, 0.125D, 1D, 0.75D); renderer.renderStandardBlock(block, x, y, z);
			break;
		case 3:
			renderer.setRenderBounds(0D, 0D, 0D, 0.75D, 1D, 0.125D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0.75D, 0D, 0D, 1D, 1D, 0.25D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0.875D, 0D, 0.25D, 1D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			break;
		case 4:
			renderer.setRenderBounds(0.875D, 0D, 0D, 1D, 1D, 0.75D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0.75D, 0D, 0.75D, 1D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0D, 0D, 0.875D, 0.75D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			break;
		case 5:
			renderer.setRenderBounds(0D, 0D, 0.25D, 0.125D, 1D, 1D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0D, 0D, 0D, 0.25D, 1D, 0.25D); renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0.25D, 0D, 0D, 1D, 1D, 0.125D); renderer.renderStandardBlock(block, x, y, z);
			break;
		}
		
		return;
	}
private static void inventorySteelCorner(Context world, Context block, SourceFaces renderer) {

		SourceFaces tessellator = renderer;
		
		
		tessellator.startDrawingQuads();

		
		renderer.setRenderBounds(0D, 0D, 0.875D, 0.75D, 1D, 1D);
		tessellator.setNormal(0F, 1F, 0F);	renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, -1F, 0F);	renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(-1F, 0F, 0F);	renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, 1F);	renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, -1F);	renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(0, 0));

		
		renderer.setRenderBounds(0.75D, 0D, 0.75D, 1D, 1D, 1D);
		tessellator.setNormal(0F, 1F, 0F);	renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, -1F, 0F);	renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(1F, 0F, 0F);	renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(-1F, 0F, 0F);	renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, 1F);	renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, -1F);	renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(0, 0));
		
		renderer.setRenderBounds(0.875D, 0D, 0D, 1D, 1D, 0.75D);
		tessellator.setNormal(0F, 1F, 0F);	renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, -1F, 0F);	renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(1F, 0F, 0F);	renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(-1F, 0F, 0F);	renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(0, 0));
		tessellator.setNormal(0F, 0F, -1F);	renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(0, 0));
		
		tessellator.draw();
	}
}
