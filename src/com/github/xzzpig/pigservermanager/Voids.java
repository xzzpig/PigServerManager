package com.github.xzzpig.pigservermanager;

public class Voids {
	public static void saveData() {
		Vars.editor.putString("data", Vars.data.toString());
		Vars.editor.apply();
	}
}
