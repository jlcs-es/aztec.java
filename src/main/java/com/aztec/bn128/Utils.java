package com.aztec.bn128;

import org.ethereum.vm.crypto.zksnark.BN128Fp;
import org.ethereum.vm.crypto.zksnark.Fp;
import org.ethereum.vm.crypto.zksnark.Params;
import org.web3j.crypto.Pair;

import java.math.BigInteger;

import static com.aztec.utils.Constants.K_MAX;
import static com.aztec.utils.Hex.randomHex;
import static org.ethereum.vm.crypto.zksnark.Params.B_Fp;

public class Utils {

    public static final String hXHex = "00164b60d0fa1eab5d56d9653aed9dc7f7473acbe61df67134c705638441c4b9";
    public static final String hYHex = "2bb1b9b55ffdcf2d7254dfb9be2cb4e908611b4adeb4b838f0442fce79416cf0";

    public static final BigInteger H_X = new BigInteger(hXHex, 16);
    public static final BigInteger H_Y = new BigInteger(hYHex, 16);

    // AZTEC's second generator point 'h'
    public static final BN128Fp H = new BN128Fp(new Fp(H_X), new Fp(H_Y), Fp._1);

    /**
     * Curve generator aztec.getG = (1, 2, 1)
     */
    public static final BN128Fp g = new BN128Fp(Fp._1, Fp._2, Fp._1);

    /**
     * The common reference string
     */
    public static final String[] t2 = new String[] {
        "0x0118c4d5b837bcc2bc89b5b398b5974e9f5944073b32078b7e231fec938883b0",
        "0x260e01b251f6f1c7e7ff4e580791dee8ea51d87a358e038b4efe30fac09383c1",
        "0x22febda3c0c0632a56475b4214e5615e11e6dd3f96e6cea2854a87d4dacc5e55",
        "0x04fc6369f7110fe3d25156c1bb9a72859cf2a04641f99ba4ee413c80da6a5fe4",
    };


    public static final String[] CRS = new String[] {
        "0x" + hXHex, "0x" + hYHex, t2[0], t2[1], t2[2], t2[3]
    };

    public static final BigInteger compressionMask = new BigInteger("8000000000000000000000000000000000000000000000000000000000000000", 16);

    public static class Point {
        private BigInteger x;
        private BigInteger y;
        public Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
        public BigInteger x() {
            return this.x;
        }
        public BigInteger y() {
            return this.y;
        }
    }


    /**
     * Compress aztec.getA bn128 point into 256 bits.
     * @param x x coordinate
     * @param y y coordinate
     * @return 256-bit compressed coordinate
     */
    public static BigInteger compress(BigInteger x, BigInteger y) {
        var compressed = x;
        if (y.testBit(0)) {
            compressed = compressed.or(compressionMask);
        }
        return compressed;
    }

    /**
     * Decompress aztec.getA 256-bit representation of aztec.getA bn128 G1 element.
     *   The first 254 bits define the x-coordinate. The most significant bit defines whether the
     *   y-coordinate is odd
     * @param compressed 256-bit compressed coordinate in BN form
     * @return BN128Fp point
     */
    public static BN128Fp decompress(BigInteger compressed) throws Exception {
        var yBit = compressed.testBit(255);
        var mask255 = BigInteger.TWO.pow(255).subtract(BigInteger.ONE);
        var x = new Fp(compressed.and(mask255));

        var y2 = x.squared().mul(x).add(B_Fp);

        var y = y2.sqrt(); // TODO quitar comprobación si nosotros ya hacemos throw en sqrt()
        if ( y.squared().sub(y2) != Fp.ZERO ) {
            throw new Exception("x^3 + 3 not a square, malformed input");
        }
        var yBI = y.toBigInteger();
        if (yBI.testBit(0) != yBit) { // y.isOdd() != yBit
            y = new Fp(Params.P.subtract(yBI));
        }
        return new BN128Fp(x, y, Fp._1);
    }


    public static BigInteger randomScalar() {
        return (new BigInteger(randomHex(32), 16)).mod(Params.R);
    }

    /**
     * Get a random point on the curve
     * @return a random point
     */
    public static BN128Fp randomPoint() {
        var x= randomScalar();
        return (BN128Fp) g.mul(x);
    }


    /**
     * Brute-force recover an AZTEC note value from a decrypted point pair.
     *   Requires the value 'k' is less than ~ 1 million
     * @param gamma the AZTEC note coordinate \gamma
     * @param gammaK the AZTEC decrypted coordinate \gamma^{k}. Computed from \sigma.h^{-a}
     * @return the value of the note
     */
    public static long recoverMessage(BN128Fp gamma, BN128Fp gammaK) throws Exception {
        return recoverMessage(gamma, gammaK, K_MAX);
    }


    /**
     * Brute-force recover an AZTEC note value from a decrypted point pair.
     *   Requires the value 'k' is less than ~ 1 million
     * @param gamma the AZTEC note coordinate \gamma
     * @param gammaK the AZTEC decrypted coordinate \gamma^{k}. Computed from \sigma.h^{-a}
     * @param K K_MAX
     * @return the value of the note
     */
    public static long recoverMessage(BN128Fp gamma, BN128Fp gammaK, int K) throws Exception {
        if (gammaK.isZero()) {
            return 1;
        }
        var gammaKAffine = gammaK.toAffine();
        var accumulator = new BN128Fp(gamma.x(), gamma.y(), gamma.z());
        int j = 1;
        while (j < K) {
            if (accumulator == gammaKAffine) {
                break;
            }
            accumulator = (BN128Fp) accumulator.add(gamma);
            j += 1;
        }
        if (j == K) {
            throw new Exception("could not find k!");
        }
        return j;
    }


}
