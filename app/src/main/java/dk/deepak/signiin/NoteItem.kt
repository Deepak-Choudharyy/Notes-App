package dk.deepak.signiin

data class NoteItem(val title: String,val description: String,val noteId:String){
    constructor(): this("","","")
}
