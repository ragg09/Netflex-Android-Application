package com.example.netflex_movie_api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

public class ProducerActivity_ViewbyID  extends AppCompatActivity {
    EditText etproducer_view_name, etproducer_view_email, etproducer_view_id, etproducer_view_website;

    Button btnproducer_update, btnproducer_delete;

    String urlProducer = "http://192.168.68.103:8000/api/producer/";
    String urlString = "http://192.168.68.103:8000/";

    URL myURL;

    SharedPreferences sp;
    String accessToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_viewbyid);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        btnproducer_delete = (Button)findViewById(R.id.btnproducer_delete);
        btnproducer_update = (Button)findViewById(R.id.btnproducer_update);

        etproducer_view_id = (EditText)findViewById(R.id.etproducer_view_id);
        etproducer_view_name = (EditText)findViewById(R.id.etproducer_view_name);
        etproducer_view_email = (EditText)findViewById(R.id.etproducer_view_email);
        etproducer_view_website = (EditText)findViewById(R.id.etproducer_view_website);

        //GETTING DATA FROM INTENT
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String email = i.getStringExtra("email");
        String website = i.getStringExtra("website");
        String id = i.getStringExtra("producer_id");

        etproducer_view_id.setText(id);
        etproducer_view_name.setText(name);
        etproducer_view_email.setText(email);
        etproducer_view_website.setText(website);

        //DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE
        btnproducer_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating new url from base URL and ID
                String deleteURL = urlProducer+etproducer_view_id.getText();

                //checking url
                try {
                    myURL = new URL(deleteURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.DELETE,     //kung anong type of request
                        deleteURL,                  //url na pupuntahan nung request
                        null,        //since get method ito, walang data na pinapasa kaya null lang
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                showToast("PRODUCER DELETED!", R.drawable.ic_remove);
                                finish();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                showToast("MAY MALING NANGYAYARE", R.drawable.ic_warning);
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
        //^^^DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE
        //=========================================================================================
        //UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE
        btnproducer_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ActorActivity_ViewbyID.this, bitmap.toString(), Toast.LENGTH_SHORT).show();
                String updateURl = urlProducer+etproducer_view_id.getText();

                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                        jsonItem.put("name", etproducer_view_name.getText());
                        jsonItem.put("email", etproducer_view_email.getText());
                        jsonItem.put("website", etproducer_view_website.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //checking url
                try {
                    myURL = new URL(updateURl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.PUT,     //kung anong type of request
                        updateURl,                  //url na pupuntahan nung request
                        jsonItem,                   //data na ilalagay sa request
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                showToast("PRODUCER UPDATED", R.drawable.ic_update);
                                finish();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                showToast("MAY MALING NANGYAYARE", R.drawable.ic_warning);
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
        //^^^UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE


    }//END of onCreate

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
