package com.jws.jwsapi.general.weighing;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WeighingApi {

    @POST("/NuevaPesada")
    Single<WeighingResponse> postNewWeighing(@Body WeighingRequest weighingRequest);
}
