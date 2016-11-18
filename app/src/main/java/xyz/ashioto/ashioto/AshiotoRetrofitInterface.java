package xyz.ashioto.ashioto;

import java.util.HashMap;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import xyz.ashioto.ashioto.retrofitClasses.AuthResponse;
import xyz.ashioto.ashioto.retrofitClasses.CountUpdateResponse;
import xyz.ashioto.ashioto.retrofitClasses.EventsList;
import xyz.ashioto.ashioto.retrofitClasses.GatesListResponse;
import xyz.ashioto.ashioto.retrofitClasses.PerGateResponse;

/**
 * Created by geek on 23/9/16.
 * All the retrofit calls are defined here
 */

public interface AshiotoRetrofitInterface {
    @Headers({"Content-type: application/json"})

    @POST("/count_update")
    Call<CountUpdateResponse> syncData(@Body HashMap<String, String> update_data);

    @POST("/per_gate")
    Call<PerGateResponse> getOverview(@Body HashMap<String, String> body);

    @GET("/listEvents")
    Call<EventsList> getEvents();

    @GET("/listGates")
    Call<GatesListResponse> getGates(@Query("event") String eventCode);

    //Login call
    @POST("/login")
    Call<AuthResponse> authenticate(@Query("email") String email, @Query("pass") String password);
}
