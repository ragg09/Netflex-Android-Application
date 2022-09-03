package com.example.netflex_movie_api;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmActivity_CUD extends AppCompatActivity implements FilmActivity_DialogFragment_ActorChooser.DialogListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener{
    private FloatingActionButton btnadd_film;
    private Spinner spnr_choose_film_cud;
    private Button btndelete_film_cud, btnupdate_film_cud, btn_change_actor_film_crud;
    private ImageButton btn_capture_film_crud, btn_choose_film_crud, btn_calendar_film_crud;

    ImageView iv_film_cud;
    EditText etid_film_crud, ettitle_film_crud, etstory_film_crud, etduration_film_crud, etdate_film_crud;

    AlertDialog.Builder deleteWarning, updateWarning;

    private String filmURL = "http://192.168.68.103:8000/api/film/";
    private String actorURL = "http://192.168.68.103:8000/api/actor/";
    private String myUrl = "http://192.168.68.103:8000/";
    private List<String> film_list;
    private URL myURL;

    private Bitmap bitmap;
    String bitmapStr;

    SharedPreferences sp;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_cud);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        btnadd_film = (FloatingActionButton)findViewById(R.id.btnadd_film);
        spnr_choose_film_cud = (Spinner)findViewById(R.id.spnr_choose_film_cud);
        btndelete_film_cud = (Button)findViewById(R.id.btndelete_film_cud);
        btnupdate_film_cud = (Button)findViewById(R.id.btnupdate_film_cud);
        btn_change_actor_film_crud = (Button)findViewById(R.id.btn_change_actor_film_crud);

        btn_capture_film_crud = (ImageButton)findViewById(R.id.btn_capture_film_crud);
        btn_choose_film_crud = (ImageButton)findViewById(R.id.btn_choose_film_crud);
        btn_calendar_film_crud = (ImageButton)findViewById(R.id.btn_calendar_film_crud);

        iv_film_cud = (ImageView)findViewById(R.id.iv_film_cud);
        etid_film_crud = (EditText)findViewById(R.id.etid_film_crud);
        ettitle_film_crud = (EditText)findViewById(R.id.ettitle_film_crud);
        etstory_film_crud = (EditText)findViewById(R.id.etstory_film_crud );
        etduration_film_crud = (EditText)findViewById(R.id.etduration_film_crud);
        etdate_film_crud = (EditText)findViewById(R.id.etdate_film_crud);

        deleteWarning = new AlertDialog.Builder(this);
        updateWarning = new AlertDialog.Builder(this);

        bitmapStr = ""; //Will be used for trapping;

        getFilm();

        btn_calendar_film_crud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataePicker();
            }
        });

        //DELETE    DELETE  DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE
        btndelete_film_cud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ung builder sinet ko sa taas part ito ng AlertDialog
                deleteWarning.setMessage("Are you sure you want to delete this movie?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFilm();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Delete Canceled", Toast.LENGTH_SHORT).show();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = deleteWarning.create();
                //Setting the title manually
                alert.setTitle("DELETING MOVIE");
                alert.show();


                //deleteFilm();
                //Toast.makeText(FilmActivity_CUD.this, etid_film_crud.getText(), Toast.LENGTH_SHORT).show();

            }
        });
        //^^^DELETE    DELETE  DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE    DELETE
        //==========================================================================================
        //UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE
        btnupdate_film_cud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilm();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Actors_to_movie_id", "");
                editor.putString("Actors_to_movie", "");
                editor.commit();
            }
        });
        //^^^UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE    UPDATE
        //==========================================================================================
        //OPEN FILM ADD ACTIVITY    OPEN FILM ADD ACTIVITY      OPEN FILM ADD ACTIVITY
        btnadd_film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FilmActivity_CUD.this, FilmActivity_Add.class);
                startActivity(i);
                finish();
            }
        });
        //^^^OPEN FILM ADD ACTIVITY    OPEN FILM ADD ACTIVITY      OPEN FILM ADD ACTIVITY
        //==========================================================================================
        //FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER
        btn_choose_film_crud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        //^^^FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER      FILE CHOOSER
        //==========================================================================================
        //Request for camera permission
        if(ContextCompat.checkSelfPermission(FilmActivity_CUD.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((FilmActivity_CUD.this),
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        //==========================================================================================
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA
        btn_capture_film_crud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA
        //==========================================================================================
        //CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR
        btn_change_actor_film_crud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Actors_to_movie_id", "");
                editor.putString("Actors_to_movie", "");
                editor.commit();

                //implement FilmActivity_DialogFragment_ActorChooser.DialogListener
                FilmActivity_DialogFragment_ActorChooser filmActivity_dialogFragment_actorChooser = new FilmActivity_DialogFragment_ActorChooser();
                filmActivity_dialogFragment_actorChooser.show(getSupportFragmentManager(), "CHOOSE ACTOR");
            }
        });
        //^^^CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR      CHANGE ACTOR

    }//END of onCreate

    public void deleteFilm(){
        String deleteFilmbyID = filmURL+etid_film_crud.getText();
        try {
            myURL = new URL(deleteFilmbyID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                deleteFilmbyID,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        showToast("MOVIE DELETED!", R.drawable.ic_remove);
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }

    private void showDataePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+month+"-"+dayOfMonth;
        etdate_film_crud.setText(date);
    }

    public void updateFilm(){
        //Toast.makeText(ActorActivity_ViewbyID.this, bitmap.toString(), Toast.LENGTH_SHORT).show();
        String updateURl = filmURL+etid_film_crud.getText();

        //creating json object
        JSONObject jsonItem = new JSONObject();

        //putting datum/data to the newly created json object
        try {

            if(bitmapStr == ""){
                jsonItem.put("title", ettitle_film_crud.getText());
                jsonItem.put("story", etstory_film_crud.getText());
                jsonItem.put("duration", etduration_film_crud.getText());
                jsonItem.put("date_released", etdate_film_crud.getText());
                jsonItem.put("uploads", "");

                if(sp.getString("Actors_to_movie_id","") != ""){
                    jsonItem.put("actor_ids", sp.getString("Actors_to_movie_id",""));
                }else{
                    jsonItem.put("actor_ids", "");
                }
            }else{
                jsonItem.put("title", ettitle_film_crud.getText());
                jsonItem.put("story", etstory_film_crud.getText());
                jsonItem.put("duration", etduration_film_crud.getText());
                jsonItem.put("date_released", etdate_film_crud.getText());
                jsonItem.put("uploads", getStringImage(bitmap));

                if(sp.getString("Actors_to_movie_id","") != ""){
                    jsonItem.put("actor_ids", sp.getString("Actors_to_movie_id",""));
                }else{
                    jsonItem.put("actor_ids", "");
                }
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

    public void getFilm_withID(String title){
        String getFilmbyID = filmURL+title;
        try {
            myURL = new URL(getFilmbyID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                getFilmbyID,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                        //getting the movie object || PS it is not the array object so no need to loop through it
                        JSONObject movie_data = response.getJSONObject("data_movie");
                        //SETTING DATA INTO VIEW
                        Picasso.get().load(myUrl + movie_data.getString("image")).into(iv_film_cud);
                        etid_film_crud.setText(movie_data.getInt("id")+"");
                        ettitle_film_crud.setText(movie_data.getString("title"));
                        etduration_film_crud.setText(movie_data.getInt("duration")+"");
                        etdate_film_crud.setText(movie_data.getString("date_released"));
                        etstory_film_crud.setText(movie_data.getString("story"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }

    public void getFilm(){
        try {
            myURL = new URL(filmURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                filmURL,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            film_list = new ArrayList<String>();
                            JSONArray array=response.getJSONArray("data");
                            for (int i=0; i<array.length(); i++ ){
                                JSONObject ob=array.getJSONObject(i);
                                film_list.add(ob.getString("id")+" "+ob.getString("title"));
                            }
                            ArrayAdapter<String> film_adapter = new ArrayAdapter<String>(FilmActivity_CUD.this, android.R.layout.simple_spinner_item, film_list);
                            film_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnr_choose_film_cud.setAdapter(film_adapter);
                            spnr_choose_film_cud.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) FilmActivity_CUD.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String title = parent.getItemAtPosition(position).toString();
        char film_id = title.charAt(0);

        //Toast.makeText(this, film_id+"", Toast.LENGTH_SHORT).show();

        getFilm_withID(film_id+"");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //GETTING BITMAP STRING USING FILE CHOOSER
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //GETTING BITMAP STRING USING CAMERA CAMPTURE
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //FILE CHOOSER METHOD | GALLERY BY DEFAULT
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    //ON AVTIVITY RESULT NG FILE CHOOSER code:1 at IMAGE CAPTURE code:100
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Picasso.get().load(filePath).fit().centerCrop().into(iv_film_cud);
                bitmapStr = bitmap.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), captureImage);
            //Toast.makeText(this, tempUri.toString(), Toast.LENGTH_SHORT).show();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), tempUri);
                Picasso.get().load(tempUri).fit().centerCrop().into(iv_film_cud);
                bitmapStr = bitmap.toString();
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

    @Override
    public void actor_names(String names) {

    }

}
