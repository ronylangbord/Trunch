package com.example.trunch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchActivity extends ActionBarActivity {


    //=========================================
    //				Constants
    //=========================================


    //=========================================
    //				Fields
    //=========================================

    String restName;
    String trunchersString;
    Button shuffleButton;
    ObjectMapper mObjectMapper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_a_trunch);

        mObjectMapper = new ObjectMapper();


        restName = getIntent().getStringExtra("restName");
        trunchersString = getIntent().getStringExtra("trunchers");

        User[] trunchers = new User[0];
        try {
            trunchers = mObjectMapper.readValue(trunchersString,User[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        showTrunchDialog(trunchers, restName);

        shuffleButton = (Button) findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShuffleDialogClass shuffle = new ShuffleDialogClass(TrunchActivity.this);
                shuffle.show();
            }
        });

    }

    public void showTrunchDialog(User[] trunchers, String restaurant) {
        TrunchDialogClass trunchDialog = new TrunchDialogClass(this, restName, trunchers);
        trunchDialog.show();
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//    }
//
//}
//
}