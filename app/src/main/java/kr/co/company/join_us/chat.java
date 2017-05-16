package kr.co.company.join_us;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;


/**
 * Created by tofot_000 on 2016-11-26.
 */

public class chat extends Activity implements OnMapReadyCallback{



    static final LatLng SUWON = new LatLng(37.280291, 127.007802);//수원 위치 는 기본적으로 띄어주기 위해서

    private GoogleMap googleMap;


    ArrayList<LatLng> listlat=new ArrayList<>();
    ArrayList<String> listname=new ArrayList<>();
    ArrayList<Double> listlongitude=new ArrayList<>();
    ArrayList<Double> listlatitude=new ArrayList<>();




    String number;//이걸 서버로 보내서 그 글번호에 맞는걸 가져와야 함 !!!!
    String kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number = bundle.getString("num");//이제 이번호를 가지고 디비로 보내서 여기에 맞는 댓글하고 글의 내용을 가지고 와야함!! 두개 따로따로!!
            kind = bundle.getString("종목");
         //   Log.d("abc",""+number);

        }


        new JSONTask().execute("http://202.30.23.51/~sap16t10/location.php");//서버에 접속하기

        MapFragment mapFragment =(MapFragment) getFragmentManager().findFragmentById(R.id.map);//구글맵 띄우기 !
        mapFragment.getMapAsync(this);

    }

    public class JSONTask extends AsyncTask<String, String, List<global>> {

        @Override
        protected List<global> doInBackground(String... params) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("number", number));



            try {
                httppost.setEntity(new UrlEncodedFormEntity(parameter, "UTF-8"));
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try{
                HttpResponse response = httpclient.execute(httppost);

                if (response != null) {
                    InputStream is = response.getEntity().getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                        Log.d("abc",""+buffer.toString());

                    }

                    String finalJson = buffer.toString();

                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONArray parentArray = parentObject.getJSONArray("loca");

                    List<global> uploadList = new ArrayList<>();

                    Gson gson = new Gson();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        global menulist = gson.fromJson(finalObject.toString(), global.class);

                        uploadList.add(menulist);

                    }
                    return uploadList;

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(final List<global> result) {
            super.onPostExecute(result);

            if(!result.isEmpty()){
                for(int i=0;i<result.size();i++){

                    double latitude2;
                    double longitude2;
                    longitude2=Double.valueOf(result.get(i).getLongitude());
                    latitude2=Double.valueOf(result.get(i).getLatitude());

                    LatLng lat = new LatLng(longitude2, latitude2);
                    listlat.add(lat);
                    listlongitude.add(longitude2);
                    listlatitude.add(latitude2);
                    listname.add(result.get(i).getId3());

                    //서버에서 받아온 아이디 위도 경도 를 각 리스트별로 저장, 위도 경도는 실수형태로 변환해서 저장시킴

                }

                for(int i=0;i<listname.size();i++) {

                    Log.d("abc","name"+listname.get(i).toString());
                    Log.d("abc","latitude"+listlatitude.get(i));
                    Log.d("abc","lat"+listlat.get(i));

                    googleMap.addMarker(new MarkerOptions().position(listlat.get(i)).title(listname.get(i).toString()));
                }

            }

        }
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        googleMap.addMarker(new MarkerOptions().position(SUWON).title("Suwon")).showInfoWindow();//수원만 항시 찍어줌

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SUWON));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }


    public void onBackPressed(){
        Intent intent=new Intent(chat.this,document.class);
        intent.putExtra("kind",kind);
        intent.putExtra("번호",number);
        startActivity(intent);
        finish();
    }

}
