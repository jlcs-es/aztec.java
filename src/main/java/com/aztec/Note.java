package com.aztec;

import org.ethereum.vm.crypto.zksnark.BN128Fp;
import org.ethereum.vm.crypto.zksnark.Fp;

import java.math.BigInteger;

import static com.aztec.bn128.Utils.H;
import static org.ethereum.vm.crypto.zksnark.Params.R;

public class Note {
    private String publicKey;
    private String viewingKey;
    private String[] access;

    // Ethereum address of note's owner
    private String owner = "0x";

    // Viewing key of note
    private BigInteger a;
    // Value of note
    private BigInteger k;
    // AZTEC commitment point \gamma, a bn128 group element, encrypts the note value
    private BN128Fp gamma;
    // AZTEC commitment point \sigma, a bn128 group element, encrypts the note value
    private BN128Fp sigma;
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
     * @param setupPoint trusted setup point BigInteger[x, y]
     * @throws Exception
     */
    public Note(
            String publicKey,
            String viewingKey,
            String[] access,
            String owner,
            BigInteger[] setupPoint
    ) throws Exception {
        if(publicKey != null && viewingKey != null)
            throw new Exception("expected one of publicKey or viewingKey, not both");

        this.owner = owner;

        if(publicKey != null) {
            if (publicKey.length() != 200) {
                throw new Exception("invalid public key length, expected 200, got " + publicKey.length());
            }

            this.a = null;
            this.k = null;


            // https://github.com/AztecProtocol/specification#type-1-utxo-notes
            // Offset	Length	Name	Type	Description
            // 0x00     0x20	gamma	bytes32	(compressed) bn128 group element
            // 0x20     0x20	sigma	bytes32	(compressed) bn128 group element
            // 0x40     0x21	ephemeral key	bytes33	ephemeral public key used to recover viewing key
            this.gamma = com.aztec.bn128.Utils.decompressHex(publicKey.substring(2, 68));
            this.sigma = com.aztec.bn128.Utils.decompressHex(publicKey.substring(68, 134));

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
            if (viewingKey.length() != 140) {
                throw new Exception("invalid viewing key length, expected 140, got ${viewingKey.length}");
            }

            this.a = new BigInteger(viewingKey.substring(2, 66), 16).mod(R);
            this.k = new BigInteger(viewingKey.substring(66, 74), 16).mod(R);
            BN128Fp mu = new BN128Fp(new Fp(setupPoint[0]), new Fp(setupPoint[1]), Fp._1);
            this.gamma = (BN128Fp) mu.mul(this.a).toAffine();
            this.sigma = (BN128Fp) this.gamma.mul(this.k).add(H.mul(this.a));
            this.metaData = secp256k1.compress(secp256k1.ec.keyFromPublic(viewingKey.slice(74, 140), 'hex').getPublic());
        }

        this.noteHash = getNoteHash(this.gamma, this.sigma);

        // Grant view access to the addresses specified in access
        if (access.isNotEmpty()) {
            this.grantViewAccess(access);
        }
    }
}
