package de.sfn_kassel.minecraft.arduino.bukkit.util;

public abstract class Condition {

	//don't let others make instance
	private Condition() {
	}
	
	public abstract boolean matches(int value);
	
	public static Condition parseCondition(String condition) {
		//TODO parse
		return null;
	}
}
