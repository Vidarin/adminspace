package com.vidarin.adminspace.util;

@FunctionalInterface
public interface BlockHolderOperation<T> {
    void call(int x, int y, int z, T value);
}
