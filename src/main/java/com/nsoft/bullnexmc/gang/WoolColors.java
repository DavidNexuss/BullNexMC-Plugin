package com.nsoft.bullnexmc.gang;

public class WoolColors {

	static final byte WHITE = 0;
	static final byte ORANGE = 1;
	static final byte MAGENTA = 2;
	static final byte LIGHT_BLUE = 3;
	static final byte YELLOW = 4;
	static final byte LIME = 5;
	static final byte PINK = 6;
	static final byte GRAY = 7;
	static final byte LIGHT_GRAY = 8;
	static final byte CYAN = 9;
	static final byte PURPLE = 10;
	static final byte BLUE = 11;
	static final byte BROWN = 12;
	static final byte GREEN = 13;
	static final byte RED = 14;
	static final byte BLACK = 15;
	
	public static byte getColor(String name) {
		
		try {
			return WoolColors.class.getField(name).getByte(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
