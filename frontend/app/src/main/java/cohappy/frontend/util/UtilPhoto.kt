package cohappy.frontend.util

import android.content.Context
import cohappy.frontend.R

fun randomPhoto(context: Context): ByteArray {
    return try {
        context.resources.openRawResource(R.drawable.casa1).use { it.readBytes() }
    } catch (e: Exception) {
        byteArrayOf()
    }
}
