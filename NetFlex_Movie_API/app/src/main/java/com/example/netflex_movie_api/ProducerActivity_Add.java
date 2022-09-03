package com.example.netflex_movie_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ProducerActivity_Add extends AppCompatActivity {
    EditText etprodudcer_name, etproducer_email, etproducer_website;
    Button btnactor_save;

    private String producerURLadd = "http://192.168.68.103:8000/api/producer/";
    private URL myURL;

    SharedPreferences sp;
    String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_add);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        etprodudcer_name = (EditText) findViewById(R.id.etproducer_name);
        etproducer_email = (EditText)findViewById(R.id.etproducer_email);
        etproducer_website = (EditText)findViewById(R.id.etproducer_website);
        btnactor_save = (Button)findViewById(R.id.btnactor_save);

        btnactor_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                    jsonItem.put("name", etprodudcer_name.getText());
                    jsonItem.put("email", etproducer_email.getText());
                    jsonItem.put("website", etproducer_website.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //checking url
                try {
                    myURL = new URL(producerURLadd);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,     //kung anong type of request
                        producerURLadd,                  //url na pupuntahan nung request
                        jsonItem,                   //data na ilalagay sa request
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                finish();
                                showToast("SAVED", R.drawable.ic_producer);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();
                            }
                        }
                ){
                    //ito na ung pag si-set ng authorization token
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", "Bearer "+accessToken);
                        return params;
                    }
                };

                //adding
                //sending the request
                requestQueue.add(jsonObjectRequest);
            }
        });
    }//END OF onCREATE

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
    //^^^CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAS

}
