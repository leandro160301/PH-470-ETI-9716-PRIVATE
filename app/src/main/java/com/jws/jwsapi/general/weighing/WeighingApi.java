package com.jws.jwsapi.general.weighing;

import com.jws.jwsapi.general.pallet.PalletRequest;
import com.jws.jwsapi.general.pallet.PalletResponse;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeighingApi {

    @POST("/NuevaPesada")
    Single<PalletResponse> postNewWeighing(@Body PalletRequest palletRequest);
}
