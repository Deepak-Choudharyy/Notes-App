package dk.deepak.signiin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dk.deepak.signiin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.CreateNoteButton.setOnClickListener {
            startActivity(Intent(this,AddNoteActivity::class.java))
        }

        binding.OpenNoteButton.setOnClickListener {
            startActivity(Intent(this,AllNotesActivity::class.java))
        }

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(this,gso).signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}