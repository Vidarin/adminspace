package com.vidarin.adminspace.util.blockholder;

@FunctionalInterface
public interface BlockHolderOperation<T> {
    void call(int x, int y, int z, T value);
}
