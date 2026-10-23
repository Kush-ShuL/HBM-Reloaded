// Generated from X5846 by tools/port_animations.py; numerical logic preserved.
// Source SHA-256: ded19935b66f7d6bca2af4b841ec72f0e37a5cbaf48a94ef1a9f3242c550dd7d
package com.hbm.client.animation.collada;

import java.util.HashMap;
import java.util.Map;

public class Animation {
	
	public static final Animation EMPTY = createBlankAnimation();
	
	public int length;
	public int numKeyFrames;
	public Map<String, Transform[]> objectTransforms = new HashMap<String, Transform[]>();
	
	private static Animation createBlankAnimation(){
		Animation anim = new Animation();
		anim.numKeyFrames = 0;
		anim.length = 0;
		anim.objectTransforms = new HashMap<String, Transform[]>();
		return anim;
	}

}