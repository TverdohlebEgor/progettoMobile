package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyHouseDTO(
    val houseCode: String,
    val images: List<ByteArray>? = null,
    val costPerMonth: Int? = null,
    val country: String? = null,
    val region: String? = null,
    val street: String? = null,
    val civicNumber: Int? = null
) {
    fun areAllNull(): Boolean {
        return images.isNullOrEmpty() &&
                costPerMonth == null &&
                country == null &&
                region == null &&
                street == null &&
                civicNumber == null
    }
}