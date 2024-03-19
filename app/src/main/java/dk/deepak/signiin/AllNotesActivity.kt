package dk.deepak.signiin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dk.deepak.signiin.databinding.ActivityAllNotesBinding
import dk.deepak.signiin.databinding.NoteupdatedialogBinding

class AllNotesActivity : AppCompatActivity(),NoteAdapter.OnItemClickListener {

    private val binding:ActivityAllNotesBinding by lazy {
        ActivityAllNotesBinding.inflate(layoutInflater)
    }

    lateinit var databaseReference: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView and set layout manager
        recyclerView = binding.notesrecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)



        /*  Read  */

        // Retrieve current user and fetch notes from Firebase database
        val currentUser = auth.currentUser
        currentUser?.let { user ->
        val noteReference = databaseReference.child("Users").child(user.uid).child("Notes")
            // Add a ValueEventListener to fetch data from the Firebase database
            noteReference.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Initialize a list to hold NoteItem objects
                 val noteList = mutableListOf<NoteItem>()
                    // Iterate through the dataSnapshot to retrieve NoteItems
                    for (noteSnapshot in snapshot.children){
                        val note = noteSnapshot.getValue(NoteItem::class.java)
                        note?.let {
                            noteList.add(it)
                        }
                    }
                    // Reverse the note list to display the newest notes first
                    noteList.reverse()
                    // Initialize and set the adapter for the RecyclerView
                    val adapter = NoteAdapter(noteList,this@AllNotesActivity)
                    recyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error if needed
                }

            })
        }
    }



    /*  Update  */

    // Implementation of the interface method for handling click events on RecyclerView items
    override fun onUpdateClick(noteId: String,currentTitle:String,currentDescription:String) {
        // Inflate the custom dialog layout for updating notes
        val dialogBinding = NoteupdatedialogBinding.inflate(LayoutInflater.from(this))
        // Create an AlertDialog to display the update note dialog
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle("Update Notes")
            .setPositiveButton("Update"){ dialog,_->
                // Retrieve the new title and description from the dialog
                val newTitle = dialogBinding.updateNoteTitle.text.toString()
                val newDescription = dialogBinding.updateNoteDescription.text.toString()
                // Call a function to update the note in the Firebase database
                updateNoteDatabase(noteId,newTitle,newDescription)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
            .create()
        // Pre-fill the dialog fields with the current note data
        dialogBinding.updateNoteTitle.setText(currentTitle)
        dialogBinding.updateNoteDescription.setText(currentDescription)
        // Show the dialog
        dialog.show()

    }
    // Function to update a note in the Firebase database
    private fun updateNoteDatabase(noteId: String, newTitle: String, newDescription: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child("Users").child(user.uid).child("Notes")
            // Create a new NoteItem object with updated data
            val UpdatedNote = NoteItem(newTitle,newDescription,noteId)
            // Update the note in the database
            noteReference.child(noteId).setValue(UpdatedNote)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Note Updated successful", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Failed to Update Note ", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    /*  Delete  */


    // Implementation of the interface method for handling delete events on RecyclerView items
    override fun onDeleteClick(NoteId: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child("Users").child(user.uid).child("Notes")
                noteReference.child(NoteId).removeValue()
            // Remove the note from the database
        }
    }
}