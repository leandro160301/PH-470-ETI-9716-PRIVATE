package com.jws.jwsapi.general.pallet;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PalletApi {

    @POST("/NuevoPallet")
    Single<PalletResponse> postNewPallet(@Body PalletRequest palletRequest);
}