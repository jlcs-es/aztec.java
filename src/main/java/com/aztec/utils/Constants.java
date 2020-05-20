package com.aztec.utils;

import java.math.BigInteger;

public class Constants {

    /**
     * Predefined proofs used for testing. Note that proofs are uint24 objects,
     * so we need to compress three uint8 (epoch, category, id) by shifting the
     * epoch and the category by 16 and 8 bits, respectively. For example:
     *
     * 65793 = 1 * 256**(2) + 1 * 256**(1) + 1 * 256**(0)
     */
    public static class Proofs {
        public static final String BOGUS_PROOF = "65538";
        public static final String BURN_PROOF = "66305";
        public static final String DIVIDEND_PROOF = "66561";
        public static final String JOIN_SPLIT_PROOF = "65793";
        public static final String MINT_PROOF = "66049";
        public static final String PRIVATE_RANGE_PROOF = "66562";
        public static final String SWAP_PROOF = "65794";
        public static final String PUBLIC_RANGE_PROOF = "66563";
    }

    /**
     * Generic scaling factor that maps between AZTEC note values and ERC20 token balances. When used for DAI,
     * 1 note value = 0.1 DAI
     */
    public static final BigInteger ERC20_SCALING_FACTOR = new BigInteger("100000000000000000", 10);
    /** Maximum value that can be held in an AZTEC Note
     *  @constant K_MAX
     *  @default 10e6
     */
    public static final int K_MAX = 10000000;
    /** Maximum value that can be held in an AZTEC note during tests
     *  @constant K_MAX_TEST
     *  @default 0
     */
    public static final int K_MAX_TEST = 16000;
    /** Minimum value that can be held in an AZTEC note
     *  @constant K_MIN
     *  @default 0
     */
    public static final int K_MIN = 0;
    /** Minimum value that can be held in an AZTEC note during tests
     *  @constant K_MIN_TEST
     *  @default 0
     */
    public static final int K_MIN_TEST = 0;
    /** Number of signatures per file in trusted setup database
     *  @constant SIGNATURES_PER_FILE
     *  @default 1000
     */
    public static final int SIGNATURES_PER_FILE = 1000;
    /**
     * Hash of a dummy AZTEC note with k = 0 and a = 1
     * @constant ZERO_VALUE_NOTE_HASH
     * @default 0x26d21f105b054b61e8d9680855c3af0633bd7c140b87de95f0ac218046fc71db
     */
    public static final String ZERO_VALUE_NOTE_HASH =
        "0x26d21f105b054b61e8d9680855c3af0633bd7c140b87de95f0ac218046fc71db";
}
