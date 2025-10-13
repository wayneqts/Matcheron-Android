package com.appmonarchy.matcheron.api;

public class APIUtils {
    public static String URL_SERVER = "https://matcheron.com/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(URL_SERVER).create(APIService.class);
    }
}
