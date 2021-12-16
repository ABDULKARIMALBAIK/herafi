package com.abdalkarimalbiekdev.herafi.Remote;

import com.abdalkarimalbiekdev.herafi.model.CraftModel;
import com.abdalkarimalbiekdev.herafi.networkModel.LatLngModel;
import com.abdalkarimalbiekdev.herafi.networkModel.YearRequests;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileImageItemModel;
import com.abdalkarimalbiekdev.herafi.model.RequestItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CategoryModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CityModel;
import com.abdalkarimalbiekdev.herafi.networkModel.CraftmanData;
import com.abdalkarimalbiekdev.herafi.networkModel.ProfileItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectDetailsItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ProjectItemModel;
import com.abdalkarimalbiekdev.herafi.networkModel.ResultModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface HerafiAPI {

    ////////Login
    @FormUrlEncoded
    @POST("Login/createToken")
    Single<ResultModel<String>> getAccessToken(@Field("craftsmanId") String craftsmanId);

    @FormUrlEncoded
    @POST("Login/login")
    Single<ResultModel<CraftmanData>> login(@Header("Authorization") String token,
                                            @Field("email") String email,
                                            @Field("password") String password);

    @FormUrlEncoded
    @POST("Login/verify")
    Single<ResultModel<CraftmanData>> verify(@Header("Authorization") String token,
                                             @Field("verifyCode") String verifyCode);

    @FormUrlEncoded
    @POST("Login/forgetPassword")
    Single<ResultModel<String>> sendForgetPassword(@Header("Authorization") String token,
                                                   @Field("email") String email,
                                                   @Field("phoneNumber") String phoneNumber,
                                                   @Field("secureId") String secureId);


    @FormUrlEncoded
    @POST("Login/loginFingerprint")
    Single<ResultModel<CraftmanData>> loginFingerprint(@Header("Authorization") String token,
                                                       @Field("fingerprintId") String fingerprintId);


    @FormUrlEncoded
    @POST("Login/loginFacebook")
    Single<ResultModel<CraftmanData>> loginFacebook(@Header("Authorization") String token,
                                                    @Field("facebookId") String facebookId);

    @FormUrlEncoded
    @POST("Login/loginGoogle")
    Single<ResultModel<CraftmanData>> loginGoogle(@Header("Authorization") String token,
                                                  @Field("googleId") String googleId);

    @FormUrlEncoded
    @POST("Login/verifyCaptcha")
    Single<ResultModel<String>> verifyHCaptchaToken(@Header("Authorization") String token,
                                                    @Field("hCaptchaToken") String hCaptchaToken);


    /////////SignUp
    @GET("signup/getCity")
    Single<ResultModel<List<CityModel>>> getCities();

    @GET("signup/getCategory")
    Single<ResultModel<List<CategoryModel>>> getCategories();

    @FormUrlEncoded
    @POST("signup/getCrafts")
    Single<ResultModel<List<CraftModel>>> getCrafts(@Field("craftId") String craftId);

    @GET("signup/createAccount")
    Single<ResultModel<String>> createAccount();

    @FormUrlEncoded
    @POST("signup/register")
    Single<ResultModel<String>> registerCraftsman(@Header("Authorization") String token,
                                                  @Field("cityId") String cityId,
                                                  @Field("description") String description,
                                                  @Field("name") String name,
                                                  @Field("email") String email,
                                                  @Field("password") String password,
                                                  @Field("phone_num") String phone_num,
                                                  @Field("size_queue") String size_queue,
                                                  @Field("lat") String lat,
                                                  @Field("lng") String lng,
                                                  @Field("secure_id") String secure_id,
                                                  @Field("identityNumber") String identityNumber,
                                                  @Field("lowest_cost") String lowest_cost,
                                                  @Field("highest_cost") String highest_cost,
                                                  @Field("id") String id);


    @FormUrlEncoded
    @POST("signup/addCraft")
    Single<ResultModel<String>> addCraft(@Header("Authorization") String token,
                                         @Field("craftmanId") String craftmanId,
                                         @Field("craftId") String craftId);

    @GET("signup/braintree_client_token")
    Single<ResultModel<String>> getBraintreeToken(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("signup/checkout")
    Single<ResultModel<String>> checkout(@Header("Authorization") String token,
                                         @Field("payment_method_nonce") String payment_method_nonce,
                                         @Field("amount") String amount);


    ////Project
    @FormUrlEncoded
    @POST("Project/craftsmanProject")
    Single<ResultModel<List<ProjectItemModel>>> getProjects(@Header("authorization") String token,
                                                            @Field("id") String id,
                                                            @Field("start") String start,
                                                            @Field("end") String end);


    ////Request
    @FormUrlEncoded
    @POST("Request/craftsmanRequest")
    Single<ResultModel<List<RequestItemModel>>> getRequests(@Header("Authorization") String token,
                                                            @Field("id") String id);

    @FormUrlEncoded
    @POST("Request/countRequest")
    Single<ResultModel<String>> countRequest(@Header("Authorization") String token,
                                             @Field("id") String craftsmanId);

    @FormUrlEncoded
    @POST("Request/acceptRequest")
    Single<ResultModel<String>> acceptRequest(@Header("Authorization") String token,
                                              @Field("craftsmanId") String craftsmanId,
                                              @Field("userId") String userId,
                                              @Field("requestId") String requestId,
                                              @Field("processType") String processType,
                                              @Field("craftCode") String craftCode);

    @FormUrlEncoded
    @POST("Request/refuseRequest")
    Single<ResultModel<String>> refuseRequest(@Header("Authorization") String token,
                                              @Field("craftsmanId") String craftsmanId,
                                              @Field("userId") String userId,
                                              @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("Request/getUserCode")
    Single<ResultModel<String>> getUserCode(@Header("Authorization") String token,
                                            @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("Request/getCraftsmanCode")
    Single<ResultModel<String>> getCraftsmanCode(@Header("Authorization") String token,
                                                 @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("Request/finishWork")
    Single<ResultModel<String>> finishWork(@Header("Authorization") String token,
                                           @Field("craftsmanId") String craftsmanId,
                                           @Field("userId") String userId,
                                           @Field("requestId") String requestId);

    @FormUrlEncoded
    @POST("Request/getLatLngCraftsman")
    Single<ResultModel<LatLngModel>> getLatLngCraftsman(@Header("Authorization") String token,
                                                      @Field("craftsmanId") String craftsmanId);


    ///Profile
    @FormUrlEncoded
    @POST("Profile/craftsmanProfile")
    Single<ResultModel<ProfileItemModel>> craftsmanProfile(@Header("Authorization") String token,
                                                           @Field("id") String id);

    @FormUrlEncoded
    @POST("Profile/craftsmanProfilePhotos")
    Single<ResultModel<List<ProfileImageItemModel>>> craftsmanImages(@Header("Authorization") String token,
                                                                     @Field("id") String id);

    @FormUrlEncoded
    @POST("Profile/craftsmanProfileCrafts")
    Single<ResultModel<List<CraftModel>>> craftsmancCrafts(@Header("Authorization") String token,
                                                           @Field("id") String id);

    @FormUrlEncoded
    @POST("Profile/changeStatusCraftsman")
    Single<ResultModel<String>> changeStatusCraftsman(@Header("Authorization") String token,
                                                      @Field("id") String id,
                                                      @Field("value_busy") String valueBusy);


    /////ProjectDetails
    @FormUrlEncoded
    @POST("ProjectDetails/craftsmanProjectDetails")
    Single<ResultModel<ProjectDetailsItemModel>>  projectDetailsData(@Header("Authorization") String token,
                                                                     @Field("projectId") String projectId,
                                                                     @Field("craftsmanId") String craftsmanId);

    @FormUrlEncoded
    @POST("ProjectDetails/craftsmanProjectDetailsPhotos")
    Single<ResultModel<List<ProfileImageItemModel>>> projectDetailsImage(@Header("Authorization") String token,
                                                                         @Field("projectId") String projectId,
                                                                         @Field("craftsmanId") String craftsmanId);


    /////Authentication
    @FormUrlEncoded
    @POST("Authentications/addFacebookLogin")
    Single<ResultModel<String>> addFacebookLogin(@Header("Authorization") String token,
                                                 @Field("facebookId") String facebookId,
                                                 @Field("craftsmanId") String craftsmanId);

    @FormUrlEncoded
    @POST("Authentications/addFingerprintLogin")
    Single<ResultModel<String>> addFingerprintLogin(@Header("Authorization") String token,
                                                    @Field("fingerprintId") String fingerprintId,
                                                    @Field("craftsmanId") String craftsmanId);

    @FormUrlEncoded
    @POST("Authentications/addGoogleLogin")
    Single<ResultModel<String>> addGoogleLogin(@Header("Authorization") String token,
                                               @Field("googleId") String googleId,
                                               @Field("craftsmanId") String craftsmanId);


    /////Chart
    @FormUrlEncoded
    @POST("Chart/getYearRequests")
    Single<ResultModel<List<YearRequests>>> getYearRequests(@Header("Authorization") String token,
                                                            @Field("id") String id,
                                                            @Field("year") String year);

}
