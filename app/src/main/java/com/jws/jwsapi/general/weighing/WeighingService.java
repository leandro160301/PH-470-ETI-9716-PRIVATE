package com.jws.jwsapi.general.weighing;

public class WeighingService {

    private final WeighingApi weighingApi;
    private final WeighingDao weighingDao;

    public WeighingService(WeighingApi weighingApi,WeighingDao weighingDao){
        this.weighingApi = weighingApi;
        this.weighingDao = weighingDao;
    }



}
