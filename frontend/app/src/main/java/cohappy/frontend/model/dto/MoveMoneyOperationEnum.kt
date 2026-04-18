package cohappy.frontend.model.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
enum class MoveMoneyOperationEnum {
    SEND,
    RETRIEVE
}