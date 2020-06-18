package br.com.bright;

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

    private String main_URL="http://prajaoficial1.hospedagemdesites.ws/dropshopping/";

    public String select_return_URL1 = main_URL + "php_select_return1.php";
    public String select_return_URL2 = main_URL + "php_select_return2.php";
    public String update_status_return_URL = main_URL + "php_update_status_return.php";
    private String insert_URL = main_URL+"php_insert.php";
    private String select_URL = main_URL+"php_select.php";
    private String update_URL = main_URL+"php_update.php";
    private String select_all_URL = main_URL+"php_select_all.php";
    public int iSends, iEnviar, iIDTabela;
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
                iSends=0;
                txtAviso.setText("Serviço Iniciado ! \n Aguardando Dados...");
                txtAviso1.setText("Total de SMS A Enviar : 0 \n Total de SMS Enviado : 0");
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
                    finish();
                    //mCountDownTimer.cancel();
                    System.exit(0);
                }
            }
        });

        btnReagenda.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                startActivity(new Intent( MainActivity.this, act_reagendas.class));
            }
        });
    }

    public void vUpdateTable(final int iID) {
        StringRequest request = new StringRequest(Request.Method.POST,
                update_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> parameters = new HashMap<String,String>();
                parameters.put("id",Integer.toString(iID));
                return parameters;
            }
        };
        requestQueue.add(request);
    }
    protected <MultipleMsg> void sendSMSMessage() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, select_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response_0) {
                try {
                    int i = 0;
                    JSONArray usuarios = response_0.getJSONArray("usuarios");
                    JSONObject usuario = usuarios.getJSONObject(i);
                    txtnome = usuario.getString("nome");
                    txtphoneNo= usuario.getString("telefone");
                    iIDTabela=usuario.getInt("id");

                    txtMessage="BRIGHT ODONTOLOGIA:\nPrezado(a) "+txtnome+"!\n";
                    //txtMessage=txtMessage+"Informamos para que no próximo dia "+ usuario.getString("ag_data")+" às "+usuario.getString("ag_hora")+"\n";

                    txtMessage=txtMessage +"Lembramos que dia "+ usuario.getString("ag_data")+" as "+usuario.getString("ag_hora") + " horas ";
                    txtMessage=txtMessage + "o(a) Sr(a) tem uma consulta agendada em nosso consultório.\n";
                    txtMessage=txtMessage + " \n";
                    txtMessage=txtMessage + "Atenciosamente, \n";
                    txtMessage=txtMessage + "Bright Assist. Odont.\n";
                    //txtMessage=txtMessage + "Assistência Odontológica \n\n";

                    txtMessage=txtMessage + "Digite Apenas a letra C ou R: \n";
                    txtMessage=txtMessage + "C - Para CONFIRMAR.\n";
                    txtMessage=txtMessage + "R - Para REAGENDAR.\n\n";
                    txtMessage=txtMessage + "Acesse o Link abaixo, para ter a localização correta!.\n";

                    //Sem Avaliação
                    txtMessage=txtMessage + "https://goo.gl/maps/Grqr6PjqGNmivu367";
                    //Com Avaliação
                    //txtMessage=txtMessage + "https://goo.gl/maps/77ChZd51z7e3KyaS7";

                    txtMessage=txtMessage + "\n\n\nAcesse nosso WhatsApp!\n";
                    txtMessage=txtMessage + "https://wa.me/5511970735606";

                    txtMessage=txtMessage + "\n\nTels: 3685-4641 / 3683-3838 ";

                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> parts = smsManager.divideMessage (txtMessage);
                    //int numParts = parts.size();
                    smsManager.sendMultipartTextMessage(txtphoneNo,null,parts,null,null);

                    iSends=iSends+1;

                    txtAviso.setText("Serviço Iniciado ! \n Enviado para: "+txtnome+ "\n" + txtphoneNo);

                    vUpdateTable(iIDTabela);
                    txtAviso1.setText("Total de SMS A Enviar : " + Integer.toString(iEnviar)+"\n Total de SMS Enviado : "+ iSends );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public <MultipleMsg> void sendSMSMessageReturn_1(final String statusRetorno, final String mMessage) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, select_return_URL1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response_1) {
                        try {
                            int i = 0;
                            JSONArray marcacoes = response_1.getJSONArray("marcacoes");
                            JSONObject marcacao = marcacoes.getJSONObject(i);
                            String phoneNo = marcacao.getString("telefone");
                            iIDTabela = marcacao.getInt("ID");
                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> parts_r1 = smsManager.divideMessage(mMessage);
                            //int numRet1 = parts.size();
                            smsManager.sendMultipartTextMessage(phoneNo, null, parts_r1, null, null);
                            vUpdateReturn(iIDTabela, statusRetorno);
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue1.add(jsonObjectRequest);
    }

    public <MultipleMsg> void sendSMSMessageReturn_2(final String statusRetorno, final String mMessage) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, select_return_URL2,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response_2) {
                        int i = 0;
                        try {
                            JSONArray marcacoes = response_2.getJSONArray("marcacoes");
                            JSONObject marcacao = marcacoes.getJSONObject(i);
                            String phoneNo = marcacao.getString("telefone");
                            iIDTabela = marcacao.getInt("ID");
                            SmsManager smsManager = SmsManager.getDefault();
                            ArrayList<String> parts_r2 = smsManager.divideMessage(mMessage);
                            //int numRet2 = parts.size();
                            smsManager.sendMultipartTextMessage(phoneNo, null, parts_r2, null, null);
                            vUpdateReturn(iIDTabela, statusRetorno);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue2.add(jsonObjectRequest);
    }

    public void vUpdateReturn(final int iID, final String statusRetorno) {

        StringRequest request = new StringRequest(POST,
                update_status_return_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response_3) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", Integer.toString(iID));
                parameters.put("status_retorno", statusRetorno);
                return parameters;
            }
        };
        requestQueue.add(request);
    }
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                sendSMSMessage();

                //sendSMSMessageReturn_1("C","Obrigado !\nCaso haja qualquer alteração na sua confirmação, favor entrar em contato!"); //gravar retorno para Confirmação de Consula
                //sendSMSMessageReturn_2("R","OK !\nPor gentileza, entrar em contato conosco para reagendar sua consulta.!"); //gravar retorno para Reagendamento de Consula

                //mCountDownTimer.cancel();
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                select_all_date();

                startTimer();
            }
        }.start();

    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    public void select_all_date() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                select_all_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response_4) {
                int ii = 0;

                try {
                    JSONArray all_usuarios = response_4.getJSONArray("all_usuarios");
                    JSONObject all_usuario = all_usuarios.getJSONObject(ii);
                    iEnviar=all_usuario.getInt("iTot");
                    txtAviso1.setText("Total de SMS A Enviar : " + Integer.toString(iEnviar)+"\n Total de SMS Enviado : 0 "  );

                    if (iEnviar == 0) {
                        txtAviso1.setText("Total de SMS A Enviar : " + Integer.toString(iEnviar)+"\n Total de SMS Enviado : 0 "  );
                        txtAviso.setText("Serviço Iniciado ! \n Aguardando Dados...");
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

/*
    class Task extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    select_all_date();

                    sendSMSMessage();

                    // if(txtAviso.getVisibility() == View.VISIBLE){
                    //     txtAviso.setVisibility(View.INVISIBLE);
                    // }else{
                    //     txtAviso.setVisibility(View.VISIBLE);
                    // }

                }
            });
        }
    }
*/

}
