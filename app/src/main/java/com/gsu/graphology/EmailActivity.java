package com.gsu.graphology;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.gsu.graphology.model.MyDataModel;
import com.gsu.graphology.parser.JSONParser;
import com.gsu.graphology.util.InternetConnection;
import com.gsu.graphology.util.Keys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

public class EmailActivity extends AppCompatActivity {

    private Button submitEmailBtn;
    private EditText emailAddressInput;
    private String emailAddress;

    private ArrayList<MyDataModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);



        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        list = new ArrayList<>();
        submitEmailBtn = findViewById(R.id.submit_email_btn);
        emailAddressInput = findViewById(R.id.editTextEmailAddress);
        getSupportActionBar().setTitle("SAMPLE SUBMISSION");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        submitEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailAddressInput.getText().toString().isEmpty()) {
                    checkEmail(v);
                }
                else
                {
                    Snackbar.make(findViewById(R.id.parentLayout), "Please enter email ID", Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkEmail(View view){
        if (InternetConnection.checkConnection(getApplicationContext())) {
            new GetDataTask().execute();
        } else {
            Snackbar.make(view, "Internet Connection Not Available", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Creating Get Data Task for Getting Data From Web
     */
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */

            dialog = new ProgressDialog(EmailActivity.this);
            dialog.setTitle("Please wait");
            dialog.setMessage("Checking your details");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            /**
             * Getting JSON Object from Web Using okHttp
             */
            JSONObject jsonObject = JSONParser.getDataFromWeb();

            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonObject != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonObject.length() > 0) {
                        /**
                         * Getting Array named "contacts" From MAIN Json Object
                         */
                        JSONArray array = jsonObject.getJSONArray(Keys.KEY_CONTACTS);

                        /**
                         * Check Length of Array...
                         */


                        int lenArray = array.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {

                                /**
                                 * Creating Every time New Object
                                 * and
                                 * Adding into List
                                 */
                                MyDataModel model = new MyDataModel();

                                /**
                                 * Getting Inner Object from contacts array...
                                 * and
                                 * From that We will get Name of that Contact
                                 *
                                 */
                                JSONObject innerObject = array.getJSONObject(jIndex);

                                String email = innerObject.getString(Keys.KEY_EMAIL);
                                String uniqueID = innerObject.getString(Keys.KEY_UNIQUE_ID);
//                                String image = innerObject.getString(Keys.KEY_IMAGE);
                                /**
                                 * Getting Object from Object "phone"
                                 */
                                //JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
                                //String phone = phoneObject.getString(Keys.KEY_MOBILE);

                                model.setEmail(email);
                                model.setUniqueID(uniqueID);
                                //                              model.setImage(image);

                                /**
                                 * Adding name and phone concatenation in List...
                                 */
                                list.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (list.size() > 0) {
                emailAddress = emailAddressInput.getText().toString().trim();
                if(containsName(list,emailAddress)) {
                    MyDataModel model = list.stream().filter(o -> o.getEmail().equals(emailAddress)).findFirst().get();
                    Log.i(JSONParser.TAG, "" + model.getUniqueID());
                    Intent uploadIntent = new Intent(EmailActivity.this, UploadActivity.class);
                    uploadIntent.putExtra("emailID",model.getEmail());
                    uploadIntent.putExtra("uniqueID",model.getUniqueID());
                    startActivity(uploadIntent);
                }
                else{
                    Snackbar.make(findViewById(R.id.parentLayout), "Email not Found", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(findViewById(R.id.parentLayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsName(final ArrayList<MyDataModel> list, final String name){
        return list.stream().filter(o -> o.getEmail().equals(name)).findFirst().isPresent();
    }
}