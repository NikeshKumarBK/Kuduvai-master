package in.kuduvai.cachii.kuduvai;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NewPurchase extends ActionBarActivity implements View.OnClickListener {

    EditText editTextDatePurchase;

    Button btnSavePurchase, btnClosePurchase;

    private SimpleDateFormat dateFormatter;
    private ProgressDialog pDialog, pDialog1;
    JSONParser jParser = new JSONParser();
    private static String url_all_teams = "http://crickon.esy.es/php_files/PlayerRequest.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.esy.es/php_files/SendRequest.php";

    // JSON Node names
    ArrayList<HashMap<String, String>> teamList;

    private static final String TAG_DATEOFPURCHASE = "dateofpurchase";
    private static final String TAG_CANINTAKE = "canintake";
    private static final String TAG_CANGIVEN= "cangiven";
    private static final String TAG_AMOUNTPAID = "amountpaid";
    private static final String TAG_BALANCE = "balance";

    private static final String TAG_SUCCESS = "Success";

    JSONArray customers = null;

    private DatePickerDialog NewPurchaseDatePicker;
    String  editTextCanIntakePurchase, editTextCanGivenPurchase, editTextAmountPaidPurchase, editTextBalancePurchase;
    int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchase);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        editTextDatePurchase = (EditText) findViewById(R.id.editTextDatePurchase);


        btnSavePurchase = (Button) findViewById(R.id.btnSavePurchase);
        btnClosePurchase = (Button) findViewById(R.id.btnClosePurchase);

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(date);
        editTextDatePurchase.setText(dateString);

        setDateTimeField();

        btnClosePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPurchase.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSavePurchase.setOnClickListener(this);


    }

    private void setDateTimeField() {
        editTextDatePurchase.setOnClickListener(this);


        Calendar newCalendar = Calendar.getInstance();
        NewPurchaseDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDatePurchase.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v == editTextDatePurchase) {
            NewPurchaseDatePicker.show();
        }
        if (v == findViewById(R.id.btnSavePurchase)) {
            SqlCommands repo = new SqlCommands(this);
            Can student = new Can();

            student.newPurchaseDate = String.valueOf(editTextDatePurchase.getText().toString());

            repo.insertPurchase(student);

            Toast.makeText(this, "New purchase made on " + student.newPurchaseDate, Toast.LENGTH_SHORT).show();

            int i = repo.canstock(1);

            int update = i + student.canIntakePurchase;
            student.stockid = 1;
            student.canstock = update;

            repo.updateCanStock(student);

            i = repo.emptycans(1);
            update = i - student.canGivenPurchase;

            student.emptycans = update;

            repo.updateEmptyCan(student);

            Intent intent = new Intent(NewPurchase.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    class SendRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = new ProgressDialog(NewPurchase.this);
            pDialog1.setMessage("Sending Request...");
            pDialog1.setIndeterminate(false);
            //pDialog.setCancelable(true);
            //pDialog1.show();
        }

        /**
         * Saving product
         */
        protected String doInBackground(String... args) {


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(TAG_CANINTAKE, editTextCanIntakePurchase));
            params.add(new BasicNameValuePair(TAG_CANGIVEN,editTextCanGivenPurchase));
            params.add(new BasicNameValuePair(TAG_AMOUNTPAID, editTextAmountPaidPurchase));
            params.add(new BasicNameValuePair(TAG_BALANCE,editTextBalancePurchase));



        // Notice that update product url accepts POST method
        JSONObject json = jParser.makeHttpRequest(url_send_request,
                "POST", params);

        // check json success tag
        try

        {
            boolean success = json.getBoolean(TAG_SUCCESS);
            Log.e("Check", "" + success);

            if (!success) {
                // successfully updated
                Intent i = getIntent();
                // send result code 100 to notify about product update
                setResult(100, i);
                finish();
            } else {
                // failed to update product
                Toast.makeText(NewPurchase.this, "Request already sent", Toast.LENGTH_SHORT).show();
            }
        }

        catch(
        JSONException e
        )

        {
            e.printStackTrace();
        }

        return null;
    }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            //pDialog1.dismiss();

        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_BALANCE,editTextBalancePurchase));
            //params.add(new BasicNameValuePair(TAG_STATUS, status));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_teams, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    customers = json.getJSONArray(TAG_AMOUNTPAID);
                    customers= json.getJSONArray(TAG_BALANCE);

                    // looping through All Products
                    for (int i = 0; i < customers.length(); i++) {
                        JSONObject c = customers.getJSONObject(i);

                        // Storing each json item in variable

                        String canintake = c.getString(TAG_CANINTAKE);
                        String cangiven= c.getString(TAG_CANGIVEN);

                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_CANINTAKE, canintake);
                        map.put(TAG_CANGIVEN, cangiven);

                        // adding HashList to ArrayList
                        teamList.add(map);
                        Log.e("Check", "Added to teamlist");

                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    flag=1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(flag==1)
            {
                Toast.makeText(NewPurchase.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
            }



            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                        /*SpecialAdapter adapter = new SpecialAdapter(AreaTstHome.this,WarningproductsList,R.layout.single_row_issue_solved,new String[] { TAG_TICKET_NO,
                                TAG_SITE_NAME,TAG_ISSUE,TAG_DATE,TAG_FIELD,TAG_STATUS},
                                new int[] { R.id.pid, R.id.allPlaceName, R.id.allLandMark,R.id.allProb,R.id.allAltRoute ,R.id.allSolved});*/
                    ListAdapter adapter1 = new SimpleAdapter(
                            NewPurchase.this, teamList,
                            R.layout.activity_purchase_list, new String[] {
                            TAG_CANGIVEN, TAG_CANINTAKE,TAG_AMOUNTPAID,TAG_BALANCE},
                            new int[] {  R.id.editTextcanGiven, R.id.editTextCanIntakePurchase, R.id.editTextAmountPaidPurchase, R.id.editTextBalancePurchase});
                    // updating listview

                    //setListAdapter(adapter);
                }
            });


        }
    }
