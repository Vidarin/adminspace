package com.vidarin.adminspace.util.blockholder;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.util.CubePos;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BlockHolderSequence<T> extends BiFunction<CubePos, T, BlockHolderSequence.Result<T>> {
    @Override
    Result<T> apply(CubePos pos, T value);

    static <T> Result<T> result(CubePos pos, T value) {
        return new Result<>(pos, value);
    }

    @Desugar
    record Result<T>(CubePos pos, T value) {}
}
