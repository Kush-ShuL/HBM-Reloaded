package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.material.*;import com.hbm.item.autogen.ScrapsItem;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryStirling extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:machine_stirling",1,0);
	}

	@Override
	public String getName() {
		return "cannery.stirling";
	}
	public CanneryBase[] seeAlso() {
		return new CanneryBase[] {
				new CanneryFirebox()
		};
	}

	@Override
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
		
		CompoundTag stirling = new CompoundTag();
		stirling.putDouble("x", 2);
		stirling.putDouble("z", 2);
		stirling.putInt("rotation", 2);
		scene0.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.Stirling(), stirling)));
		/*
		 * When rewinding, the NBT tag persists. We have to manually set all variable values with UpdateActor.
		 */
		scene0.add(new ActionUpdateActor(1, "speed", 0F));
		scene0.add(new ActionUpdateActor(1, "y", 2D));
		scene0.add(new ActionUpdateActor(1, "type", 0));
		scene0.add(new ActionUpdateActor(1, "hasCog", true));
		scene0.add(new ActionUpdateActor(1, "spin", 0F));
		scene0.add(new ActionUpdateActor(1, "lastSpin", 0F));
		
		scene0.add(new ActionWait(10));
		
		scene0.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, -45, new Object[][] {{JarSymbols.text("cannery.stirling.0")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		
		scene0.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, 40, new Object[][] {{JarSymbols.text("cannery.stirling.1")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(2));
		scene0.add(new ActionWait(5));
		scene0.add(new ActionUpdateActor(0, "open", true));
		scene0.add(new ActionWait(30));
		scene0.add(new ActionUpdateActor(0, "isOn", true));
		scene0.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), -50, 40, new Object[][] {{JarSymbols.item("minecraft:coal",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionRemoveActor(2));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionUpdateActor(0, "open", false));
		scene0.add(new ActionWait(30));
		
		for(int i = 0; i < 60; i++) {
			scene0.add(new ActionUpdateActor(1, "speed", i / 5F));
			scene0.add(new ActionWait(1));
		}
		
		scene0.add(new ActionWait(20));
		
		scene0.add(new ActionSetTile(1, 2, 2, new Dummies.JarDummyConnector()));
		scene0.add(new ActionSetTile(0, 2, 2, new Dummies.JarDummyConnector()));
		scene0.add(new ActionSetTile(0, 1, 2, new Dummies.JarDummyConnector()));
		scene0.add(new ActionSetTile(0, 1, 3, new Dummies.JarDummyConnector()));
		scene0.add(new ActionSetBlock(0, 2, 2, JarSymbols.block("hbm:red_cable")));
		scene0.add(new ActionSetBlock(0, 1, 2, JarSymbols.block("hbm:red_cable")));
		scene0.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 0));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 1));
		scene0.add(new ActionWait(40));
		
		JarScene scene1 = new JarScene(script);
		
		scene1.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, -45, new Object[][] {{JarSymbols.text("cannery.stirling.2")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(2));
		
		CompoundTag burner = new CompoundTag(); burner.putDouble("x", 2); burner.putDouble("y", 1); burner.putDouble("z", 2); burner.putInt("rotation", 5);
		scene1.add(new ActionCreateActor(0, new ActorTileEntity(new JarMachineActors.Burner(), burner)));
		scene1.add(new ActionUpdateActor(1, "y", 3D));
		scene1.add(new ActionSetTile(1, 2, 2, null));
		scene1.add(new ActionSetTile(1, 3, 2, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetTile(0, 3, 2, new Dummies.JarDummyConnector()));
		scene1.add(new ActionSetBlock(0, 3, 2, JarSymbols.block("hbm:red_cable")));
		
		for(int i = 0; i < 100; i++) {
			scene1.add(new ActionUpdateActor(1, "speed", (i + 60) / 5F));
			scene1.add(new ActionWait(1));
		}
		
		scene1.add(new ActionWait(20));
		scene1.add(new ActionUpdateActor(1, "hasCog", false));
		scene1.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 0));
		
		for(int i = 0; i < 160; i += 10) {
			scene1.add(new ActionUpdateActor(1, "speed", (160 - i) / 5F));
			scene1.add(new ActionWait(1));
		}
		scene1.add(new ActionUpdateActor(1, "speed", 0F));
		
		scene1.add(new ActionWait(20));
		
		scene1.add(new ActionCreateActor(2, new ActorFancyPanel(LegacyFonts.get(), 0, -45, new Object[][] {{JarSymbols.text("cannery.stirling.3")}}, 250)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(60));
		scene1.add(new ActionRemoveActor(2));
		scene1.add(new ActionWait(20));
		scene1.add(new ActionUpdateActor(1, "hasCog", true));
		scene1.add(new ActionUpdateActor(1, "type", 1));
		scene1.add(new ActionWait(20));
		
		for(int i = 0; i < 60; i++) {
			scene1.add(new ActionUpdateActor(1, "speed", i / 5F));
			scene1.add(new ActionWait(1));
		}
		
		scene1.add(new ActionSetBlock(0, 1, 3, JarSymbols.block("hbm:machine_detector"), 1));
		scene1.add(new ActionWait(100));
		
		script.addScene(scene0).addScene(scene1);
		return script;
	}
	
}
