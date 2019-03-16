package io.github.paul1365972.simulation.util;

import java.util.Random;

public class NameGenerator {
	
	//Make first 5 about 11 times more common
	private static final Syl[] vowels = new Syl[] {
			new Syl("a", 7), new Syl("e", 7), new Syl("i", 7), new Syl("o", 7), new Syl("u", 7),
			
			new Syl("ae", 7), new Syl("ai", 7), new Syl("ao", 7), new Syl("au", 7), new Syl("aa", 7),
			new Syl("ea", 7), new Syl("eo", 7), new Syl("eu", 7), new Syl("ee", 7),
			new Syl("ia", 7), new Syl("io", 7), new Syl("iu", 7), new Syl("ii", 7),
			new Syl("oa", 7), new Syl("oe", 7), new Syl("oi", 7), new Syl("ou", 7), new Syl("oo", 7),
			new Syl("eau", 7), new Syl("'", 4), new Syl("y", 7)
	};
	
	//Make first 22 chars 3 times more common
	private static final Syl[] consonants = new Syl[] {
			new Syl("b", 7), new Syl("c", 7), new Syl("d", 7), new Syl("f", 7), new Syl("g", 7), new Syl("h", 7),
			new Syl("j", 7), new Syl("k", 7), new Syl("l", 7), new Syl("m", 7), new Syl("n", 7), new Syl("p", 7),
			new Syl("r", 7), new Syl("s", 7), new Syl("t", 7), new Syl("v", 7), new Syl("w", 7),
			new Syl("br", 6), new Syl("dr", 6), new Syl("fr", 6), new Syl("gr", 6), new Syl("kr", 6),
			
			new Syl("qu", 6), new Syl("x", 7), new Syl("y", 7), new Syl("z", 7),
			new Syl("sc", 7),
			new Syl("ch", 7), new Syl("gh", 7), new Syl("ph", 7), new Syl("sh", 7), new Syl("th", 7), new Syl("wh", 6),
			new Syl("ck", 5), new Syl("nk", 5), new Syl("rk", 5), new Syl("sk", 7),
			new Syl("cl", 6), new Syl("fl", 6), new Syl("gl", 6), new Syl("kl", 6), new Syl("ll", 6), new Syl("pl", 6), new Syl("sl", 6),
			new Syl("cr", 6),
			new Syl("pr", 6), new Syl("sr", 6), new Syl("tr", 6),
			new Syl("ss", 5),
			new Syl("st", 7), new Syl("str", 6)
	};
	
	public static String randomName(Random rng, int minsyl, int maxsyl) {
		StringBuilder sb = new StringBuilder();
		int syllables = rng.nextInt(maxsyl - minsyl) + minsyl;
		boolean isVowel = rng.nextBoolean();
		for (int i = 0; i < syllables; i++) {
			Syl syl;
			do {
				if (isVowel) {
					int index;
					do {
						index = rng.nextInt(vowels.length);
					} while (index >= 5 && rng.nextFloat() > 1 / 10f);
					syl = vowels[index];
				} else {
					int index;
					do {
						index = rng.nextInt(consonants.length);
					} while (index >= 22 && rng.nextFloat() > 1 / 2f);
					syl = consonants[index];
				}
			} while ((i != 0 || (syl.mask & 2) == 0) && (i != syllables - 1 || (syl.mask & 1) == 0) && (i <= 0 || i >= syllables - 1 || (syl.mask & 4) == 0));
			sb.append(syl.string);
			isVowel = !isVowel;
		}
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
	
	private static final class Syl {
		public final String string;
		public final byte mask;
		
		public Syl(String string, int mask) {
			this.string = string;
			this.mask = (byte) mask;
		}
		
		@Override
		public String toString() {
			return "Syl[" + "string=\"" + string + "\", mask=" + mask + "]";
		}
	}
}
