###### **TODO: Portfolio (Gestione Spese)**



Implementazioni Mancanti:



Attualmente la logica di base del ViewModel per il caricamento e l'inserimento dei debiti è presente, ma mancano due funzionalità chiave per completare l'esperienza utente nella sezione Spese.



***1. Pagamento di un Debito***



È necessario implementare la logica per permettere a un utente di segnare un debito come "pagato".



Azione Utente: L'utente dovrebbe avere la possibilità (tramite un pulsante o uno swipe sull'elemento della lista) di confermare l'avvenuto pagamento.



Aggiornamento UI: A seguito del successo della chiamata API, la lista delle transazioni locali e i saldi (Totale Da Saldare / Da Ricevere) dovranno aggiornarsi automaticamente.



***2. Visualizzazione Dettaglio Pagamenti***



La lista attuale mostra le transazioni raggruppate, ma non fornisce dettagli specifici su "chi ha pagato chi" in un contesto multi-utente.



Espansione Elemento: È necessario rendere cliccabile l'elemento della spesa (TransactionItem) nella lista. Al click, l'elemento dovrebbe espandersi (es. usando un AnimatedVisibility di Compose).



Dettagli da Mostrare: Nell'area espansa, deve essere visibile un resoconto chiaro dello stato della spesa condivisa, indicando quali coinquilini hanno già saldato la loro quota e chi, invece, deve ancora pagare.



Integrazione Dati: Verificare che il DTO restituito dall'API (o la logica di aggregazione nel ViewModel) fornisca le informazioni necessarie per popolare questa sezione espansa con i nomi degli utenti e lo stato di pagamento relativo alla specifica transazione.

