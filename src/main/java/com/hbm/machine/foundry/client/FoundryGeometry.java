package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
import com.hbm.machine.foundry.FoundryContent.Kind;
import java.awt.Color;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
/** Generated directly from all five X5846 RenderFoundry classes. Bounds, faces, UV flips and order unchanged. */
public final class FoundryGeometry {
 public static List<FoundryFaces.Face> build(Kind kind,BlockGetter level,BlockPos pos,int light,boolean inventory){
  Context context=new Context(kind,level,pos,light);FoundryFaces renderer=new FoundryFaces(light);
  if(kind==Kind.MOLD)renderer.setRenderBounds(0,0,0,1,.5,1);else renderer.setRenderBounds(0,0,0,1,1,1);
  if(inventory)switch(kind){case CHANNEL->inventoryChannel(context,context,renderer);case TANK->inventoryTank(context,context,renderer);case OUTLET,SLAGTAP->inventoryOutlet(context,context,renderer);case MOLD->inventoryMold(context,context,renderer);case BASIN->inventoryBasin(context,context,renderer);default->{}}
  else switch(kind){case CHANNEL->worldChannel(context,context,renderer);case TANK->worldTank(context,context,renderer);case OUTLET,SLAGTAP->worldOutlet(context,context,renderer);case MOLD->worldMold(context,context,renderer);case BASIN->worldBasin(context,context,renderer);default->{}}
  return List.copyOf(renderer.faces);
 }
 private static final class Context {
  final Kind kind;final BlockGetter level;final BlockPos pos;final int light;
  final String iconTop,iconSide,iconBottom,iconInner,iconLava="lava_gray",iconFront,iconLock="foundry_outlet_lock",iconFilter="foundry_outlet_filter",iconSideOutlet="foundry_tank_side_outlet",iconSideUpper="foundry_tank_upper",iconSideUpperOutlet="foundry_tank_upper_outlet";
  Context(Kind kind,BlockGetter level,BlockPos pos,int light){this.kind=kind;this.level=level;this.pos=pos;this.light=light;String prefix=kind.id();iconTop=prefix+"_top";iconSide=prefix+"_side";iconBottom=prefix+"_bottom";iconInner=prefix+"_inner";iconFront=prefix+"_front";}
  FoundryBlockEntity getTileEntity(int x,int y,int z){return level!=null&&level.getBlockEntity(pos.offset(x,y,z)) instanceof FoundryBlockEntity tile?tile:null;}
  Kind getBlock(int x,int y,int z){return level!=null&&level.getBlockState(pos.offset(x,y,z)).getBlock() instanceof FoundryBlock block?block.kind:null;}
  int getBlockMetadata(int x,int y,int z){return level==null?3:level.getBlockState(pos.offset(x,y,z)).hasProperty(FoundryBlock.FACING)?level.getBlockState(pos.offset(x,y,z)).getValue(FoundryBlock.FACING).get3DDataValue():0;}
  boolean canConnectTo(Context world,int x,int y,int z,Direction side){return level!=null&&FoundryContent.block(Kind.CHANNEL).connects(level,pos.offset(x,y,z),side);}
 }
private static void worldChannel(Context world, Context block, FoundryFaces renderer) {
 int x=0,y=0,z=0;
		
		FoundryFaces tessellator = renderer;
		int colorMult = 0xFFFFFF;
		float r = (float) (colorMult >> 16 & 255) / 255.0F;
		float g = (float) (colorMult >> 8 & 255) / 255.0F;
		float b = (float) (colorMult & 255) / 255.0F;
		
		float mulBottom = 0.5F;
		float mulTop = 1.0F;
		float mulZ = 0.8F;
		float mulX = 0.6F;

		int brightness = world.light;
		int fullBright = 240;
		tessellator.setBrightness(brightness);

		if(false) {
			float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			float aG = (r * 30.0F + g * 70.0F) / 100.0F;
			float aB = (r * 30.0F + b * 70.0F) / 100.0F;
			r = aR;
			g = aG;
			b = aB;
		}
		
		Context channel = (Context) block;
		
		FoundryBlockEntity te = world.getTileEntity(x, y, z);
		FoundryBlockEntity tile = null;
		
		if(te instanceof FoundryBlockEntity) {
			tile = (FoundryBlockEntity) te;
		}
		
		boolean doRender = tile != null ? (tile.amount > 0 && tile.type != null) : false;
		double level = doRender ? tile.amount * 0.25D / tile.capacity() : 0;
		Color color = doRender ? new Color(tile.type.moltenColor()).brighter() : null;
		
		if(color != null) {
			double brightener = 0.7D;
			int nr = (int) (255D - (255D - color.getRed()) * brightener);
			int ng = (int) (255D - (255D - color.getGreen()) * brightener);
			int nb = (int) (255D - (255D - color.getBlue()) * brightener);
			
			color = new Color(nr, ng, nb);
		}

		boolean posX = channel.canConnectTo(world, x, y, z, Direction.EAST);
		boolean negX = channel.canConnectTo(world, x, y, z, Direction.WEST);
		boolean posZ = channel.canConnectTo(world, x, y, z, Direction.SOUTH);
		boolean negZ = channel.canConnectTo(world, x, y, z, Direction.NORTH);
		
		double height = 0.5D;
		
		renderer.setRenderBounds(0.375D, 0D, 0.375D, 0.625D, 0.125D, 0.625D);
		tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		renderer.setRenderBounds(0.3125D, 0D, 0.3125D, 0.6875D, 0.125D, 0.6875D);
		tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		
		if(doRender) {
			renderer.setRenderBounds(0.375D, 0.125D, 0.375D, 0.625D, 0.125D + level, 0.625D);
			tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			tessellator.setBrightness(fullBright);
			renderer.renderFaceYPos(block, x, y, z, channel.iconLava);
			tessellator.setBrightness(brightness);
		}
		
		if(posX) {
			renderer.setRenderBounds(0.625D, 0D, 0.3125D, 1D, 0.125D, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
			
			renderer.setRenderBounds(0.625D, 0D, 0.3125D, 1D, height, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceZPos(block, x, y, z, channel.iconInner);
			
			renderer.setRenderBounds(0.625D, 0D, 0.625D, 1D, height, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceZPos(block, x, y, z, channel.iconSide);
			
			if(doRender) {
				renderer.setRenderBounds(0.625D, 0.125D, 0.3125D, 1D, 0.125D + level, 0.6875D);
				tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
				tessellator.setBrightness(fullBright);
				renderer.renderFaceYPos(block, x, y, z, channel.iconLava);
				renderer.renderFaceXPos(block, x, y, z, channel.iconLava);
				tessellator.setBrightness(brightness);
			}
			
		} else {
			renderer.setRenderBounds(0.625, 0D, 0.3125D, 0.6875D, height, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceXPos(block, x, y, z, channel.iconSide);
		}
		
		if(negX) {
			renderer.setRenderBounds(0D, 0D, 0.3125D, 0.375D, 0.125D, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
			
			renderer.setRenderBounds(0D, 0D, 0.3125D, 0.375D, height, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceZPos(block, x, y, z, channel.iconInner);
			
			renderer.setRenderBounds(0D, 0D, 0.625D, 0.375D, height, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceZPos(block, x, y, z, channel.iconSide);
			
			if(doRender) {
				renderer.setRenderBounds(0D, 0.125D, 0.3125D, 0.375D, 0.125D + level, 0.6875D);
				tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
				tessellator.setBrightness(fullBright);
				renderer.renderFaceYPos(block, x, y, z, channel.iconLava);
				renderer.renderFaceXNeg(block, x, y, z, channel.iconLava);
				tessellator.setBrightness(brightness);
			}
			
		} else {
			renderer.setRenderBounds(0.3125D, 0D, 0.3125D, 0.375D, height, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceXPos(block, x, y, z, channel.iconInner);
		}
		
		if(posZ) {
			renderer.setRenderBounds(0.3125D, 0D, 0.625D, 0.6875D, 0.125D, 1D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
			
			renderer.setRenderBounds(0.3125D, 0D, 0.625D, 0.375D, height, 1D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceXPos(block, x, y, z, channel.iconInner);
			
			renderer.setRenderBounds(0.625D, 0D, 0.625D, 0.6875D, height, 1D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceXPos(block, x, y, z, channel.iconSide);
			
			if(doRender) {
				renderer.setRenderBounds(0.3125D, 0.125D, 0.625D, 0.6875D, 0.125D + level, 1D);
				tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
				tessellator.setBrightness(fullBright);
				renderer.renderFaceYPos(block, x, y, z, channel.iconLava);
				renderer.renderFaceZPos(block, x, y, z, channel.iconLava);
				tessellator.setBrightness(brightness);
			}
			
		} else {
			renderer.setRenderBounds(0.3125D, 0D, 0.625D, 0.6875D, height, 0.6875D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceZPos(block, x, y, z, channel.iconSide);
		}
		
		if(negZ) {
			renderer.setRenderBounds(0.3125D, 0D, 0D, 0.6875D, 0.125D, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
			
			renderer.setRenderBounds(0.3125D, 0D, 0D, 0.375D, height, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceXPos(block, x, y, z, channel.iconInner);
			
			renderer.setRenderBounds(0.625D, 0D, 0D, 0.6875D, height, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXNeg(block, x, y, z, channel.iconInner);
			renderer.renderFaceXPos(block, x, y, z, channel.iconSide);
			
			if(doRender) {
				renderer.setRenderBounds(0.3125D, 0.125D, 0D, 0.6875D, 0.125D + level, 0.375D);
				tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
				tessellator.setBrightness(fullBright);
				renderer.renderFaceYPos(block, x, y, z, channel.iconLava);
				renderer.renderFaceZNeg(block, x, y, z, channel.iconLava);
				tessellator.setBrightness(brightness);
			}
			
		} else {
			renderer.setRenderBounds(0.3125D, 0D, 0.3125D, 0.6875D, height, 0.375D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);
			renderer.renderFaceZPos(block, x, y, z, channel.iconInner);
		}
		
		return;
	}
private static void inventoryChannel(Context world, Context block, FoundryFaces renderer) {
		
		double height = 0.5D;
		double x = 0;
		double y = 0;
		double z = 0;
		
		
		Context channel = (Context) block;

		FoundryFaces tessellator = renderer;
		tessellator.startDrawingQuads();
		
		//center
		renderer.setRenderBounds(0.375D, 0D, 0.375D, 0.625D, 0.125D, 0.625D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		renderer.setRenderBounds(0.3125D, 0D, 0.3125D, 0.6875D, 0.125D, 0.6875D);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		
		//pos X bottom
		renderer.setRenderBounds(0.625D, 0D, 0.3125D, 1D, 0.125D, 0.6875D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconSide);

		//pos X
		renderer.setRenderBounds(0.625D, 0D, 0.3125D, 1D, height, 0.375D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconInner);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconSide);

		renderer.setRenderBounds(0.625D, 0D, 0.625D, 1D, height, 0.6875D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconSide);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconSide);

		//neg x bottom
		renderer.setRenderBounds(0D, 0D, 0.3125D, 0.375D, 0.125D, 0.6875D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);

		//neg x
		renderer.setRenderBounds(0D, 0D, 0.3125D, 0.375D, height, 0.375D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconInner);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);

		renderer.setRenderBounds(0D, 0D, 0.625D, 0.375D, height, 0.6875D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconSide);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);

		//pos Z bottom
		renderer.setRenderBounds(0.3125D, 0D, 0.625D, 0.6875D, 0.125D, 1D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconSide);

		//pos z
		renderer.setRenderBounds(0.3125D, 0D, 0.625D, 0.375D, height, 1D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconSide);

		renderer.setRenderBounds(0.625D, 0D, 0.625D, 0.6875D, height, 1D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconInner);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconSide);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, channel.iconSide);

		//neg z bottom
		renderer.setRenderBounds(0.3125D, 0D, 0D, 0.6875D, 0.125D, 0.375D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, channel.iconBottom);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);

		//neg z
		renderer.setRenderBounds(0.3125D, 0D, 0D, 0.375D, height, 0.375D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconSide);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconInner);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);

		renderer.setRenderBounds(0.625D, 0D, 0D, 0.6875D, height, 0.375D);
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, channel.iconTop);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, channel.iconInner);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, channel.iconSide);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, channel.iconSide);

		tessellator.draw();
	}
private static void worldTank(Context world, Context block, FoundryFaces renderer) {
 int x=0,y=0,z=0;
		
		FoundryFaces tessellator = renderer;
		int colorMult = 0xFFFFFF;
		float r = (float) (colorMult >> 16 & 255) / 255.0F;
		float g = (float) (colorMult >> 8 & 255) / 255.0F;
		float b = (float) (colorMult & 255) / 255.0F;
		
		float mulBottom = 0.5F;
		float mulTop = 1.0F;
		float mulZ = 0.8F;
		float mulX = 0.6F;

		int brightness = world.light;
		int fullBright = 240;
		tessellator.setBrightness(brightness);

		if(false) {
			float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			float aG = (r * 30.0F + g * 70.0F) / 100.0F;
			float aB = (r * 30.0F + b * 70.0F) / 100.0F;
			r = aR;
			g = aG;
			b = aB;
		}
		
		Context tank = (Context) block;
		
		FoundryBlockEntity te = world.getTileEntity(x, y, z);
		FoundryBlockEntity tile = null;
		
		if(te instanceof FoundryBlockEntity) {
			tile = (FoundryBlockEntity) te;
		}

		boolean conPosX = world.getBlock(x + 1, y, z) == Kind.TANK;
		boolean conNegX = world.getBlock(x - 1, y, z) == Kind.TANK;
		boolean conPosZ = world.getBlock(x, y, z + 1) == Kind.TANK;
		boolean conNegZ = world.getBlock(x, y, z - 1) == Kind.TANK;
		boolean conPosY = world.getBlock(x, y + 1, z) == Kind.TANK;
		boolean conNegY = world.getBlock(x, y - 1, z) == Kind.TANK;

		boolean outPosX = world.getBlock(x + 1, y, z) == Kind.OUTLET && world.getBlockMetadata(x + 1, y, z) == Direction.EAST.get3DDataValue();
		boolean outNegX = world.getBlock(x - 1, y, z) == Kind.OUTLET && world.getBlockMetadata(x - 1, y, z) == Direction.WEST.get3DDataValue();
		boolean outPosZ = world.getBlock(x, y, z + 1) == Kind.OUTLET && world.getBlockMetadata(x, y, z + 1) == Direction.SOUTH.get3DDataValue();
		boolean outNegZ = world.getBlock(x, y, z - 1) == Kind.OUTLET && world.getBlockMetadata(x, y, z - 1) == Direction.NORTH.get3DDataValue();
		
		boolean doRender = tile != null ? (tile.amount > 0 && tile.type != null) : false;
		double max = 0.75D + (conNegY ? 0.125D : 0) + (conPosY ? 0.125D : 0);
		double level = doRender ? tile.amount * max / tile.capacity() : 0;
		Color color = doRender ? new Color(tile.type.moltenColor()).brighter() : null;
		
		if(color != null) {
			double brightener = 0.7D;
			int nr = (int) (255D - (255D - color.getRed()) * brightener);
			int ng = (int) (255D - (255D - color.getGreen()) * brightener);
			int nb = (int) (255D - (255D - color.getBlue()) * brightener);
			
			color = new Color(nr, ng, nb);
		}
		
		renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
		
		if(!conNegY) {
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 0.125D, 1D);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, tank.iconBottom);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, tank.iconBottom);
		}
		
		if(!conPosX) {
			renderer.setRenderBounds(0.875D, 0D, 0D, 1D, 1D, 1D);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXPos(block, x, y, z, conNegY ? (outPosX ? tank.iconSideUpperOutlet : tank.iconSideUpper) : (outPosX ? tank.iconSideOutlet : tank.iconSide));
			renderer.renderFaceXNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			if(conPosZ) renderer.renderFaceZPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			if(conNegZ) renderer.renderFaceZNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, tank.iconTop);
		}
		
