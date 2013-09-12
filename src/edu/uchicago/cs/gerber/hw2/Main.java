package edu.uchicago.cs.gerber.hw2;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//here's a list of currency codes defined by iso4217
//http://www.xe.com/iso4217.php (get the currency codes)

public class Main extends Activity implements OnClickListener, OnItemSelectedListener {

    private Button mCalcButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;

    //we could use an associative data-structure here, rather than parellel arrays
    //but we would need the index as well, and we also use the arrays in several Android methods
    private String[] mStrNames;
    private String[] mStrCodes;

    private Spinner mSpinnerFor, mSpinnerHom;
    private String mStrForCode, mStrHomCode;

    //used for shared preferences
    static final String FOR = "FOR";
    static final String HOM = "HOM";
    static final String CURRENCY_KEY = "rhs";
    static final String ERROR_KEY = "error";

    static final String URL_WIKI = "http://en.m.wikipedia.org/wiki/ISO_4217";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String[] strFulls = getResources().getStringArray(R.array.currs);
        mStrCodes = new String[strFulls.length];
        mStrNames = new String[strFulls.length];
        assignCodes(strFulls, mStrCodes, mStrNames);

        mConvertedTextView = (TextView) findViewById(R.id.txtConverted);
        mAmountEditText = (EditText) findViewById(R.id.edtAmount);
        mCalcButton = (Button) findViewById(R.id.btnCalc);
        mSpinnerFor = (Spinner) findViewById(R.id.spnFor);
        mSpinnerHom = (Spinner) findViewById(R.id.spnHom);

        mCalcButton.setOnClickListener(this);//this obj is the listner

        ArrayAdapter<String> araAdapter = new ArrayAdapter<String>(
                this,//context
                android.R.layout.simple_spinner_item,//layout view when closed
                mStrNames//model
        );
        //layout seen when the spinner is open
        araAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerHom.setAdapter(araAdapter);
        mSpinnerFor.setAdapter(araAdapter);

        mSpinnerFor.setOnItemSelectedListener(this);
        mSpinnerHom.setOnItemSelectedListener(this);

        if(savedInstanceState == null && (PrefsMgr.getInt(this, FOR) == -99) ||
                (PrefsMgr.getInt(this, HOM) == -99)){
            mSpinnerFor.setSelection(findPositionGivenCode("cny", mStrCodes));
            mSpinnerHom.setSelection(findPositionGivenCode("usd", mStrCodes));

        }else {
            mSpinnerFor.setSelection(PrefsMgr.getInt(this, FOR));
            mSpinnerHom.setSelection(PrefsMgr.getInt(this, HOM));
        }
    }
    private int findPositionGivenCode(String strCode, String[] mStrCodes){
        for (int i = 0; i < mStrCodes.length; i++) {
//            String mStrCode = mStrCodes[i];
            if(mStrCodes[i].equalsIgnoreCase(strCode)){
                return i;
            }
        }
        return -99;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_invert:
                invertCurrencies();
                break;

            case R.id.mnu_codes:
                launchBrowser(URL_WIKI);
                break;

            case R.id.mnu_exit:
                finish();
                break;


        }

        return true;
    }


    private void assignCodes(String[] strFulls, String[] strCodes,
                             String[] strNames) {

        int nPipe;
        for (int nC = 0; nC < strFulls.length; nC++) {

            nPipe = strFulls[nC].indexOf('|');
            strCodes[nC] = strFulls[nC].substring(0, nPipe);
            strNames[nC] = strFulls[nC].substring(nPipe + 1,
                    strFulls[nC].length());
        }

    }
        //check to see if the phone is online
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;

    }
    private void invertCurrencies(){
        int nFor = mSpinnerFor.getSelectedItemPosition();
        int nHom = mSpinnerHom.getSelectedItemPosition();

        mSpinnerFor.setSelection(nHom);
        mSpinnerHom.setSelection(nFor);

        PrefsMgr.setInt(this, FOR, nHom);
        PrefsMgr.setInt(this, HOM, nFor);
    }
    private void toastNoConnectivity(){
        Toast.makeText(this,getString(R.string.no_connect), Toast.LENGTH_LONG).show();
    }

    private void launchBrowser(String strUri){
        if(isOnline())
        {
            Uri uri = Uri.parse(strUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
    }
    private class CurrencyTask extends AsyncTask<String, Void, JSONObject>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Main.this, "Calculating Conversion...", "one momment please", true);
        }

        @Override
        protected void onPostExecute(JSONObject jso) {

//            static final String CURRENCY_KEY = "rhs";
//            static final String ERROR_KEY = "error";

            String strCurrency = "";
            String strError = "";
            try {
                strCurrency = jso.getString(CURRENCY_KEY);
                strError = jso.getString(ERROR_KEY);
                if(!strCurrency.equals("")){
                    mConvertedTextView.setText(truncate(strCurrency));
                }
                else{
                    Toast.makeText(Main.this, "Error in Currency request execution"+strError, Toast.LENGTH_LONG).show();
                    mConvertedTextView.setText("");
                }
            } catch (JSONException e) {
                Toast.makeText(Main.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }//end of post execute
        private String truncate(String strOrig){
            if(strOrig.length() > 20){
                return strOrig.substring(0, 20);
            }else{
                return strOrig;
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String strAmount = params[0];

            String url = String.format(
                    "http://www.google.com/ig/calculator?hl=en&q=%s%s=?%s",
                    strAmount, mStrForCode, mStrHomCode);
            JSONObject jso = new JSONParser().getJSONFromUrl(url);

            return jso;
        }
    }
    @Override
    public void onClick(View view) {
        new CurrencyTask().execute(mAmountEditText.getText().toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long ld) {
        //when you click on the spinner itself
        switch (parent.getId()){
            case R.id.spnFor:
                mStrForCode = mStrCodes[position];
                PrefsMgr.setInt(this, FOR, position);
                break;

            case R.id.spnHom:
                mStrHomCode = mStrCodes[position];
                PrefsMgr.setInt(this, HOM, position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

// for to home

// http://www.google.com/ig/calculator?hl=en&q=1GBP=?USD
// {lhs: "1 British pound",rhs: "1.5126 U.S. dollars",error: "",icc: true}

// }
