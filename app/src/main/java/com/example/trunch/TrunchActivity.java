package com.example.trunch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

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
    ImageView mMatchScreen;
    String restName;
    String trunchers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_a_trunch);


        restName = getIntent().getStringExtra("restName");
        Log.d("rony", "in trunch activity. restName:" + restName);
        trunchers = getIntent().getStringExtra("trunchers");
        Log.d("rony", "in trunch activity. trunchers:" +  trunchers);
        showTrunchDialog(trunchers, restName);

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

