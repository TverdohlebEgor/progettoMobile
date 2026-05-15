package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.util.randomPhoto
import cohappy.frontend.view.ad.AdListView

@Preview(showBackground = true, name = "Chat List - Success")
@Composable
fun PreviewEmptyList() {
    AdListView(
        false,
        "",
        emptyList(),
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Chat List - Loading")
@Composable
fun PreviewLoading() {
    AdListView(
        true,
        "",
        emptyList(),
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Chat List - Success")
@Composable
fun PreviewFullList() {
    val house = GetHouseAdvertesimentDTO(
        houseCode = "house1",
        listOf(randomPhoto(LocalContext.current)),
        100,
        "IT",
        "Lazio",
        "Via Roma",
        1,
        HouseStateEnum.PUBLIC,
        "user1",
        listOf(randomPhoto(LocalContext.current)),
        "Email@",
        "329915",
        "Casa"
    )
    AdListView(
        false,
        "",
        listOf(
            house,
            house,
            house.copy(costPerMonth = 200),
            house,
            house,
            house,
            house
        ),
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Chat List - Success")
@Composable
fun PreviewFilteredList() {
    val house = GetHouseAdvertesimentDTO(
        houseCode = "house1",
        listOf(randomPhoto(LocalContext.current)),
        100,
        "IT",
        "Lazio",
        "Via Roma",
        1,
        HouseStateEnum.PUBLIC,
        "user1",
        listOf(randomPhoto(LocalContext.current)),
        "Email@",
        "329915",
        "Casa"
    )
    AdListView(
        false,
        "Via Roma",
        listOf(
            house.copy(costPerMonth = 200)
        ),
        {},
        {}
    )
}
