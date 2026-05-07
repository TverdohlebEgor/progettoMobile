package cohappy.frontend.model


data class Chore (
    val choreCode : String,
    val title : String,
    val description : String,
    val assignedToCode : String,
    val assigneeName : String,
    val isCompleted : Boolean,
    val dayLabel : String
){
}