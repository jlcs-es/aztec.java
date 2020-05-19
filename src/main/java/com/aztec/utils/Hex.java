package com.aztec.utils;

import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.util.Random;

public class Hex {
    public static boolean containsHexPrefix(String input) {
        return (input != null && input.startsWith("0x"));
    }

    public static String cleanHexPrefix(String input) {
        return Numeric.containsHexPrefix(input)
                ? input.substring(2)
                : input;
    }

    public static String prependHexPrefix(String input) {
        return !Numeric.containsHexPrefix(input)
                ? "0x" + input
                : input;
    }

    public static String randomHex(int size) {
        var bytes = new byte[size];
        new Random().nextBytes(bytes);
        var hex = "";
        for(var b : bytes) {
            hex += String.format("%02X", b);
        }
        return hex;
    }
}
