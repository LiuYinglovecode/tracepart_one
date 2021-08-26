package Utils;

import java.util.Random;

public class RandomNum {
    public static String getRandomNumWithSize(int size) {
        String res = null;

        String max = "";
        for (int i = 0; i < size; i++) {
            max += "9";
        }

        int maxNum = Integer.parseInt(max);
        long seed = System.currentTimeMillis();

        if (maxNum > 0) {
            Random random = new Random(seed);
            int tmp = random.nextInt(maxNum);

            res = String.format("%0" + size + "d", tmp);
        }
        return res;
    }
}
