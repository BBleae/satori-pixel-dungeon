package studio.baka.satoripixeldungeon.utils;

import com.watabou.utils.Random;

//This class defines the parameters for seeds in ShatteredPD and contains a few convenience methods
public class DungeonSeed {

    private static final long TOTAL_SEEDS = 5429503678976L; //26^9 possible seeds

    public static long randomSeed() {
        return Random.Long(TOTAL_SEEDS);
    }

    //Seed codes take the form @@@-@@@-@@@ where @ is any letter from A to Z (only uppercase)
    //This is effectively a base-26 number system, therefore 26^9 unique seeds are possible.

    //Seed codes exist to make sharing and inputting seeds easier
    //ZZZ-ZZZ-ZZZ is much easier to enter and share than 5,429,503,678,975


    //Takes a seed code (@@@@@@@@@) and converts it to the equivalent long value
    public static long convertFromCode(String code) {
        if (code.length() != 9)
            throw new IllegalArgumentException("codes must be 9 A-Z characters.");

        long result = 0;
        for (int i = 8; i >= 0; i--) {
            char c = code.charAt(i);
            if (c > 'Z' || c < 'A')
                throw new IllegalArgumentException("codes must be 9 A-Z characters.");

            result += (c - 65) * Math.pow(26, (8 - i));
        }
        return result;
    }

    //Takes a long value and converts it to the equivalent seed code
    public static String convertToCode(long seed) {
        if (seed < 0 || seed >= TOTAL_SEEDS)
            throw new IllegalArgumentException("seeds must be within the range [0, TOTAL_SEEDS)");

        //this almost gives us the right answer, but its 0-p instead of A-Z
        String interrim = Long.toString(seed, 26);
        StringBuilder result = new StringBuilder();

        //so we convert
        for (int i = 0; i < 9; i++) {

            if (i < interrim.length()) {
                char c = interrim.charAt(i);
                if (c <= '9') c += 17; //convert 0-9 to A-J
                else c -= 22; //convert a-p to K-Z

                result.append(c);

            } else {
                result.insert(0, 'A'); //pad with A (zeroes) until we reach length of 9

            }
        }

        return result.toString();
    }

    //Using this we can let users input 'fun' plaintext seeds and convert them to a long equivalent.
    // This is basically the same as string.hashcode except with long, and accounting for overflow
    // to ensure the produced seed is always in the range [0, TOTAL_SEEDS)
    public static long convertFromText(String inputText) {
        long total = 0;
        for (char c : inputText.toCharArray()) {
            total = 31 * total + c;
        }
        if (total < 0) total += Long.MAX_VALUE;
        total %= TOTAL_SEEDS;
        return total;
    }

}
