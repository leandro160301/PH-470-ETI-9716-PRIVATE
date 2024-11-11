package com.jws.jwsapi.scale;

public interface ScaleOperations {
    void setTare();

    void setZero();

    String format(String weight);

    void removeTare();
}
