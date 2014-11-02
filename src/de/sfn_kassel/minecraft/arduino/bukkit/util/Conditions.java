package de.sfn_kassel.minecraft.arduino.bukkit.util;

public class Conditions {
	
	//deny Access
	private Conditions() {
	}
	
	public static Condition parseCondition(String condition) {
		condition = condition.trim();
		
		try {
			String comparator = ("!=<>".indexOf(condition.charAt(0)) == -1) ? "" : condition.substring(0, 1);// ">=" (default) or first char
			if (!comparator.isEmpty() && condition.length() > 1) {
				char c = condition.charAt(1);
				if (c == '=' || c == '>')// is two-char-comparator? --> add 2nd char
					comparator += c;
			}
			final int compareValue = Integer.parseInt(condition.substring(comparator.length(), condition.length()).trim());
			
			// as Lambda
			/*if (comparator.equals("<="))
				return v -> v <= compareValue;
			else if (comparator.equals(">=") || comparator.isEmpty())
				return v -> v >= compareValue;
			else if (comparator.equals("!=") || comparator.equals("<>"))
				return v -> v != compareValue;
			else if (comparator.equals("=") || comparator.equals("=="))
				return v -> v == compareValue;
			else if (comparator.equals("<"))
				return v -> v < compareValue;
			else if (comparator.equals(">"))
				return v -> v > compareValue;*/
			
			//not as Lambda
			if (comparator.equals("<=")) {
				return new Condition() {// (as non-Lambda:)
					@Override
					public boolean matches(int value) {
						return value <= compareValue;
					}
				};
			} else if (comparator.equals(">=")) {
				return new Condition() {
					@Override
					public boolean matches(int value) {
						return value >= compareValue;
					}
				};
			} else if (comparator.equals("!=") || comparator.equals("<>")) {
				return new Condition() {
					@Override
					public boolean matches(int value) {
						return value != compareValue;
					}
				};
			} else if (comparator.equals("=") || comparator.equals("==")) {
				return new Condition() {
					@Override
					public boolean matches(int value) {
						return value == compareValue;
					}
				};
			} else if (comparator.equals("<")) {
				return new Condition() {
					@Override
					public boolean matches(int value) {
						return value < compareValue;
					}
				};
			} else if (comparator.equals(">")) {
				return new Condition() {
					@Override
					public boolean matches(int value) {
						return value > compareValue;
					}
				};
			}
		} catch (Exception e) {
			//condition not in right syntax
		}
		return null;
	}
}
