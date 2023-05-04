package andoid.tassio.tentativa1m;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class TelaPrincipal extends AppCompatActivity {

    private TextView nomeUsuario,emailUsuario;
    private Button bt_deslogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(TelaPrincipal.this,TelaLogin.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void IniciarComponentes(){
        nomeUsuario = findViewById(R.id.textNomeUsuario);
        emailUsuario = findViewById(R.id.textEmailUsuario);
        bt_deslogar = findViewById(R.id.bt_deslogar);

    }
}