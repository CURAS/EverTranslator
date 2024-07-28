package tw.firemaples.onscreenocr.translator.tencent

import android.util.Base64
import tw.firemaples.onscreenocr.utils.Logger
import tw.firemaples.onscreenocr.utils.Utils
import java.util.concurrent.ThreadLocalRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TencentTranslatorAPI {
    private val logger: Logger by lazy { Logger(this::class) }
    private val apiService: TencentAPIService by lazy {
        Utils.retrofit.create(TencentAPIService::class.java)
    }

    suspend fun translate(text: String, from: String, to: String, apiId: String?, apiKey: String?): Result<String> {
        logger.debug("Start translate, text: $text, from: $from, to: $to")

        val queryMap = hashMapOf(
            "Action" to "TextTranslate",
            "Language" to "zh-CN",
            "Nonce" to ThreadLocalRandom.current().nextInt(100000).toString(),
            "ProjectId" to "0",
            "Region" to "ap-beijing",
            "SecretId" to (apiId?:""),
            "Source" to from,
            "SourceText" to text,
            "Target" to to,
            "Timestamp" to (System.currentTimeMillis()/1000).toString(),
            "Version" to "2018-03-21",
        )
        queryMap["Signature"] = genSignature(queryMap, apiId?:"", apiKey?:"")
        val result = apiService.translate(queryMap)
        logger.debug("Translate result: $result")

        if (!result.isSuccessful) {
            return Result.failure(
                IllegalStateException(
                    "API failed(${result.code()}): ${result.errorBody()?.toString()}"
                )
            )
        }

        val response = result.body()?.get("Response") as? Map<String, Any>
            ?: return Result.failure(
                IllegalStateException(
                    "Got full empty result"
                )
            )

        if (response.contains("Error")) {
            val error = response["Error"] as? Map<String, String>
            return Result.failure(
                IllegalStateException(
                    "API failed(${error?.get("Code")}): ${error?.get("Message")}"
                )
            )
        }

        if (!response.contains("TargetText")) {
            return Result.failure(
                IllegalStateException(
                    "Got translation empty result"
                )
            )
        }

        return Result.success((response["TargetText"]).toString())
    }

    private fun genSignature(map: Map<String, String>, apiId: String, apiKey: String): String {
        val queryString =
            map.keys.toList().sorted().joinToString("&") { key -> "$key=${map[key]}" }
        val plainString = "GETtmt.tencentcloudapi.com/?$queryString"

        val type = "HmacSHA1"
        val secret = SecretKeySpec(apiKey.toByteArray(), type)
        val mac = Mac.getInstance(type)
        mac.init(secret)
        val digest = mac.doFinal(plainString.toByteArray())

        val cipherString =  Base64.encodeToString(digest, Base64.DEFAULT)
        return cipherString
    }
}
