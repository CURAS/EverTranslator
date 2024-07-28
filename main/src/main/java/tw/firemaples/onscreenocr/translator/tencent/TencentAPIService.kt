package tw.firemaples.onscreenocr.translator.tencent

//import androidx.annotation.Keep
//import com.google.common.base.Objects
//import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface TencentAPIService {
    @GET("https://tmt.tencentcloudapi.com/")
    suspend fun translate(
        @QueryMap
        queryMap: Map<String, String>,
    ): Response<Map<String,Any>>
}

//// Mapping Classes
//@Keep
//data class TranslateResponse(
//    @Json(name = "Response")
//    val response: ResponseData,
//)
//
//@Keep
//data class ResponseData(
//    @Json(name = "Error")
//    val error: ErrorData?,
//    @Json(name = "TargetText")
//    val targetText: String?,
//    @Json(name = "Source")
//    val source: String?,
//    @Json(name = "Target")
//    val target: String?,
//    @Json(name = "RequestId")
//    val requestId: String,
//)
//
//@Keep
//data class ErrorData(
//    @Json(name = "Code")
//    val code: String,
//    @Json(name = "Message")
//    val message: String,
//)
