package com.jws.jwsapi.general.pallet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PalletApi {

    @POST("/NuevoPallet")
    Call<Void> postNewPallet(@Body Pallet pallet);
}