		if(!conNegX) {
			renderer.setRenderBounds(0D, 0D, 0D, 0.125D, 1D, 1D);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			renderer.renderFaceXNeg(block, x, y, z, conNegY ? (outNegX ? tank.iconSideUpperOutlet : tank.iconSideUpper) : (outNegX ? tank.iconSideOutlet : tank.iconSide));
			
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			if(conPosZ) renderer.renderFaceZPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			if(conNegZ) renderer.renderFaceZNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, tank.iconTop);
		}
		
		if(!conPosZ) {
			renderer.setRenderBounds(0D, 0D, 0.875D, 1D, 1D, 1D);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, conNegY ? (outPosZ ? tank.iconSideUpperOutlet : tank.iconSideUpper) : (outPosZ ? tank.iconSideOutlet : tank.iconSide));
			renderer.renderFaceZNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			if(conPosX) renderer.renderFaceXPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			if(conNegX) renderer.renderFaceXNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, tank.iconTop);
		}
		
		if(!conNegZ) {
			renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 0.125D);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			renderer.renderFaceZNeg(block, x, y, z, conNegY ? (outNegZ ? tank.iconSideUpperOutlet : tank.iconSideUpper) : (outNegZ ? tank.iconSideOutlet : tank.iconSide));
			
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			if(conPosX) renderer.renderFaceXPos(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			if(conNegX) renderer.renderFaceXNeg(block, x, y, z, conPosY ? tank.iconBottom : tank.iconInner);
			
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, tank.iconTop);
		}
		
		if(doRender) {
			double height = conNegY ? 0D : 0.125D;
			renderer.setRenderBounds(0D, height, 0D, 1D, height + level, 1D);
			tessellator.setColorOpaque_F(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
			tessellator.setBrightness(fullBright);
			renderer.renderFaceYPos(block, x, y, z, tank.iconLava);
			if(conPosX) renderer.renderFaceXPos(block, x, y, z, tank.iconLava);
			if(conNegX) renderer.renderFaceXNeg(block, x, y, z, tank.iconLava);
			if(conPosZ) renderer.renderFaceZPos(block, x, y, z, tank.iconLava);
			if(conNegZ) renderer.renderFaceZNeg(block, x, y, z, tank.iconLava);
			tessellator.setBrightness(brightness);
		}
		
		return;
	}
