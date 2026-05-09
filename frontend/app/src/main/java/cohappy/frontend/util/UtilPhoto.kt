package cohappy.frontend.util

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun randomPhoto(context: Context): ByteArray {
    return try {
        val resId = context.resources.getIdentifier("casa1", "drawable", context.packageName)
        if (resId == 0) return byteArrayOf()

        context.resources.openRawResource(resId).use { it.readBytes() }
    } catch (e: Exception) {
        byteArrayOf()
    }
}

fun byteArrayToImageBitmap(byteArray: ByteArray?): ImageBitmap? {
    if (byteArray == null) return null
    val androidBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    return androidBitmap.asImageBitmap()
}