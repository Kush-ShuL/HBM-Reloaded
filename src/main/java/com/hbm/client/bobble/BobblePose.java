package com.hbm.client.bobble;
import com.hbm.block.bobble.BobbleType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
/** Original RenderBobble methods mechanically adapted to BobbleDraw, with every actual variant and accessory. */
final class BobblePose {
    private final BobbleDraw draw;private final long time;private final BobbleDraw.Model bobble;
    BobblePose(BobbleDraw draw,long time){this.draw=draw;this.time=time;this.bobble=draw.model("models/trinkets/bobble.obj",true);}
	public static final ResourceLocation socket = ResourceLocation.parse("hbm:textures/models/trinkets/socket.png");
	public static final ResourceLocation glow = ResourceLocation.parse("hbm:textures/models/trinkets/glow.png");
	public static final ResourceLocation lamp = ResourceLocation.parse("hbm:textures/block/fluorescent_lamp.png");

	public static final ResourceLocation bobble_vaultboy = ResourceLocation.parse("hbm:textures/models/trinkets/vaultboy.png");
	public static final ResourceLocation bobble_hbm = ResourceLocation.parse("hbm:textures/models/trinkets/hbm.png");
	public static final ResourceLocation bobble_pu238 = ResourceLocation.parse("hbm:textures/models/trinkets/pellet.png");
	public static final ResourceLocation bobble_frizzle = ResourceLocation.parse("hbm:textures/models/trinkets/frizzle.png");
	public static final ResourceLocation bobble_vt = ResourceLocation.parse("hbm:textures/models/trinkets/vt.png");
	public static final ResourceLocation bobble_doc = ResourceLocation.parse("hbm:textures/models/trinkets/doctor17ph.png");
	public static final ResourceLocation bobble_blue = ResourceLocation.parse("hbm:textures/models/trinkets/thebluehat.png");
	public static final ResourceLocation bobble_pheo = ResourceLocation.parse("hbm:textures/models/trinkets/pheo.png");
	public static final ResourceLocation bobble_adam = ResourceLocation.parse("hbm:textures/models/trinkets/adam29.png");
	public static final ResourceLocation bobble_uffr = ResourceLocation.parse("hbm:textures/models/trinkets/uffr.png");
	public static final ResourceLocation bobble_vaer = ResourceLocation.parse("hbm:textures/models/trinkets/vaer.png");
	public static final ResourceLocation bobble_nos = ResourceLocation.parse("hbm:textures/models/trinkets/nos.png");
	public static final ResourceLocation bobble_drillgon = ResourceLocation.parse("hbm:textures/models/trinkets/drillgon200.png");
	public static final ResourceLocation bobble_cirno = ResourceLocation.parse("hbm:textures/models/trinkets/cirno.png");
	public static final ResourceLocation bobble_microwave = ResourceLocation.parse("hbm:textures/models/trinkets/microwave.png");
	public static final ResourceLocation bobble_peep = ResourceLocation.parse("hbm:textures/models/trinkets/peep.png");
	public static final ResourceLocation bobble_mellow = ResourceLocation.parse("hbm:textures/models/trinkets/mellowrpg8.png");
	public static final ResourceLocation bobble_mellow_glow = ResourceLocation.parse("hbm:textures/models/trinkets/mellowrpg8_glow.png");
	public static final ResourceLocation bobble_abel = ResourceLocation.parse("hbm:textures/models/trinkets/abel.png");
	public static final ResourceLocation bobble_abel_glow = ResourceLocation.parse("hbm:textures/models/trinkets/abel_glow.png");
	public void renderBobble(BobbleType type) {
		
		draw.glEnable(GL11.GL_LIGHTING);
		draw.glEnable(GL12.GL_RESCALE_NORMAL);
		
		bindTexture(socket);
		bobble.renderPart("Socket");
		
		switch(type) {
		case STRENGTH:
		case PERCEPTION:
		case ENDURANCE:
		case CHARISMA:
		case INTELLIGENCE:
		case AGILITY:
		case LUCK:		bindTexture(bobble_vaultboy); break;
			
		case BOB:		bindTexture(bobble_hbm); break;
		case PU238:		bindTexture(bobble_pu238); break;
		case FRIZZLE:	bindTexture(bobble_frizzle); break;
		case VT:		bindTexture(bobble_vt); break;
		case DOC:		bindTexture(bobble_doc); break;
		case BLUEHAT:	bindTexture(bobble_blue); break;
		case PHEO:		bindTexture(bobble_pheo); break;
		case CIRNO:		bindTexture(bobble_cirno); break;
		case ADAM29:	bindTexture(bobble_adam); break;
		case UFFR:		bindTexture(bobble_uffr); break;
		case VAER:		bindTexture(bobble_vaer); break;
		case NOS:		bindTexture(bobble_nos); break;
		case DRILLGON:	bindTexture(bobble_drillgon); break;
		case MICROWAVE:	bindTexture(bobble_microwave); break;
		case PEEP:		bindTexture(bobble_peep); break;
		case MELLOW:	bindTexture(bobble_mellow); break;
		case ABEL:		bindTexture(bobble_abel); break;
		default:		bindTexture(ResourceLocation.parse("hbm:textures/models/thegadget3_.png"));
		}
		
		switch(type) {
		case PU238:		renderPellet(type); break;
		case UFFR:		renderFumo(type); break;
		case DRILLGON:	renderDrillgon(type); break;
		default: renderGuy(type);
		}
		
		draw.glPushMatrix();
		renderPost(type);
		draw.glPopMatrix();
		
		renderSocket(type);
	}
	
	

