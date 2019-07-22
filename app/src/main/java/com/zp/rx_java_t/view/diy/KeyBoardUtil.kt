package com.zp.rx_java_t.view.diy

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import com.zp.rx_java_t.R
import java.lang.ref.SoftReference

class KeyBoardUtil(context: Context) : KeyboardView.OnKeyboardActionListener {

    var keyBoardOutPutListener: ((String) -> Unit)? = null
    private var softReference: SoftReference<Context>? = null

    init {
        softReference = SoftReference(context)
    }

    companion object {
        const val KEY_BOARD_VAL_OK = "OK"
        const val KEY_BOARD_VAL_DEL = "DEL"
        const val KEY_BOARD_VAL_NOTHING = "NOTHING"

        private const val KEY_BOARD_ZERO = 10000
        private const val KEY_BOARD_ONE = 10001
        private const val KEY_BOARD_TWO = 10002
        private const val KEY_BOARD_THREE = 10003
        private const val KEY_BOARD_FOUR = 10004
        private const val KEY_BOARD_FIVE = 10005
        private const val KEY_BOARD_SIX = 10006
        private const val KEY_BOARD_SEVEN = 10007
        private const val KEY_BOARD_EIGHT = 10008
        private const val KEY_BOARD_NINE = 10009

        private const val KEY_BOARD_OK = 11000
        private const val KEY_BOARD_DEL = 10010
    }

    fun showKeyBoard(keyboardView: KeyboardView?) {
        val context = softReference?.get() ?: return
        keyboardView?.let {
            it.keyboard = Keyboard(context, R.xml.my_key_board)
            it.isPreviewEnabled = false
            it.setOnKeyboardActionListener(this)
        }
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        when (primaryCode) {
            KEY_BOARD_ZERO -> keyBoardOutPutListener?.invoke("0")
            KEY_BOARD_ONE -> keyBoardOutPutListener?.invoke("1")
            KEY_BOARD_TWO -> keyBoardOutPutListener?.invoke("2")
            KEY_BOARD_THREE -> keyBoardOutPutListener?.invoke("3")
            KEY_BOARD_FOUR -> keyBoardOutPutListener?.invoke("4")
            KEY_BOARD_FIVE -> keyBoardOutPutListener?.invoke("5")
            KEY_BOARD_SIX -> keyBoardOutPutListener?.invoke("6")
            KEY_BOARD_SEVEN -> keyBoardOutPutListener?.invoke("7")
            KEY_BOARD_EIGHT -> keyBoardOutPutListener?.invoke("8")
            KEY_BOARD_NINE -> keyBoardOutPutListener?.invoke("9")
            KEY_BOARD_OK -> keyBoardOutPutListener?.invoke(KEY_BOARD_VAL_OK)
            KEY_BOARD_DEL -> keyBoardOutPutListener?.invoke(KEY_BOARD_VAL_DEL)
            else -> keyBoardOutPutListener?.invoke(KEY_BOARD_VAL_NOTHING)
        }
    }

    override fun onText(text: CharSequence?) = Unit
    override fun swipeRight() = Unit
    override fun onPress(primaryCode: Int) = Unit
    override fun onRelease(primaryCode: Int) = Unit
    override fun swipeLeft() = Unit
    override fun swipeUp() = Unit
    override fun swipeDown() = Unit
}