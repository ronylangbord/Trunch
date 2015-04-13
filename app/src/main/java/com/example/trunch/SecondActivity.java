package com.example.trunch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.trunch.R.id.white_bar;


public class SecondActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener {
    //=========================================
    //				Constants
    //=========================================

    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    TagsCompletionView mTagsCompletionView;
    HorizontialListView mRestContainer;
    FoodTag[] foodTags;
    Restaurant[] restTotal;
    ArrayList<Restaurant> restAdapterList;
    ArrayAdapter<Restaurant> restAdapter;
    ArrayAdapter<FoodTag> foodTagAdapter;
    ObjectMapper mMapper;
    InputMethodManager mInputManger;
    Typeface robotoFont;
    ActionBar actionBar;

    //=========================================
    //				Activity Lifecycle
    //=========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_1);

//        actionBar = getActionBar();
//        BitmapDrawable background = new BitmapDrawable
//                (BitmapFactory.decodeResource(getResources(), R.drawable.trunch_logo_small));
//
//        actionBar.setBackgroundDrawable(background);

        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mTagsCompletionView = (TagsCompletionView) findViewById(R.id.searchView);
        // mMainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        mRestContainer = (HorizontialListView) findViewById(R.id.restContainer);
        mMapper = new ObjectMapper();
        mInputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        robotoFont  = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");


        // parse json from SharedPref and init all components
        parseAndInit(SharedPrefUtils.getRests(mSharedPreferences)
                , SharedPrefUtils.getFoodTags(mSharedPreferences));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }




    //=========================================
    //				Private Methods
    //=========================================


    private void parseAndInit(String jsonRest, String jsonTags) {
        // parse
        parseJsonRest(jsonRest);
        parseJsonTags(jsonTags);
        //init
        initRestContainer();
        initTokenView();
        adjustTokenView();
    }

    // for TrunchCheckerService
    public static PendingIntent getSyncPendingIntent(Context context) {
        Intent intent = new Intent(context, TrunchCheckerService.class);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }


    //=========================================
    //			Json Parser
    //=========================================


    private void parseJsonRest(String jsonRest) {
        try {
            restTotal = mMapper.readValue(jsonRest,Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parseJsonTags(String json) {
        try {
            foodTags = mMapper.readValue(json,FoodTag[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=========================================
    //		    HorizontalListView
    //=========================================

    private void initRestContainer() {
        restAdapterList = new ArrayList<>();
        restAdapter = new ArrayAdapter<Restaurant>(this, R.layout.rest_item, restAdapterList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context context = parent.getContext();
                View retval = LayoutInflater.from(context).inflate(R.layout.rest_item, null);
                ImageButton restBtn = (ImageButton) retval.findViewById(R.id.imageButton);
                TextView restName = (TextView) retval.findViewById(R.id.restName);

                //setting the "choose a restaurant" string and creating a shadow
                TextView mChooseRest = (TextView) findViewById(R.id.choose_a_rest);
                mChooseRest.setTypeface(robotoFont);
                int radius = 15;
                int xOffSet = 10;
                int yOffSet = 10;
                int shadowColor = Color.BLACK;
                mChooseRest.setShadowLayer(radius, xOffSet, yOffSet, shadowColor);


                ImageView mWhiteBar = (ImageView) findViewById(white_bar);
                mChooseRest.setVisibility(View.VISIBLE);
                mWhiteBar.setVisibility(View.VISIBLE);
                final Restaurant rest = getItem(position);
                restName.setText(rest.getName());
                String imgName = rest.getName().toLowerCase().replaceAll(" ", "_");
                int path = getResources().getIdentifier(imgName, "drawable", getPackageName());
                restBtn.setImageResource(path);
                //Picasso.with(context).load(rest.getImage()).resize(350,350).into(restBtn);
                return retval;
            }
        };
        mRestContainer.setAdapter(restAdapter);
        mRestContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                TextView restTitle = (TextView) view.findViewById(R.id.restName);
                TextView dialogText = (TextView) findViewById(R.id.txt_dia);
//                dialogText.setTypeface(robotoFont);
                final String restName = (String) restTitle.getText();
                CustomDialogClass cdd = new CustomDialogClass(SecondActivity.this, restName);
                cdd.show();
            }

            ;


        });
    }

            //=========================================
            //	            TokenView
            //=========================================

            private void initTokenView() {
                foodTagAdapter = new FilteredArrayAdapter<FoodTag>(this, R.layout.food_tag_layout, foodTags) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {

                            LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                            convertView = l.inflate(R.layout.food_tag_layout, parent, false);
                        }

                        FoodTag ft = getItem(position);
                        ((TextView)convertView.findViewById(R.id.name)).setText(ft.getTag());
                        ((TextView)convertView.findViewById(R.id.rest)).setText(ft.getType());

                        return convertView;
                    }

                    @Override
                    protected boolean keepObject(FoodTag obj, String mask) {
                        mask = mask.toLowerCase();
                        int secondWord = obj.getTag().indexOf(" ") + 1;
                        return obj.getTag().toLowerCase().startsWith(mask) || obj.getTag().toLowerCase().startsWith(mask,secondWord);
                    }
                };


            }

            private void adjustTokenView() {
                mTagsCompletionView.setAdapter(foodTagAdapter);
                mTagsCompletionView.setTokenListener(this);
                mTagsCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
                mTagsCompletionView.allowDuplicates(false);
                mTagsCompletionView.setCursorVisible(true);
                mInputManger.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                mTagsCompletionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  mMainContainer.setVisibility(View.VISIBLE);
                        if (mTagsCompletionView.getObjects().size() > 2) {
                            mInputManger.hideSoftInputFromWindow(mTagsCompletionView.getWindowToken(), 0);
                        } else {
                            mInputManger.showSoftInput(mTagsCompletionView, 0);
                            mTagsCompletionView.setCursorVisible(true);
                        }
                    }
                });
            }

            @Override
            public void onTokenAdded(Object token) {
                if (((FoodTag) token).isRest()) {
                    TokenViewUtils.restTokenAdded(token, mTagsCompletionView, mInputManger);
                } else {
                    TokenViewUtils.foodTokenAdded(token, mTagsCompletionView, mInputManger);
                }
                List<Object> tokens = mTagsCompletionView.getObjects();
                TokenViewUtils.refreshRest(tokens, restTotal,
                        restAdapterList, restAdapter);
            }


            @Override
            public void onTokenRemoved(Object token) {
                List<Object> tokens = mTagsCompletionView.getObjects();
                TokenViewUtils.refreshRest(tokens, restTotal,
                        restAdapterList, restAdapter);
            }


}
