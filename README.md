# WatchApp
## Descrizione del progetto

Questo progetto nasce con l’obiettivo di sviluppare un sistema intelligente, innovativo e non invasivo per il monitoraggio continuo di pazienti affetti da malattie neurodegenerative come Alzheimer, Parkinson o SLA.

Utilizzando uno smartwatch, il sistema raccoglie automaticamente dati vitali e comportamentali, tra cui frequenza cardiaca, sonno, tempo passato all’esterno e minuti di conversazione telefonica. Questi dati vengono trasmessi via **Bluetooth Low Energy (BLE)** a uno smartphone, che funge da gateway verso il **cloud**, dove sono elaborati da algoritmi di intelligenza artificiale.

Il sistema è progettato per:
- Rilevare segnali di deterioramento sociale;
- Notificare il paziente in caso di comportamenti anomali;
- Allertare caregiver in caso di persistenza del problema;
- Visualizzare lo stato clinico in tempo del paziente attraverso una dashboard intuitiva.

Questa soluzione è progettata per garantire:

- Facilità d’uso, anche da parte di utenti anziani o con capacità cognitive ridotte;
- Basso costo di implementazione e scalabilità per l’adozione su larga scala;
- Sicurezza e tutela della privacy, nella gestione dei dati sensibili raccolti.
## Architettura del Sistema

Il sistema è progettato secondo un’architettura a microservizi, scalabile e modulare, che consente la gestione indipendente dei diversi componenti funzionali. L’infrastruttura si basa su servizi backend containerizzati (Docker) e su un'app mobile utilizzata dal paziente.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e789da38-c2d2-4d80-9cba-3dfba9060f5c" alt="Architettura del sistema" width="600"/>
</p>

### Servizi Backend

- **User Service**: è responsabile della gestione degli utenti all'interno del sistema. In particolare, si occupa delle operazioni di registrazione, autenticazione, autorizzazione e aggiornamento dei dati di profilo. È il primo punto di contatto per l’accesso ai servizi dell’applicazione, e garantisce che ogni richiesta sia associata a un utente valido.

- **DataCollector Service**: ha il compito di ricevere, filtrare e memorizzare i dati raccolti dallo smartwatch e dallo smartphone del paziente. I dati includono informazioni biometriche (come la frequenza cardiaca), dati di posizione, durata delle chiamate e dati relativi al sonno. Questo microservizio rappresenta il punto di ingresso dei dati grezzi nel sistema.

- **DataPrediction Service**: è responsabile dell’elaborazione dei dati aggregati relativi allo stato fisico e sociale dell’utente, comprendente sia aspetti comportamentali (quali i minuti trascorsi fuori casa) sia parametri fisici (quali la frequenza cardiaca, la durata del sonno e delle chiamate). L’obiettivo è produrre una valutazione complessiva tramite il modello di intelligenza artificiale, utile per monitorare la socialità e il benessere generale del paziente.
In particolare DataPrediction prende i dati aggregati da DataCollector, li riorganizza e li struttura secondo il formato previsto dal modello, richiamando poi il microservizio Python dedicato all’inferenza. Il risultato dell’elaborazione è una stima del comportamento sociale dell’utente “behaviour” (che può essere buono, normale o cattivo rispettivamente) in base ai dati più recenti raccolti dal sistema.

- **AIModel Service**: è il componente responsabile dell’elaborazione predittiva finale all’interno del sistema. Il suo compito principale consiste nell’analizzare un insieme eterogeneo di dati raccolti quotidianamente per ogni paziente (frequenza cardiaca, durata delle chiamate, durata del sonno, spostamenti) e fornire una valutazione del comportamento sociale dell’utente sotto forma di etichetta predittiva (label).

- **Notification Service**: si occupa della gestione delle notifiche inviate agli assistenti o agli utenti del sistema. Le notifiche possono riguardare, ad esempio, la registrazione dell’utente, la conferma di eventi importanti o l’avviso di situazioni di bassa socialità rilevate dal modello predittivo. Le notifiche vengono salvate nel database e sono consultabili attraverso apposite interfacce o endpoint dedicati.


### Frontend Mobile

- **Applicazione mobile (frontend)**  
Il frontend mobile dell’applicazione è stato sviluppato in React Native ed è progettato per operare su dispositivi Android, in quanto alcune funzionalità fondamentali, come l’accesso al registro delle chiamate, richiedono permessi speciali non disponibili su iOS.
L’applicazione consente all’utente di visualizzare in tempo reale i dati raccolti durante la giornata corrente (frequenza cardiaca, ore totali di sonno, posizione e durata chiamate), oltre a eventuali notifiche o segnalazioni provenienti dai modelli di intelligenza artificiale. 

## Repositori dei componenti

- User Service: [User](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-User-PizzolanteCioffi)
- DataCollector Service: [DataCollector](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-DataCollector-PizzolanteCioffi)
- DataPrediction Service: [DataPrediction](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-DataPrediction-PizzolanteCioffi)
- Notification Service: [Notification](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-Notification-PizzolanteCioffi)
- AIModel Service: [AIModel](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-AIModel-PizzolanteCioffi)
- Frontend: [Frontend](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-Frontend-PizzolanteCioffi)
- Pagina web: [Presentation](https://github.com/UniSalento-IDALab-IoTCourse-2024-2025/wot-project-2024-2025-Presentation-PizzolanteCioffi)

## Notification Service

Il microservizio Notification si occupa della gestione delle notifiche inviate agli assistenti o agli utenti del sistema. Esso mette a disposizione diverse funzionalità:
- la creazione e l’invio delle notifiche;
- la cancellazione di una notifica tramite identificativo univoco;
- la consultazione dell’elenco completo delle notifiche registrate;
- la verifica della presenza di notifiche inviate nei due giorni precedenti per uno specifico paziente.

Notification interagisce con i seguenti componenti del sistema:
- con User, per ottenere informazioni di profilo utili alla personalizzazione dei messaggi e alla gestione del destinatario;
- con DataPrediction, per ricevere il segnale di bassa socialità e generare l’opportuna notifica;
- con il frontend, per consentire la visualizzazione delle notifiche agli utenti autorizzati o agli assistenti.
  
La comunicazione con il frontend e con gli altri microservizi avviene attraverso diversi canali. Le API RESTful permettono la consultazione e la gestione sincrona delle notifiche, garantendo un accesso diretto e immediato alle informazioni da parte dell’interfaccia utente. I messaggi MQTT, veicolati dal message broker RabbitMQ, vengono invece utilizzati per la generazione asincrona di notifiche da parte degli altri moduli interni al sistema, rendendo possibile una comunicazione non bloccante tra i componenti. Inoltre, il microservizio utilizza Firebase Cloud Messaging (FCM) per l’invio push delle notifiche direttamente agli smartphone dei pazienti e dei caregiver, assicurando così una consegna tempestiva anche quando l’applicazione non è attivamente in esecuzione.
