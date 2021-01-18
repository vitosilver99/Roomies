package com.example.roomies.calendario;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomies.MainActivity;
import com.example.roomies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.joda.time.DateTimeComparator;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MainActivity mainActivity;
    //FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UdCasa;
    DocumentSnapshot DocumentoCasa;
    ArrayList<UtentiClass> utentiClasses;
    ArrayList<MansioniClass> mansioniClasses;
    ArrayList<EventiClass> eventiClasses;
    ArrayList<CalendarDay> dates;
    MaterialCalendarView calendarView;
    List<EventiClass> eventiListGiornoSelezionato;
    RecyclerView recyclerView;
    RecyclerviewAdapterVisualizzaEvento recyclerviewAdapterVisualizzaEvento;

    ImageView imageView_avvio;
    TextView textView_emptyEventi;
    TextView scritta_iniziale;


    public CalendarFragment(String UdCasa) {
        this.UdCasa = UdCasa;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fStore = FirebaseFirestore.getInstance();

        //Log.d("Numero_utenti",""+documentSnapshot.get("numero_utenti").toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_calendar, container, false);
        //Toast.makeText(view.getContext(),"Mi trovo nel fragment"+ fAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();

        Log.d("Casa id: ",""+UdCasa);

        calendarView = view.findViewById(R.id.calendar_event);
        imageView_avvio = view.findViewById(R.id.empty_calendario);
        textView_emptyEventi = view.findViewById(R.id.empty_text_calendario);
        scritta_iniziale = view.findViewById(R.id.text_open_calendar);

        aggiornaRecyclerView(view);

        //evento click su un giorno del calendario
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //visualizzazione eventi nella recyclerView
                scritta_iniziale.setVisibility(View.INVISIBLE);
                imageView_avvio.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                String date_selezionata = date.getDay()+"-"+date.getMonth()+"-"+date.getYear();

                DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
                //Log.d("struttura data", eventiClasses.get(0).getData()+"");

                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    Date d_selezionata = format.parse(date_selezionata);
                    Log.d("num", eventiClasses.size()+"");
                    int check =0;
                    eventiListGiornoSelezionato = new ArrayList<>();
                    for( int i = 0;i<eventiClasses.size();i++) {
                        //Log.d("struttura data", eventiClasses.get(i).getData()+"");
                        if(dateTimeComparator.compare(d_selezionata, eventiClasses.get(i).getData())==0){
                            Log.d("sono dentro l'if", "sono dentro");
                            //EventiRecyclerView eventiRecyclerView = new EventiRecyclerView(eventiClasses.get(i).getIdEvento(),eventiClasses.get(i).getData().toString());
                            eventiListGiornoSelezionato.add(eventiClasses.get(i));
                            check= 1;
                        }
                    }


                    if(eventiListGiornoSelezionato.size()==0){
                        imageView_avvio.setVisibility(View.VISIBLE);
                        textView_emptyEventi.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else{
                        recyclerviewAdapterVisualizzaEvento.setEventiRecyclerViewList(eventiListGiornoSelezionato);
                        recyclerView.setAdapter(recyclerviewAdapterVisualizzaEvento);
                        recyclerView.setVisibility(View.VISIBLE);
                        imageView_avvio.setVisibility(View.INVISIBLE);
                        textView_emptyEventi.setVisibility(View.INVISIBLE);
                    }



                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //click sul singolo evento
                RecyclerTouchListener touchListener = new RecyclerTouchListener(getActivity(),recyclerView);
                touchListener.setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Toast.makeText(view.getContext(),eventiListGiornoSelezionato.get(position).getNome(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                        .setSwipeOptionViews(R.id.delete_task,R.id.edit_task)
                        .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                            @Override
                            public void onSwipeOptionClicked(int viewID, int position) {
                                switch (viewID){
                                    case R.id.delete_task:

                                        //query per eliminare l'evento che l'utente vuole eliminare
                                        fStore.collection("case").document(UdCasa).collection("eventi").document(eventiListGiornoSelezionato.get(position).getIdEvento())
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Date data_selezionata = eventiListGiornoSelezionato.get(position).getData();

                                                //rimuovo l'evento che l'utente vuole eliminare e in più controllo se in quel giorno ci sono altri eventi
                                                //se non ci sono tolgo il pallino rosso dal calendario e faccio uscire l'immagine con il testo al posto della recyclerview

                                                int num_giorni=0;
                                                for(int i=0;i<eventiClasses.size();i++){
                                                    if(eventiClasses.get(i).getIdEvento().equals(eventiListGiornoSelezionato.get(position).getIdEvento())){
                                                        eventiClasses.remove(i);
                                                        //Log.d("numero degli eventi",""+eventiClasses.size());
                                                        synchronized(recyclerView){
                                                            recyclerView.notify();
                                                        }
                                                    }
                                                    else{
                                                        if(eventiClasses.get(i).getData().equals(data_selezionata)){
                                                            num_giorni++;
                                                        }
                                                    }
                                                }
                                                //Log.d("num_giorni",""+num_giorni);

                                                String day = (String) DateFormat.format("dd",data_selezionata);
                                                String month = (String) DateFormat.format("MM",data_selezionata);
                                                String year = (String) DateFormat.format("yyyy",data_selezionata);

                                                CalendarDay calendarDay =  CalendarDay.from( Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));

                                                if(num_giorni==0){
                                                    for (int i = 0;i<dates.size();i++){
                                                        if(dates.get(i).equals(calendarDay)){
                                                            //Log.d("calendar day","dates"+dates.size());
                                                            dates.remove(i);
                                                            //Log.d("calendar day","dates"+dates.size());
                                                        }
                                                    }
                                                    calendarView.removeDecorators();
                                                    calendarView.addDecorator(new EventDecorator( -65536,dates));

                                                    //TODO aggiungere l'immagine con il testo se non ci sono più eventi da visualizzare
                                                }
                                                eventiListGiornoSelezionato.remove(position);
                                                recyclerviewAdapterVisualizzaEvento.setEventiRecyclerViewList(eventiListGiornoSelezionato);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG);
                                            }
                                        });
                                        break;

                                    case R.id.edit_task:
                                        Task<DocumentSnapshot> documentSnapshot =  fStore.collection("case").document(UdCasa).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Map<String, Object> map = documentSnapshot.getData();
                                                        utentiClasses= new ArrayList<>();

                                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                            if (entry.getKey().equals("utenti")) {
                                                                ArrayList arrayList = (ArrayList) entry.getValue();
                                                                for(int i=0; i<arrayList.size(); i++ ) {
                                                                    Map<String, Object> map_utenti = (Map<String, Object>) arrayList.get(i);
                                                                    UtentiClass utenti = new UtentiClass(map_utenti.get("nome_cognome").toString(), map_utenti.get("user_id").toString());
                                                                    utentiClasses.add(utenti);
                                                                }
                                                            }
                                                        }

                                                        PopUpModificaEvento popUpClass = new PopUpModificaEvento(utentiClasses,UdCasa,eventiListGiornoSelezionato.get(position),CalendarFragment.this, view);
                                                        popUpClass.showPopupWindow(view);
                                                        Toast.makeText(view.getContext(),""+eventiListGiornoSelezionato.get(position).getNome(),Toast.LENGTH_SHORT).show();


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                        break;

                                }
                            }
                        });
                recyclerView.addOnItemTouchListener(touchListener);

            }
        });


        //evento che si verifica quando cambio mese, quando viene eseguito aggiorna il calendario con i loro relativi eventi della casa facendo uscire un puntino
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                // Do something here
                Log.d("mese","01-"+date.getMonth()+"-"+date.getYear());

                String date_query_first = "01-"+date.getMonth()+"-"+date.getYear();
                String date_query_last = getUltimoGiornoMese(date)+"-"+date.getMonth()+"-"+date.getYear();

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                try {
                        Date date_q_first = format.parse(date_query_first);
                        Date date_q_last = format.parse(date_query_last);
                        System.out.println(date_q_first);

                        queryCalendario(date_q_first,date_q_last);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        //Log.d("Numero utenti: ",""+mansioniClasses.getNome());

        FloatingActionButton fab =  view.findViewById(R.id.add_event_floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Prendo il documento associato alla casa inerente all'utente loggato e creo la lista utenti e mansioni
                Task<DocumentSnapshot> documentSnapshot =  fStore.collection("case").document(UdCasa).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        DocumentoCasa = document;
                                        Log.d("Numero casa  : ",DocumentoCasa.get("numero_utenti").toString());

                                        Map<String, Object> map = DocumentoCasa.getData();
                                        Log.d("mappa documento",map+"");
                                        mansioniClasses = new ArrayList<MansioniClass>();

                                        //creo la lista delle mansioni
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if (entry.getKey().equals("lista_mansioni")) {
                                                ArrayList arrayList = (ArrayList) entry.getValue();
                                                for(int i=0; i<arrayList.size(); i++ )
                                                {
                                                    Log.d("numero mansioni ", arrayList.get(i).toString()+"");
                                                    MansioniClass utenti = new MansioniClass(arrayList.get(i).toString());
                                                    mansioniClasses.add(utenti);
                                                }
                                                Log.d("numero mansioni ", mansioniClasses.size()+"");

                                        /*
                                        mansioniClasses = (ArrayList<MansioniClass>) entry.getValue();
                                        Log.d("TAG", entry.getValue().toString());
                                        Log.d("TAG", "Numero: "+mansioniClasses.size() + " Primo elemento: "+mansioniClasses.get(0));*/
                                            }
                                        }

                                        utentiClasses = new ArrayList<UtentiClass>();

                                        //creo la lista degli utenti
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if (entry.getKey().equals("utenti")) {
                                                ArrayList arrayList = (ArrayList) entry.getValue();
                                                for(int i=0; i<arrayList.size(); i++ )
                                                {
                                                    Map<String, Object> map_utenti = (Map<String, Object>) arrayList.get(i);
                                                    UtentiClass utenti = new UtentiClass(map_utenti.get("nome_cognome").toString(),map_utenti.get("user_id").toString());
                                                    utentiClasses.add(utenti);
                                                }
                                                //Log.d("numero elementi ",""+utentiClasses.size());
                                                //Log.d("elemento 0 ",""+utentiClasses.get(0).getNome_cognome());
                                                //Log.d("elemento 1 ",""+utentiClasses.get(1).getNome_cognome());
                                            }
                                        }

                                        PopUpClass popUpClass = new PopUpClass(utentiClasses,mansioniClasses,UdCasa);
                                        popUpClass.showPopupWindow(view);

                                    } else {
                                        Toast.makeText(view.getContext(),"Errore di connessione", Toast.LENGTH_LONG);
                                    }
                                } else {
                                    Log.d(TAG,"get failed with ", task.getException());
                                }
                            }
                        });

            }
        });

        return view;

    }

    //mi restituisce l'ultimo giorno del mese controllando se l'anno è bisestile o meno
    public int getUltimoGiornoMese(CalendarDay calendarDay) {
        int mese = calendarDay.getMonth();
        int anno = calendarDay.getYear();
        int numDays = 01;
        //boolean isLeapYear = ((anno % 4 == 0) && (anno % 100 != 0) || (anno % 400 == 0));

        switch (mese) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
                if (((anno % 4 == 0) &&
                        !(anno % 100 == 0))
                        || (anno % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                System.out.println("Invalid month.");
                break;
        }

        return numDays;
    }

    public int getUltimoGiornoMese(String mese, String anno) {
        int m = Integer.parseInt(mese);
        int yyyy = Integer.parseInt(anno);
        int numDays = 01;
        //boolean isLeapYear = ((anno % 4 == 0) && (anno % 100 != 0) || (anno % 400 == 0));

        switch (m) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
                if (((yyyy % 4 == 0) &&
                        !(yyyy % 100 == 0))
                        || (yyyy % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                System.out.println("Invalid month.");
                break;
        }

        return numDays;
    }

    public void queryCalendario(Date date_q_first , Date date_q_last ) {

        Query first = fStore.collection("case").document(UdCasa).collection("eventi")
                .whereGreaterThanOrEqualTo("giorno", date_q_first)
                .whereLessThanOrEqualTo("giorno",date_q_last)
                .orderBy("giorno", Query.Direction.ASCENDING);

        first.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException error) {
                eventiClasses = new ArrayList<EventiClass>();
                dates = new ArrayList<>();

                Log.d("risultato ",documentSnapshots.getDocuments().size()+"");
                for(int i = 0; i< documentSnapshots.getDocuments().size();i++) {
                    // Log.d("giorno",map.get("giorno").toString());

                    Date date_evento = documentSnapshots.getDocuments().get(i).getTimestamp("giorno").toDate();
                    //Log.d("daat_evento_query",""+date_evento.toString());
                    String dd = (String) DateFormat.format("dd",   date_evento);
                    String MM = (String) DateFormat.format("MM",   date_evento);
                    String yyyy = (String) DateFormat.format("yyyy",   date_evento);

                    Map<String, Object> map = documentSnapshots.getDocuments().get(i).getData();
                    //Log.d("mappa",""+map.toString());
                    ArrayList map_utenti = (ArrayList) documentSnapshots.getDocuments().get(i).get("coinquilini");

                    List<UtentiClass> utenti_partecipanti = new ArrayList<>();

                    for(int j = 0; j<map_utenti.size(); j++) {
                        Map<String, Object> app = (Map<String, Object>) map_utenti.get(j);
                        UtentiClass utente = new UtentiClass(app.get("nome_cognome").toString(),app.get("userId").toString());
                        utenti_partecipanti.add(utente);
                    }

                    EventiClass evento = new EventiClass(documentSnapshots.getDocuments().get(i).getId(),map.get("nome").toString(),date_evento,map.get("descrizione").toString(),utenti_partecipanti);
                    eventiClasses.add(evento);

                    //mi aggiunge il puntino al calendario
                    CalendarDay calendarDay =  CalendarDay.from( Integer.parseInt(yyyy),Integer.parseInt(MM),Integer.parseInt(dd));
                    dates.add(calendarDay);
                }
                //fa uscire i pallini rossi ai giorni
                calendarView.removeDecorators();
                calendarView.addDecorator(new EventDecorator( Color.parseColor("black"),dates));

                //-65536 colore rosso che stava prima
            }
        });

        /*
        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        eventiClasses = new ArrayList<EventiClass>();
                        dates = new ArrayList<>();

                        Log.d("risultato ",documentSnapshots.getDocuments().size()+"");
                        for(int i = 0; i< documentSnapshots.getDocuments().size();i++) {
                            // Log.d("giorno",map.get("giorno").toString());

                            Date date_evento = documentSnapshots.getDocuments().get(i).getTimestamp("giorno").toDate();
                            //Log.d("daat_evento_query",""+date_evento.toString());
                            String dd = (String) DateFormat.format("dd",   date_evento);
                            String MM = (String) DateFormat.format("MM",   date_evento);
                            String yyyy = (String) DateFormat.format("yyyy",   date_evento);

                            Map<String, Object> map = documentSnapshots.getDocuments().get(i).getData();
                            //Log.d("mappa",""+map.toString());
                            ArrayList map_utenti = (ArrayList) documentSnapshots.getDocuments().get(i).get("coinquilini");

                            List<UtentiClass> utenti_partecipanti = new ArrayList<>();

                            for(int j = 0; j<map_utenti.size(); j++) {
                                Map<String, Object> app = (Map<String, Object>) map_utenti.get(j);
                                UtentiClass utente = new UtentiClass(app.get("nome_cognome").toString(),app.get("userId").toString());
                                utenti_partecipanti.add(utente);
                            }

                            EventiClass evento = new EventiClass(documentSnapshots.getDocuments().get(i).getId(),map.get("nome").toString(),date_evento,map.get("descrizione").toString(),utenti_partecipanti);
                            eventiClasses.add(evento);

                            //mi aggiunge il puntino al calendario
                            CalendarDay calendarDay =  CalendarDay.from( Integer.parseInt(yyyy),Integer.parseInt(MM),Integer.parseInt(dd));
                            dates.add(calendarDay);
                        }
                        //fa uscire i pallini rossi ai giorni
                        calendarView.removeDecorators();
                        calendarView.addDecorator(new EventDecorator( Color.parseColor("black"),dates));
                        //-65536 colore rosso che stava prima
                    }
                });*/
    }

    public void aggiornaRecyclerView(View view) {
        Date currentTime = Calendar.getInstance().getTime();
        String mm = (String) DateFormat.format("MM", currentTime);
        String yyyy = (String) DateFormat.format("yyyy", currentTime);

        String date_query_first = "01-"+mm+"-"+yyyy;
        String date_query_last = getUltimoGiornoMese(mm,yyyy)+"-"+mm+"-"+yyyy;

        //aggiorno il mese che visualizzo quando apro il fragment calendario
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date_q_first = format.parse(date_query_first);
            Date date_q_last = format.parse(date_query_last);
            System.out.println(date_q_first);

            queryCalendario(date_q_first,date_q_last);

            //calendarView.setDateSelected(CalendarDay.today(),true);
            calendarView.setSelectionColor(Color.parseColor("#F4A261"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        imageView_avvio.setVisibility(View.VISIBLE);
        scritta_iniziale.setVisibility(View.VISIBLE);


        recyclerView = view.findViewById(R.id.recyclerview_calendar_eventi);
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerviewAdapterVisualizzaEvento = new RecyclerviewAdapterVisualizzaEvento(view.getContext());
    }

}
