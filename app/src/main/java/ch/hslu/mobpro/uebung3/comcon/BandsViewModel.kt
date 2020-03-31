package ch.hslu.mobpro.uebung3.comcon

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class BandsViewModel : ViewModel() {
    private val url = "https://wherever.ch/hslu/rock-bands/"
    private var retrofit: Retrofit? = null
    get() = Retrofit.Builder().client(OkHttpClient().newBuilder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(url)
            .build()
    private var bandsService: BandApiService? = null

    var bands: MutableLiveData<List<BandCode>>? = null
    var currentBand: MutableLiveData<BandInfo?>? = null

    init {
        bands = MutableLiveData()
        currentBand = MutableLiveData()
        bands?.value = emptyList()
        bandsService = retrofit?.create(BandApiService::class.java)
    }

    fun getBands(){
        bandsService?.getBands()?.enqueue(object : Callback<List<BandCode>> {
            override fun onResponse(call: Call<List<BandCode>>, response: Response<List<BandCode>>) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    bands?.value = response.body().orEmpty()
                }
            }

            override fun onFailure(call: Call<List<BandCode>>, t: Throwable) {
                throw Exception("Failed to load JSON")
            }
        })
    }

    fun getCurrentBand(code: String){
        bandsService?.getCurrentBand(code)?.enqueue(object : Callback<BandInfo> {
            override fun onResponse(call: Call<BandInfo>, response: Response<BandInfo>) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                     currentBand?.value = response.body()
                }
            }

            override fun onFailure(call: Call<BandInfo>, t: Throwable) {
                throw Exception("Failed to load JSON")
            }
        })
    }

    fun resetViewModel() {
        bands?.value = emptyList()
        currentBand?.value = BandInfo("", emptyList(), 0, "", null) ;
    }
}