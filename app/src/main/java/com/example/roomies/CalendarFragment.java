package com.example.roomies;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.MoreObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Color;
import com.google.type.DateTime;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatDayFormatter;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

        Date currentTime = Calendar.getInstance().getTime();
        String mm = (String) DateFormat.format("MM", currentTime);
        String yyyy = (String) DateFormat.format("yyyy", currentTime);

        String date_query_first = "01-"+mm+"-"+yyyy;
        String date_query_last = getUltimoGiornoMese(mm,yyyy)+"-"+mm+"-"+yyyy;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date_q_first = format.parse(date_query_first);
            Date date_q_last = format.parse(date_query_last);
            System.out.println(date_q_first);

            Query first = fStore.collection("case").document(UdCasa).collection("eventi")
                    .whereGreaterThanOrEqualTo("giorno", date_q_first)
                    .whereLessThanOrEqualTo("giorno",date_q_last)
                    .orderBy("giorno", Query.Direction.ASCENDING);

            first.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            eventiClasses = new ArrayList<EventiClass>();
                            dates = new ArrayList<>();

                            Log.d("risultato ",documentSnapshots.getDocuments().size()+"");
                            for(int i = 0; i< documentSnapshots.getDocuments().size();i++) {
                                //Map<String, Object> map = documentSnapshots.getDocuments().get(i).getData();
                                // Log.d("giorno",map.get("giorno").toString());

                                Date date_evento = documentSnapshots.getDocuments().get(i).getTimestamp("giorno").toDate();

                                EventiClass evento = new EventiClass(documentSnapshots.getDocuments().get(i).getId(),date_evento);
                                eventiClasses.add(evento);

                                String dd = (String) DateFormat.format("dd",   date_evento);
                                String MM = (String) DateFormat.format("MM",   date_evento);
                                String yyyy = (String) DateFormat.format("yyyy",   date_evento);

                                //mi aggiunge il puntino al calendario
                                CalendarDay calendarDay =  CalendarDay.from( Integer.parseInt(yyyy),Integer.parseInt(MM),Integer.parseInt(dd));
                                dates.add(calendarDay);
                            }

                            calendarView.addDecorator(new EventDecorator( -65536,dates));
                        }
                    });

        } catch (ParseException e) {
            e.printStackTrace();
        }


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

                        Query first = fStore.collection("case").document(UdCasa).collection("eventi")
                                .whereGreaterThanOrEqualTo("giorno", date_q_first)
                                .whereLessThanOrEqualTo("giorno",date_q_last)
                                .orderBy("giorno", Query.Direction.ASCENDING);

                                first.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                                eventiClasses = new ArrayList<EventiClass>();
                                                dates = new ArrayList<>();

                                                Log.d("risultato ",documentSnapshots.getDocuments().size()+"");
                                                for(int i = 0; i< documentSnapshots.getDocuments().size();i++) {
                                                    //Map<String, Object> map = documentSnapshots.getDocuments().get(i).getData();
                                                    // Log.d("giorno",map.get("giorno").toString());

                                                    Date date_evento = documentSnapshots.getDocuments().get(i).getTimestamp("giorno").toDate();

                                                    EventiClass evento = new EventiClass(documentSnapshots.getDocuments().get(i).getId(),date_evento);
                                                    eventiClasses.add(evento);

                                                    String dd = (String) DateFormat.format("dd",   date_evento);
                                                    String MM = (String) DateFormat.format("MM",   date_evento);
                                                    String yyyy = (String) DateFormat.format("yyyy",   date_evento);

                                                    //mi aggiunge il puntino al calendario
                                                    CalendarDay calendarDay =  CalendarDay.from( Integer.parseInt(yyyy),Integer.parseInt(MM),Integer.parseInt(dd));
                                                    dates.add(calendarDay);
                                                }

                                                calendarView.addDecorator(new EventDecorator( -65536,dates));
                                            }
                                        });

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
                                                Log.d("numero elementi ",""+utentiClasses.size());
                                                Log.d("elemento 0 ",""+utentiClasses.get(0).getNome_cognome());
                                                Log.d("elemento 1 ",""+utentiClasses.get(1).getNome_cognome());
                                            }
                                        }

                                        //da togliere
                                        /*
                                        eventiClasses = new ArrayList<EventiClass>();
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if (entry.getKey().equals("eventi")) {
                                                ArrayList arrayList = (ArrayList) entry.getValue();
                                                for(int i=0; i<arrayList.size(); i++ )
                                                {
                                                    Map<String, Object> map_eventi = (Map<String, Object>) arrayList.get(i);
                                                    Timestamp timestamp = (Timestamp) map_eventi.get("data");
                                                    //DateTime myDateTime = DateTime.parser().parse;
                                                    //Log.d("data",date+"");
                                                    EventiClass evento = new EventiClass(map_eventi.get("evento_id").toString(), map_eventi.get("data").toString());
                                                    eventiClasses.add(evento);
                                                }
                                                Log.d("numero elementi ",""+eventiClasses.size());
                                                Log.d("elemento 0 ",""+eventiClasses.get(0).getIdEvento());
                                            }
                                        }*/

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

}
