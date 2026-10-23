package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;

public class ActionRemoveActor implements IJarAction {
	
	int id;
	
	public ActionRemoveActor(int id) {
		this.id = id;
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) {
		scene.script.actors.remove(id);
	}
}
