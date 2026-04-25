package cohappy.frontend.client

import android.util.Base64
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class LocalDateAdapter {
    @ToJson fun toJson(value: LocalDate): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE)
    @FromJson fun fromJson(value: String): LocalDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
}

class LocalDateTimeAdapter {
    @ToJson fun toJson(value: LocalDateTime): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    @FromJson fun fromJson(value: String): LocalDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

class ByteArrayAdapter {
    @ToJson fun toJson(value: ByteArray): String = Base64.encodeToString(value, Base64.NO_WRAP)
    @FromJson fun fromJson(value: String): ByteArray = Base64.decode(value, Base64.DEFAULT)
}
object ClientSingleton {
    //PER PC
    private const val BASE_URL = "http://10.0.2.2:8080/"

    //PER MOBILE
    //private const val BASE_URL =/*"http://192.168.1.10:8080/"*/"http://192.168.1.28:8080/"

    private val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .add(LocalDateTimeAdapter())
        .add(ByteArrayAdapter())
        .build()


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    val userApi: UserApiClient by lazy { retrofit.create(UserApiClient::class.java) }
    val houseApi: HouseApiClient by lazy { retrofit.create(HouseApiClient::class.java) }
    val chatApi: ChatApiClient by lazy { retrofit.create(ChatApiClient::class.java) }
    val portfolioApi: PortfolioApiClient by lazy { retrofit.create(PortfolioApiClient::class.java) }
    val choreApi: ChoreApiClient by lazy { retrofit.create(ChoreApiClient::class.java) }
}