package com.zp.rx_java_t.util

import android.media.MediaPlayer
import android.media.MediaRecorder

/**
 * 录音相关
 */
class RecordUtil {

    // 录音
    private var recorder: MediaRecorder? = null
    // 播放音频
    private var mediaPlayer: MediaPlayer? = null
    // 音频路径
    var path: String? = null

    /** 是否在录音 */
    var isRecorder = false
    /** 是否在播放录音 */
    var isPlaying = false

    var autoPlayRecordListener: AutoPlayRecordListener? = null

    private object BUILDER { val builder = RecordUtil() }

    companion object {
        fun getInstance() = BUILDER.builder
    }

    /**
     * 开始录音
     */
    fun startRecorder() {
        if (isPlaying || isRecorder) {
            L.e("正在录音或正在播放录音中，不能执行该操作")
            return
        }
        try {
            path = MyFileUtil.getPathForPath(MyFileUtil.SOUND) + MyFileUtil.getFileName(".aac")
            // 配置mMediaRecorder相应参数
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC) // 从麦克风采集声音数据
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // 设置保存文件格式为MP4
                setAudioSamplingRate(44100) // 设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // 设置声音数据编码格式,音频通用格式是AAC
                setAudioEncodingBitRate(96000) // 设置编码频率
                setOutputFile(path) // 设置录音保存的文件
                prepare() // 开始录音
                start()
                isRecorder = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *  结束录音
     */
    fun stopRecord() {
        if (isRecorder) {
            // 停止录音
            recorder?.stop()
            isRecorder = false
            releaseRecorder()
        }
    }

    /**
     * 释放录音相关资源
     */
    private fun releaseRecorder() {
        if (null != recorder) {
            recorder?.release()
            recorder = null
            // 防止出现异常，一定要加
            isRecorder = false
        }
    }

    /**
     * 播放音频文件
     */
    fun startPlay() {
        if (isPlaying || isRecorder) {
            L.e("正在录音或正在播放录音中，不能执行该操作")
            return
        }
        try {
            // 初始化播放器
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)  // 设置播放音频数据文件
                setOnCompletionListener { stopPlay(false) } // 设置播放监听事件
                setOnErrorListener { _, _, _ -> // 播放发生错误监听事件
                    stopPlay(true)
                    true
                }
                setVolume(1f, 1f) // 播放器音量配置
                isLooping = false //是否循环播放
                prepare() // 准备及播放
                start()
            }
            isPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlay(true)
        }
    }

    /**
     * 设置播放音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float) {
        if (isPlaying) {
            mediaPlayer?.setVolume(leftVolume, rightVolume)
        }
    }

    /**
     * 停止播放或播放失败处理
     */
    fun stopPlay(isErrorEnd: Boolean) {
        if (isErrorEnd) { // 出错而停止播放
            L.e("停止播放失败")
        }
        isPlaying = false
        releasePlay()
        autoPlayRecordListener?.stopPlay(isErrorEnd)
    }

    /**
     * 释放播放资源
     */
    fun releasePlay() {
        mediaPlayer?.apply {
            setOnCompletionListener(null)
            setOnErrorListener(null)
            stop()
            reset()
            release()
        }
        mediaPlayer = null
        // 防止出现异常情况
        isPlaying = false
    }

    /**
     * 清除所有
     */
    fun clearAll() {
        releaseRecorder()
        releasePlay()
        path = null
        // 单列模式 一定要赋为空，否则内存泄漏
        autoPlayRecordListener = null
    }

    interface AutoPlayRecordListener {
        /**
         * 自动停止播放
         * @param isErrorEnd 是否是出错而停止的
         */
        fun stopPlay(isErrorEnd: Boolean)
    }

}