package com.hbm.client.wiaj.actions;

import com.hbm.client.wiaj.JarScene;
import com.hbm.client.wiaj.WorldInAJar;

public class ActionSetZoom implements IJarAction {
	
	int time;
	double zoom;
	
	public ActionSetZoom(double zoom, int time) {
		this.zoom = zoom / (time + 1);
		this.time = time;
	}

	@Override
	public int getDuration() {
		return this.time;
	}

	@Override
	public void act(WorldInAJar world, JarScene scene) {
		
		if(this.getDuration() == 0) {
			scene.script.lastZoom = scene.script.zoom = this.zoom;
		} else {
			scene.script.zoom += this.zoom;
		}
	}
}
