package com.lvmo.tocatef2.ui.Transision;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.aplicacion.Constantes;
import com.lvmo.tocatef2.ui.FindGameActivity;
import com.lvmo.tocatef2.ui.GameActivity;
import com.lvmo.tocatef2.ui.InicioSesion.MainActivity;
import com.lvmo.tocatef2.ui.Raking.RankingActivity;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransisionActivity extends AppCompatActivity {

    private ScrollView layoutPprogressBar, layoutMenuJuego;
    private TextView tvCargando,tvnombreUid, tvpuntosUid;;
    private CircleImageView fotoPerfil;
    ImageView iCompartir;
    private Uri image_uri;
    private FirebaseAuth fireBaseAuth;
    private  InterstitialAd mInterstitialAd;
    private StorageReference mStorageRef;

    private FirebaseFirestore db;
    private String uid,nombreJuno="";
    private int puntosUid;
    private User userjUno;
    String  jugadaId;
    boolean jPausado;
    int contSalir=0;
    CountDownTimer countDownTimerDial;
    static long millisecondsleft;
    CountDownTimer countDownTimerSalir;

    String tiponoti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transision);



        layoutPprogressBar =findViewById(R.id.LayoutProgressBar);
        layoutMenuJuego= findViewById(R.id.menuJuegoTrans);
        fotoPerfil=findViewById(R.id.fotoPerfil);
        tvCargando=findViewById(R.id.textViewCargando);
        tvnombreUid= findViewById(R.id.NombreUid);
        tvpuntosUid= findViewById(R.id.PuntosUid);
        iCompartir= findViewById(R.id.imageView);
        fireBaseAuth =fireBaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        uid=fireBaseAuth.getCurrentUser().getUid();
        initProgressBar();
        changeMenuVisibility(false);
        jPausado=false;
        getnombrePuntos(uid);
        descargarFoto(uid);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeMenuVisibility(true);
                tvnombreUid.setText(nombreJuno);
                puntosUid=userjUno.getPoints();
                tvpuntosUid.setText(String.format(getString(R.string.pun_totales),puntosUid));
            }
        }, 2000);

        actulizarDBuser("on");

        eventoclic();
        bannerAnuncio();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContaineronlineuser,new UserOnlineFragment())
                .commit();
        DetectarJugada();

    }

    private void eventoclic() {
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoPerfil.setImageResource(R.drawable.tocame);
                SelectProfilePic();
            }
        });

        Button btPartidaLibre = findViewById(R.id.buttonBuscarPartida);
        btPartidaLibre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(TransisionActivity.this, FindGameActivity.class);
                i.putExtra(Constantes.EXTRA_TIPO_PARTIDA,"pLibre");
                startActivity(i);
            }
        });

        Button btJugar = findViewById(R.id.buttonJugar);
        btJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(TransisionActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        Button btRanking = findViewById(R.id.buttonRanking);
        btRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interticalAuncio();
                Intent i= new Intent(TransisionActivity.this, RankingActivity.class);
                startActivity(i);

            }
        });

        Button btOnline = findViewById(R.id.buttonOnline);
        btOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(TransisionActivity.this,FindGameActivity.class);
                i.putExtra(Constantes.EXTRA_TIPO_PARTIDA,"cearDynamickLink");
                startActivity(i);
            }
        });

        FloatingActionButton fbtUsuarioPers = findViewById(R.id.fab);
        fbtUsuarioPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actulizarDBuser("off");
                    fireBaseAuth.signOut();
                    interticalAuncio();
                    LoginManager.getInstance().logOut();
                    Intent i= new Intent(TransisionActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

            }
        });
        FloatingActionButton fbtCompartir = findViewById(R.id.fab1);

        fbtCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.linkinivto));
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.dynamicklinks));//  i.putExtra(Intent.EXTRA_TEXT, url.toString());
                startActivity(i);
            }
        });
    }
    private void changeMenuVisibility(boolean ShowMenu) {
        layoutPprogressBar.setVisibility(ShowMenu ? View.GONE: View.VISIBLE);
        layoutMenuJuego.setVisibility(ShowMenu ? View.VISIBLE: View.GONE);
    }
    private void initProgressBar() {
        tvCargando =findViewById(R.id.textViewCargando);
        ProgressBar progresBar = findViewById(R.id.progressBarCargando);

        progresBar.setIndeterminate(true);
        tvCargando.setText(R.string.StvCargando4);

        changeMenuVisibility(true);
    }

    /*-------- tomar foto y subir foto -----------*/
    private void SubirImangen() {
        StorageReference filepath = mStorageRef.child(String.format(getString(R.string.SmStorageRef),uid));
        filepath.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(TransisionActivity.this,getString(R.string.subioFoto), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(TransisionActivity.this,getString(R.string.subioFoto), Toast.LENGTH_LONG).show();
                    }
                });
    }
    /*-------- descarga foto y  optener usuario  -----------*/
    private void getnombrePuntos(String idUsuario){ //regresa nombreJuno,puntosUid, foto perfil
        db.collection("users")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(TransisionActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userjUno=documentSnapshot.toObject(User.class);
                        nombreJuno=documentSnapshot.get("name").toString();
                    }
                });
    }
    private void descargarFoto(String idUsuario) {
        StorageReference filepath = mStorageRef.child(String.format(getString(R.string.SmStorageRef),idUsuario));
        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    Picasso.get()
                            .load(downloadUrl)
                            .resize(100, 100)
                            .error(R.drawable.tocate)
                            .into(fotoPerfil);
                }
            }
        }).addOnFailureListener(TransisionActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransisionActivity.this, getString(R.string.necesitasFoto), Toast.LENGTH_LONG).show();
                SelectProfilePic();
            }
        });
    }
    /*-------- descarga foto y  optener usuario  -----------*//*-------- tomar foto y subur foto -----------*/

    /*-------- Anuncios Anuncios Anuncios-----------*/
    private void bannerAnuncio(){
         AdView mAdView;
        AdView adView2 = new AdView(this);
        adView2.setAdSize(AdSize.SMART_BANNER);
        adView2.setAdUnitId(getString(R.string.Banner_ad_unit_id));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
       mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private void interticalAuncio() {
       int n = new Random().nextInt(3);
       if(n==2) {
           mInterstitialAd = new InterstitialAd(this);
           mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
           AdRequest adRequest = new AdRequest.Builder().build();
           mInterstitialAd.loadAd(adRequest);

           mInterstitialAd.setAdListener(new AdListener() {
               @Override
               public void onAdLoaded() {
                   if (mInterstitialAd.isLoaded()) {
                       mInterstitialAd.show();
                   } /*else {
                    Toast.makeText(TransisionActivity.this, "nose cargo el anuncio", Toast.LENGTH_LONG);
                }*/
               }
           });
       }
    }
    /*-------- Anuncios Anuncios Anuncios-----------*/

    /*--------  Code is for selecting image from galary or camera -----------*/
    private void SelectProfilePic() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(TransisionActivity.this);
        builder.setTitle(getString(R.string.AgregaFoto));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission,1000);
                        }
                        else {
                            openCamera();
                        }
                    }
                    else {
                        openCamera();
                    }
                }
                else if (options[item].equals("Choose from Gallery")){

                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);

                }else if (options[item].equals("Cancel")) {
                    Toast.makeText(TransisionActivity.this,getString(R.string.necesitasFoto),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(takePictureIntent, 1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                //permission from pop up was denied.
                Toast.makeText(TransisionActivity.this, "Permission Denied...", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    fotoPerfil.setImageURI(image_uri);
                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    fotoPerfil.setImageURI(image_uri);
                    break;
            }
            SubirImangen();
        }
    }
    /*-------- Code is for selecting image from galary or camera -----------*/

    /*-------- ciclo de vida Activity -----------*/
    @Override
    protected void onResume() {
        super.onResume();
        actulizarDBuser("on");
        jPausado=false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        actulizarDBuser("on");

   /*     eventoclic();
        bannerAnuncio();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContaineronlineuser,new UserOnlineFragment())
                .commit();*/
        DetectarJugada();
        jPausado=false;
        if(contSalir==2){
            dialogo_NotificacionJugada();
            tiponoti="salir";
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DetectarJugada();
        actulizarDBuser("on");
        jPausado=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        actulizarDBuser("off");
        jPausado=true;
        DetectarJugada();

    }
    @Override
    protected void onPause() {
        super.onPause();
        jPausado=true;
        DetectarJugada();
    }
    @Override
    public void onBackPressed() {
        if(contSalir==0){
        Toast.makeText(TransisionActivity.this, getString(R.string.TpresioneSalir),Toast.LENGTH_SHORT).show();
        contSalir++;
        }
        else
        {
            actulizarDBuser("off");
            super.onBackPressed();
            finishAffinity();
            //System.exit(0);*/
        }

        countDownTimerSalir = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            contSalir=0;
            }
        }.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /*-------- ciclo de vida Activity -----------*/
    private void actulizarDBuser(String onLine) {
        db.collection("users")
                .document(uid)
                .update("online",onLine)
                .addOnSuccessListener(TransisionActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(TransisionActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /*-------- notificacion invitacion a jugar -----------*/
    private void DetectarJugada() {
        db.collection("jugadas")
                .whereEqualTo("invitacionID",uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(),getString(R.string.TlistenFaild),Toast.LENGTH_LONG).show();
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if(!  doc.get("jugadorUnoID").equals(uid)){
                            if(doc.get("jugadorDosID").equals("")){
                                if(doc.get("abandonoID").equals("")){
                                    if(doc.get("ganadorID").equals("")){
                                        jugadaId = doc.getId();
                                        //  Toast.makeText(TransisionActivity.this,"Funciona funciona"+ jugadaId,Toast.LENGTH_LONG).show();
                                        if(jPausado){
                                             crearNotificacionInterna(jugadaId);
                                        }
                                        else{
                                            dialogo_NotificacionJugada();
                                            tiponoti="notifica";}
                                        break;
                                    }}}
                            }
                        }
                    }
                });

    }
    private void crearNotificacionInterna(String jugadaId) {
        Notification.Builder mBuilder = new Notification.Builder(TransisionActivity.this)
                .setContentTitle("Tienes una partida pendiente")
                .setContentText("Apresurate, te esperan")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(Notification.PRIORITY_MAX)
                .setLights(Color.CYAN,1,0)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOngoing(false);//sirve para canerla solo cuando netras a laplaicacion.
               // .setLargeIcon(R.drawable.tocate)

        Intent i= new Intent(TransisionActivity.this, FindGameActivity.class);
        i.putExtra(Constantes.EXTRA_TIPO_PARTIDA,"pinvDirecta");
        i.putExtra(Constantes.EXTRA_JUGADA_ID,jugadaId);
        ///startActivity(i);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(TransisionActivity.this);
        stackBuilder.addParentStack(FindGameActivity.class);
        stackBuilder.addNextIntent(i);

        PendingIntent pendingIntet =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntet);

        NotificationManager notificationManager= (NotificationManager) getSystemService(TransisionActivity.NOTIFICATION_SERVICE);
        notificationManager.notify(Constantes.EXTRA_NOTIFICACION_ID,mBuilder.build());
    }
    private void dialogo_NotificacionJugada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransisionActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_dialgonotificacion, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView tvTitulo = view.findViewById(R.id.tvTitulo);
        TextView tvcuerpo = view.findViewById(R.id.cuerpo);
        tvcuerpo.setText(getString(R.string.noti_Cuerpo));
        tvTitulo.setText(getString(R.string.noti_Titulo));

        Button aceptar = view.findViewById(R.id.Aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(tiponoti.equals("notifica"))  {
                  Toast.makeText(getApplicationContext(), R.string.ComienzaJu, Toast.LENGTH_LONG).show();
                Intent i= new Intent(TransisionActivity.this, FindGameActivity.class);
                i.putExtra(Constantes.EXTRA_TIPO_PARTIDA,"pinvDirecta");
                i.putExtra(Constantes.EXTRA_JUGADA_ID,jugadaId);
                startActivity(i);
              }else{
                  onDestroy();
              };
                countDownTimerDial.cancel();/// hay que ponerlo afuera del else
                dialog.dismiss();
            }
        });

        final Button cancelar = view.findViewById(R.id.Cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tiponoti.equals("notifica"))  {
                    cancelarJugada();
                }
                countDownTimerDial.cancel();
                dialog.dismiss();
            }
        });
        countDownTimerDial = new CountDownTimer(35000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisecondsleft = millisUntilFinished;
                cancelar.setText("Cancelar (" + String.format("%02d", millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                if(tiponoti.equals("notifica"))  {  cancelarJugada();}
                dialog.dismiss();
            }
        }.start();

    }
    private void cancelarJugada() {
        db.collection("jugadas")
                .document(jugadaId)
                .update("jugadorDosID",uid, "abandonoID",uid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TransisionActivity.this, getText(R.string.TrechazarJugada), Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    /*-------- notificacion invitacion a jugar -----------*/

}
