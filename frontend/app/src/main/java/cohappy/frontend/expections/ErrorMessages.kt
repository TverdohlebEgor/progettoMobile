package cohappy.frontend.expections

object ErrorMessages {
    const val SERVER_ERROR = "Errore del server, ci scusiamo per il disagio"
    const val WRONG_CREDENTIAL_LOGIN = "Le credenziali fornite non sono corrette"
    const val ALREADY_USED_CREDENTIAL_REGISTRATION = "Le credenziali fornite sono gia connesse ad un account"

    const val USER_NOT_FOUND_CREATE_CHAT= "Uno degli utenti passati come partecipanti non esiste"
    const val USER_NOT_FOUND_GET_CHATS= "Lo userCode passato non è connesso a nessun utente"
    const val USER_NOT_FOUND_PORTFOLIO = "Impossibile recuperare il portfolio: utente non trovato"
    const val CHAT_NOT_FOUND = "La chat richiesta non esiste"
    const val MESSAGE_EMPTY = "Il messaggio non può essere vuoto"
}