package com.example.tazeen.androidproject;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;import android.app.ProgressDialog;import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;import org.json.JSONObject;
import android.telephony.TelephonyManager;
import android.content.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextUtils;

import com.example.DBsqlite.MyDbHelper;


public class Login_Screen extends Activity
{
    //IMEI( International Mobile Equipment Identity )
    String str_UserName , str_Password , strIMEI;
    EditText edittxtUserName , editTextPassWord ;
    Button btnLogin;  private ProgressDialog pDialog;
    String result;
    MyDbHelper dbhelper;
    String strAuthentication_Token;
    String strEmail;
    String strName;
    String strPassword ;
    String strPhone ;
    String strRole ;
    String strStatus ;
    String strUserId ;
    String strUserName ;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);

        dbhelper = new MyDbHelper(this);

        TelephonyManager    telephonyManager = ( TelephonyManager )getSystemService( Context.TELEPHONY_SERVICE );
        strIMEI = telephonyManager.getDeviceId();

        init();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Vallidations();
                new GetLoginDetails().execute();
            }
        });
    }



    /************************************************************************************************************************************************************************************/
    private class GetLoginDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Login_Screen.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getLoginDetails();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

        }

    }
/****************************************************************************************************************************************************************/

    public void getLoginDetails()
    {

        String strUrl = "http://103.24.4.60/CLASSNK1/MobileService.svc/Get_UserDetails/user_name/"+str_UserName+"/user_password/"+str_Password+"/DeviceID/"+strIMEI;

        InputStream inputStream = null;

        try {


            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(strUrl));
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        }

        catch (Exception e)
        {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        String jsonStr = result;
        if (jsonStr != null)
        {
            try
            {//this is try block
                JSONObject jsonObj = new JSONObject(jsonStr);
                String jsonResult = jsonObj.toString().trim();
                Log.e("jsonResult " , " = " + jsonResult);

                JSONArray jsonArray = jsonObj.getJSONArray("Get_UserDetailsResult");
                String strJsonArray = jsonArray.toString();
                Log.e("strJsonArray " , " = " + strJsonArray ) ;

                for(int i = 0; i < jsonArray .length(); i++)
                {
                    JSONObject jobjLoginDetails = jsonArray.getJSONObject(i);

                     strAuthentication_Token = jobjLoginDetails.getString("Authentication_Token");
                     strEmail = jobjLoginDetails.getString("Email");
                     strName = jobjLoginDetails.getString("Name");
                     strPassword = jobjLoginDetails.getString("Password");
                     strPhone = jobjLoginDetails.getString("Phone");
                     strRole = jobjLoginDetails.getString("Role");
                     strStatus = jobjLoginDetails.getString("Status");
                     strUserId = jobjLoginDetails.getString("User_ID");
                     strUserName = jobjLoginDetails.getString("User_Name");

                    Log.e("strAuthenticationToken " , " = " + strAuthentication_Token ) ;
                    Log.e("strEmail " , " = " + strEmail ) ;
                    Log.e("strName " , " = " + strName ) ;
                    Log.e("strPassword " , " = " + strPassword ) ;
                    Log.e("strPhone " , " = " + strPhone ) ;
                    Log.e("strRole " , " = " + strRole ) ;
                    Log.e("strStatus " , " = " + strStatus ) ;
                    Log.e("strUserId " , " = " + strUserId ) ;
                    Log.e("strUserName " , " = " + strUserName ) ;

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        /*strPhone = "abc" ; strRole = "xyz";*/
        dbhelper.InsertUserDetails(strAuthentication_Token, strEmail, strName, strPassword, strPhone, strRole, strStatus, strUserId, strUserName);

        Log.e(" Data Inserted " , " Succesfully !!!!!!!!!!!! " );


    }
    /****************************************************************************************************************************************************************/


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /****************************************************************************************************************************************************************/
    public void Vallidations()
    {


        str_UserName = edittxtUserName.getText().toString();

        if(TextUtils.isEmpty(str_UserName)) {
            edittxtUserName.setError("UserName can not be empty . ");
            return;
        }

        str_Password = editTextPassWord.getText().toString().trim();
        if(TextUtils.isEmpty(str_Password)) {
            editTextPassWord.setError("Password can not be empty . ");
            return;
        }
    }
    /****************************************************************************************************************************************************************/
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
    /****************************************************************************************************************************************************************/
    public void init()
    {
         edittxtUserName = (EditText)findViewById(R.id.editText_UserName);
         editTextPassWord  = (EditText)findViewById(R.id.editText_Password);
         btnLogin = (Button)findViewById(R.id.button_Login);
    }
    /****************************************************************************************************************************************************************/
    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    /****************************************************************************************************************************************************************/
    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

}
//http://android-er.blogspot.in/2014/12/swipe-to-slide-animated-activity.html