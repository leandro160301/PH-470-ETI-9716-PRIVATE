package com.jws.jwsapi.core.user;

import androidx.annotation.NonNull;

public class UserUtils {
    @NonNull
    static String getNewPin(int Codigo) {
        return String.valueOf(((Codigo + 3031) * 6) / 4);
    }
}
