package com.zp.rx_java_t.view.music

import android.media.MediaPlayer
import com.zp.rx_java_t.content.getAppContext
import com.zp.rx_java_t.util.system.L

class MusicHelp {

    private object Builder {
        val HELP = MusicHelp()
    }

    companion object {
        fun getInstance() = Builder.HELP
    }

    private var player: MediaPlayer? = null

    fun init(block: () -> Unit, path: String? = null) {
        try {
            if (player == null) {
                player = MediaPlayer()
            }
            if (path.isNullOrEmpty()) {
                val fd = getAppContext().assets.openFd("music.mp3")
                player?.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            } else {
                player?.setDataSource(path)
            }
            player?.prepareAsync()
            player?.setOnPreparedListener {
                block.invoke()
                L.i("音乐流媒体加载完成")
            }
            player?.setOnCompletionListener {
                it.isLooping = true
                it.start()
                L.i("播放完成，正在重新播放. . . ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playOrPasue() {
        if (isPlaying()) {
            player?.pause()
        } else {
            player?.start()
        }
    }

    fun isPlaying() = player?.isPlaying ?: false

    fun reset() {
        if (isPlaying()) {
            player?.pause()
        }
        player?.stop()
        player?.release()
        player = null
    }

}