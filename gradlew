package com.example.brgt;
import android.Manifest;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

import static com.android.volley.Request.Method.POST;

public class MainActivity extends AppCompatActivity {

    public smsEnviado smsenv=new smsEnviado();

    private static final long START_TIME_IN_MILLIS = 20000;
    //private static final long START_TIME_IN_MILLIS = 15000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 10;

    private String main_URL="http://prajaoficial1.hospedagemdesites.ws/dropshopping/dev/";

    public String select_return_URL1 = main_URL + "php_select_return1.php";
    public String select_return_URL2 = main_URL + "php_select_return2.php";
    public String update_status_return_URL = main_URL + "php_update_status_return.php";
    private String insert_URL = main_URL+"php_insert.php";
    private String select_URL = main_URL+"php_select.php";
    private String update_URL = main_URL+"php_update.php";
    private String select_all_URL = main_URL+"php_select_all.php";
    public int iSends, iEnviado, iIDTabela;
    private String sTot;
    private boolean bLoop;
    private LocationManager locationManager;
    private LocationListener locationListener;

    Timer timer = new Timer();
    String txtMessage, txtphoneNo, txtnome;
    Date DiaAtual;
    TextView txtAviso,txtAviso1,mTextViewCountDown;
    EditText editId, editMessage;
    Button btnStop, btnStart, btnLoc,btnSend1,btnSend2,btnReagenda;
    RequestQueue requestQueue,requestQueue1,requestQueue2;
    private Object permissions;
    //dblshot.mysql.dbaas.com.br

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsEnviado smsenv= new smsEnviado();
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnReagenda= (Button) findViewById(R.id.btnReagenda);
/*        btnSend1=(Button) findViewById(R.id.btnSend1);
        btnSend2=(Button) findViewById(R.id.btnSend2);*/
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        requestQueue2 = Volley.newRequestQueue(getApplicationContext());

        txtAviso = (TextView) findViewById(R.id.txtAviso);
        txtAviso1 = (TextView) findViewById(R.id.txtAviso1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);

        btnStart.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                txtAviso.setText("Serviço Iniciado ! \n Aguardando Dados...");

                select_all_date();
                startTimer();
                //Task task = new Task();
                //timer.schedule(task, 50000, 50000);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                    timer.cancel();
          