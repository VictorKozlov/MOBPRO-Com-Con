package ch.hslu.mobpro.uebung3.comcon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BandsViewModel : ViewModel() {
    val bands: MutableLiveData<List<BandCode>> = MutableLiveData()

    init {
        bands.value = emptyList()
    }
}