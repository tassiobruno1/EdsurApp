package andoid.tassio.tentativa1m;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TelaPrincipal extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE_LOCATION=100;
    private final static int REQUEST_CODE_CAMERA=101;

    private TextView nomeUsuario,emailUsuario;
    private Button bt_deslogar, bt_localizacao, bt_camera;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        getSupportActionBar().hide();
        IniciarComponentes();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(TelaPrincipal.this,TelaLogin.class);
                startActivity(intent);
                finish();
            }
        });

        bt_localizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificaGPS()) {
                    acessarLocalizacao();
                }
            }
        });

        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acessarCamera();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    emailUsuario.setText(email);

                }

            }
        });

    }

    private void IniciarComponentes(){
        nomeUsuario = findViewById(R.id.textNomeUsuario);
        emailUsuario = findViewById(R.id.textEmailUsuario);
        bt_deslogar = findViewById(R.id.bt_deslogar);
        bt_localizacao = findViewById(R.id.bt_location);
        bt_camera = findViewById(R.id.bt_camera);

    }

    private void acessarLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(TelaPrincipal.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    String latitude = "Latitude :" +addresses.get(0).getLatitude();
                                    String longitude = "Longitude :"+addresses.get(0).getLongitude();
                                    String endereco = "Endereço :"+addresses.get(0).getAddressLine(0);
                                    String pais = "País :"+addresses.get(0).getCountryName();

                                    String localizacao = latitude + "\n" + longitude + "\n" + endereco + "\n" + pais;
                                    mostrarLocalizacao(localizacao);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }else {
            solicitaPermissaoLocalizacao();
        }
    }

    private void acessarCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamera();
        }else {
            solicitaPermissaoCamera();
        }
    }

    private void solicitaPermissaoLocalizacao() {
        ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION);
    }

    private void solicitaPermissaoCamera() {
        ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]
                {Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE_LOCATION){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                acessarLocalizacao();
            }
            else {
                mostrarAlertaMensagem("Atenção", "Para usar esta funcionalidade é preciso permitir o acesso à sua localização!");
            }
        }

        if (requestCode==REQUEST_CODE_CAMERA){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirCamera();
            }
            else {
                mostrarAlertaMensagem("Atenção", "Para usar esta funcionalidade é preciso permitir o acesso à sua câmera!");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void abrirCamera() {
        Intent intent = new Intent(this, TelaCamera.class);
        startActivity(intent);
    }

    private void mostrarLocalizacao(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Localização atual")
                .setMessage(mensagem)
                .setNegativeButton("fechar", null)
                .show();
    }

    private void mostrarAlertaMensagem(String titulo, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setNegativeButton("fechar", null)
                .show();
    }

    private Boolean verificaGPS() {
        Boolean result = false;
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        try {
            result = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!result) {
            // notify user
            new AlertDialog.Builder(this)
                    .setTitle("Localização")
                    .setMessage("Você precisa ativar o serviço de localização do seu dispositivo")
                    .setPositiveButton("Ir para configurações", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .show();
        }
        return result;
    }
}