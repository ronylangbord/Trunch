package com.example.trunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by or on 4/3/2015.
 */



public class LinkedinConnectActivity extends Activity{

    //=========================================
    //				Constants
    //=========================================
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String linkedinHost = "api.linkedin.com";
    private static final String topCardUrl = "https://" + linkedinHost
            + "/v1/people/~:(first-name," + "last-name,headline,picture-url)?format=json";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    ObjectMapper mMapper;
    User mUser;


    //=========================================
    //				Activity Lifecycle
    //=========================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_activity);

        final Activity thisActivity = this;


        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mMapper = new ObjectMapper();

        //Initialize session
        Button liLoginButton = (Button) findViewById(R.id.linkedinButton);
        liLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                       findViewById(R.id.splash_screen).setVisibility(View.VISIBLE);
                       getUserDetailsFromLinkedin();
                    }
                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), "failed "
                                + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }, true);
            }
        });






            /*@Override
            public void onClick(View v) {
                // need to put linkdin logic here




            }
        });*/
    }

    //=========================================
    //				Private Methods
    //=========================================

    private void returnToMainActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("user", mUser);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private void saveUserAndReturnToMain(String userDetailsJson) {
        // make user
        try {
            Map map = (Map) mMapper.readValue(userDetailsJson, Object.class);
            String userStringData = (String) map.get("responseData");
            mUser = mMapper.readValue(userStringData ,User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save userId sharePrefs
        SharedPrefUtils.saveUserID(mSharedPreferences,getAndroidKey());
        // return to MainActivity
        returnToMainActivity();

        // save user to server and return to MainActivity
        //new SaveUserAsync().execute("http://www.mocky.io/v2/551f2c7ede0201b30f690e3c");
    }

    private void getUserDetailsFromLinkedin() {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(LinkedinConnectActivity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse response) {
                saveUserAndReturnToMain(response.toString());
            }

            @Override
            public void onApiError(LIApiError error) {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                Toast.makeText(getApplicationContext(), "failed: please try again..."
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<NameValuePair> getUserPostParams() {
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    private String getAndroidKey() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    //=========================================
    //		JsonDownload AsyncTask Class
    //=========================================

    private class SaveUserAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Get json from linkedin
            return RequestManger.requestPost(params[0], getUserPostParams());

        }

        @Override
        protected void onPostExecute(String json) {
            String userDetailsJson =  json;
            if (userDetailsJson != null) {
                // save userId sharePrefs
                SharedPrefUtils.saveUserID(mSharedPreferences,getAndroidKey());
                // return to MainActivity
                returnToMainActivity();

            } else {
                LISessionManager.getInstance(getApplicationContext()).clearSession();
                Toast.makeText(getApplicationContext(), "failed: please try again..."
                        , Toast.LENGTH_LONG).show();
            }
        }
    }




}
