package com.example.trunch;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ronylangbord on 4/11/15.
 */
public class TrunchDialogClass extends Dialog implements View.OnClickListener {


     Activity activity;
     Button ok;
     String restaurant;
     User[] trunchers;
     TextView alertText;
    Typeface robotoFont;


    public TrunchDialogClass(Activity a, String restName, User[] trunchers) {
        super(a);
        this.activity = a;
        this.restaurant = restName;
        this.trunchers = trunchers;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trunch_alert);

        ok = (Button) findViewById(R.id.ok_button_trunch);
        alertText = (TextView) findViewById(R.id.trunch_text);
        String truncherOne = trunchers[0].getFirstName() + " " + trunchers[0].getLastName();
        String truncherTwo = trunchers[1].getFirstName() + " " + trunchers[1].getLastName();
      //  robotoFont  = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
     //   alertText.setTypeface(robotoFont);
        alertText.setText("You are having lunch with: " + truncherOne + " and " +
                truncherTwo + "at " + restaurant);
        alertText.setTextColor(Color.BLACK);
        alertText.setTextSize(15);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_button_trunch:

                dismiss();
                break;

            default:
                break;
        }
        dismiss();

    }


}
