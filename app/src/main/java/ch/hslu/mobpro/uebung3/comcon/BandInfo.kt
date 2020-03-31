package ch.hslu.mobpro.uebung3.comcon

data class BandInfo(
    val name: String,
    val members: List<String>,
    val foundingYear: Int,
    val homeCountry: String,
    val bestOfCdCoverImageUrl: String?
)