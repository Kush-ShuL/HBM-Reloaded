package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.fluid.HbmFluids;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryCentrifuge extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:machine_gascent",1,0);
	}

	@Override
	public String getName() {
		return "cannery.centrifuge";
	}

	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(9, 5, 5);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		
		scene0.add(new ActionSetZoom(2, 0));
		
		for(int x = world.sizeX - 1; x >= 0 ; x--) {
			for(int z = 0; z < world.sizeZ; z++) {
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:brick_block")));
			}
			
			if(x == 7) {
				scene0.add(new ActionSetTile(7, 1, 2, new Dummies.JarDummyConnector()));
				scene0.add(new ActionSetBlock(7, 1, 2, JarSymbols.block("hbm:barrel_tcalloy")));
			}
			
			if(x == 6) {
				JarTiles.Pipe duct = new JarTiles.Pipe();
				duct.setType(HbmFluids.UF6);
				scene0.add(new ActionSetTile(6, 1, 2, duct));
				scene0.add(new ActionSetBlock(6, 1, 2, JarSymbols.block("hbm:fluid_duct_neo"), 0));
			}
			
			if(x == 5) {
				scene0.add(new ActionSetTile(5, 1, 2, new Dummies.JarDummyConnector()));
				CompoundTag cent = new CompoundTag();
				cent.putDouble("x", 5);
				cent.putDouble("y", 1);
				cent.putDouble("z", 2);
				cent.putInt("rotation", 2);
				scene0.add(new ActionCreateActor(0, new ActorTileEntity(new JarMachineActors.GasCent(), cent)));
			}
			
			scene0.add(new ActionWait(2));
		}

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -15, -50, new Object[][] {{JarSymbols.text("cannery.centrifuge.0")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(1));
		
		JarScene scene1 = new JarScene(script);

		scene1.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -15, 10, new Object[][] {{JarSymbols.text("cannery.centrifuge.1")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.CENTER)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(1));

		scene1.add(new ActionSetZoom(4, 20));
		
		scene1.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), 0, 40, new Object[][] {{JarSymbols.text("cannery.centrifuge.2")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(1));
		scene1.add(new ActionSetZoom(-2, 20));
		scene1.add(new ActionWait(20));
		
		CompoundTag c2 = new CompoundTag(); c2.putDouble("x", 4); c2.putDouble("y", 1); c2.putDouble("z", 2); c2.putInt("rotation", 2);
		scene1.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.GasCent(), c2)));
		scene1.add(new ActionWait(10));
		
		scene1.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, 0, new Object[][] {{JarSymbols.text("cannery.centrifuge.3")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.CENTER)));
		
		scene1.add(new ActionWait(100));
		scene1.add(new ActionRemoveActor(2));
		scene1.add(new ActionSetZoom(-2, 20));
		
		scene1.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, 0, new Object[][] {{JarSymbols.text("cannery.centrifuge.4")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.CENTER)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(2));

		CompoundTag c3 = new CompoundTag(); c3.putDouble("x", 3); c3.putDouble("y", 1); c3.putDouble("z", 2); c3.putInt("rotation", 2);
		scene1.add(new ActionCreateActor(2, new ActorTileEntity(new JarMachineActors.GasCent(), c3)));
		scene1.add(new ActionWait(10));
		CompoundTag c4 = new CompoundTag(); c4.putDouble("x", 2); c4.putDouble("y", 1); c4.putDouble("z", 2); c4.putInt("rotation", 2);
		scene1.add(new ActionCreateActor(3, new ActorTileEntity(new JarMachineActors.GasCent(), c4)));
		scene1.add(new ActionWait(10));
		
		scene1.add(new ActionCreateActor(4, new ActorFancyPanel(LegacyFonts.get(), 0, 0, new Object[][] {{JarSymbols.text("cannery.centrifuge.5")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.CENTER)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(4));
		
		scene1.add(new ActionCreateActor(4, new ActorFancyPanel(LegacyFonts.get(), 28, -30, new Object[][] {{JarSymbols.item("hbm:upgrade_gc_speed",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene1.add(new ActionCreateActor(5, new ActorFancyPanel(LegacyFonts.get(), 45, 35, new Object[][] {{" = ", JarSymbols.item("hbm:nugget_u238",11,0), JarSymbols.item("hbm:nugget_u235",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		
		script.addScene(scene0).addScene(scene1);
		return script;
	}
	
}
