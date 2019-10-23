package com.zp.rx_java_t.view.music

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.zp.rx_java_t.R
import java.lang.ref.SoftReference

class MusicService : Service() {

    private var musicBinder: MusicBind? = null

    override fun onCreate() {
        super.onCreate()
        musicBinder = MusicBind(this)
        // Android O上才显示通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showNotify()
        }
    }

    override fun onBind(intent: Intent?) = musicBinder

    override fun onDestroy() {
        super.onDestroy()
        musicBinder?.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotify() {
        val CHANNEL_ID = "musicPlay_ID"
        // 设定的通知渠道名称
        val channelName = "音乐播放"
        // 设置通知的重要程度
        val importance = NotificationManager.IMPORTANCE_LOW
        // 构建通知渠道
        val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
        channel.description = "后台音乐播放服务"
        // 在创建的通知渠道上发送通知
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.mipmap.pet_logo_round) // 设置通知图标
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.music_bg))
                .setContentTitle("音乐播放") // 设置通知标题
                .setContentText("播放《紧急呼叫119》主题曲") // 设置通知内容
                .setAutoCancel(false) // 用户触摸时，自动关闭
                .setOngoing(true) // 设置处于运行状态
        // 向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        // 将服务置于启动状态 NOTIFICATION_ID指的是创建的通知的ID
        startForeground(111, builder.build())
    }

    class MusicBind(musicService: MusicService) : Binder() {

        private val soft by lazy {
            SoftReference<MusicService>(musicService)
        }

        fun start() {
            // TODO 可单独在后台配置音乐播放
//            MusicHelp.getInstance().init({
//                MusicHelp.getInstance().playOrPasue()
//            })

        }

        fun clear() {
//            MusicHelp.getInstance().reset()
            soft.clear()
        }

    }

}