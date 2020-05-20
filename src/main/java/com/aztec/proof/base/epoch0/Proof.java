package com.aztec.proof.base.epoch0;

import com.aztec.Note;
import java.math.BigInteger;
import org.ethereum.vm.crypto.zksnark.Params;

/**
 * Class to create, store and pass around AZTEC proofs and related transcripts, outputs and hashes
 */
public class Proof {
    private ProofType type;
    private Note[] inputNotes;
    private int m;
    private Note[] outputNotes;
    private String sender;
    private BigInteger publicValue;
    private String publicOwner;

    public Proof(
        ProofType type,
        Note[] inputNotes,
        Note[] outputNotes,
        String sender,
        BigInteger publicValue,
        String publicOwner
    )
        throws Exception {
        this.type = type;
        this.inputNotes = inputNotes;
        this.m = inputNotes.length;
        this.outputNotes = outputNotes;
        this.sender = sender;
        // https://github.com/AztecProtocol/AZTEC/blob/2248b4b08ffbd8a1d98cfc04a4fbcb9aa23977d9/packages/aztec.js/src/proof/base/epoch0/proof.js#L35
        if (publicValue.compareTo(BigInteger.ZERO) < 0) {
            this.publicValue = Params.R.add(publicValue);
        } else {
            this.publicValue = publicValue;
        }
        this.publicOwner = publicOwner;
        this.validateInputs();
        this.constructChallengeHashes();
    }

    /**
     * Validate that the inputs in the constructor are well-formed
     */
    private void validateInputs() throws Exception {
        // TODO
    }

    private void constructChallengeHashes() {
        // TODO
    }
}
