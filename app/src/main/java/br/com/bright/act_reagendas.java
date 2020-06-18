package br.com.bright;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import br.com.bright.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class act_reagendas<Static> extends AppCompatActivity  {
    Context context;
    Document doc = null;
    private AlertDialog alerta;
    RequestQueue requestQueue;
    Button btnLista ;
    public ListView myLitsView;

    vUpdateReturn updateReturn = new vUpdateReturn();

    private String main_URL="http://prajaoficial1.hospedagemdesites.ws/dropshopping/";
    private String select_reagendas_URL = main_URL+"php_select_reagendas.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reagendas);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        setTitle("Pedido de Reagendamento");

        ArrayList<String> listout = new ArrayList<>();
        ListView myLitsView = (ListView) findViewById(R.id.lista);
        LoadAllProducts loadAllProducts = new LoadAllProducts();
        loadAllProducts.execute();

        myLitsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            Object obj =parent.getItemAtPosition(position);
                //Procura a # para pegar o ID
                String sId = obj.toString();
                int ini = sId.indexOf("#");
                ini++;
                int end=sId.length();
                sId=sId.substring(ini,end);

                //Procura () para pegar o Nome
                String sNome = obj.toString();
                ini = sNome.indexOf("(");
                ini++;
                end=sNome.indexOf(")");

                sNome=sNome.substring(ini,end);

                AlertDialog.Builder builder = new AlertDialog.Builder(act_reagendas.this);
                builder.setTitle("Reagendamento");
                builder.setMessage("Confirma o reagendamento de \n"+sNome+"?");
                final String finalSId = sId;
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        updateReturn.vUpdate(finalSId,requestQueue,"3");

                        LoadAllProducts loadAllProducts = new LoadAllProducts();
                        loadAllProducts.execute();
                        finish();
                        startActivity(new Intent( act_reagendas.this, act_reagendas.class));

                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

               }
        });
    } //onCreate

    private class LoadAllProducts extends AsyncTask<Void, Void, ArrayList<String>> {

        ListView listView = null;
        Context context = null;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> list = new ArrayList<>();
            URL url;
            HttpURLConnection hpptURLConnection =  null;
            BufferedReader bufferedReader = null;
            String JsonFile;
            try {
                url = new URL(select_reagendas_URL);
                hpptURLConnection = (HttpURLConnection) url.openConnection();
                hpptURLConnection.setRequestMethod("POST");
                hpptURLConnection.setRequestProperty("Content-length", "0");
                hpptURLConnection.setUseCaches(false);
                hpptURLConnection.setAllowUserInteraction(false);
                hpptURLConnection.setConnectTimeout(100000);
                hpptURLConnection.setReadTimeout(100000);
                hpptURLConnection.connect();

                int responseCode = hpptURLConnection.getResponseCode();

                InputStream inputStream =  hpptURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer=  new StringBuffer();
                String line = "";
                while ((line=bufferedReader.readLine())!=null) {
                    stringBuffer.append(line);
                }

                JsonFile  = stringBuffer.toString();
                JSONObject response = new JSONObject(JsonFile);
                JSONArray usuarios = response.getJSONArray("usuarios");

                for (int i = 0 ; i < usuarios.length(); i++) {
                    JSONObject usuario = usuarios.getJSONObject(i);
                    DemoAgenda itemAgenda = new DemoAgenda();
                    itemAgenda.setStrNome(usuario.getString("nome"));
                    itemAgenda.setStrTelefone(usuario.getString("telefone"));
                    itemAgenda.setStrAg_data(usuario.getString("ag_data"));
                    itemAgenda.setStrAg_hora(usuario.getString("ag_hora"));
                    itemAgenda.setStrId_agenda(usuario.getString("id_agenda"));
                    itemAgenda.setiID(usuario.getInt("id"));

                    list.add("("+usuario.getString("nome")+")\n"+
                            usuario.getString("telefone")+"\nÚltima Consulta: "+
                            usuario.getString("ag_data")+"  Às  "+
                            usuario.getString("ag_hora")+"\nID Agenda:- #"+
                            Integer.toString(usuario.getInt("id"))
                    );
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    hpptURLConnection.disconnect();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return  list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            ListView myLitsView = (ListView) findViewById(R.id.lista);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(act_reagendas.this, android.R.layout.simple_list_item_1, strings);

            myLitsView.setAdapter(arrayAdapter);

        }
    }

}