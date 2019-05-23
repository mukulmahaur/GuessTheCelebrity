package com.example.mukul.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenCeleb=0;
    ImageView imageView;
    int locationofcorrect=0;
    String[] answers = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void chooseAnswer(View view) throws ExecutionException, InterruptedException {

        if (view.getTag().toString().equals(Integer.toString(locationofcorrect))){
            Toast.makeText(getApplicationContext(),"Correct!!!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Sorry!!!It was "+celebnames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
        }
            newquestions();
    }

    public class DownloadClass extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while(data!=-1){
                    char current = (char)data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void newquestions() {
        Random random = new Random();
        chosenCeleb = random.nextInt(celeburls.size());
        Bitmap celebimage;

        ImageDownloader imageDownloader = new ImageDownloader();
        try{
            celebimage = imageDownloader.execute(celeburls.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebimage);
            locationofcorrect = random.nextInt(4);
            for (int i=0;i<4;i++){
                if (i==locationofcorrect){
                    answers[i] = celebnames.get(chosenCeleb);

                }
                else{
                    int temp = random.nextInt(celebnames.size());
                    while (temp == chosenCeleb){
                        temp = random.nextInt(celebnames.size());
                    }
                    answers[i] = celebnames.get(temp);
                }
                button0.setText(answers[0]);
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadClass myclass = new DownloadClass();
        String result = null;
        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button1);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);
        try {
            result = myclass.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);
            while(matcher.find()){
                String temp = matcher.group(1);
                temp = temp.replace("profile","big_profile");
                celeburls.add(temp);
                System.out.println(temp);
            }
            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitResult[0]);
            while(matcher.find()){
                celebnames.add(matcher.group(1));
//                System.out.println(matcher.group(1));
            }
            newquestions();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
