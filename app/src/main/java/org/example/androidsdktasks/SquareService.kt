package org.example.androidsdktasks

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger

class SquareService : Service(){
    companion object{
        private const val TAG = "SquareServiceRemote"
        const val MSG_CALCULATE_SQUARE = 1
        const val MSG_SQUARE_RESULT = 2
        const val KEY_RESULT = "key_result"
        const val KEY_PID = "key_pid"
    }

    private val messenger = Messenger(IncomingHandler())

    private class IncomingHandler : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                MSG_CALCULATE_SQUARE -> {
                    val number = msg.arg1
                    val square = number.toLong() * number.toLong()
                    val currentPid = android.os.Process.myPid()

                    val replyTo = msg.replyTo
                    if (replyTo != null){
                        val replyMsg = Message.obtain(null, MSG_SQUARE_RESULT)
                        replyMsg.data = Bundle().apply {
                            putLong(KEY_RESULT, square)
                            putInt(KEY_PID, currentPid)
                        }
                        replyTo.send(replyMsg)
                    }
                }
                else -> super.handleMessage(msg)
            }

        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }
}