private static void inventoryTank(Context world, Context block, FoundryFaces renderer) {

		FoundryFaces tessellator = renderer;
		Context basin = (Context) block;
		double x = 0;
		double y = 0;
		double z = 0;
		
		

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, basin.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.875D, z, basin.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, basin.iconBottom);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceXPos(block, x - 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceXNeg(block, x + 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceZPos(block, x, y, z - 0.875D, basin.iconInner);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceZNeg(block, x, y, z + 0.875D, basin.iconInner);
		tessellator.draw();
	}
private static void worldOutlet(Context world, Context block, FoundryFaces renderer) {
 int x=0,y=0,z=0;
		
		FoundryFaces tessellator = renderer;
		int colorMult = 0xFFFFFF;
		float r = (float) (colorMult >> 16 & 255) / 255.0F;
		float g = (float) (colorMult >> 8 & 255) / 255.0F;
		float b = (float) (colorMult & 255) / 255.0F;
		
		float mulBottom = 0.5F;
		float mulTop = 1.0F;
		float mulZ = 0.8F;
		float mulX = 0.6F;
		
		double bot = 0.0D;
		double top = 0.5D;

		if(false) {
			float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			float aG = (r * 30.0F + g * 70.0F) / 100.0F;
			float aB = (r * 30.0F + b * 70.0F) / 100.0F;
			r = aR;
			g = aG;
			b = aB;
		}
		
		Context outlet = (Context) block;
		int meta = world.getBlockMetadata(x, y, z);
		FoundryBlockEntity tile = world.getTileEntity(x, y, z);
		FoundryBlockEntity tileOutlet = tile instanceof FoundryBlockEntity ? (FoundryBlockEntity) tile : null;

		int brightness = world.light;
		tessellator.setBrightness(brightness);
		
		if(meta == 4) {
			renderer.setRenderBounds(0.625D, bot, 0.3125D, 1D, top, 0.6875D);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, outlet.iconTop);
			renderer.renderFaceYPos(block, x, y - 0.375D, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, outlet.iconSide);
			renderer.renderFaceZPos(block, x, y, z - 0.3125D, outlet.iconInner);
			renderer.f = true;
			renderer.renderFaceZNeg(block, x, y, z, outlet.iconSide);
			renderer.renderFaceZNeg(block, x, y, z + 0.3125D, outlet.iconInner);
			renderer.f = false;
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXPos(block, x, y, z, outlet.iconFront);
			renderer.renderFaceXNeg(block, x, y, z, outlet.iconFront);
			
			if(tileOutlet != null && tileOutlet.filter != null) {
				renderer.setRenderBounds(0.96875D, 0.0625, 0.375D, 0.96875D, top, 0.625D);
				renderer.renderFaceXPos(block, x, y, z, outlet.iconFilter);
				renderer.renderFaceXNeg(block, x, y, z, outlet.iconFilter);
			}
			
			if(tileOutlet != null && tileOutlet.closed()) {
				renderer.setRenderBounds(0.9375D, 0.0625, 0.375D, 0.9375D, top, 0.625D);
				renderer.renderFaceXPos(block, x, y, z, outlet.iconLock);
				renderer.renderFaceXNeg(block, x, y, z, outlet.iconLock);
			}
		}
		
		if(meta == 5) {
			renderer.setRenderBounds(0D, bot, 0.3125D, 0.375D, top, 0.6875D);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, outlet.iconTop);
			renderer.renderFaceYPos(block, x, y - 0.375D, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, outlet.iconSide);
			renderer.renderFaceZPos(block, x, y, z - 0.3125D, outlet.iconInner);
			renderer.f = true;
			renderer.renderFaceZNeg(block, x, y, z, outlet.iconSide);
			renderer.renderFaceZNeg(block, x, y, z + 0.3125D, outlet.iconInner);
			renderer.f = false;
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.renderFaceXPos(block, x, y, z, outlet.iconFront);
			renderer.renderFaceXNeg(block, x, y, z, outlet.iconFront);
			
			if(tileOutlet != null && tileOutlet.filter != null) {
				renderer.setRenderBounds(0.03125D, 0.0625, 0.375D, 0.03125D, top, 0.625D);
				renderer.renderFaceXPos(block, x, y, z, outlet.iconFilter);
				renderer.renderFaceXNeg(block, x, y, z, outlet.iconFilter);
			}
			
			if(tileOutlet != null && tileOutlet.closed()) {
				renderer.setRenderBounds(0.0625D, 0.0625, 0.375D, 0.0625D, top, 0.625D);
				renderer.renderFaceXPos(block, x, y, z, outlet.iconLock);
				renderer.renderFaceXNeg(block, x, y, z, outlet.iconLock);
			}
		}
		
		if(meta == 2) {
			renderer.setRenderBounds(0.3125D, bot, 0.625D, 0.6875D, top, 1D);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, outlet.iconTop);
			renderer.renderFaceYPos(block, x, y - 0.375D, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.f = true;
			renderer.renderFaceXPos(block, x, y, z, outlet.iconSide);
			renderer.renderFaceXPos(block, x - 0.3125D, y, z, outlet.iconInner);
			renderer.f = false;
			renderer.renderFaceXNeg(block, x, y, z, outlet.iconSide);
			renderer.renderFaceXNeg(block, x + 0.3125D, y, z, outlet.iconInner);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, outlet.iconFront);
			renderer.renderFaceZNeg(block, x, y, z, outlet.iconFront);
			
			if(tileOutlet != null && tileOutlet.filter != null) {
				renderer.setRenderBounds(0.375D, 0.0625, 0.96875D, 0.625D, top, 0.96875D);
				renderer.renderFaceZPos(block, x, y, z, outlet.iconFilter);
				renderer.renderFaceZNeg(block, x, y, z, outlet.iconFilter);
			}
			
			if(tileOutlet != null && tileOutlet.closed()) {
				renderer.setRenderBounds(0.375D, 0.0625, 0.9375D, 0.625D, top, 0.9375D);
				renderer.renderFaceZPos(block, x, y, z, outlet.iconLock);
				renderer.renderFaceZNeg(block, x, y, z, outlet.iconLock);
			}
		}
		
		if(meta == 3) {
			renderer.setRenderBounds(0.3125D, bot, 0D, 0.6875D, top, 0.375D);
			tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
			renderer.renderFaceYNeg(block, x, y, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
			renderer.renderFaceYPos(block, x, y, z, outlet.iconTop);
			renderer.renderFaceYPos(block, x, y - 0.375D, z, outlet.iconBottom);
			tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
			renderer.f = true;
			renderer.renderFaceXPos(block, x, y, z, outlet.iconSide);
			renderer.renderFaceXPos(block, x - 0.3125D, y, z, outlet.iconInner);
			renderer.f = false;
			renderer.renderFaceXNeg(block, x, y, z, outlet.iconSide);
			renderer.renderFaceXNeg(block, x + 0.3125D, y, z, outlet.iconInner);
			tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
			renderer.renderFaceZPos(block, x, y, z, outlet.iconFront);
			renderer.renderFaceZNeg(block, x, y, z, outlet.iconFront);
			
			if(tileOutlet != null && tileOutlet.filter != null) {
				renderer.setRenderBounds(0.375D, 0.0625D, 0.03125, 0.625D, top, 0.03125D);
				renderer.renderFaceZPos(block, x, y, z, outlet.iconFilter);
				renderer.renderFaceZNeg(block, x, y, z, outlet.iconFilter);
			}
			
			if(tileOutlet != null && tileOutlet.closed()) {
				renderer.setRenderBounds(0.375D, 0.0625, 0.0625D, 0.625D, top, 0.0625D);
				renderer.renderFaceZPos(block, x, y, z, outlet.iconLock);
				renderer.renderFaceZNeg(block, x, y, z, outlet.iconLock);
			}
		}
		
		return;
	}
