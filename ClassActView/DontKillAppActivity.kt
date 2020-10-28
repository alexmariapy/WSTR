package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.writingstar.autotypingandtextexpansion.R
import kotlinx.android.synthetic.main.activity_dont_kill_app.*


class DontKillAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_dont_kill_app)

    }

    override fun onResume() {
        super.onResume()
        doki_content.loadContent()
        doki_content.setButtonsVisibility(true)
        doki_content.setOnCloseListener {
            finish()
        }
    }
}
