package ch.hslu.mobpro.uebung3.comcon

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


public interface BandApiService {
    @GET("all.json")
    fun getBands(): Call<List<BandCode>>
}