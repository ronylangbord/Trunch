package com.example.trunch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

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
    String trunchers;
    Button shuffleButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_a_trunch);


        restName = getIntent().getStringExtra("restName");
        trunchers = getIntent().getStringExtra("trunchers");
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

    public void showTrunchDialog(String trunchers, String restaurant) {
        TrunchDialogClass trunchDialog = new TrunchDialogClass(this, restName, trunchers);
        trunchDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}

