package com.hbm.client.wiaj.cannery;
import com.hbm.client.wiaj.*;import com.hbm.client.wiaj.actions.*;import com.hbm.client.wiaj.actors.*;import com.hbm.client.wiaj.actors.ActorFancyPanel.Orientation;import com.hbm.client.gui.font.LegacyFonts;import com.hbm.material.*;import com.hbm.item.autogen.ScrapsItem;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.item.ItemStack;
public class CanneryCrucible extends CanneryBase {

	@Override
	public ItemStack getIcon() {
		return JarSymbols.item("hbm:machine_crucible",1,0);
	}

	@Override
	public String getName() {
		return "cannery.crucible";
	}
	
	public CanneryBase[] seeAlso() {
		return new CanneryBase[] {
				new CanneryFoundryChannel()
		};
	}

	@Override
	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(5, 4, 5);
		JarScript script = new JarScript(world);
		
		JarScene scene0 = new JarScene(script);
		scene0.add(new ActionSetZoom(3, 0));
		
		for(int x = 0; x < 5 ; x++) {
			for(int z = 0; z < 5; z++) {
				scene0.add(new ActionSetBlock(x, 0, z, JarSymbols.block("minecraft:brick_block")));
			}
		}
		scene0.add(new ActionWait(5));
		for(int x = 1; x < 4 ; x++) {
			for(int z = 1; z < 4; z++) {
				scene0.add(new ActionSetBlock(x, 1, z, JarSymbols.block("minecraft:brick_block")));
			}
		}
		scene0.add(new ActionWait(5));

		scene0.add(new ActionSetTile(2, 2, 2, new JarTiles.Crucible() {{ meta = 14; }}));
		scene0.add(new ActionCreateActor(1, new ActorTileEntity(new JarMachineActors.Crucible(), new CompoundTag() {{ putInt("x", 2); putInt("y", 2); putInt("z", 2); }})));

		scene0.add(new ActionWait(10));
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -30, new Object[][] {{JarSymbols.text("cannery.crucible.0")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -30, new Object[][] {{JarSymbols.text("cannery.crucible.1")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		
		scene0.add(new ActionWait(60));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(10));

		for(int x = 1; x < 4 ; x++) for(int z = 1; z < 4; z++) scene0.add(new ActionSetBlock(x, 1, z, JarSymbols.block("minecraft:air"))); 
		
		CompoundTag firebox = new CompoundTag(); firebox.putDouble("x", 2); firebox.putDouble("y", 1); firebox.putDouble("z", 2); firebox.putInt("rotation", 5);
		scene0.add(new ActionCreateActor(2, new ActorTileEntity(new JarMachineActors.Firebox(), firebox)));
		
		scene0.add(new ActionUpdateActor(2, "open", true));
		scene0.add(new ActionWait(30));
		scene0.add(new ActionUpdateActor(2, "isOn", true));

		scene0.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), -50, 25, new Object[][] {{JarSymbols.item("minecraft:coal",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		
		scene0.add(new ActionWait(20));
		scene0.add(new ActionRemoveActor(0));
		scene0.add(new ActionWait(10));
		scene0.add(new ActionUpdateActor(2, "open", false));
		scene0.add(new ActionWait(30));
		
		JarScene scene1 = new JarScene(script);
		
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -30, new Object[][] {{JarSymbols.text("cannery.crucible.2")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionRotateBy(45, -60, 20));
		scene1.add(new ActionWait(10));
		
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, 0, new Object[][] {{JarSymbols.text("cannery.crucible.3")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.CENTER)));
		scene1.add(new ActionWait(40));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), -30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.4")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), -30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.5")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), -30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.6")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.LEFT)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionWait(20));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.7")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.8")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene1.add(new ActionWait(60));

		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.9")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene1.add(new ActionWait(60));
		
		scene1.add(new ActionRemoveActor(0));
		scene1.add(new ActionRotateBy(-45, 60, 20));
		scene1.add(new ActionWait(10));
		
		JarScene scene2 = new JarScene(script);
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 30, 0, new Object[][] {{JarSymbols.text("cannery.crucible.10")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.RIGHT)));
		scene2.add(new ActionWait(60));
		
