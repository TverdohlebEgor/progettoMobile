package cohappy.frontend.util

import android.content.Context
import android.view.View

fun randomPhoto(context: Context): ByteArray {
    val isPreview = View(context).isInEditMode

    return try {
        val resId = context.resources.getIdentifier("casa1", "drawable", context.packageName)
        if (resId == 0) return byteArrayOf() // Not found

        context.resources.openRawResource(resId).use { it.readBytes() }
    } catch (e: Exception) {
        byteArrayOf()
    }
}
