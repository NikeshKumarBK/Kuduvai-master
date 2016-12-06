package in.kuduvai.cachii.kuduvai;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;

public class NewCustomer extends ActionBarActivity implements View.OnClickListener{

    Button btnSave;
    Button btnClose;

    private int _Student_Id=0;private SimpleDateFormat dateFormatter;
    private ProgressDialog pDialog, pDialog1;
    JSONParser jParser = new JSONParser();
    private static String url_all_teams = "http://crickon.esy.es/php_files/PlayerRequest.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.esy.es/php_files/SendRequest.php";

    // JSON Node names
    ArrayList<HashMap<String, String>> teamList;

    private static final String TAG_CUSTOMERNAME = "customername";
    private static final String TAG_NOOFCANS = "noofcans";
    private static final String TAG_CUSTOMERCONTACT = "CaptainId";
    private static final String TAG_CUSTOMERADDRESS = "CaptainName";
    private static final String TAG_BASEPRICE= "baseprice";

    private static final String TAG_SUCCESS = "success";
    JSONArray customers = null;

    private DatePickerDialog NewPurchaseDatePicker;
    String editTextName,editTextContactno,editTextnoofcans,editTextprice,editTextAddr;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnClose = (Button) findViewById(R.id.btnClose);
        teamList = new ArrayList<HashMap<String, String>>();
        //WarningproductsList = new ArrayList<HashMap<String, String>>();
        btnSave.setOnClickListener(this);

        btnClose.setOnClickListener(this);


        _Student_Id =0;
        Intent intent = getIntent();
        _Student_Id =intent.getIntExtra("student_Id", 0);
        SqlCommands repo = new SqlCommands(this);

        int i=repo.basePrice(1);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_customer) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClick(View view) {
        if (view == findViewById(R.id.btnSave)){
            SqlCommands repo = new SqlCommands(this);
            Can student = new Can();

            student.student_ID=_Student_Id;
            //Toast.makeText(this,"Long success",Toast.LENGTH_SHORT).show();

            if (_Student_Id==0){


                _Student_Id = repo.insert(student);
                repo.createTable(_Student_Id,student.name);
                Toast.makeText(this, "New Customer Inserted "+_Student_Id, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(NewCustomer.this,MainActivity.class);
                startActivity(intent);
                finish();

            }else{

                repo.update(student);
                Toast.makeText(this,"Student Record updated",Toast.LENGTH_SHORT).show();
            }
        }else if (view== findViewById(R.id.btnClose)){
            Intent intent=new Intent(NewCustomer.this, CustomerList.class);
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
            pDialog1 = new ProgressDialog(NewCustomer.this);
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

            params.add(new BasicNameValuePair(TAG_CUSTOMERNAME,editTextName));
            params.add(new BasicNameValuePair(TAG_CUSTOMERADDRESS, editTextAddr));
            params.add(new BasicNameValuePair(TAG_NOOFCANS, editTextnoofcans));
            params.add(new BasicNameValuePair(TAG_BASEPRICE, editTextprice));

            params.add(new BasicNameValuePair(TAG_CUSTOMERCONTACT, editTextContactno));



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
                    Toast.makeText(NewCustomer.this, "Request already sent", Toast.LENGTH_SHORT).show();
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
            params.add(new BasicNameValuePair(TAG_CUSTOMERNAME,editTextName ));
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
                    customers= json.getJSONArray(TAG_CUSTOMERNAME);

                    // looping through All Products
                    for (int i = 0; i < customers.length(); i++) {
                        JSONObject c = customers.getJSONObject(i);

                        // Storing each json item in variable

                        String customername = c.getString(TAG_CUSTOMERNAME);
                        String customercontact= c.getString(TAG_CUSTOMERCONTACT);
                        String customeraddress= c.getString(TAG_CUSTOMERADDRESS);


                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_CUSTOMERNAME, customername);
                        map.put(TAG_CUSTOMERCONTACT, customercontact);
                        map.put(TAG_CUSTOMERADDRESS, customeraddress);

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
                Toast.makeText(NewCustomer.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
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
                            NewCustomer.this, teamList,
                            R.layout.activity_delivery_status, new String[] {
                            TAG_CUSTOMERADDRESS,TAG_CUSTOMERNAME, TAG_CUSTOMERCONTACT},
                            new int[] { R.id.customer_name, R.id.customer_contact});
                    // updating listview


                }
            });


        }
    }