private static void inventoryOutlet(Context world, Context block, FoundryFaces renderer) {
		
		double bot = 0.0D;
		double top = 0.5D;
		double x = 0;
		double y = 0;
		double z = 0;
		
		
		Context outlet = (Context) block;

		FoundryFaces tessellator = renderer;
		tessellator.startDrawingQuads();
		
		renderer.setRenderBounds(0.3125D, bot, 0D, 0.6875D, top, 0.375D);
		
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, outlet.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.375D, z, outlet.iconBottom);

		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, outlet.iconBottom);

		tessellator.setNormal(1F, 0F, 0F);
		renderer.f = true;
		renderer.renderFaceXPos(block, x, y, z, outlet.iconSide);
		renderer.renderFaceXPos(block, x - 0.3125D, y, z, outlet.iconInner);
		renderer.f = false;

		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, outlet.iconSide);
		renderer.renderFaceXNeg(block, x + 0.3125D, y, z, outlet.iconInner);

		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, outlet.iconFront);

		tessellator.draw();
	}
private static void worldMold(Context world, Context block, FoundryFaces renderer) {
 int x=0,y=0,z=0;
		
		FoundryFaces tessellator = renderer;
		int colorMult = 0xFFFFFF;
		float r = (float) (colorMult >> 16 & 255) / 255.0F;
		float g = (float) (colorMult >> 8 & 255) / 255.0F;
		float b = (float) (colorMult & 255) / 255.0F;
		
		float mulBottom = 0.5F;
		float mulTop = 1.0F;
		float mulZ = 0.8F;
		float mulX = 0.6F;

		int brightness = world.light;
		tessellator.setBrightness(brightness);

		if(false) {
			float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			float aG = (r * 30.0F + g * 70.0F) / 100.0F;
			float aB = (r * 30.0F + b * 70.0F) / 100.0F;
			r = aR;
			g = aG;
			b = aB;
		}
		
		Context mold = (Context) block;

		tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
		renderer.renderFaceYPos(block, x, y, z, mold.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.375D, z, mold.iconBottom);
		tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
		renderer.renderFaceYNeg(block, x, y, z, mold.iconBottom);
		tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
		renderer.renderFaceXPos(block, x, y, z, mold.iconSide);
		renderer.renderFaceXNeg(block, x, y, z, mold.iconSide);
		renderer.renderFaceXPos(block, x - 0.875D, y, z, mold.iconInner);
		renderer.renderFaceXNeg(block, x + 0.875D, y, z, mold.iconInner);
		tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
		renderer.renderFaceZPos(block, x, y, z, mold.iconSide);
		renderer.renderFaceZNeg(block, x, y, z, mold.iconSide);
		renderer.renderFaceZPos(block, x, y, z - 0.875D, mold.iconInner);
		renderer.renderFaceZNeg(block, x, y, z + 0.875D, mold.iconInner);

		return;
	}
