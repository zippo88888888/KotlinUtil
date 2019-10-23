package com.zp.rx_java_t.view.music

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.util.system.L
import kotlinx.android.synthetic.main.layout_music_view.view.*
import java.lang.ref.SoftReference

class MusicView : ConstraintLayout {

    companion object {
        private const val ANIM_DURATION = 5000L
        private const val MSG_CODE = 0
    }

    /** 记录当前已经执行的时间 */
    private var currentPlayTime = 0L
    private var objectAnim: ObjectAnimator? = null

    // 音频是否已经加载完毕  电池
    private var isOnCompletion = false
    private var handler: MusicHandler? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAll(context!!)
    }

    private fun initAll(context: Context) {
        View.inflate(context, R.layout.layout_music_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        MusicHelp.getInstance().init({
            isOnCompletion = true
        })
        handler = MusicHandler(this)
        music_view_playPic.setOnClickListener {
            play()
        }
        music_view_pausePic.setOnClickListener {
            pause()
        }
    }

    fun play() {
        if (MusicHelp.getInstance().isPlaying()) return
        if (objectAnim == null) {
            objectAnim = ObjectAnimator.ofFloat(music_view_pic, "rotation", 0f, 360f).run {
                duration = ANIM_DURATION
                interpolator = LinearInterpolator()
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
                this
            }
            handler?.postDelayed({
                handler?.sendEmptyMessage(MSG_CODE)
            }, ANIM_DURATION)
        }
        if (isOnCompletion) {
            objectAnim?.start()
            objectAnim?.currentPlayTime = this@MusicView.currentPlayTime
            MusicHelp.getInstance().playOrPasue()
            setPlayState()
        } else {
            showToast("音频加载中...")
        }
    }

    fun pause() {
        if (objectAnim?.isRunning == true) {
            currentPlayTime = objectAnim?.currentPlayTime ?: 0L
            objectAnim?.cancel()
        }
        MusicHelp.getInstance().playOrPasue()
        setPauseState()
    }

    private fun setPlayState() {
        music_view_playPic.visibility = View.GONE
        music_view_pausePic.visibility = View.VISIBLE
    }

    private fun setPauseState() {
        music_view_playPic.visibility = View.VISIBLE
        music_view_pausePic.visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        objectAnim?.end()
        objectAnim?.cancel()
        objectAnim?.removeAllListeners()
        objectAnim = null
        handler?.removeMessages(MSG_CODE)
        handler?.removeCallbacksAndMessages(null)
        handler?.clear()
        handler = null
        MusicHelp.getInstance().reset()
        L.e("销毁Music相关服务")
        super.onDetachedFromWindow()
    }

    class MusicHandler(musicView: MusicView) : Handler() {

        private val soft by lazy {
            SoftReference<MusicView>(musicView)
        }

        fun clear() {
            soft.clear()
        }

        override fun handleMessage(msg: Message?) {
            // 防止用户在5秒前退出造成崩溃的问题
            soft.get()?.music_view_bg?.visibility = View.GONE
        }

    }

}