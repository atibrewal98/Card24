package com.example.ankit.assignment2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button rePick;
    Button checkInput;
    Button clear;
    Button left;
    Button right;
    Button plus;
    Button minus;
    Button multiply;
    Button divide;
    TextView expression;

    ImageButton[] cards;

    private void initCardImage() {
        for (int i = 0; i < 4; i++) {
            int resID = getResources().getIdentifier("back_0","drawable","com.example.ankit.assignment2");
                    cards[i].setImageResource(resID);
        }
    }

    int[] data;
    int[] card;
    private void pickCard(){
        data = new int[4];
        card = new int[4];
        final int random1 = new Random().nextInt(52) + 1;
        final int random2 = new Random().nextInt(52) + 1;
        final int random3 = new Random().nextInt(52) + 1;
        final int random4 = new Random().nextInt(52) + 1;
        card[0]=random1;
        card[1]=random2;
        card[2]=random3;
        card[3]=random4;
        data[0]=random1 % 13;
        data[1]=random2 % 13;
        data[2]=random3 % 13;
        data[3]=random4 % 13;
        /*Set the value of K to be 13*/
        for(int i = 0; i < 4; i++){
            if(data[i] == 0)
                data[i] += 13;
        }
        setClear();
    }

    private void clickCard(int i) {
        int resId;
        String num;
        Integer value;
        if (imageCount[i] == 0) {
            resId = getResources().getIdentifier("back_0","drawable", "com.example.ankit.assignment2");
                    cards[i].setImageResource(resId);
            cards[i].setClickable(false);
            value = data[i];
            num = value.toString();
            expression.append(num);
            imageCount[i] ++;
        }
    }

    private boolean checkInput(String input) {
        Jep jep = new Jep();
        Object res;
        try {
            jep.parse(input);
            res = jep.evaluate();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Wrong Expression", Toast.LENGTH_SHORT).show();
            return false;
        } catch (EvaluationException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Wrong Expression", Toast.LENGTH_SHORT).show();
            return false;
        }
        Double ca = (Double)res;
        if (Math.abs(ca - 24) < 1e-6)
            return true;
        return false;
    }

    int[] imageCount;
    private void setClear(){
        int resID;
        imageCount = new int[4];
        expression.setText("");
        for (int i = 0; i < 4; i++) {
            imageCount[i] = 0;
            resID = getResources().getIdentifier
                    ("card"+card[i],"drawable", "com.example.ankit.assignment2");
                            cards[i].setImageResource(resID);
            cards[i].setClickable(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cards = new ImageButton[4];
        cards[0] = (ImageButton) findViewById(R.id.card1);
        cards[1] = (ImageButton) findViewById(R.id.card2);
        cards[2] = (ImageButton) findViewById(R.id.card3);
        cards[3] = (ImageButton) findViewById(R.id.card4);

        rePick = (Button)findViewById(R.id.repick);
        checkInput = (Button)findViewById(R.id.checkinput);
        left = (Button)findViewById(R.id.left);
        right = (Button)findViewById(R.id.right);
        plus = (Button)findViewById(R.id.plus);
        minus = (Button)findViewById(R.id.minus);
        multiply = (Button)findViewById(R.id.multiply);
        divide = (Button)findViewById(R.id.divide);
        clear = (Button)findViewById(R.id.clear);
        expression = (TextView)findViewById(R.id.input);

        pickCard();

        cards[0].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(0);
            }
        });
        cards[1].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(1);
            }
        });
        cards[2].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(2);
            }
        });
        cards[3].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(3);
            }
        });

        left.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append("(");
            }
        });
        right.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append(")");
            }
        });
        plus.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append("+");
            }
        });
        minus.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append("-");
            }
        });
        multiply.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append("*");
            }
        });
        divide.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                expression.append("/");
            }
        });

        clear.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                setClear();
            }
        });

        checkInput.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
               String inputStr = expression.getText().toString();
               if(checkInput(inputStr)){
                   Toast.makeText(MainActivity.this, "Correct Answer", Toast.LENGTH_SHORT).show();
                   pickCard();
               }else{
                   Toast.makeText(MainActivity.this, "Wrong Answer", Toast.LENGTH_SHORT).show();
                   setClear();
               }
            }
        });
    }
}
