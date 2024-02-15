package com.example.swipeassignment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageViewLogo: ImageView = findViewById(R.id.imageViewLogo)

        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        imageViewLogo.startAnimation(zoomInAnimation)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
