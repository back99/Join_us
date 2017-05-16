package kr.co.company.join_us;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tofot_000 on 2016-12-12.
 */

public class unique extends AppCompatActivity  {

    Button btn_unique;
    String selec;
    ImageLoader imageloader;
    DisplayImageOptions defaultOptions;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unique);
        btn_unique=(Button)findViewById(R.id.btn_uniq);
        selec="주짓수";

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);

        //이미지 띄우기 위해서!!!!

        listView = (ListView)findViewById(R.id.speciallist) ;

        defaultOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(1)).build();



            new JSONTask().execute("http://202.30.23.51/~sap16t10/image.php");



        btn_unique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이제 글로 넘어가야 함
                Intent intent=new Intent(unique.this,board_pan.class);
                intent.putExtra("종목",selec);
                startActivity(intent);
                finish();

            }
        });

    }

    public class JSONTask extends AsyncTask<String, String, List<jujit>> {

        @Override
        protected List<jujit> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("image");

                List<jujit> provinceList = new ArrayList<>();

                Gson gson = new Gson();
                for(int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    jujit province = gson.fromJson(finalObject.toString(), jujit.class);

                    provinceList.add(province);

                }
                return provinceList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
             /*finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            try{
                if(reader != null){
                    reader.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }*/
            return null;

        }

        @Override
        protected void onPostExecute(final List<jujit> result) {
            super.onPostExecute(result);

            if(result != null) {
                CityListAdapter adapter = new CityListAdapter(getApplicationContext(), R.layout.specail, result);

                listView.setAdapter(adapter);


            } else {
                Toast.makeText(getApplicationContext(), "서버로부터 데이터를 불러오지 못하였습니다. 인터넷 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CityListAdapter extends ArrayAdapter {
        private List<jujit> provinceList;
        private int resource;

        private LayoutInflater inflater;


        public CityListAdapter(Context context, int resource, List<jujit> objects) {
            super(context, resource, objects);
            provinceList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);

                holder.spe_title = (TextView)convertView.findViewById(R.id.spe_title);
                holder.spe_content = (TextView)convertView.findViewById(R.id.spe_content);
                holder.spe_imageView=(ImageView)convertView.findViewById(R.id.spe_imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar4);

            //이미지만 따로 이렇게 저장

            // Then later, when you want to display image

            imageloader.getInstance().displayImage(provinceList.get(position).getSimage(), holder.spe_imageView, defaultOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            }); // Default options will be used


            holder.spe_content.setText(provinceList.get(position).getScontent());
            holder.spe_title.setText(provinceList.get(position).getStitle());
            return convertView;
        }
    }
    class ViewHolder{

        private TextView spe_title;
        private ImageView spe_imageView;
        private TextView spe_content;

    }

}
