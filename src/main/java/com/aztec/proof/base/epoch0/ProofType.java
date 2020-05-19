package com.aztec.proof.base.epoch0;

public enum ProofType {
    BURN("BURN"),
    DIVIDEND("DIVIDEND"),
    JOIN_SPLIT("JOIN_SPLIT"),
    MINT("MINT"),
    PRIVATE_RANGE("PRIVATE_RANGE"),
    PUBLIC_RANGE("PUBLIC_RANGE"),
    SWAP("SWAP");

    private String name;

    ProofType(String name) {
        this.name = name;
    }
}
