package com.zp.rx_java_t.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.view.music.MusicService
import kotlinx.android.synthetic.main.activity_music.*

class MusicActivity : BaseActivity(), ServiceConnection {

    private var isConnected = false

    override fun getContentView() = R.layout.activity_music

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("Music")
        music_playBtn.setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            musicView.play()
        }

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isConnected = true
        (service as MusicService.MusicBind).start()
    }

    override fun onServiceDisconnected(name: ComponentName?) = Unit

    override fun onDestroy() {
        if (isConnected) {
            unbindService(this)
        }
        super.onDestroy()
    }
}