	public double[] rotLeftArm = {0, 0, 0};
	public double[] rotRightArm = {0, 0, 0};
	public double[] rotLeftLeg = {0, 0, 0};
	public double[] rotRightLeg = {0, 0, 0};
	public double rotBody = 0;
	public double[] rotHead = {0, 0, 0};
	
	public void resetFigurineRotation() {
		rotLeftArm = new double[]{0, 0, 0};
		rotRightArm = new double[]{0, 0, 0};
		rotLeftLeg = new double[]{0, 0, 0};
		rotRightLeg = new double[]{0, 0, 0};
		rotBody = 0;
		rotHead = new double[]{0, 0, 0};
	}
	
	@SuppressWarnings("incomplete-switch") 
	public void setupFigurineRotation(BobbleType type) {
		switch(type) {
		case STRENGTH:
			rotLeftArm = new double[]{0, 25, 135};
			rotRightArm = new double[]{0, -45, 135};
			rotLeftLeg = new double[]{0, 0, -5};
			rotRightLeg = new double[]{0, 0, 5};
			rotHead = new double[]{15, 0, 0};
			break;
		case PERCEPTION:
			rotLeftArm = new double[]{0, -15, 135};
			rotRightArm = new double[]{-5, 0, 0};
			break;
		case ENDURANCE:
			rotBody = 45;
			rotLeftArm = new double[]{0, -25, 30};
			rotRightArm = new double[]{0, 45, 30};
			rotHead = new double[]{0, -45, 0};
			break;
		case CHARISMA:
			rotBody = 45;
			rotRightArm = new double[]{0, -45, 90};
			rotLeftLeg = new double[]{0, 0, -5};
			rotRightLeg = new double[]{0, 0, 5};
			rotHead = new double[]{-5, -45, 0};
			break;
		case INTELLIGENCE:
			rotHead = new double[]{0, 30, 0};
			rotLeftArm = new double[]{5, 0, 0};
			rotRightArm = new double[]{15, 0, 170};
			break;
		case AGILITY:
			rotLeftArm = new double[]{0, 0, 60};
			rotRightArm = new double[]{0, 0, -45};
			rotLeftLeg = new double[]{0, 0, -15};
			rotRightLeg = new double[]{0, 0, 45};
			break;
		case LUCK:
			rotLeftArm = new double[]{135, 45, 0};
			rotRightArm = new double[]{-135, -45, 0};
			rotRightLeg = new double[]{-5, 0, 0};
			break;
		case VT:
			rotLeftArm = new double[]{0, -45, 60};
			rotRightArm = new double[]{0, 0, 45};
			rotLeftLeg = new double[]{2, 0, 0};
			rotRightLeg = new double[]{-2, 0, 0};
			break;
		case BLUEHAT:
			rotLeftArm = new double[]{0, 90, 60};
			break;
		case FRIZZLE:
			rotLeftArm = new double[]{0, 15, 45};
			rotRightArm = new double[]{0, 0, 80};
			rotLeftLeg = new double[]{0, 0, 2};
			rotRightLeg = new double[]{0, 0, -2};
			break;
		case ADAM29:
			rotRightArm = new double[]{0, 0, 60};
			break;
		case PHEO:
			rotLeftArm = new double[]{0, 0, 80};
			rotRightArm = new double[]{0, 0, 45};
			break;
		case VAER:
			rotLeftArm = new double[]{0, -5, 45};
			rotRightArm = new double[]{0, 15, 45};
			break;
		case PEEP:
			rotLeftArm = new double[]{0, 0, 1};
			rotRightArm = new double[]{0, 0, 1};
			break;
		case MELLOW:
			rotLeftArm = new double[]{0, 10, 0};
			rotRightArm = new double[]{0, -10, 0};
			rotLeftLeg = new double[]{3, 5, 2};
			rotRightLeg = new double[]{-3, -5, 0};
			break;
		case ABEL:
			rotLeftArm = new double[]{0, 80, 90};
			rotRightArm = new double[]{0, -80, 90};
			break;
		}
	}
	
