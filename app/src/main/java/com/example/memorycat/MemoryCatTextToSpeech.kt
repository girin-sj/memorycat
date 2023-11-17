package com.example.memorycat

import android.speech.tts.TextToSpeech
import java.util.Locale
class MemoryCatTextToSpeech : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    // TextToSpeech override 함수
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 예외처리
            } else {
                // 예외처리
            }
        } else {
            // 예외처리
        }
    }

    fun speakWord(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun stopWord() {
        tts!!.stop()
        tts!!.shutdown()
    }
}