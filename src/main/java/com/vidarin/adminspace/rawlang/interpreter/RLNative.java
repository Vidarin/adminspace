package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.TermOSVersion;

public interface RLNative extends RLCallable {
    TermOSVersion minOSVersion();
}