	public void renderGuy(BobbleType type) {

		resetFigurineRotation();
		setupFigurineRotation(type);

		draw.glPushMatrix();
		draw.glRotated(rotBody, 0, 1, 0);
		
		if(type == BobbleType.PEEP) bobble.renderPart("PeepTail");
		
		draw.glDisable(GL11.GL_CULL_FACE);
		
		String suffix = type.skinLayers ? "" : "17";
		
		draw.glEnable(GL11.GL_BLEND);
		draw.glAlphaFunc(GL11.GL_GREATER, 0);
		draw.blendSeparate(770, 771, 1, 0);
		draw.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		
		draw.glPushMatrix();
			draw.glTranslated(0, 1, -0.125);
			draw.glRotated(rotLeftLeg[0], 1, 0, 0);
			draw.glRotated(rotLeftLeg[1], 0, 1, 0);
			draw.glRotated(rotLeftLeg[2], 0, 0, 1);
			draw.glTranslated(0, -1, 0.125);
			bobble.renderPart("LL" + suffix);
		draw.glPopMatrix();

		
		draw.glPushMatrix();
			draw.glTranslated(0, 1, 0.125);
			draw.glRotated(rotRightLeg[0], 1, 0, 0);
			draw.glRotated(rotRightLeg[1], 0, 1, 0);
			draw.glRotated(rotRightLeg[2], 0, 0, 1);
			draw.glTranslated(0, -1, -0.125);
			bobble.renderPart("RL" + suffix);
		draw.glPopMatrix();
		
		

		
		draw.glPushMatrix();
			draw.glTranslated(0, 1.625, -0.25);
			draw.glRotated(rotLeftArm[0], 1, 0, 0);
			draw.glRotated(rotLeftArm[1], 0, 1, 0);
			draw.glRotated(rotLeftArm[2], 0, 0, 1);
			draw.glTranslated(0, -1.625, 0.25);
			bobble.renderPart("LA" + suffix);
		draw.glPopMatrix();

		
		draw.glPushMatrix();
			draw.glTranslated(0, 1.625, 0.25);
			draw.glRotated(rotRightArm[0], 1, 0, 0);
			draw.glRotated(rotRightArm[1], 0, 1, 0);
			draw.glRotated(rotRightArm[2], 0, 0, 1);
			draw.glTranslated(0, -1.625, -0.25);
			bobble.renderPart("RA" + suffix);
		draw.glPopMatrix();

		
		bobble.renderPart("Body" + suffix);

		
		double speed = 0.005;
		double amplitude = 1;
		
		draw.glPushMatrix();
		draw.glTranslated(0, 1.75, 0);
		draw.glRotated(Math.sin(time * speed) * amplitude, 1, 0, 0);
		draw.glRotated(Math.sin(time * speed + (Math.PI * 0.5)) * amplitude, 0, 0, 1);
		
		draw.glRotated(rotHead[0], 1, 0, 0);
		draw.glRotated(rotHead[1], 0, 1, 0);
		draw.glRotated(rotHead[2], 0, 0, 1);
		
		draw.glTranslated(0, -1.75, 0);
		bobble.renderPart("Head" + suffix);

		if(type == BobbleType.VT) bobble.renderPart("Horn");
		if(type == BobbleType.PEEP) bobble.renderPart("PeepHat");
		
		if(type == BobbleType.VAER) {
			draw.glTranslated(0.25, 1.9, 0.075);
			draw.glRotated(-60, 0, 0, 1);
			draw.glScaled(0.5, 0.5, 0.5);
			this.renderItem(ResourceLocation.parse("hbm:textures/item/cigarette.png"));
		}
		
		if(type == BobbleType.NOS) {
			draw.glTranslated(0, 1.75, 0);
			draw.glRotated(180, 1, 0, 0);
			double scale = 0.095D;
			draw.glScaled(scale, scale, scale);
			this.bindTexture(ResourceLocation.parse("hbm:textures/armor/hat.png"));
			draw.model("models/armor/hat.obj",false).renderAll();
		}
		
		draw.glPopMatrix();
		
		draw.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		draw.glDisable(GL11.GL_BLEND);
		
		draw.glEnable(GL11.GL_CULL_FACE);
		
		draw.glPopMatrix();
	}
	
