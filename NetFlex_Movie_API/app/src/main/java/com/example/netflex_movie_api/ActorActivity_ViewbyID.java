package com.example.netflex_movie_api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ActorActivity_ViewbyID extends AppCompatActivity {
    EditText etactor_view_name, etactor_view_note, etactor_view_id;
    ImageView iv_actor_view;
    Button btnactor_update, btnactor_delete, btnactor_change_image;
    String bitmapStr;
    String urlActor = "http://192.168.68.103:8000/api/actor/";
    String urlString = "http://192.168.68.103:8000/";

    URL myURL;

    SharedPreferences sp;
    String accessToken;

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_viewbyid);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        etactor_view_id = (EditText)findViewById(R.id.etactor_view_id);
        etactor_view_name = (EditText)findViewById(R.id.etactor_view_name);
        etactor_view_note = (EditText)findViewById(R.id.etactor_view_note);
        iv_actor_view = (ImageView)findViewById(R.id.iv_actor_view);
        btnactor_change_image = (Button)findViewById(R.id.btnactor_change_image);
        btnactor_update = (Button)findViewById(R.id.btnactor_update);
        btnactor_delete = (Button)findViewById(R.id.btnactor_delete);

        //GETTING DATA FROM INTENT
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String note = i.getStringExtra("note");
        String image = i.getStringExtra("image");
        String id = i.getStringExtra("actor_id");
        bitmapStr = ""; //Will be used for trapping;


        if (image == "") {
            iv_actor_view.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get().load(urlString + image).into(iv_actor_view);
        }

        // +"" This is to convert int to String type, otherwise it will report an error
        etactor_view_id.setText(id);
        etactor_view_name.setText(name);
        etactor_view_note.setText(note);

        //DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE
        btnactor_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating new url from base URL and ID
                String searchURL = urlActor+etactor_view_id.getText();

                //checking url
                try {
                    myURL = new URL(searchURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.DELETE,     //kung anong type of request
                        searchURL,                  //url na pupuntahan nung request
                        null,        //since get method ito, walang data na pinapasa kaya null lang
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                showToast("ACTOR DELETED!", R.drawable.ic_actor_remove);
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
                    public Map<String, String> getHeaders() throws AuthFailureError{
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
        //FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER
        btnactor_change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        //FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER
        //==========================================================================================
        //UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE
        btnactor_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ActorActivity_ViewbyID.this, bitmap.toString(), Toast.LENGTH_SHORT).show();
                String updateURl = urlActor+etactor_view_id.getText();

                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                    if(bitmapStr == ""){
                        jsonItem.put("name", etactor_view_name.getText());
                        jsonItem.put("note", etactor_view_note.getText());
                        jsonItem.put("uploads", "");
                    }else{
                        jsonItem.put("name", etactor_view_name.getText());
                        jsonItem.put("note", etactor_view_note.getText());
                        jsonItem.put("uploads", getStringImage(bitmap));
                    }

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

                                showToast("ACTOR UPDATED", R.drawable.ic_update);
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



    }//END NG ONCREATE

    //GETTING BITMAP STRING
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //FILE CHOOSER METHOD | GALLERY BY DEFAULT
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
    }

    //ON AVTIVITY RESULT NG FILE CHOOSER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmapStr = bitmap.toString();
                //Setting the Bitmap to ImageView
                //Picasso.get().load(bitmap.into(imageView);
                //Picasso.get().load(filePath).resize(100, 100).
                Picasso.get().load(filePath).fit().centerCrop().into(iv_actor_view);
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
