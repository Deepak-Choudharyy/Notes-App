package dk.deepak.signiin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dk.deepak.signiin.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    private val binding : ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }
    lateinit var databaseReference: DatabaseReference
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase database reference and authentication object
        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()


        binding.saveNoteButton.setOnClickListener {
            // Get title and description from edit text fields
             val title = binding.etTitle.text.toString()
             val description = binding.etDescription.text.toString()
            // Check if title or description is empty
            if (title.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Fill both field", Toast.LENGTH_SHORT).show()
            }else{
                // Get current user from Firebase authentication
                val currenUser = auth.currentUser
                currenUser?.let { user ->
                    // Generate a unique key for the note
                    val NoteKey = databaseReference.child("Users").child(user.uid).child("Notes").push().key

                    val noteItem = NoteItem(title, description,NoteKey?:"")
                    // Check if note key is not null
                    if(NoteKey!=null){
                        // Save the note item to Firebase database
                        databaseReference.child("Users").child(user.uid).child("Notes").child(NoteKey).setValue(noteItem)
                            .addOnCompleteListener{task ->
                                // Check if note is saved successfully
                                if (task.isSuccessful){
                                    Toast.makeText(this, "Note Save successful", Toast.LENGTH_SHORT).show()
                                    finish()
                                }else{
                                    Toast.makeText(this, "Failed to Save note", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

            }
        }
    }
}