	public void renderPellet(BobbleType type) {

		draw.glEnable(GL11.GL_CULL_FACE);

		draw.glPushMatrix();
		draw.glPushAttrib(GL11.GL_LIGHTING_BIT);
		draw.fullbright();
		draw.glDisable(GL11.GL_LIGHTING);
		bobble.renderPart("Pellet");

		draw.glDisable(GL11.GL_TEXTURE_2D);
		draw.glEnable(GL11.GL_BLEND);
		draw.glAlphaFunc(GL11.GL_GREATER, 0);

		draw.blendSeparate(770, 771, 1, 0);
		draw.glColor4f(1.0F, 1.0F, 0.0F, 0.1F + (float) Math.sin(time * 0.001D) * 0.05F);
		bobble.renderPart("PelletShine");
		draw.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		draw.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		draw.glDisable(GL11.GL_BLEND);
		draw.glEnable(GL11.GL_TEXTURE_2D);

		draw.glEnable(GL11.GL_LIGHTING);
		draw.glPopAttrib();
		draw.glPopMatrix();
	}
	
	public void renderFumo(BobbleType type) {

		draw.glEnable(GL11.GL_CULL_FACE);
		bobble.renderPart("Fumo");
		
		double speed = 0.005;
		double amplitude = 1;
		
		draw.glPushMatrix();
		draw.glTranslated(0, 0.75, 0);
		draw.glRotated(Math.sin(time * speed) * amplitude, 1, 0, 0);
		draw.glRotated(Math.sin(time * speed + (Math.PI * 0.5)) * amplitude, 0, 0, 1);
		draw.glTranslated(0, -0.75, 0);

		draw.glDisable(GL11.GL_CULL_FACE);
		bobble.renderPart("FumoHead");
		
		draw.glPopMatrix();
	}
	
	public void renderDrillgon(BobbleType type) {
		bobble.renderPart("Drillgon");
	}

