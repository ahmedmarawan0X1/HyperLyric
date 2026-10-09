package com.lidesheng.hyperlyric.lyric.view.line

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Detects the first strong directional character in lyric text. Neutral punctuation and
 * whitespace do not override the direction of the actual lyric.
 */
internal fun String.isRtlLyricText(): Boolean {
    var index = 0
    while (index < length) {
        val codePoint = codePointAt(index)
        when (Character.getDirectionality(codePoint)) {
            Character.DIRECTIONALITY_RIGHT_TO_LEFT,
            Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC -> return true
            Character.DIRECTIONALITY_LEFT_TO_RIGHT -> return false
        }
        index += Character.charCount(codePoint)
    }
    return false
}

/** True for Arabic-script characters, including Arabic Supplement and presentation forms. */
internal fun String.containsArabicText(): Boolean = any { char ->
    char in '\u0600'..'\u06FF' ||
            char in '\u0750'..'\u077F' ||
            char in '\u08A0'..'\u08FF' ||
            char in '\uFB50'..'\uFDFF' ||
            char in '\uFE70'..'\uFEFF'
}

/** Draws RTL text as a shaped run instead of sending it through Canvas' LTR drawText path. */
internal fun Canvas.drawLyricText(
    text: String,
    x: Float,
    y: Float,
    paint: Paint,
    isRtl: Boolean
) {
    if (isRtl && text.isNotEmpty()) {
        // Keep the run anchored at the same left-edge origin used by measurement,
        // clipping and scrolling. Shaping is handled by drawTextRun itself.
        drawTextRun(
            text, 0, text.length, 0, text.length,
            x, y, true, paint
        )
    } else {
        drawText(text, x, y, paint)
    }
}
