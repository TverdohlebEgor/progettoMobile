package cohappy.frontend.client

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ClientSingleton {
    //PER PC
    //private const val BASE_URL = "http://10.0.2.2:8080/"

    //PER MOBILE
    private const val BASE_URL ="http://192.168.1.28:8080/"
    private val moshi = Moshi.Builder()
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