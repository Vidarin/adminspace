package com.vidarin.adminspace.rawlang;

public enum TermOSVersion {
    RAW_1_0(0);

    private final int rank;

    TermOSVersion(int rank) {
        this.rank = rank;
    }

    public boolean satisfies(TermOSVersion ver) {
        return this.rank >= ver.rank;
    }
}