private static void inventoryMold(Context world, Context block, FoundryFaces renderer) {

		FoundryFaces tessellator = renderer;
		Context basin = (Context) block;
		double x = 0;
		double y = 0;
		double z = 0;
		
		
		
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, basin.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.375D, z, basin.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, basin.iconBottom);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceXPos(block, x - 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceXNeg(block, x + 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceZPos(block, x, y, z - 0.875D, basin.iconInner);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceZNeg(block, x, y, z + 0.875D, basin.iconInner);
		tessellator.draw();
	}
private static void worldBasin(Context world, Context block, FoundryFaces renderer) {
 int x=0,y=0,z=0;
		
		FoundryFaces tessellator = renderer;
		int colorMult = 0xFFFFFF;
		float r = (float) (colorMult >> 16 & 255) / 255.0F;
		float g = (float) (colorMult >> 8 & 255) / 255.0F;
		float b = (float) (colorMult & 255) / 255.0F;
		
		float mulBottom = 0.5F;
		float mulTop = 1.0F;
		float mulZ = 0.8F;
		float mulX = 0.6F;

		int brightness = world.light;
		tessellator.setBrightness(brightness);

		if(false) {
			float aR = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			float aG = (r * 30.0F + g * 70.0F) / 100.0F;
			float aB = (r * 30.0F + b * 70.0F) / 100.0F;
			r = aR;
			g = aG;
			b = aB;
		}
		
		Context basin = (Context) block;

		tessellator.setColorOpaque_F(r * mulTop, g * mulTop, b * mulTop);
		renderer.renderFaceYPos(block, x, y, z, basin.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.875D, z, basin.iconBottom);
		tessellator.setColorOpaque_F(r * mulBottom, g * mulBottom, b * mulBottom);
		renderer.renderFaceYNeg(block, x, y, z, basin.iconBottom);
		tessellator.setColorOpaque_F(r * mulX, g * mulX, b * mulX);
		renderer.renderFaceXPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceXNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceXPos(block, x - 0.875D, y, z, basin.iconInner);
		renderer.renderFaceXNeg(block, x + 0.875D, y, z, basin.iconInner);
		tessellator.setColorOpaque_F(r * mulZ, g * mulZ, b * mulZ);
		renderer.renderFaceZPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceZNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceZPos(block, x, y, z - 0.875D, basin.iconInner);
		renderer.renderFaceZNeg(block, x, y, z + 0.875D, basin.iconInner);

		return;
	}
private static void inventoryBasin(Context world, Context block, FoundryFaces renderer) {

		FoundryFaces tessellator = renderer;
		Context basin = (Context) block;
		double x = 0;
		double y = 0;
		double z = 0;
		
		

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		renderer.renderFaceYPos(block, x, y, z, basin.iconTop);
		renderer.renderFaceYPos(block, x, y - 0.875D, z, basin.iconBottom);
		tessellator.setNormal(0F, -1F, 0F);
		renderer.renderFaceYNeg(block, x, y, z, basin.iconBottom);
		tessellator.setNormal(1F, 0F, 0F);
		renderer.renderFaceXPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceXPos(block, x - 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(-1F, 0F, 0F);
		renderer.renderFaceXNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceXNeg(block, x + 0.875D, y, z, basin.iconInner);
		tessellator.setNormal(0F, 0F, 1F);
		renderer.renderFaceZPos(block, x, y, z, basin.iconSide);
		renderer.renderFaceZPos(block, x, y, z - 0.875D, basin.iconInner);
		tessellator.setNormal(0F, 0F, -1F);
		renderer.renderFaceZNeg(block, x, y, z, basin.iconSide);
		renderer.renderFaceZNeg(block, x, y, z + 0.875D, basin.iconInner);
		tessellator.draw();
	}
}
