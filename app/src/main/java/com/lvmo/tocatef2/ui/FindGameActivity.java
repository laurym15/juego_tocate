package com.lvmo.tocatef2.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.lvmo.tocatef2.model.Jugada;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.aplicacion.Constantes;

public class FindGameActivity extends AppCompatActivity {
    private TextView tvCargando;
    private ScrollView layoutPprogressBar, layoutMenuJuego;

    private FirebaseAuth fireBaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private FirebaseAnalytics analytics;

    private String uid,jugadaId="";
    private boolean crearInv=false;
     private ListenerRegistration listenerRegistration = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);

        layoutPprogressBar =findViewById(R.id.LayoutProgressBar);
        layoutMenuJuego= findViewById(R.id.menuJuego);

        fireBaseAuth =fireBaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        firebaseUser=fireBaseAuth.getCurrentUser();
        assert firebaseUser != null;
        uid=firebaseUser.getUid();
        initProgressBar();
        changeMenuVisibility(false);
        optenerTipoJugada();
    }
    /*-------- jugada libre -----------*/
    private void buscarJugadaLibre() {
        tvCargando.setText(R.string.StvCargando5);
        db.collection("jugadas")
                .whereEqualTo("jugadorDosID", "" )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {// Crear nueva partida
                               crearNuevaJugada();
                            } else {
                                boolean Jencontrado=false;
                                for(DocumentSnapshot docJugada : task.getResult().getDocuments()){
                                    if(docJugada.get("invitacionID").equals("") ){
                                        if(!docJugada.get("jugadorUnoID").equals(uid) ){
                                        Jencontrado=true;
                                    jugadaId = docJugada.getId();
                                Jugada jugada = docJugada.toObject(Jugada.class);
                                jugada.setJugadorDosID(uid);
                                db.collection("jugadas")
                                        .document(jugadaId)
                                        .set(jugada)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                starGame();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        changeMenuVisibility(true);
                                        Toast.makeText(FindGameActivity.this, getString(R.string.errorOptenerJugada), Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            }}
                                    if(!Jencontrado) crearNuevaJugada();
                                }
                            }
                        }
                    }
                });
        }

    private void crearNuevaJugada() {
    tvCargando.setText(R.string.StvCargando1);
    Jugada nuevaJugada =new Jugada(uid,"");
    db.collection("jugadas")
            .add(nuevaJugada)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                jugadaId=documentReference.getId();
                    esperarJugador();
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            changeMenuVisibility(true);
            Toast.makeText(FindGameActivity.this,getString(R.string.eCrearJug),Toast.LENGTH_LONG).show();
        }
    });
    }
    /*-------- jugada libre -----------*/
    private void esperarJugador() {
        tvCargando.setText(R.string.StvCargando2);
        listenerRegistration = db.collection("jugadas")
                .document(jugadaId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(!documentSnapshot.get("jugadorDosID").equals("")){
                            tvCargando.setText(R.string.StvCargando3);
                            final Handler handler = new Handler();
                            final Runnable r= new Runnable() {
                                @Override
                                public void run() {
                                    starGame();
                                }
                            };
                            handler.postDelayed(r, 1000);
                        }
                    }
                });

    }

    private void starGame() {
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }

        Intent i=new Intent(FindGameActivity.this, MultPlayerActivity.class);
        i.putExtra(Constantes.EXTRA_JUGADA_ID,jugadaId);
        startActivity(i);
        jugadaId="";
    }

    private void initProgressBar() {
        tvCargando =findViewById(R.id.textViewCargando);
        ProgressBar progresBar = findViewById(R.id.progressBarCargando);

        progresBar.setIndeterminate(true);
        tvCargando.setText(R.string.StvCargando4);

        changeMenuVisibility(true);
    }

    private void changeMenuVisibility(boolean ShowMenu) {
        layoutPprogressBar.setVisibility(ShowMenu ? View.GONE: View.VISIBLE);
        layoutMenuJuego.setVisibility(ShowMenu ? View.VISIBLE: View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!crearInv) {
            if (listenerRegistration != null) {
                listenerRegistration.remove(); }

            if (!jugadaId.equals("")) {
                db.collection("jugadas")
                        .document(jugadaId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                jugadaId = "";
                            }
                        });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        crearInv=false;
    }

    /*-------- jugada por inivtacion -----------*/
    private void crearNuevaJugadaRef(final boolean crearJugada, final String IDusuario) {
        changeMenuVisibility(false);//ver botones
        Jugada nuevaJugada =new Jugada(uid,IDusuario);
        tvCargando.setText(R.string.StvCargando1);

        db.collection("jugadas")
                .add(nuevaJugada)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        jugadaId=documentReference.getId();
                        if(crearJugada){
                            String datosJugada=jugadaId+"-"+uid;
                            shareShortDynamicLink(datosJugada);}
                        esperarJugador();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                changeMenuVisibility(true);
                Toast.makeText(FindGameActivity.this,getString(R.string.eCrearJug),Toast.LENGTH_LONG).show();
            }
        });

    }
    private void buscarJugadaRef() {
        changeMenuVisibility(false);
        tvCargando.setText(R.string.StvCargando5);
        db.collection("jugadas")
                .document(jugadaId)
                .update("jugadorDosID",uid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        starGame();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindGameActivity.this,getString( R.string.Tpartidaeliminada), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void linkReception( ){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if(pendingDynamicLinkData != null){
                            analytics = FirebaseAnalytics.getInstance(FindGameActivity.this);
                            Uri deepLink =pendingDynamicLinkData.getLink();
                            String referenceLink =deepLink.toString(); //formato  datosjugada = idjugada - idjuagoruno
                            try {
                                referenceLink =referenceLink.substring(referenceLink.lastIndexOf("?")+1);
                                String idJugadaRef = referenceLink.substring(0,referenceLink.lastIndexOf("-"));
                                String idJugadorUno = referenceLink.substring(referenceLink.lastIndexOf("-")+1);
                                //tvLink.setText(referenceLink+"/-------/"+idJugadaRef+" /-------/ "+idJugadorUno);
                                jugadaId=idJugadaRef;
                                buscarJugadaRef();
                                //  llama a la funcion jugada
                            }catch (Exception e){Toast.makeText(FindGameActivity.this, "link roto", Toast.LENGTH_SHORT).show();}

                            FirebaseAppInvite invite= FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                            if (invite != null){
                                String invitationId=invite.getInvitationId();
                                if(!TextUtils.isEmpty(invitationId))
                                    Toast.makeText(FindGameActivity.this, "inivitation Id "+ invitationId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FindGameActivity.this, "onfailure" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void shareShortDynamicLink(String datosjugada ){
        Task<ShortDynamicLink> createLinkTask= FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(bulidDynamicLink(datosjugada)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()){
                            Uri shortLink=task.getResult().getShortLink();
                            String msg =getString(R.string.juegaconmigo) + shortLink.toString();
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.linkinivto));
                            i.putExtra(Intent.EXTRA_TEXT,msg);
                            startActivity(i);
                        }else{
                            Toast.makeText(FindGameActivity.this, getString(R.string.ErrorLink), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private String bulidDynamicLink(String datosjugada){//formato  datosjugada = idjuagada - idjuagoruno
        return "https://tocatef2.page.link/?" +
                "link=" +
                "https://play.google.com/store/apps/details?id=com.lvmo.tocatef2/dj?" +datosjugada+
                "&apn" +
                "com.lvmo.tocatef2.ui.PruebasActivity" +
                "&st="+
               "Comparte esta App"+
                "&sd=" +
              "Busca:+Tocate+by+Laury."+
                "&utm_source=" +
                "AndroidApp";
    }
    /*-------- jugada por inivtacion -----------*/

    private void optenerTipoJugada(){
        Intent i = getIntent();
        if(i!=null) {
            String tipoPartida = i.getStringExtra(Constantes.EXTRA_TIPO_PARTIDA);
              String partidaID=i.getStringExtra(Constantes.EXTRA_JUGADA_ID);
            if(tipoPartida==null){
                linkReception( );
            }else {
                String aa =tipoPartida.substring(0,6);
                if(aa.equals("Invita")) {
                    crearNuevaJugadaRef(false, tipoPartida.substring(tipoPartida.lastIndexOf("?") + 1));
                    crearInv = false;
                }else
                { switch (tipoPartida) {
                    case "pinvDirecta":
                      //  Toast.makeText(FindGameActivity.this, tipoPartida, Toast.LENGTH_LONG).show();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(FindGameActivity.NOTIFICATION_SERVICE);
                        notificationManager.cancel(Constantes.EXTRA_NOTIFICACION_ID);
                        jugadaId=partidaID;
                        buscarJugadaRef();
                        break;

                    case "pLibre":
                        buscarJugadaLibre();
                        crearInv = false;
                        break;

                    case "cearDynamickLink":
                        crearNuevaJugadaRef(true, uid);
                        crearInv = true;
                        break;

                }
                }
            }
        }
      /*  if(tipoPartida==null){
            linkReception( );
        }else{
            if(tipoPartida.equals("pLibre")){
                buscarJugadaLibre();
                crearInv=false;
                }else{
                if(tipoPartida.equals("cearPartida")){
                    crearNuevaJugadaRef(true, uid);
                    crearInv = true;
                }else{
                    crearNuevaJugadaRef(false, tipoPartida.substring(tipoPartida.lastIndexOf("?") + 1));
                    crearInv = false; }
            }
            }
       */
    }
}
