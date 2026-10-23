package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.fluid.HbmFluids;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryWillow extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:plant_item",1,2);
	}

	@Override
	public String getName() {
		return "cannery.willow";
	}

	@Override
	public JarScript createScript() {

		WorldInAJar world = new WorldInAJar(5, 5, 5);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		scene0.add(new ActionSetZoom(3, 0));
		
		for(int x = world.sizeX - 1; x >= 0 ; x--) {
			for(int z = 0; z < world.sizeZ; z++) {
				scene0.add(new ActionSetBlock(x, 1, z, JarSymbols.block("minecraft:grass")));
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:dirt")));
			}
			scene0.add(new ActionWait(2));
		}
		
		scene0.add(new ActionWait(8));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_flower"), 4));
		
		scene0.add(new ActionWait(10));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.0")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(80));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.1")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(80));
		scene0.add(new ActionRemoveActor(0));

		scene0.add(new ActionWait(10));
		scene0.add(new ActionSetBlock(2, 1, 1, JarSymbols.block("minecraft:air")));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionSetBlock(2, 1, 1, JarSymbols.block("minecraft:water")));
		
		scene0.add(new ActionWait(20));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.2")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(80));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_flower"), 5));
		
		scene0.add(new ActionWait(20));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.3")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(80));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_tall"), 1));
		scene0.add(new ActionSetBlock(2, 3, 2, JarSymbols.block("hbm:plant_tall"), 1 + 8));
		
		scene0.add(new ActionWait(20));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_tall"), 2));
		scene0.add(new ActionSetBlock(2, 3, 2, JarSymbols.block("hbm:plant_tall"), 2 + 8));
		scene0.add(new ActionWait(20));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -35, new Object[][] {{JarSymbols.text("cannery.willow.4")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionWait(80));
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -35, new Object[][] {{JarSymbols.text("cannery.willow.5")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(100));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(20));
		scene0.add(new ActionSetBlock(2, 1, 2, JarSymbols.block("hbm:dirt_oily")));
		
		scene0.add(new ActionWait(20));
		scene0.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_tall"), 3));
		scene0.add(new ActionSetBlock(2, 3, 2, JarSymbols.block("hbm:plant_tall"), 3 + 8));
		scene0.add(new ActionSetBlock(2, 1, 2, JarSymbols.block("minecraft:dirt")));
		scene0.add(new ActionWait(20));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -35, new Object[][] {{JarSymbols.text("cannery.willow.6")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene0.add(new ActionWait(100));
		scene0.add(new ActionRemoveActor(0));
		
		JarScene scene1 = new JarScene(script);
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -35, new Object[][] {{JarSymbols.text("cannery.willow.7")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(100));
		scene1.add(new ActionRemoveActor(0));
		
		scene1.add(new ActionWait(20));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 10, -25, new Object[][] {{"=", JarSymbols.item("hbm:plant_flower",1,4), "x1 + ", JarSymbols.item("hbm:plant_item",1,2), "x3-6"}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionSetBlock(2, 3, 2, JarSymbols.block("minecraft:air")));
		scene1.add(new ActionSetBlock(2, 2, 2, JarSymbols.block("hbm:plant_flower"), 4));
		scene1.add(new ActionWait(60));
		
		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionWait(20));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.8")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(100));
		
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -15, new Object[][] {{JarSymbols.text("cannery.willow.9")}}, 150)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(100));
		scene1.add(new ActionRemoveActor(0));
		
		script.addScene(scene0).addScene(scene1);
		return script;
	}
}
