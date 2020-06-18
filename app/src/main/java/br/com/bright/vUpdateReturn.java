package br.com.bright;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class vUpdateReturn {

    private String update_status_return_URL = "http://prajaoficial1.hospedagemdesites.ws/dropshopping/dev/php_update_status_return.php";

    public void vUpdate(final String sId, final RequestQueue requestQueue, final String status_retorno ) {

        StringRequest request = new StringRequest(Request.Method.POST,
                update_status_return_URL, new Response.Listener<String>() {
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
                parameters.put("id",sId);
                parameters.put("status_retorno",status_retorno);
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}