		scene2.add(new ActionRemoveActor(0));
		scene2.add(new ActionWait(10));
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -40, new Object[][] {{JarSymbols.item("minecraft:gold_ingot",11,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene2.add(new ActionWait(20));
		
		scene2.add(new ActionRemoveActor(0));
		scene2.add(new ActionSetTile(2, 2, 2, new JarTiles.Crucible() {{ meta = 14; recipeStack.add(new MaterialStack(HbmMaterials.bySymbol("MAT_GOLD"), HbmMaterials.shape("BLOCK").q(16))); }}));
		scene2.add(new ActionWait(10));
		
		scene2.add(new ActionSetTile(0, 1, 2, new JarTiles.Basin()));
		scene2.add(new ActionCreateActor(3, new ActorTileEntity(new JarMachineActors.Foundry(), new CompoundTag() {{ putInt("x", 0); putInt("y", 1); putInt("z", 2); }})));
		scene2.add(new ActionSetBlock(0, 1, 2, JarSymbols.block("hbm:foundry_basin")));
		
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 42, 25, new Object[][] {{JarSymbols.item("hbm:mold/block",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene2.add(new ActionWait(20));
		scene2.add(new ActionRemoveActor(0));
		scene2.add(new ActionSetTile(0, 1, 2, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold/block",0,0); }}));
		
		scene2.add(new ActionWait(20));
		
		for(int i = 0; i < 60; i++) {
			final int j = i;
			scene2.add(new ActionSetTile(2, 2, 2, new JarTiles.Crucible() {{ meta = 14; recipeStack.add(new MaterialStack(HbmMaterials.bySymbol("MAT_GOLD"), HbmMaterials.shape("BLOCK").q((60 - j), 60) * 16)); }}));
			scene2.add(new ActionSetTile(0, 1, 2, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold/block",0,0); type = HbmMaterials.bySymbol("MAT_GOLD"); amount = HbmMaterials.shape("BLOCK").q(j + 1, 60); }}));
			scene2.add(new ActionWait(1));
		}
		
		scene2.add(new ActionWait(40));
		scene2.add(new ActionSetTile(0, 1, 2, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold/block",0,0); slots[1] = JarSymbols.item("minecraft:gold_block",1,0); }}));
		scene2.add(new ActionWait(20));
		scene2.add(new ActionSetTile(0, 1, 2, new JarTiles.Basin() {{ slots[0] = JarSymbols.item("hbm:mold/block",0,0); }}));
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 42, 25, new Object[][] {{JarSymbols.item("minecraft:gold_block",1,0)}}, 0)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene2.add(new ActionWait(20));
		scene2.add(new ActionRemoveActor(0));
		
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -30, new Object[][] {{JarSymbols.text("cannery.crucible.11")}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene2.add(new ActionWait(60));
		scene2.add(new ActionRemoveActor(0));
		scene2.add(new ActionCreateActor(0, new ActorFancyPanel(LegacyFonts.get(), 0, -30, new Object[][] {{JarSymbols.item("minecraft:iron_shovel",1,0), " -> ", ScrapsItem.create(new MaterialStack(HbmMaterials.bySymbol("MAT_GOLD"), 1))}}, 200)
				.setColors(colorCopper).setOrientation(Orientation.BOTTOM)));
		scene2.add(new ActionSetTile(2, 2, 2, new JarTiles.Crucible() {{ meta = 14; }}));
		scene2.add(new ActionWait(20));
		scene2.add(new ActionRemoveActor(0));
		
		script.addScene(scene0).addScene(scene1).addScene(scene2);
		
		return script;
	}
	
}
