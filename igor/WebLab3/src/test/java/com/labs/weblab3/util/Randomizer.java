package com.labs.weblab3.util;

import java.util.Random;

public class Randomizer {

    private static final Random random = new Random();

    /*
     *
     * Generates random Double value
     *
     *  */
    public static double generateRandomDouble() {
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return Double.longBitsToDouble((((long) bytes[0] & 0xff) << 56) |
                (((long) bytes[1] & 0xff) << 48) |
                (((long) bytes[2] & 0xff) << 40) |
                (((long) bytes[3] & 0xff) << 32) |
                (((long) bytes[4] & 0xff) << 24) |
                (((long) bytes[5] & 0xff) << 16) |
                (((long) bytes[6] & 0xff) << 8) |
                (((long) bytes[7] & 0xff)));
    }

    /*
     *
     * Generates random Double value: [from; to)
     *
     *  */
    public static double generateRandom(double from, double to) {
        if (to < from) {
            double tmp = from;
            from = to;
            to = tmp;
        }
        return from + (to - from) * random.nextDouble();
    }

}
