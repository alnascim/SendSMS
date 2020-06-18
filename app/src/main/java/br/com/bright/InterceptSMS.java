package br.com.bright;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.*;
import static com.android.volley.Request.Method.POST;

public class InterceptSMS extends BroadcastReceiver {
    private static final int PERMISSION_REQUEST_CODE = 1;
    public String main_URL = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/";
    public String insert_URL_Return = main_URL + "php_insert_return.php";

    public String TAG = InterceptSMS.class.getSimpleName();
    public String NUMBER_FILTER = "Teste";
    public int iIDTabela;

    String txtMessage, txtnome;
    TextView texto;
    String phone = "";
    String bodyMessage = "",bm="";
    String message = "";
    RequestQueue requestQueue, requestQueue1, requestQueue2;

    @Override
    public void onReceive(Context context, Intent intent) {
        requestQueue = Volley.newRequestQueue(context);
        requestQueue1 = Volley.newRequestQueue(context);
        requestQueue2 = Volley.newRequestQueue(context);

        Bundle extras = intent.getExtras();

        if (extras != null) {
            Object[] smsExtra = (Object[]) extras.get("pdus");
            for (int i = 0; i < smsExtra.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                phone = sms.getDisplayOriginatingAddress();
                bodyMessage = sms.getMessageBody();

            }

            final String bm=sel_prob_sms_return(bodyMessage.toUpperCase());

            if (bm.equals("C")) {

                final String finalPhone = phone;
                StringRequest request = new StringRequest(POST,
                        insert_URL_Return, new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //sendSMSMessageReturn_1("C", "Obrigado ! \nCaso haja qualquer alteração na sua confirmação, favor entrar em contato!"); //gravar retorno para Confirmação de Consula

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        String sPhone = finalPhone;
                        sPhone = sPhone.replace("+55", "");
                        parameters.put("telefone", sPhone);
                        parameters.put("status_retorno", bm);
                        parameters.put("benviado", "0");
                        return parameters;
                    }
                };
                requestQueue.add(request);
            }
            if (bm.equals("R"))  {
                final String finalPhone = phone;
                StringRequest request = new StringRequest(POST,
                        insert_URL_Return, new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //sendSMSMessageReturn_2("R", "OK !\nEntraremos em contato para reagendar a sua consulta.!"); //gravar retorno para Reagendamento de Consula
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        String sPhone = finalPhone;
                        sPhone = sPhone.replace("+55", "");
                        parameters.put("telefone", sPhone);
                        parameters.put("status_retorno", bm);
                        parameters.put("benviado", "0");
                        return parameters;
                    }
                };
                requestQueue.add(request);
            }

        }

    }
    String sel_prob_sms_return(final String bodyMess) {
        String sRet="";
        if (bodyMess.equals("R")) {
                sRet="R"; }
        else if (bodyMess.equals("REAGENDAR")) {
                sRet="R"; }
        else if (bodyMess.equals("C")) {
                sRet="C";}
        else if (bodyMess.equals("SIM")) {
            sRet="C";}
        else if (bodyMess.equals("OK")) {
            sRet="C";}
        else if (bodyMess.equals("PODE SIM")) {
            sRet="C";}
        else if (bodyMess.equals("PODE CONFIRMAR")) {
            sRet="C";}

        return sRet;
    }





/*    public String sel_prob_sms_return(final String bodyMess) {
        final int i = 0;
        String main_URL = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/dev/";
        String select_prob_sms_return = main_URL + "php_prob_sms_return.php";
        String bodyMessage_r = null;
        JsonObjectRequest JsonObjectRequest = new StringRequest(Method.POST,
                select_prob_sms_return, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                JSONArray probs_sms = null;

                try {

                    probs_sms = response.getJSONArray("usuarios");
                    JSONObject prob_sms = probs_sms.getJSONObject(i);
                    String bodyMessage_r = prob_sms.getString("bodyMessage");
                 } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
*//*
            @Override

            public void onResponse(String response) {
                JSONArray probs_sms = null;
                int i = 0;
                try {
                    probs_sms = response.getJSONArray("usuarios");
                    JSONObject prob_sms = probs_sms.getJSONObject(i);
                    String bodyMessage_r = prob_sms.getString("bodyMessage");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
*//*
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("message_return", bodyMess);
                return parameters;
            }
        };
        requestQueue.add(jsonObjectRequest);

        return bodyMessage_r;
    }*/
}