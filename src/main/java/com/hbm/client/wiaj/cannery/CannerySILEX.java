package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.fluid.HbmFluids;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CannerySILEX extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:machine_silex",1,0);
	}

	@Override
	public String getName() {
		return "cannery.silex";
	}

	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(17, 5, 5);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		
		scene0.add(new ActionSetZoom(2, 0));
		
		for(int x = world.sizeX - 1; x >= 0 ; x--) {
			for(int z = 0; z < world.sizeZ; z++) {
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:brick_block")));
			}
			
			scene0.add(new ActionWait(1));
		}
		
		scene0.add(new ActionWait(9));
		
		CompoundTag fel = new CompoundTag();
		fel.putDouble("x", 11D);
		fel.putDouble("y", 1D);
		fel.putDouble("z", 2D);
		fel.putInt("rotation", 5);
		fel.putInt("length", 11);
		scene0.add(new ActionCreateActor(0, new ActorTileEntity(new JarMachineActors.Fel(), fel)));

		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 15, -5, new Object[][] {{JarSymbols.text("cannery.silex.0")}}, 100)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));

		scene0.add(new ActionWait(80));
		scene0.add(new ActionRemoveActor(3));
		scene0.add(new ActionWait(10));

		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.item("hbm:laser_crystal_co2",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 1));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.item("hbm:laser_crystal_bismuth",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 2));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.item("hbm:laser_crystal_cmb",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 3));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.item("hbm:laser_crystal_dnt",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 4));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.item("hbm:laser_crystal_digamma",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 5));
		scene0.add(new ActionWait(20));
		
		scene0.add(new ActionRemoveActor(3));
		scene0.add(new ActionUpdateActor(0, "mode", 0));
		scene0.add(new ActionWait(20));
		
		for(int y = 1; y < 4; y++) {
			for(int z = 1; z < 4; z++) {
				scene0.add(new ActionSetBlock(5, y, z, JarSymbols.block("minecraft:stone")));
			}
			scene0.add(new ActionWait(5));
		}
		
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.text("cannery.silex.1")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionUpdateActor(0, "mode", 3));
		scene0.add(new ActionUpdateActor(0, "length", 4));
		
		scene0.add(new ActionWait(40));
		scene0.add(new ActionUpdateActor(0, "length", 11));
		scene0.add(new ActionSetBlock(5, 2, 2, JarSymbols.block("minecraft:fire")));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionSetBlock(5, 2, 2, JarSymbols.block("minecraft:air")));
		scene0.add(new ActionWait(40));
		
		for(int y = 1; y < 4; y++) {
			for(int z = 1; z < 4; z++) {
				scene0.add(new ActionSetBlock(5, y, z, JarSymbols.block("hbm:brick_concrete")));
			}
			
			if(y == 2) {
				scene0.add(new ActionUpdateActor(0, "length", 4));
			}
			
			scene0.add(new ActionWait(5));
		}
		
		scene0.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.text("cannery.silex.2")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionWait(40));
		scene0.add(new ActionRemoveActor(3));
		
		for(int y = 3; y > 0; y--) {
			for(int z = 1; z < 4; z++) {
				scene0.add(new ActionSetBlock(5, y, z, JarSymbols.block("minecraft:air")));
			}
			
			if(y == 2) {
				scene0.add(new ActionUpdateActor(0, "length", 11));
			}
			
			scene0.add(new ActionWait(5));
		}
		
		scene0.add(new ActionUpdateActor(0, "mode", 0));
		
		JarScene scene1 = new JarScene(script);
		
		CompoundTag silex0 = new CompoundTag();
		silex0.putDouble("x", 5D);
		silex0.putDouble("y", 1D);
		silex0.putDouble("z", 2D);
		silex0.putInt("rotation", 2);
		scene1.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.Silex(), silex0)));
		
		scene1.add(new ActionWait(20));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 42, -18, new Object[][] {{JarSymbols.text("cannery.silex.3")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene1.add(new ActionWait(80));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 60, 32, new Object[][] {{JarSymbols.text("cannery.silex.4")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		
		scene1.add(new ActionWait(60));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 12, 32, new Object[][] {{JarSymbols.text("cannery.silex.5")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(3));
		scene1.add(new ActionWait(10));

		JarTiles.Pipe duct = new JarTiles.Pipe();
		duct.setType(HbmFluids.PEROXIDE);
		scene1.add(new ActionSetTile(5, 2, 0, duct));
		scene1.add(new ActionSetTile(5, 1, 0, duct));
		scene1.add(new ActionSetTile(6, 1, 0, duct));
		scene1.add(new ActionSetTile(7, 1, 0, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetTile(5, 2, 1, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetBlock(5, 2, 0, JarSymbols.block("hbm:fluid_duct_neo")));
		scene1.add(new ActionSetBlock(5, 1, 0, JarSymbols.block("hbm:fluid_duct_neo")));
		scene1.add(new ActionSetBlock(6, 1, 0, JarSymbols.block("hbm:fluid_duct_neo")));
		scene1.add(new ActionSetBlock(7, 1, 0, JarSymbols.block("hbm:barrel_tcalloy")));
		
		scene1.add(new ActionWait(20));
		scene1.add(new ActionUpdateActor(0, "mode", 3));
		scene1.add(new ActionWait(10));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 42, -18, new Object[][] {{JarSymbols.text("cannery.silex.6")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene1.add(new ActionWait(80));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), 42, -18, new Object[][] {{JarSymbols.text("cannery.silex.7")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene1.add(new ActionWait(60));
		
		scene1.add(new ActionCreateActor(3, new ActorFancyPanel(LegacyFonts.get(), -7, -15, new Object[][] {{JarSymbols.text("cannery.silex.8")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(3));
		scene1.add(new ActionWait(10));
		
		CompoundTag silex1 = new CompoundTag();
		silex1.putDouble("x", 1D);
		silex1.putDouble("y", 1D);
		silex1.putDouble("z", 2D);
		silex1.putInt("rotation", 2);
		scene1.add(new ActionCreateActor(2, new ActorTileEntity(new JarMachineActors.Silex(), silex1)));
		scene1.add(new ActionWait(10));
		
		for(int i = 1; i < 5; i++) {
			scene1.add(new ActionSetTile(i, 1, 0, duct));
			scene1.add(new ActionSetBlock(i, 1, 0, JarSymbols.block("hbm:fluid_duct_neo")));
		}
		scene1.add(new ActionSetTile(1, 2, 0, duct));
		scene1.add(new ActionSetBlock(1, 2, 0, JarSymbols.block("hbm:fluid_duct_neo")));
		scene1.add(new ActionSetTile(1, 2, 1, new Dummies.JarDummyConnector()));
		
		scene1.add(new ActionWait(20));
		
		script.addScene(scene0).addScene(scene1);
		return script;
	}
	
}
