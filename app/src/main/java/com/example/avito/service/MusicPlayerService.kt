package com.example.avito.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avito.R
import com.example.avito.player.PlayerViewModel
import com.example.avito.viewmodel.DownloadedTracksViewModel

class MusicPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var playerViewModel: PlayerViewModel
    private val CHANNEL_ID = "MusicServiceChannel"
    private val NOTIFICATION_ID = 1
    private val ACTION_PLAY = "ACTION_PLAY"
    private val ACTION_PAUSE = "ACTION_PAUSE"
    private val ACTION_NEXT = "ACTION_NEXT"
    private val ACTION_PREV = "ACTION_PREVIOUS"

    private val broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("ForegroundServiceType")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PLAY -> {
                    mediaPlayer?.start()
                }

                ACTION_PAUSE -> {
                    mediaPlayer?.pause()
                }

                ACTION_NEXT -> {
                    playerViewModel.playNextTrack(context!!)
                }

                ACTION_PREV -> {
                    playerViewModel.playBackTrack(context!!)
                }
            }
            startForeground(NOTIFICATION_ID, buildNotification())
        }
    }

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this, "MusicServiceSession").apply {
            isActive = true
        }

        mediaPlayer?.isLooping = true

        val filter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PREV)
        }
        registerReceiver(broadcastReceiver, filter)
        createNotificationChannel()
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val metadata = mediaSession?.controller?.metadata
        val title = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "Avito Player"
        val artist = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "Играет трек..."
        val largeIconUri = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)

        val playIntent = Intent(ACTION_PLAY)
        val pauseIntent = Intent(ACTION_PAUSE)
        val nextIntent = Intent(ACTION_NEXT)
        val previousIntent = Intent(ACTION_PREV)

        val playPendingIntent = PendingIntent.getBroadcast(
            this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pausePendingIntent = PendingIntent.getBroadcast(
            this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextPendingIntent = PendingIntent.getBroadcast(
            this, 2, nextIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val prevPendingIntent = PendingIntent.getBroadcast(
            this,
            3,
            previousIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val prevAction = NotificationCompat.Action(
            android.R.drawable.ic_media_previous,
            "Previous",
            prevPendingIntent
        )
        val playAction = NotificationCompat.Action(
            android.R.drawable.ic_media_play,
            "Play",
            playPendingIntent
        )
        val pauseAction = NotificationCompat.Action(
            android.R.drawable.ic_media_pause,
            "Pause",
            pausePendingIntent
        )


        val nextAction = NotificationCompat.Action(
            android.R.drawable.ic_media_next,
            "Next",
            nextPendingIntent
        )
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSession?.sessionToken)

        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Avito Player")
//            .setContentText("Играет трек...")
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_icon_music)
            .addAction(prevAction)
            .addAction(if (mediaPlayer?.isPlaying == true) pauseAction else playAction)
            .addAction(nextAction)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал уведомлений для музыкального сервиса"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        mediaPlayer?.release()
        mediaSession?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}