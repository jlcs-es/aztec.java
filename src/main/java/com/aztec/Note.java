package com.aztec;

import com.aztec.bn128.Utils.Point;

import java.math.BigInteger;

public class Note {
    private String publicKey;
    private String viewingKey;
    private String[] access;
    private String owner = "0x";
    private Point setupPoint;

    // Viewing key of note
    private BigInteger a;
    // Value of note
    private BigInteger k;
    // AZTEC commitment point \gamma, a bn128 group element, encrypts the note value
    private Point gamma;
    // AZTEC commitment point \sigma, a bn128 group element, encrypts the note value
    private Point sigma;
    // Note's metadata
    private String metaData;
    // keccak256 hash of note coordinates, aligned in 32-byte chunks.
    // Alignment is [gamma.x, gamma.y, sigma.x, sigma.y]
    private String noteHash;

    /**
     * @param publicKey hex-formatted public key
     * @param viewingKey hex-formatted viewing key
     * @param access mapping between an Ethereum address and the linked publickey
     * @param owner Ethereum address of note owner
     * @param setupPoint trusted setup point
     * @throws Exception
     */
    public Note(
            String publicKey,
            String viewingKey,
            String[] access,
            String owner,
            Point setupPoint
    ) throws Exception {
        if(publicKey != null && viewingKey != null)
            throw new Exception("expected one of publicKey or viewingKey, not both");
        if(publicKey != null) {
            if (publicKey.length() != 200) {
                throw new Exception("invalid public key length, expected 200, got " + publicKey.length());
            }
            // https://github.com/AztecProtocol/specification#type-1-utxo-notes
            // Offset	Length	Name	Type	Description
            // 0x00     0x20	gamma	bytes32	(compressed) bn128 group element
            // 0x20     0x20	sigma	bytes32	(compressed) bn128 group element
            // 0x40     0x21	ephemeral key	bytes33	ephemeral public key used to recover viewing key
            this.gamma = bn128.curve.decodePoint(publicKey.substring(2, 68));
            this.sigma = bn128.curve.decodePoint(publicKey.substring(68, 134));

            // https://github.com/AztecProtocol/specification#metadata
            /**
             * Note's metadata - general purpose property in which useful information can be stored.
             * By default it contains the note's ephemeral key - a secp256k1 group element which
             * the note owner can use to compute the note's viewing key.
             *
             * Arbitrary additional information can be later supplied, by calling setMetaData()
             */
            this.metaData = secp256k1.compress(secp256k1.ec.keyFromPublic(publicKey.slice(134, 200), 'hex').getPublic());
        }
        if (viewingKey != null) {
            if (viewingKey.length !== 140) {
                throw new Exception("invalid viewing key length, expected 140, got ${viewingKey.length}");
            }

            this.a = BigInteger(viewingKey.substring(2, 66), 16).mod(curveOrder);
            this.k = BigInteger(viewingKey.substring(66, 74), 16).mod(curveOrder);
            var mu = BN128Fp(Fp(setupPoint.x), Fp(setupPoint.y), Fp_1)
            var gmm = mu.mul(this.a).toAffine()
            this.gamma = Point(gmm.x.v, gmm.y.v)
            var sgm = gmm.mul(this.k!!).add(h.mul(this.a!!))
            this.sigma = Point(sgm.x.v, sgm.y.v)
            this.metaData = secp256k1.compress(secp256k1.ec.keyFromPublic(viewingKey.slice(74, 140), 'hex').getPublic());
        }

        this.noteHash = getNoteHash(this.gamma, this.sigma)

        // Grant view access to the addresses specified in access
        if (access.isNotEmpty()) {
            this.grantViewAccess(access);
        }
    }
}
