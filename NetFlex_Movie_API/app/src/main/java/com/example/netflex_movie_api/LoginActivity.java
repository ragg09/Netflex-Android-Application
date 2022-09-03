package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etemail, etpassword;
    private Button btnlogin, btnregister;
    private CheckBox cbRememberMe;

    SharedPreferences sp;

    private String LoginURL = "http://192.168.68.103:8000/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);

        etemail = (EditText) findViewById(R.id.etemail);
        etpassword = (EditText)findViewById(R.id.etpassword);

        btnlogin = (Button)findViewById(R.id.btnlogin);
        btnregister = (Button)findViewById(R.id.btnregister);

        cbRememberMe = (CheckBox)findViewById(R.id.cbRememberMe);

        LoginDecision();

        //LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                    jsonItem.put("email", etemail.getText());
                    jsonItem.put("password", etpassword.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,    //kung anong type of request
                        LoginURL,               //url na pupuntahan nung request
                        jsonItem,               //data na ilalagay sa request
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    //saving credentials using shared preference if checkbox is checked
                                    if(cbRememberMe.isChecked()){
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("email", etemail.getText().toString());
                                        editor.putString("password", etpassword.getText().toString());
                                        editor.commit();
                                        showToast("ARCHIVE CREDENTIALS", R.drawable.ic_remember);
                                    }

                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("accessToken", response.getString("access_token"));
                                    editor.commit();


                                    String accessToken = response.getString("access_token");
                                    //Toast.makeText(getApplicationContext(), accessToken, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("access_token", accessToken);
                                    startActivity(intent);
                                    finish();

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        (error)->{
                            Toast.makeText(getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();

                        }
                );

                //adding
                //sending the request
                requestQueue.add(jsonObjectRequest);
            }
        });
        //^^^ LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN     LOGIN  LOGIN
        //==========================================================================================
        //REGISTER      REGISTER      REGISTER      REGISTER      REGISTER      REGISTER      REGISTER
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
                registerDialogFragment.show(getSupportFragmentManager(), "REGISTRATION FORM");
            }
        });
        //^^^ REGISTER      REGISTER      REGISTER      REGISTER      REGISTER      REGISTER      REGISTER


    }

    public void LoginDecision(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        if(sp.getString("email", "") == ""){
            showToast("MAG LOGIN KA MUNA", R.drawable.ic_login);
        }else{
            autoLogin();
        }
    }

    public void autoLogin(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);

        //creating json object
        JSONObject jsonItem = new JSONObject();

        //putting datum/data to the newly created json object USING THE SHARED PREFERENCE STRINGS
        try {
            jsonItem.put("email", sp.getString("email", ""));
            jsonItem.put("password", sp.getString("password", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //initialize a new request queue instance
        //to prevent multiple request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,    //kung anong type of request
                LoginURL,               //url na pupuntahan nung request
                jsonItem,               //data na ilalagay sa request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String accessToken = response.getString("access_token");
                            //Toast.makeText(getApplicationContext(), accessToken, Toast.LENGTH_LONG).show();

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("accessToken", accessToken);
                            editor.commit();


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                (error)->{
                    showToast("MAY MALING NANGYAYARE", R.drawable.ic_warning);

                }
        );

        //adding
        //sending the request
        requestQueue.add(jsonObjectRequest);

    }

    //==========================================================================================
    //CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST
    public void showToast(String message, int icon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast, (ViewGroup) findViewById(R.id.toast_root));
        TextView toastText = layout.findViewById(R.id.toast_text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(message);
        toastImage.setImageResource(icon);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    //^^^CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST

}
