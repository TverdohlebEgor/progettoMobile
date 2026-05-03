package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyHouseDTO(
    val houseCode: String,
    val newHouseCode: String? = null,
    val images: List<ByteArray>? = null,
    val costPerMonth: Int? = null,
    val country: String? = null,
    val region: String? = null,
    val street: String? = null,
    val civicNumber: Int? = null
) {
    fun areAllNull(): Boolean {
        return newHouseCode == null &&
                images.isNullOrEmpty() &&
                costPerMonth == null &&
                country == null &&
                region == null &&
                street == null &&
                civicNumber == null
    }
}