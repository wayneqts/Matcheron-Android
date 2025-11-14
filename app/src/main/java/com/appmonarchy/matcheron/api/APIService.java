package com.appmonarchy.matcheron.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {
    @Multipart
    @POST("login.php")
    Call<JsonObject> login(@Part("email") RequestBody email, @Part("password") RequestBody pw);
    @Multipart
    @POST("delete_user.php")
    Call<JsonObject> deleteAcc(@Part("id") RequestBody id, @Part("password") RequestBody pw);
    @Multipart
    @POST("forget_submit.php")
    Call<JsonObject> forgotPw(@Part("email") RequestBody email);

    @POST("addmsg.php")
    Call<JsonObject> sendMess(@Body RequestBody rqBody);
    @GET()
    Call<JsonArray> getDataArr(@Url String url);

    @GET()
    Call<JsonArray> getArrById(@Url String url, @Query("id") String id);
    @GET()
    Call<JsonArray> getArrBysId(@Url String url, @Query("sid") String id);
    @GET("addlikes.php")
    Call<JsonObject> addPeopleOpt(@Query("id") String id, @Query("id2") String id2, @Query("type") String type);

    @GET("msglist.php")
    Call<JsonObject> getMessList(@Query("sid") String sid, @Query("id") String rid);
    @GET("join_event.php")
    Call<JsonObject> joinEv(@Query("uid") String sid, @Query("eid") String rid);

    @GET()
    Call<JsonObject> getDataById(@Url String url, @Query("id") String id);
    @GET("userlist.php")
    Call<JsonObject> getUserList();

    @GET("search_listing.php")
    Call<JsonObject> getSearchList(@Query("id") String id, @Query("fname") String fName);

    @POST("signup_submit.php")
    Call<JsonObject> register(@Body RequestBody rqBody);
    @POST("update_imei.php")
    Call<JsonObject> updateImei(@Body RequestBody rqBody);
    @POST("signuppush.php")
    Call<JsonObject> updateEmei(@Body RequestBody rqBody);
    @POST("signup_social.php")
    Call<JsonObject> loginSocial(@Body RequestBody rqBody);
    @POST("deletepic.php")
    Call<JsonObject> deleteImg(@Body RequestBody rqBody);
    @POST("profile_update.php")
    Call<JsonObject> updatePf(@Body RequestBody rqBody);
    @POST("update_photos.php")
    Call<JsonObject> updateImg(@Body RequestBody rqBody);
    @POST("csubmit.php")
    Call<JsonObject> contact(@Body RequestBody rqBody);
    @POST("addblock.php")
    Call<JsonObject> blockUser(@Body RequestBody rqBody);
    @GET("currentcountry.php")
    Call<JsonObject> getCountry();
    @GET("events.php")
    Call<JsonObject> getEvents();

    @Multipart
    @POST("search_pefrance.php")
    Call<JsonObject> setSearch(@Part("uid") RequestBody uid, @Part("fname") RequestBody fName, @Part("seeking") RequestBody seeking, @Part("age1") RequestBody age1, @Part("age2") RequestBody age2, @Part("height1") RequestBody height1,
                               @Part("height2") RequestBody height2, @Part("weight1") RequestBody weight1, @Part("weight2") RequestBody weight2, @Part("country") RequestBody country,
                               @Part("state") RequestBody state, @Part("religion") RequestBody religion, @Part("goal") RequestBody goal, @Part("pairing") RequestBody pairing, @Part("country_ori") RequestBody origin);
}
