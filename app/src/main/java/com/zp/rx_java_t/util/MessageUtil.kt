package com.zp.rx_java_t.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.zp.rx_java_t.MainActivity
import com.zp.rx_java_t.R
import com.zp.rx_java_t.util.system.SPUtil
import com.zp.rx_java_t.util.system.Toaster
import org.jetbrains.annotations.NotNull

@RequiresApi(Build.VERSION_CODES.N)
@Deprecated("应该用不到")
object MessageUtil {

    init {
        TODO("目前该类只测试过Android 8.0,7.0,6.0,5.0的通知，其他版本还未测试，理论上来说都可以的！")
    }

    private const val SP_KEY_PET_ALL_MESSAGE_STR = "all_message_key"

    /** 互动消息 ID */
    const val PET_HU_DONG_ID = "RX_JAVA_HU_DONG"
    /** 点赞消息 ID */
    const val PET_DIAN_ZAN_ID = "RX_JAVA_DIAN_ZAN"
    /** 客服、聊天消息 ID */
    const val PET_KE_FU_ID = "RX_JAVA_KE_FU"
    /** 订单消息 ID */
    const val PET_DING_DAN_ID = "RX_JAVA_DING_DAN"
    /** 系统消息 ID */
    const val PET_XI_TONG_ID = "RX_JAVA_XI_TONG"

    private val MSG_ID_ARRAYS by lazy {
        arrayOf(
                PET_HU_DONG_ID,
                PET_DIAN_ZAN_ID,
                PET_KE_FU_ID,
                PET_DING_DAN_ID,
                PET_XI_TONG_ID
        )
    }

    private val MSG_NAME_ARRAYS by lazy {
        arrayOf(
                "互动消息",
                "点赞",
                "客服、聊天",
                "订单消息",
                "系统消息"
        )
    }

    private val MSG_IMPORTANCE_ARRAYS by lazy {
        arrayOf(
                NotificationManager.IMPORTANCE_HIGH,
                NotificationManager.IMPORTANCE_HIGH,
                NotificationManager.IMPORTANCE_DEFAULT,
                NotificationManager.IMPORTANCE_DEFAULT,
                NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    /**
     * 创建消息类别 Android 8.0以上
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(context: Context) {
        val hasMsgKey = SPUtil.get(SP_KEY_PET_ALL_MESSAGE_STR, false) as Boolean
        if (!hasMsgKey) {
            MSG_ID_ARRAYS.indices.forEach {
                createNotificationByImportance(
                        context, MSG_ID_ARRAYS[it],
                        MSG_NAME_ARRAYS[it],
                        MSG_IMPORTANCE_ARRAYS[it]
                )
            }
            SPUtil.put(SP_KEY_PET_ALL_MESSAGE_STR, true)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationByImportance(context: Context, msgID: String, msgName: String, importance: Int) {
        val notificationChannel = NotificationChannel(msgID, msgName, importance)
        // 显示数字角标
        notificationChannel.setShowBadge(true)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

    /**
     * 显示消息
     * @param messageID     消息ID
     * @param message       消息内容
     */
    fun showMessage(@NotNull context: Context, @NotNull messageID: String, @NotNull vararg message: String) {
        val contains = MSG_ID_ARRAYS.contains(messageID)
        if (!contains) throw IllegalArgumentException("messageID type is error," +
                "Check to see if messageID exists in the MSG_ID_ARRAYS collection")
        // Android 8.0以上单独使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification(context)
            showMessageForO(context, message, messageID)
        } else {
            showMessageForElse(context, message, messageID)
        }
    }

    /**
     * Android 8.0以上的通知消息
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageForO(context: Context, message: Array<out String>, messageID: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(messageID)
        if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
            Toaster.makeText("收到一条通知，但您已关闭该通知显示！您可在设置中手动打开")
            return
        }
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = NotificationCompat.Builder(context, messageID)
                .setContentTitle(message[0])
                .setContentText(message[1])
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.pet_logo_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.pet_logo_round))
                .setAutoCancel(true)
                .setNumber(2)
                .build()
        manager.notify(1, notification)
    }

    /**
     * Android 8.0 以下的通知
     */
    private fun showMessageForElse(context: Context, message: Array<out String>, messageID: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentTitle(message[0])
                .setContentText(message[1])// 详细内容
                .setContentIntent(contentIntent)// 设置点击意图
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.pet_logo_round))
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.pet_logo_round)
                .setAutoCancel(true)

        // TODO 部分国产手机消息不会自动展示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC) // 5.0以后添加,锁屏显示通知
                    .setCategory(Notification.CATEGORY_MESSAGE) // 设置通知类别
            if (messageID == PET_HU_DONG_ID || messageID == PET_DIAN_ZAN_ID) {
                builder.priority = Notification.PRIORITY_MAX
            } // 自动展示
            else {
            }
            builder.priority = Notification.PRIORITY_DEFAULT // 通知栏中显示
        }
        manager.notify(1, builder.build())
    }

}