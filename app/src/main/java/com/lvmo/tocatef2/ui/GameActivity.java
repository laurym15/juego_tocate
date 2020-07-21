package com.lvmo.tocatef2.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.ui.Raking.RankingActivity;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;
import com.squareup.picasso.Picasso;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private FirebaseAuth fireBaseAuth;
    private User userjUno;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private Uri image_uri;

    private TextView etInicio, etCuentaReg, tvAciertos2, tvOrden;
    private ImageView fotoPerfil, IvjDos;

    private int anchoPant, altoPant,  puntosUid = 0;
    private String  uid,nombreJuno = "";
    private Random aleatorio;

    final static long INTERVAL = 1000;
    final static long TIMEOUT = 62000;
    static long millisecondsleft;
    static long millisecondsleftDial;
    boolean isPause = false, isParche = true;
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimerDial;
    CountDownTimer countDownTimeronResume;
    private int n;

    private ConstraintLayout layoutCargando;
    private ProgressBar pbCargando;
    TextView tvCargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        layoutCargando=findViewById(R.id.layoutCargando);
        pbCargando=findViewById(R.id.progressBarCargando);
        tvCargando=findViewById(R.id.textViewCargando);
        pbCargando.setIndeterminate(true);
        tvCargando.setText(R.string.StvCargando1);
        layoutCargando.setVisibility(View.VISIBLE);

        etInicio = findViewById(R.id.NomJuno);
        tvAciertos2 = findViewById(R.id.TvAciertos);
        etCuentaReg = findViewById(R.id.TvCuentaReg);
        tvOrden = findViewById(R.id.tvTocate);
        fotoPerfil = findViewById(R.id.imageJuno);
        IvjDos = findViewById(R.id.imageJdos);
        fotoPerfil.setImageResource(R.drawable.tocate);
        IvjDos.setImageResource(R.drawable.tocame);

        fireBaseAuth =fireBaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        uid=fireBaseAuth.getCurrentUser().getUid();

        getnombrePuntos(uid);
        descargarFoto(uid);

        etInicio.setVisibility(View.INVISIBLE);
        fotoPerfil.setVisibility(View.INVISIBLE);
        IvjDos.setVisibility(View.INVISIBLE);
        tvOrden.setVisibility(View.INVISIBLE);
        etCuentaReg.setVisibility(View.INVISIBLE);
        tvAciertos2.setVisibility(View.INVISIBLE);


        //temporizador para ve bistec
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPantalla();
                puntosUid = 0;
                actualizarAciertos();
                etInicio.setVisibility(View.VISIBLE);
                tvOrden.setVisibility(View.VISIBLE);
                fotoPerfil.setVisibility(View.VISIBLE);
                IvjDos.setVisibility(View.VISIBLE);
                etCuentaReg.setVisibility(View.VISIBLE);
                tvAciertos2.setVisibility(View.VISIBLE);
                eventoClic();
                layoutCargando.setVisibility(View.GONE);
            }
        }, 2000);
    }
    private void eventoClic() {
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (n) {
                    case 0:
                        puntosUid++;
                        break;
                    case 1:
                        if (puntosUid >= 0) puntosUid--;

                        break;
                }
                actualizarAciertos();
            }
        });
        IvjDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (n) {
                    case 0:
                        if (puntosUid >= 1) puntosUid=puntosUid-2;
                        break;
                    case 1:
                        puntosUid++;
                        break;
                }
                actualizarAciertos();
            }
        });
    }
    private void actualizarAciertos() {
        n = new Random().nextInt(2);
        EtTocate(n);
        moverImangen(IvjDos, fotoPerfil);
        moverImangen(fotoPerfil, IvjDos);
        tvAciertos2.setText(String.format(getString(R.string.Aciertos), puntosUid));
    }

    /*--------Game over funciones -----------*/
    private void funcionGameover() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.risamujer);
        mp.start();
        isPause = true;    // guardar puntos en base de datos
        userjUno.setPoints(userjUno.getPoints() + puntosUid);
        userjUno.setGameDone(userjUno.getGameDone() + 1);
        db.collection("users")
                .document(uid)
                .set(userjUno).addOnSuccessListener(GameActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(GameActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        etCuentaReg.setText(R.string.SetCuentaReg);
        dialogo_Gameover();
    }
    private void dialogo_Gameover() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_gameover, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView tvTitulo = view.findViewById(R.id.tvMensaje);
        TextView tvGanador = view.findViewById(R.id.tvGanador);
        tvGanador.setText(String.format(getString(R.string.Ganaste),nombreJuno));
        tvTitulo.setText(R.string.SetInicio2);
        TextView tvPuntos = view.findViewById(R.id.tvPuntos);
        tvPuntos.setText(String.format(getString(R.string.tusaciertos), puntosUid));

        Button reinicio = view.findViewById(R.id.Reiniciar);
        reinicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.ComienzaJu, Toast.LENGTH_LONG).show();
                puntosUid = 0;
                actualizarAciertos();
                dialog.dismiss();
                countDownTimerDial.cancel();
                isPause = false;
                isParche = true;
                onResume();
            }
        });

        final Button ranking = view.findViewById(R.id.verRankin);
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.VamosRan, Toast.LENGTH_LONG).show();
                Intent i = new Intent(GameActivity.this, RankingActivity.class);
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
                ranking.setText("Ranking (" + String.format("%02d", millisecondsleftDial / 1000) + ")");
            }
            @Override
            public void onFinish() {
                Intent i = new Intent(GameActivity.this, RankingActivity.class);
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        }.start();
    }
    /*--------Game over funciones -----------*/

    /*--------Mover imagen cambinar etiqueta -----------*/
    private void EtTocate(int varTocate) {
        switch (varTocate) {
            case 0:
                tvOrden.setText(getString(R.string.SetTocate));
                etInicio.setText(String.format(getString(R.string.SetInicio1),nombreJuno,getString(R.string.SetTocate)));
                break;
            case 1:
                tvOrden.setText(getString(R.string.SetTocame1));
                etInicio.setText(String.format(getString(R.string.SetInicio1),nombreJuno,getString(R.string.SetTocame1)));
                break;
        }
    }
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
            ranY = funcionAleatorio(0, altoPant -2*imagen0.getHeight());
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
                tvOrden.setY(location[1] +(imagen0.getHeight()/2));
                break;
            case 1:
                tvOrden.setX(ranX);
                tvOrden.setY(ranY +(imagen0.getHeight()*(4/3)));
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

    /*-------- descarga foto y  optener usuario  -----------*/
    private void getnombrePuntos(String idUsuario){ //regresa nombreJuno,puntosUid, foto perfil
        db.collection("users")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(GameActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userjUno=documentSnapshot.toObject(User.class);
                        nombreJuno=documentSnapshot.get("name").toString();
                        if(nombreJuno.length()>10){
                            nombreJuno = nombreJuno.substring(0,10);}
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
        }).addOnFailureListener(GameActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GameActivity.this, getString(R.string.necesitasFoto), Toast.LENGTH_LONG).show();
                SelectProfilePic();
            }
        });
    }
    /*-------- descarga foto y  optener usuario  -----------*/

    /*--------  Code is for selecting image from galary or camera -----------*/
    private void SelectProfilePic() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
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
                    Toast.makeText(GameActivity.this,getString(R.string.necesitasFoto),Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    finish();

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
                Toast.makeText(GameActivity.this, getString(R.string.Toast_permiso), Toast.LENGTH_LONG).show();
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
    private void SubirImangen() {
        StorageReference filepath = mStorageRef.child(String.format(getString(R.string.SmStorageRef),uid));
        filepath.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Toast.makeText(GameActivity.this,getString(R.string.subioFoto), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(GameActivity.this,getString(R.string.subioFoto), Toast.LENGTH_LONG).show();
                    }
                });
    }
    /*-------- Code is for selecting image from galary or camera -----------*/

    /*-------- ciclo de vida Activity -----------*/
    @Override
    protected void onResume() {
        super.onResume();
        if (!isPause) {
            countDownTimer = new CountDownTimer(TIMEOUT, INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millisecondsleft = millisUntilFinished;
                    etCuentaReg.setText(":" + String.format("%02d", millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    if (isParche) funcionGameover();
                }
            }.start();

        } else {
            countDownTimeronResume = new CountDownTimer(millisecondsleft, INTERVAL) {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void onTick(long millisUntilFinished) {
                    millisecondsleft = millisUntilFinished;
                    etCuentaReg.setText(":" + String.format("%02d", millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    if (isParche) {
                        funcionGameover();
                    }
                }
            }.start();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isPause = true;
        }
        if (countDownTimeronResume != null) {
            countDownTimeronResume.cancel();
        }
    }
    @Override
    public void onBackPressed() {
    Intent i=new Intent(GameActivity.this, TransisionActivity.class);
    startActivity(i);
    }
    /*-------- ciclo de vida Activity -----------*/

}