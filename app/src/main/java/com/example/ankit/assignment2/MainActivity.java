package com.example.ankit.assignment2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ankit.assignment2.MESSAGE";
    Button start;
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayGame.class);
        EditText editText = (EditText) findViewById(R.id.input);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start);

        start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                sendMessage(view);
            }
        });
    }
}
