package com.example.hedgehog.gridviewandbitmap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bWeb, bGallery, bCamera;
    GridView gridView;
    ArrayAdapter<String> adapter;
    ArrayList<String> al = new ArrayList<>();{
        al.add("11111111");
        al.add("22222222");
        al.add("33333333");
        al.add("44444444");
        al.add("55555555");
        al.add("66666666");
        al.add("77777777");
        al.add("88888888");
        al.add("99999999");
        al.add("00000000");
    }
    Bitmap bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bWeb = (Button) findViewById(R.id.bWeb);
        bGallery = (Button) findViewById(R.id.bGallery);
        bCamera = (Button) findViewById(R.id.bCamera);
        bWeb.setOnClickListener(this);
        bGallery.setOnClickListener(this);
        bCamera.setOnClickListener(this);
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new MyArrayAdapter(this, R.id.gridView, al);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bWeb:
                AsyncTask<Integer,Void,Integer> at = new MyAsynkTask();
                at.execute(1);

                adapter.notifyDataSetChanged();
                break;
            case R.id.bGallery:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

                adapter.notifyDataSetChanged();
                break;
            case R.id.bCamera:
                dispatchTakePictureIntent();

                adapter.notifyDataSetChanged();

        }

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);
            Log.d ("asdf", bitmap.toString());


        }
        adapter.notifyDataSetChanged();
    }


    public class MyArrayAdapter extends ArrayAdapter<String>{
        Context context;


        public MyArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.item_layout, parent, false);
            TextView text = (TextView) rootView.findViewById(R.id.text);
            ImageView image = (ImageView) rootView.findViewById(R.id.image);
            image.setImageBitmap(bitmap);
            text.setText(al.get(position));
            return rootView;
        }
    }

    class MyAsynkTask extends AsyncTask <Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            String responce = null;
            InputStream is = null;
            String myurl = "https://pokeapi.co/media/img/1383571573.78.png";

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode!=200){
                    Log.d("asdf", "" + responseCode);
                }
                is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);



            } catch (Exception e) {

            }


            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            adapter.notifyDataSetChanged();
        }
    }
}
