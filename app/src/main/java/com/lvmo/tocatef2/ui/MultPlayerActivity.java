package com.lvmo.tocatef2.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lvmo.tocatef2.model.Jugada;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.aplicacion.Constantes;
import com.lvmo.tocatef2.ui.Raking.RankingActivity;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class MultPlayerActivity extends AppCompatActivity {

    static long millisecondsleft;
    static long millisecondsleftDial;
    CountDownTimer countDownTimer,countDownTimerDial;
    private TextView tvnombreJuno, tvnombreJdos, tvAciertos,tvAciertos2,tvOrden, tvOrdenfija,etCuentaReg;
    private ImageView IvjUno, IvjDos;
    private Random aleatorio;
    private FirebaseAuth fireBaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private ListenerRegistration listenerJugada =null;
    private Jugada jugada;
    private User userj, userjUno,userjDos;
    private String uid,jugadaId, nombreJuno="", nombreJdos="";

    private int n,cont1=0,cont2=0;

    private int  anchoPant, altoPant;
    private StorageReference mStorageRef;

    private ConstraintLayout layoutCargando;
    private ProgressBar pbCargando;
    TextView tvCargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_player);

        layoutCargando=findViewById(R.id.layoutCargando);
        pbCargando=findViewById(R.id.progressBarCargando);
        tvCargando=findViewById(R.id.textViewCargando);
        pbCargando.setIndeterminate(true);
        tvCargando.setText(R.string.StvCargando1);
        layoutCargando.setVisibility(View.VISIBLE);

        tvnombreJuno = findViewById(R.id.NomJuno);
        tvnombreJdos = findViewById(R.id.NomJdos);
        tvAciertos= findViewById(R.id.TvAciertos);
        tvAciertos2= findViewById(R.id.TvAciertos2);
        etCuentaReg= findViewById(R.id.TvCuentaReg);
        tvOrden= findViewById(R.id.tvTocate);
        tvOrdenfija=findViewById(R.id.tvTocatefija);
        IvjUno = findViewById(R.id.imageJuno);
        IvjDos = findViewById(R.id.imageJdos);

        tvnombreJuno.setText(String.format(getString(R.string.Njuno), nombreJuno));
        tvnombreJdos.setText(String.format(getString(R.string.Njdos), nombreJdos));

        fireBaseAuth =fireBaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        firebaseUser=fireBaseAuth.getCurrentUser();
        Intent intent = getIntent();
        jugadaId = intent.getStringExtra(Constantes.EXTRA_JUGADA_ID);
        uid=firebaseUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();



        initPantalla();// puede que o sirva esta configurado para api29
        moverImangen(IvjDos,IvjUno);
        moverImangen(IvjUno,IvjDos);

        eventoClic();

    }

    private void eventoClic() {

        IvjUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
                switch (n){
                    case 0:
                        if(jugada.getJugadorUnoID().equals(uid)) {
                            cont2=jugada.getPuntosjugadorDosID();
                            cont1++;
                        }
                        break;
                    case 1:
                        if(jugada.getJugadorDosID().equals(uid) ) {
                            cont1=jugada.getPuntosjugadorUnoID();
                            cont2++;
                        }
                        else{cont2=jugada.getPuntosjugadorDosID();
                            cont1--;}
                        break;
                    case 2:
                        if(jugada.getJugadorUnoID().equals(uid)) {
                            cont2=jugada.getPuntosjugadorDosID();
                            cont1++;
                        }
                        else{
                            cont1=jugada.getPuntosjugadorUnoID();
                            cont2--;
                        }
                        break;
                }
                actualizarDBjugada();
                EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
            }
        });

        IvjDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
                switch (n){
                    case 0:
                        if(jugada.getJugadorDosID().equals(uid)) {
                            cont1=jugada.getPuntosjugadorUnoID();
                            cont2++;
                        }
                        break;
                    case 1:
                        if(jugada.getJugadorUnoID().equals(uid)) {
                            cont2=jugada.getPuntosjugadorDosID();
                            cont1++;
                        }
                        else{
                            cont1=jugada.getPuntosjugadorUnoID();
                            if(cont2>0) cont2--;
                        }
                        break;
                    case 2:
                        if(jugada.getJugadorDosID().equals(uid) ) {
                            cont1=jugada.getPuntosjugadorUnoID();
                            cont2++;
                        }
                        else{cont2=jugada.getPuntosjugadorDosID();
                            if(cont1>0) cont1--;}
                        break;
                }
                actualizarDBjugada();
                EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
            }
        });

    }

    /*------- Actilizar - Recibir jugada  -----------*/
    private void jugadaListener() {
        listenerJugada =db.collection("jugadas")
                .document(jugadaId)
                .addSnapshotListener(MultPlayerActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot Snapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(MultPlayerActivity.this,getString(R.string.errorOptenerJugada),Toast.LENGTH_LONG).show();
                            return;
                        }
                        String soruce =Snapshot != null
                                && Snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
                        if(Snapshot.exists() && soruce.equals("Server")){
                            //Parsenado documentSnapshot> a jagada
                            jugada =Snapshot.toObject(Jugada.class);
                            if(nombreJuno.isEmpty() || nombreJdos.isEmpty()){
                               getnombresJugadores();
                                n=jugada.getTocate();
                                EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);

                                countDownTimer= new CountDownTimer(65000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        millisecondsleft = millisUntilFinished;
                                        etCuentaReg.setText(":" + String.format("%02d", millisUntilFinished / 1000));
                                    }
                                    @Override
                                    public void onFinish() {
                                        funcionGameover();
                                        dialogo_Gameover(true);
                                    }
                                }.start();
                            }
                            recibirDBjugada();
                        }
                    }
                });

    }
    private void actualizarDBuser() {
        User jugadorActilizar;
        if(jugada.getJugadorUnoID().equals(uid)) {
            userjUno.setPoints(userjUno.getPoints() + jugada.getPuntosjugadorUnoID());
            userjUno.setGameDone(userjUno.getGameDone() + 1);
            userjUno.setOnline("on");
            jugadorActilizar=userjUno;
        }else{
            userjDos.setPoints(userjDos.getPoints() +   jugada.getPuntosjugadorDosID());
            userjDos.setGameDone(userjDos.getGameDone() + 1);
            userjDos.setOnline("on");
            jugadorActilizar=userjDos;
        }
        db.collection("users")
                .document(uid)
                .set(jugadorActilizar)
                .addOnSuccessListener(MultPlayerActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(MultPlayerActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void actualizarDBjugada() {
        tvAciertos.setText( String.format(getString(R.string.Dosp), cont1));
        tvAciertos2.setText(String.format(getString(R.string.Dosp), cont2));
        jugada.setPuntosjugadorUnoID(cont1);
        jugada.setPuntosjugadorDosID(cont2);
        n=new Random().nextInt(3);
        jugada.setTocate(n);
        db.collection("jugadas")
                .document(jugadaId)
                .set(jugada);
        EtTocate(n,jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
        moverImangen(IvjDos,IvjUno);
        moverImangen(IvjUno,IvjDos);
    }
    private void recibirDBjugada() {
        if(jugada.getAbandonoID().equals("")){
            tvAciertos.setText(String.format(getString(R.string.Dosp),jugada.getPuntosjugadorUnoID()));
            tvAciertos2.setText( String.format(getString(R.string.Dosp),jugada.getPuntosjugadorDosID()));
            EtTocate( jugada.getTocate(),jugada.getJugadorUnoID(),jugada.getJugadorDosID(),uid);
            moverImangen(IvjDos,IvjUno);
            moverImangen(IvjUno,IvjDos);
        }
        else{
            countDownTimer.cancel();
            dialogo_Gameover(false);}
    }
    /*------- Actilizar - Recibir jugada  -----------*/

    /*-------- descarga foto y  optener usuario  -----------*/
    private void getnombresJugadores() {
        db.collection("users")
                .document(jugada.getJugadorUnoID())
                .get()
                .addOnSuccessListener(MultPlayerActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userjUno=documentSnapshot.toObject(User.class);
                        nombreJuno=documentSnapshot.get("name").toString();
                        if(nombreJuno.length()>10){
                            nombreJuno = nombreJuno.substring(0,10); }
                        tvnombreJuno.setText(nombreJuno);

                        StorageReference filepath = mStorageRef.child(String.format(getString(R.string.SmStorageRef),jugada.getJugadorUnoID()));

                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Picasso.get()
                                            .load(downloadUrl)
                                            .resize(100, 100)
                                            .error(R.drawable.tocate)
                                            .into(IvjUno);
                                }
                            }
                        }).addOnFailureListener(MultPlayerActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MultPlayerActivity.this, getString(R.string.necesitasFoto), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });



        db.collection("users")
                .document(jugada.getJugadorDosID())
                .get()
                .addOnSuccessListener(MultPlayerActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userjDos=documentSnapshot.toObject(User.class);
                        nombreJdos=documentSnapshot.get("name").toString();
                        if(nombreJdos.length()>10){
                            nombreJdos = nombreJdos.substring(0,10);}
                        tvnombreJdos.setText(nombreJdos);

                        StorageReference filepath = mStorageRef.child(String.format(getString(R.string.SmStorageRef),jugada.getJugadorDosID()));

                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Picasso.get()
                                            .load(downloadUrl)
                                            .resize(100, 100)
                                            .error(R.drawable.tocate)
                                            .into(IvjDos);
                                    layoutCargando.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(MultPlayerActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MultPlayerActivity.this, getString(R.string.necesitasFoto), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

    }
    /*-------- descarga foto y  optener usuario  -----------*/

    /*--------Mover imagen cambinar etiqueta -----------*/
    private void EtTocate(int varTocate, String UidjugadorUno, String Uidjugadordos, String Uidtel) {
        switch (varTocate) {
            case 0:
                tvOrden.setText(getString(R.string.SetTocate));
                tvOrdenfija.setText(getString(R.string.SetTocate));
                break;
            case 1:
                if (UidjugadorUno.equals(Uidtel)) {
                    tvOrden.setText(String.format(getString(R.string.Toca), nombreJdos));
                    tvOrdenfija.setText(String.format(getString(R.string.Toca), nombreJdos));
                } else {
                    tvOrden.setText(getString(R.string.SetTocateAti));
                    tvOrdenfija.setText(getString(R.string.SetTocateAti));
                }
                break;
            case 2:
                if (Uidjugadordos.equals(Uidtel)) {
                    tvOrden.setText(String.format(getString(R.string.Toca),  nombreJuno));
                    tvOrdenfija.setText(String.format(getString(R.string.Toca),  nombreJuno));
                } else {
                    tvOrden.setText(R.string.SetTocateAti);
                    tvOrdenfija.setText(R.string.SetTocateAti);
                }
                break;
        }}
    private void moverImangen(ImageView imagen0, ImageView imagen1) {
        int ranX, ranY, rangoXmin, rangoYmin, rangoXmax, rangoYmax;

        int[] location = new int[2];
        imagen0.getLocationOnScreen(location);

        rangoXmin = location[0] - imagen1.getWidth();
        rangoXmax = location[0] + imagen0.getWidth();
        int stop;
        do {
            ranX = funcionAleatorio(0, anchoPant - imagen0.getWidth());
            if (ranX < rangoXmin) stop = 1;
            else {
                if (ranX >= rangoXmax) stop = 1;
                else stop = 0;
            }
        } while (stop == 0);
        imagen1.setX(ranX);

        rangoYmin = location[1] - imagen1.getHeight();
        rangoYmax = location[1] + imagen0.getHeight();

        do {
            ranY = funcionAleatorio(0, altoPant - 2*imagen0.getHeight());
            if (ranY < rangoYmin) stop = 1;
            else {
                if (ranY >= rangoYmax) stop = 1;
                else stop = 0;
            }
        } while (stop == 0);
        imagen1.setY(ranY);
        switch (new Random().nextInt(2)) {
            case 0:
                tvOrden.setX(location[0]);
                int aa=location[1] +(imagen0.getHeight()/2);
                tvOrden.setY(aa);
                break;
            case 1:
                tvOrden.setX(ranX);
                int bb=ranY +(imagen0.getHeight()*(4/3));
                tvOrden.setY(bb);
                break;
        }
    }
    private int funcionAleatorio(int min, int maximo) {
        return (aleatorio.nextInt(((maximo - min) + 1) + min));
    }
    private void initPantalla() {
        aleatorio = new Random();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        anchoPant = metrics.widthPixels; // ancho absoluto en pixels
        altoPant = metrics.heightPixels; // alto absoluto en pixels
    }
    /*--------Mover imagen - cambiar etiqueta -----------*/

    /*--------Game over funciones -----------*/
    private void funcionGameover() {
        if(jugada.getPuntosjugadorUnoID()>jugada.getPuntosjugadorDosID()){
            jugada.setGanadorID(jugada.getJugadorUnoID());
        }else  {
            jugada.setGanadorID(jugada.getJugadorDosID());
        }
        db.collection("jugadas")
                .document(jugadaId)
                .set(jugada);
        actualizarDBuser();
    }
    private void dialogo_Gameover(boolean Salirjue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultPlayerActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_gameover, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        ImageView imagen=view.findViewById(R.id.ivEntrar);
        TextView tvTitulo = view.findViewById(R.id.tvMensaje);
        TextView tvGanador = view.findViewById(R.id.tvGanador);

        if (jugada.getPuntosjugadorUnoID() > jugada.getPuntosjugadorDosID()) {
            if (jugada.getJugadorUnoID().equals(uid)) {
                tvGanador.setText(String.format(getString(R.string.Ganaste), nombreJuno));
            } else {
                tvGanador.setText(String.format(getString(R.string.Perdiste), nombreJdos));
                imagen.setImageResource(R.drawable.tocate);
            }
        } else {
            if (jugada.getJugadorDosID().equals(uid)) {
                tvGanador.setText(String.format(getString(R.string.Ganaste), nombreJdos));
            } else {
                tvGanador.setText(String.format(getString(R.string.Perdiste), nombreJuno));
                imagen.setImageResource(R.drawable.tocate);
            }
        }

        if (!Salirjue) {
            if (jugada.getJugadorUnoID().equals(uid)) {
                tvTitulo.setText(getString(R.string.abandonopartida, nombreJdos));
            } else {
                tvTitulo.setText(getString(R.string.abandonopartida, nombreJuno));
            }

        } else {
            tvTitulo.setText(R.string.SetInicio2);
        }
        TextView tvPuntos = view.findViewById(R.id.tvPuntos);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.risamujer);
        mp.start();
        etCuentaReg.setText(R.string.SetCuentaReg);

        if (jugada.getJugadorUnoID().equals(uid)) {
            tvPuntos.setText(String.format(getString(R.string.tusaciertos), cont1));
        } else {
            tvPuntos.setText(String.format(getString(R.string.tusaciertos), cont2));
        }
        Button reinicio = view.findViewById(R.id.Reiniciar);
        reinicio.setText(getString(R.string.Regresar));
        reinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.Regresar), Toast.LENGTH_LONG).show();
                Intent i = new Intent(MultPlayerActivity.this, TransisionActivity.class);
                startActivity(i);
                finish();
                dialog.dismiss();
                countDownTimerDial.cancel();
            }
        });
        final Button ranking = view.findViewById(R.id.verRankin);
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.VamosRan, Toast.LENGTH_LONG).show();
                Intent i = new Intent(MultPlayerActivity.this, RankingActivity.class);
                startActivity(i);
                dialog.dismiss();
                countDownTimerDial.cancel();
                finish();
            }
        });
        countDownTimerDial = new CountDownTimer(18000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisecondsleftDial = millisUntilFinished;
                ranking.setText("Ranking (" + String.format("%02d", millisUntilFinished / 1000) + ")");
            }
            @Override
            public void onFinish() {
                Intent i = new Intent(MultPlayerActivity.this, RankingActivity.class);
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        }.start();

    }
    /*--------Game over funciones -----------*/
    /*-------- ciclo de vida Activity -----------*/
    @Override
    protected void onStart() {
        super.onStart();
        jugadaListener();
    }
    protected void onStop() {
        super.onStop();

        if(listenerJugada!=null){
            listenerJugada.remove();
        }
        funcionGameover();
        countDownTimer.cancel();
    }
    @Override
    public void onBackPressed() {
        jugada.setAbandonoID(uid);
        funcionGameover();
        countDownTimer.cancel();
        Intent i=new Intent(MultPlayerActivity.this,TransisionActivity.class);
        startActivity(i);
        finish();

    }
    /*-------- ciclo de vida Activity -----------*/
}