package com.imad.quickclassquiz.utils;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.TextRandomProvider;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomCodeGenerator {

    public static String getRandomCode(int length, String generatorString) {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .usingRandom(max -> {
                            int random;
                            try {
                                SecureRandom a = SecureRandom.getInstance("SHA1PRNG");
                                a.setSeed(generatorString.getBytes("UTF-8"));
                                random = a.nextInt(max);
                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                random = new Random().nextInt(max);
                            }
                            return random;
                        })
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
        return randomStringGenerator.generate(length).toLowerCase();
    }

    public static String getRandomCode(String generatorString) {
        RandomStringGenerator randomStringGenerator =
                new RandomStringGenerator.Builder()
                        .usingRandom(max -> {
                            int random;
                            try {
                                SecureRandom a = SecureRandom.getInstance("SHA1PRNG");
                                a.setSeed(generatorString.getBytes("UTF-8"));
                                random = a.nextInt(max);
                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                random = new Random().nextInt(max);
                            }
                            return random;
                        })
                        .withinRange('0', 'z')
                        .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                        .build();
        return randomStringGenerator.generate(8).toLowerCase();
    }
}
