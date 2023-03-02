package andoid.tassio.tentativa1m;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void teladois(View view) {
        Intent in = new Intent(MainActivity.this, TelaLogin.class);
        startActivity(in);
    }
}