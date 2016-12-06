package in.kuduvai.cachii.kuduvai;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NewBooking extends ListActivity implements View.OnClickListener {

    // Button btnAdd,btnGetAll;

    Button btnBookNow;
    EditText editTextDateBooking;
    int i;

    private DatePickerDialog NewPurchaseDatePicker;
    String student_Id,student_name,editTextnoofcansBooking;
    int flag=0;
    private SimpleDateFormat dateFormatter;
    private ProgressDialog pDialog, pDialog1;
    JSONParser jParser = new JSONParser();

    private static String url_all_teams = "http://crickon.esy.es/php_files/PlayerRequest.php";

    // url to get all products list
    private static String url_send_request = "http://crickon.esy.es/php_files/SendRequest.php";

    // JSON Node names
    ArrayList<HashMap<String, String>> studentList;
    private static final String TAG_DUEDATEBOOKING = "duedatebooking";
    private static final String TAG_CUSTOMERNAME= "customername";
    private static final String TAG_CUSTOMERID = "customerId";
    private static final String TAG_NUMBEROFCANS = "noofcans";
    private static final String TAG_SUCCESS = "Success";

    JSONArray customers = null;

    ArrayList<HashMap<String, String>> StudentList;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        editTextDateBooking = (EditText) findViewById(R.id.editTextDateBooking);

        btnBookNow = (Button) findViewById(R.id.btnBookNow);

        btnBookNow.setOnClickListener(this);

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(date);
        editTextDateBooking.setText(dateString);

        setDateTimeField();

        SqlCommands repo = new SqlCommands(this);


        ArrayList<HashMap<String, String>>
                studentList = repo.getStudentList();
        studentList = new ArrayList<>();
        if (studentList.size() != 0) {
            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SqlCommands repo = new SqlCommands(getApplicationContext());


                }

            });
            ListAdapter adapter = new SimpleAdapter(NewBooking.this, studentList, R.layout.view_customer, new String[]{"id", "name"}, new int[]{R.id.customer_id, R.id.customer_name});

            setListAdapter(adapter);
        } else {
            Toast.makeText(this, "No Customer!", Toast.LENGTH_SHORT).show();
        }

    }

    /*public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    private void setDateTimeField() {
        editTextDateBooking.setOnClickListener(this);


        Calendar newCalendar = Calendar.getInstance();
        NewPurchaseDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editTextDateBooking.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {

        if (view == editTextDateBooking) {
            NewPurchaseDatePicker.show();
        }

        if (view == findViewById(R.id.btnBookNow)) {
            final SqlCommands repo = new SqlCommands(this);
            final Can student = new Can();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewBooking.this);

            // Setting Dialog Title
            alertDialog.setTitle("Confirm Booking...");

            // Setting Dialog Message


            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    // Write your code here to invoke YES event
                    student.dueDateBooking = String.valueOf(editTextDateBooking.getText().toString());

                    repo.insertBooking(student);

                    Toast.makeText(getApplicationContext(), "New Booking made for " + student.customerNameBooking
                            , Toast.LENGTH_SHORT).show();

                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    Toast.makeText(getApplicationContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
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


    class SendRequest extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = new ProgressDialog(NewBooking.this);
            pDialog1.setMessage("Sending Request...");
            pDialog1.setIndeterminate(false);
            //pDialog.setCancelable(true);
            //pDialog1.show();
        }

        protected String doInBackground(String... args) {


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair(TAG_CUSTOMERID,student_Id ));
            params.add(new BasicNameValuePair(TAG_CUSTOMERNAME,student_name ));
            params.add(new BasicNameValuePair(TAG_NUMBEROFCANS,editTextnoofcansBooking ));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_send_request,
                    "POST", params);

            // check json success tag
            try {
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
                    Toast.makeText(NewBooking.this, "Request already sent", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            //pDialog1.dismiss();

        }
    }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_CUSTOMERID,student_Id ));
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
                    customers = json.getJSONArray(TAG_CUSTOMERNAME);

                    // looping through All Products
                    for (int i = 0; i < customers.length(); i++) {
                        JSONObject c = customers.getJSONObject(i);

                        // Storing each json item in variable

                        String student_Id = c.getString(TAG_CUSTOMERID);
                        String student_name= c.getString(TAG_CUSTOMERNAME);
                        String editTextnoofcansBooking= c.getString(TAG_NUMBEROFCANS);



                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_CUSTOMERNAME, student_Id);
                        map.put(TAG_CUSTOMERNAME, student_name);
                        map.put(TAG_NUMBEROFCANS, editTextnoofcansBooking);

                        // adding HashList to ArrayList
                        studentList.add(map);
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
                Toast.makeText(NewBooking.this,"No Teams found in this Area!!",Toast.LENGTH_SHORT).show();
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
                            NewBooking.this, studentList,
                            R.layout.activity_customer_list, new String[] {
                            TAG_CUSTOMERID, TAG_CUSTOMERNAME,TAG_DUEDATEBOOKING,TAG_NUMBEROFCANS},
                            new int[] { R.id.customer_id, R.id.customer_contact, R.id.customer_id_booking, R.id.customer_name, R.id.customer_contact});
                    // updating listview
                    setListAdapter(adapter1);
                    //setListAdapter(adapter);
                }
            });


        }
    }


