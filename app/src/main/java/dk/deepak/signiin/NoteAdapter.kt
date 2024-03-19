package dk.deepak.signiin

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import dk.deepak.signiin.databinding.EachitemBinding

class NoteAdapter(private val notes: List<NoteItem>,private val itemClickListener:OnItemClickListener) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    interface OnItemClickListener{
        fun onUpdateClick(noteId:String,title:String,description:String)
        fun onDeleteClick(noteId:String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = EachitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)

    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.binding.updateButton.setOnClickListener {
            itemClickListener.onUpdateClick(note.noteId,note.title,note.description)
        }
        holder.binding.deleteButton.setOnClickListener {
            itemClickListener.onDeleteClick(note.noteId)
        }

    }

    override fun getItemCount(): Int {
       return notes.size
    }

    class NoteViewHolder(val binding: EachitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteItem) {
            binding.titleTextView.text = note.title
            binding.descriptionTextView.text = note.description

        }

    }
}