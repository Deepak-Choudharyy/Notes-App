package dk.deepak.signiin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import dk.deepak.signiin.databinding.ActivityWelcomeScreenBinding

class WelcomeScreen : AppCompatActivity() {
    private lateinit var binding : ActivityWelcomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
       binding= ActivityWelcomeScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()


//        changing the color of "Welcome" word in between
        val welcomeText = "Welcome"
        val spannableString = SpannableString(welcomeText)
        // Set the color of the first 5 characters ("Welcome") to red
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#ff1919")),0,5,0)
        // Set the color of the remaining characters to a darker shade of brown
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#312222")),5,welcomeText.length,0)
        // Set the modified SpannableString to the TextView
        binding.welcomeText.text = spannableString

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },3000)
    }
}