	private ResourceLocation shot_tex = ResourceLocation.parse("hbm:textures/models/modeluboinik.png");
	
	
	@SuppressWarnings("incomplete-switch")
	public void renderPost(BobbleType type) {
		switch(type) {
		case BLUEHAT:
			double scale = 0.0625D;
			draw.glTranslated(0D, 0.875D, -0.5D);
			draw.glRotated(-90, 0, 1, 0);
			draw.glRotated(-160, 0, 0, 1);
			draw.glScaled(scale, scale, scale);
			bindTexture(ResourceLocation.parse("hbm:textures/armor/hev_helmet.png"));
			draw.model("models/armor/hev.obj",false).renderPart("Head");
			break;
		case FRIZZLE:
			draw.glPushMatrix();
			draw.glTranslated(0.8, 1.6, 0.4);
			draw.glScaled(0.125, 0.125, 0.125);
			draw.glRotated(90, 0, 1, 0);
			draw.glRotated(10, 1, 0, 0);
			this.bindTexture(ResourceLocation.parse("hbm:textures/models/weapons/n_i_4_n_i.png"));
			draw.glShadeModel(GL11.GL_SMOOTH);
			draw.model("models/weapons/n_i_4_n_i.obj",false).renderPart("FrameDark");
			draw.model("models/weapons/n_i_4_n_i.obj",false).renderPart("Grip");
			draw.model("models/weapons/n_i_4_n_i.obj",false).renderPart("FrameLight");
			draw.model("models/weapons/n_i_4_n_i.obj",false).renderPart("Cylinder");
			draw.model("models/weapons/n_i_4_n_i.obj",false).renderPart("Barrel");
			draw.glShadeModel(GL11.GL_FLAT);
			draw.glPopMatrix();
			
			draw.glTranslated(0.3, 1.4, -0.2);
			draw.glRotated(-100, 1, 0, 0);
			draw.glScaled(0.5, 0.5, 0.5);
			renderItem(ResourceLocation.parse("hbm:textures/item/weapon_mod_special.doubloons.png"));
			break;
		case ADAM29:
			draw.glTranslated(0.4, 1.15, 0.4);
			draw.glScaled(0.5, 0.5, 0.5);
			renderItem(ResourceLocation.parse("hbm:textures/item/can_redbomb.png"));
			break;
		case PHEO:
			draw.glTranslated(0.5, 1.15, 0.45);
			draw.glRotated(-60, 1, 0, 0);
			draw.glScaled(2, 2, 2);
			this.bindTexture(ResourceLocation.parse("hbm:textures/models/shimmer_axe.png"));
			draw.model("models/shimmer_axe.obj",true).renderAll();
			break;
		case BOB:
			draw.glShadeModel(GL11.GL_SMOOTH);
			draw.glPushMatrix();
			draw.glTranslatef(0, 0.6875F, 0.625F);
			draw.glRotated(-90, 1, 0, 0);
			draw.glScaled(0.125, 0.125, 0.125);
			bindTexture(ResourceLocation.parse("hbm:textures/models/weapons/fatman_mininuke.png"));
			draw.glTranslatef(-6, 0, 0);
			for(int i = -1; i <= 1; i++) {
				draw.glTranslatef(3, 0, 0);
				draw.model("models/weapons/fatman.obj",false).renderPart("MiniNuke");
			}
			draw.glPopMatrix();
			draw.glPushMatrix();
			draw.glTranslatef(0.25F, 0.3125F, -0.5F);
			draw.glRotated(-90, 1, 0, 0);
			draw.glRotated(90, 0, 1, 0);
			draw.glScaled(0.1, 0.1, 0.1);
			bindTexture(ResourceLocation.parse("hbm:textures/models/weapons/double_barrel_sacred_dragon.png"));
			draw.model("models/weapons/sacred_dragon.obj",false).renderPart("Stock");
			draw.model("models/weapons/sacred_dragon.obj",false).renderPart("BarrelShort");
			draw.model("models/weapons/sacred_dragon.obj",false).renderPart("Buckle");
			draw.model("models/weapons/sacred_dragon.obj",false).renderPart("Lever");
			draw.glPopMatrix();
			draw.glShadeModel(GL11.GL_FLAT);
			break;
		case VAER:
			this.bindTexture(shot_tex);
			draw.glTranslated(0.6, 1.5, 0);
			draw.glRotated(140, 0, 0, 1);
			draw.glRotated(-60, 0, 1, 0);
			draw.glTranslated(-0.2, 0, 0);
			draw.glScaled(0.5, 0.5, 0.5);
			
			break;
		case MELLOW:
			draw.glPushAttrib(GL11.GL_LIGHTING_BIT);
			draw.fullbright();
			bindTexture(bobble_mellow_glow);
			renderGuy(type);
			draw.glEnable(GL11.GL_BLEND);
			draw.glAlphaFunc(GL11.GL_GREATER, 0);
			draw.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			this.bindTexture(lamp);
			bobble.renderPart("Fluoro");
			this.bindTexture(glow);
			bobble.renderPart("Glow");
			draw.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			draw.glDisable(GL11.GL_BLEND);
			draw.glPopAttrib();
			break;
		case ABEL:
			draw.glPushAttrib(GL11.GL_LIGHTING_BIT);
			draw.fullbright();
			bindTexture(bobble_abel_glow);
			renderGuy(type);
			draw.glPopAttrib();
			break;
		}
	}
	
    private void bindTexture(ResourceLocation texture){draw.bind(texture);}
    private void renderItem(ResourceLocation texture){draw.icon(texture);}
    private void renderSocket(BobbleType type){draw.label(type.label,type==BobbleType.VT?0xff0000:0xffffff);}
}
