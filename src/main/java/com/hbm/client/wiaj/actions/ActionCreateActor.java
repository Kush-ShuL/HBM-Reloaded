package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;
import com.hbm.client.wiaj.actors.ISpecialActor;

public class ActionCreateActor implements IJarAction {
	
	int id;
	ISpecialActor actor;
	
	public ActionCreateActor(int id, ISpecialActor actor) {
		this.id = id;
		this.actor = actor;
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) {
		scene.script.actors.put(id, actor);
	}
}
