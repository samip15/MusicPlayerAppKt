package com.sam.musicplayerappkt

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mp = MediaPlayer.create(this, R.raw.music)
        mp.isLooping = true
        mp.setVolume(0.5f, 0.5f)
        totalTime = mp.duration
        // volume bar
        val volume_Bar = findViewById<SeekBar>(R.id.volumeBar)
        volume_Bar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    var volumeNum = progress / 100.0f
                    mp.setVolume(volumeNum, volumeNum)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )

        // position Bar
        val position_Bar = findViewById<SeekBar>(R.id.positionBar)
        position_Bar.max = totalTime
        position_Bar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            }
        )
        // thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()

    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what
            // update positionBar
            val position_Bar = findViewById<SeekBar>(R.id.positionBar)
            position_Bar.progress = currentPosition
            // update labels
            var elapsedTime = createTimeLabel(currentPosition)
            val elased_Time_Lebel = findViewById<TextView>(R.id.elapsedTimeLabel)
            elased_Time_Lebel.text = elapsedTime
            var remainingTime = createTimeLabel(totalTime - currentPosition)
            val remaining_Time_Lebel = findViewById<TextView>(R.id.remainingTimeLabel)
            remaining_Time_Lebel.text = "-$remainingTime"
        }
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time/1000/60
        var sec = time/1000 % 60
        timeLabel = "$min:"
        if (sec<10) timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }

    fun playBtnClick(v: View) {
        if (mp.isPlaying) {
            // stop
            mp.pause()
            val playBtn = findViewById<Button>(R.id.play_Btn)
            playBtn.setBackgroundResource(R.drawable.play)
        } else {
            // start
            mp.start()
            val playBtn = findViewById<Button>(R.id.play_Btn)
            playBtn.setBackgroundResource(R.drawable.stop)
        }
    }
}