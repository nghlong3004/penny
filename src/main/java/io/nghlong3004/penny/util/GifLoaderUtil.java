package io.nghlong3004.penny.util;

import java.util.Random;

public class GifLoaderUtil {
    public static String getRandomUrl(String... gifs) {
        Random random = new Random();
        int index = Math.abs(random.nextInt()) % gifs.length;
        return gifs[index];
    }
}
