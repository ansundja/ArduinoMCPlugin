package de.sfn_kassel.minecraft.arduino.bukkit.util;

public class Conditions {
	
	public static void main(String[] args) {
		System.out.println("abcdefg".substring(0, 1));
	}
	
	public static Condition parseCondition(String condition) {
		condition = condition.trim();
		
		try {
			String comparator = condition.substring(0, 1);
			char c = condition.charAt(1);
			if (c == '=' || c == '>')
				comparator += c;
			final int compareValue = Integer.parseInt(condition.substring(comparator.length(), condition.length()).trim());
			
			if (comparator.equals("<=")) {
				return v -> v <= compareValue;
//				return new Condition() {// (as non-Lambda:)
//					@Override
//					public boolean matches(int value) {
//						return value <= compareValue;
//					}
//				};
			} else if (comparator.equals(">=")) {
				return v -> v >= compareValue;
			} else if (comparator.equals("!=") || comparator.equals("<>")) {
				return v -> v != compareValue;
			} else if (comparator.equals("=") || comparator.equals("==")) {
				return v -> v == compareValue;
			} else if (comparator.equals("<")) {
				return v -> v < compareValue;
			} else if (condition.startsWith(">")) {
				return v -> v > compareValue;
			}
		} catch (Exception e) {
			//condition not in right syntax
		}
		return null;
	}
}
