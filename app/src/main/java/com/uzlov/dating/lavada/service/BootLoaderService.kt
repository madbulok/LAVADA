package com.uzlov.dating.lavada.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootLoaderService : BroadcastReceiver() {

    override fun onReceive(context: Context?, p1: Intent?) {
        Toast.makeText(context, "restartProcess", Toast.LENGTH_SHORT).show()
        context?.startService(Intent(context, MatchesService::class.java))
        context?.startService(Intent(context, NewMessageService::class.java))

    }
}