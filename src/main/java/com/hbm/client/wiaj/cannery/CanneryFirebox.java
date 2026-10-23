package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.material.*;import com.hbm.item.autogen.ScrapsItem;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryFirebox extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:heater_firebox",1,0);
	}

	@Override
	public String getName() {
		return "cannery.firebox";
	}

	public JarScript createScript() {

		WorldInAJar world = new WorldInAJar(5, 5, 5);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		
		scene0.add(new ActionSetZoom(3, 0));
		
		for(int x = world.sizeX - 1; x >= 0 ; x--) {
			for(int z = 0; z < world.sizeZ; z++) {
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:brick_block")));
			}
			scene0.add(new ActionWait(2));
		}
		
		scene0.add(new ActionWait(8));
		
		CompoundTag firebox = new CompoundTag(); firebox.putDouble("x", 2); firebox.putDouble("y", 1); firebox.putDouble("z", 2); firebox.putInt("rotation", 5);
		scene0.add(new ActionCreateActor(0, new ActorTileEntity(new JarMachineActors.Firebox(), firebox)));
		
		scene0.add(new ActionWait(10));

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), 0, -10, new Object[][] {{JarSymbols.text("cannery.firebox.0")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), 0, -10, new Object[][] {{JarSymbols.text("cannery.firebox.1")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(1));
		scene0.add(new ActionWait(5));
		scene0.add(new ActionUpdateActor(0, "open", true));
		scene0.add(new ActionWait(30));
		
		scene0.add(new ActionUpdateActor(0, "isOn", true));

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("minecraft:coal",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("hbm:coke_coal",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("hbm:solid_fuel",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("hbm:rocket_fuel",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("hbm:solid_fuel_bf",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionRemoveActor(1));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionUpdateActor(0, "open", false));
		scene0.add(new ActionWait(30));

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), 0, -10, new Object[][] {{JarSymbols.text("cannery.firebox.2")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(80));

		scene0.add(new ActionCreateActor(1, new ActorFancyPanel(LegacyFonts.get(), 0, -10, new Object[][] {{JarSymbols.text("cannery.firebox.3")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(1));
		scene0.add(new ActionWait(10));
		
		JarScene scene1 = new JarScene(script);
		
		CompoundTag stirling = new CompoundTag();
		stirling.putDouble("x", 2);
		stirling.putDouble("y", 2);
		stirling.putDouble("z", 2);
		stirling.putInt("rotation", 2);
		stirling.putBoolean("hasCog", true);
		scene1.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.Stirling(), stirling)));
		scene1.add(new ActionUpdateActor(1, "speed", 0F));

		scene1.add(new ActionWait(10));
		scene1.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, -45, new Object[][] {{JarSymbols.text("cannery.firebox.4")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(2));
		scene1.add(new ActionWait(10));
		
		for(int i = 0; i < 60; i++) {
			scene1.add(new ActionUpdateActor(1, "speed", i / 5F));
			scene1.add(new ActionWait(1));
		}
		
		scene1.add(new ActionSetTile(1, 2, 2, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetTile(0, 2, 2, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetTile(0, 1, 2, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetTile(0, 1, 3, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetBlock(0, 2, 2, JarSymbols.block("hbm:red_cable")));
		scene1.add(new ActionSetBlock(0, 1, 2, JarSymbols.block("hbm:red_cable")));
		scene1.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 0));
		scene1.add(new ActionWait(10));
		scene1.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 1));
		scene1.add(new ActionWait(100));
		
		script.addScene(scene0).addScene(scene1);
		return script;
	}
	
}
