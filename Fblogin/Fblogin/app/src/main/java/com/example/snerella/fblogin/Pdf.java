package com.example.snerella.fblogin;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;


public class Pdf extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {

    private final int SPEECH_RECOGNITION_CODE = 1;

    private TextView txtOutput;
    private ImageButton btnMicrophone;
    private Button stop;
    private Button start;
    public File[] imagelist;
    public String[] pdflist;
    private ImageButton btnprevBookmark;
    private ImageButton btnnextBookmark;
    private ImageButton btnBookmark;
    private ImageButton btnDeleteBookmark;

    File globalPath;
    public static String FILE_PATH = "file_path";
    public String fileToLoad;
    ArrayList<String> listFolders = new ArrayList<String>();
    String path;

    int c = 0;
    int z = 1;
    int x = 0;
    int d = 1;
    File root;
    File f;
    public String[] textlist;
    TextToSpeech t1, t2, t3,t4;
    String parsedText;
    String[] sentences;
    int i, flag;
    int pagecount;
    int totalpages;
    int currentPage;
    private float[] sensorvalue;
    HashMap<String, String> params;
    String words;
    LinkedList l;
    private HashMap<String, MyArrayList> bookmark;
    private static String BOOKMARK_FILE = "bookmark.ser";
    private static String TAG = "PDF_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        readBookmarks();
        l = new LinkedList();
        SensorManager Sensormgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor LightSensor = Sensormgr.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor != null) {
            Sensormgr.registerListener(
                    LightSensorListener,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            Toast.makeText(Pdf.this, "Light Sensor not found!", Toast.LENGTH_SHORT).show();
        }
        t3 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t3.setLanguage(Locale.UK);
                }
                t3.speak("Do you want me to start reading?", t3.QUEUE_FLUSH, null);

            }

        });

        flag = 0;
        pagecount = 1;
        totalpages = 0;
//        t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    t2.setLanguage(Locale.UK);
//                }
//            }
//        });
        Log.e("tag", "pdfclass");
        txtOutput = (TextView) findViewById(R.id.txt_output);
        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);
        stop = (Button) findViewById(R.id.stop);
        start = (Button) findViewById(R.id.start);
        btnBookmark = (ImageButton) findViewById(R.id.btn_bookmark);
        btnprevBookmark = (ImageButton) findViewById(R.id.btn_prev_bk);
        btnnextBookmark = (ImageButton) findViewById(R.id.btn_next_bk);
        btnDeleteBookmark = (ImageButton) findViewById(R.id.btn_bookmark_delete);

        btnBookmark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                MyArrayList value = null;
                if(bookmark.containsKey(path))
                {
                    value = bookmark.get(path);
                }

                else
                {
                    value = new MyArrayList();

                }
                if(value.contains(currentPage))
                {
                    Toast.makeText(getApplicationContext(), "I remember this page already!", Toast.LENGTH_LONG).show();
                }
                else {
                    value.insert(currentPage);
                    bookmark.put(path, value);
                    Toast.makeText(getApplicationContext(), "Bookmarked! Hurrah!", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnprevBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(bookmark.containsKey(path))
                {
                    MyArrayList value1 = bookmark.get(path);
                    int returnValue = value1.lowerValueOfPage(currentPage);
                    if(returnValue > -1)
                    {
                        PDFView pv = (PDFView) findViewById(R.id.pdfView);
                        pv.jumpTo(returnValue);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No more bookmarks folks!",Toast.LENGTH_SHORT).show();
                    }


                }



            }



        });

        btnnextBookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(bookmark.containsKey(path))
                {
                    MyArrayList value1 = bookmark.get(path);
                    int returnValue = value1.greaterValueOfPage(currentPage);
                    if(returnValue > -1)
                    {
                        PDFView pv = (PDFView) findViewById(R.id.pdfView);
                        pv.jumpTo(returnValue);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No more bookmarks folks!",Toast.LENGTH_SHORT).show();
                    }


                }



            }



        });

        btnDeleteBookmark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (bookmark.containsKey(path)) {
                MyArrayList value = bookmark.get(path);
                if(value.contains(currentPage)){

                    value.remove(value.indexOf(currentPage));
                    bookmark.put(path, value);
                    Toast.makeText(getApplicationContext(), "Bookmark Deleted", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Oops! Page not bookmarked!", Toast.LENGTH_LONG).show();
                }


                }
            }
        });
        btnMicrophone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (t1 != null) {
                    contstop();
                }
                acceptVoiceInput();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startReading();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopReading();
            }
        });

        path = getIntent().getExtras().getString(FILE_PATH);

        if (path != null && (path.length() > 0)) {
            fileView(path);
        }


    }

    private final SensorEventListener LightSensorListener
            = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                sensorvalue = new float[10];
                for(i=0;i<10;i++)
                {
                    sensorvalue[i] = event.values[0];
                }
                float sum =0;
                float average =0;
                for(int i=0;i<10;i++)
                {
                    sum += sensorvalue[i];
                }
                average = sum/10;
                System.out.println("Average"+average);
                //sensorvalue[] = event.values[0];
                //IF TRESHOLD FOR READING:
                if (average >= 5000 && d == 1) {
                    System.out.println("hello");
                    String not = "It is bright outside,do you want me start reading?";
                    if(t1!=null)
                    {if (!(t1.isSpeaking())) {

                        t4 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    t4.setLanguage(Locale.UK);
                                    String noti1 = "It is bright outside,do you want me start reading?";
                                    t4.speak(noti1, t2.QUEUE_FLUSH, null);
                                }
                            }
                        });

                        //t2.speak(not, t2.QUEUE_FLUSH, null);
                        d = 0;
                        z=1;

                       // c=1;
                        //acceptVoiceInput();}
                    }}
                    else
                    {
                        t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    t2.setLanguage(Locale.UK);
                                    String noti1 = "It is bright outside,do you want me start reading?";
                                    t2.speak(noti1, t2.QUEUE_FLUSH, null);
                                }
                            }
                        });
                        String not1 = "It is bright outside,do you want me start reading?";
                        System.out.println("hello again");
                        t2.speak(not1, t2.QUEUE_FLUSH, null);
                        d = 0;
                        z=1;

                    }

                }



                    //IF TRESHOLD FOR STOPPING:
                    //       sensorvalue=4300;
                //
                    if (average < 3000 && z == 1 && x==1 ) {
                        System.out.println("hello11");
                        String not = "It is not bright anymore.Do you want me stop reading?";

                        if (t1 != null) {
                            if(t1.isSpeaking() == true) {
                                contstop();
                                t2.speak(not, t2.QUEUE_FLUSH, null);
                                flag = 1;
                                z = 0;
                                d = 1;
                        //        average = 1000;
                            }
                        }

                    }
                }
            }

        }

        ;


        //Method to display the pdf
        public void fileView(String path) {
            f = new File(path);
            if (f.exists()) {
                PDFView pv = (PDFView) findViewById(R.id.pdfView);
                pv.fromFile(f)
                        .onPageChange(this)
                        .onLoad(this)
                        .load();
            }
        }

        //Method to change page nums
        public void onPageChanged(int page, int pageCount) {
            TextView tv = (TextView) findViewById(R.id.pagelabels);
            tv.setText((page + 1) + " / " + pageCount);
            currentPage = page;
        }

        //Method to display page num soon after pdf is loaded
        public void loadComplete(int nbPages) {
            TextView tv = (TextView) findViewById(R.id.pagelabels);
            tv.setText("1" + " / " + nbPages);
            currentPage = 0;
        }


        /**
         * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
         */
        private void acceptVoiceInput() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "Speak something...");
            try {
                startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Speech recognition is not supported in this device.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Callback for speech recognition activity
         */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch (requestCode) {
                case SPEECH_RECOGNITION_CODE: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String text = result.get(0);
                        txtOutput.setText(text);
                        if (text.equals("stop")) {
                            txtOutput.setText("STOP ON NOTIFICATION");
                           stopReading();
                        }
                        if (text.equals("start")) {
                            txtOutput.setText("START ON NOTIFICATION");
                            startReading();
                        }
                        if (text.equals("start")) {
                            Toast.makeText(this, "YESSS", Toast.LENGTH_LONG);
                            startReading();
                        }
                        if (text.equals("yes") && flag == 1) {
                            flag = 0;
                            stopReading();
                        }

                    }
                    break;
                }

            }
        }



        public void startReading() {
            //Pdf parsing and text to speech code here
            Toast.makeText(this, "STARTED", Toast.LENGTH_LONG).show();
            x = 1;
            TextView tv = (TextView) findViewById(R.id.pagelabels);
            String input = tv.getText().toString();
            String[] parts = input.split(" / ");
            String part1 = parts[0];
            String part2 = parts[1];
            pagecount = Integer.parseInt(part1);
            totalpages = Integer.parseInt(part2);
            PDFBoxResourceLoader.init(getApplicationContext());
            PDDocument document = null;
            try {
                document = PDDocument.load(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
//       while(pagecount<=totalpages) {
            try {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfStripper.setStartPage(pagecount);
                pdfStripper.setEndPage(totalpages);
                parsedText = pdfStripper.getText(document);
                sentences = parsedText.split("\\.");
                if (c != 1) {
                    for (i = 0; i < sentences.length; i++) {
                        l.add(i, sentences[i]);
                        //Toast.makeText(this, sentences[i], Toast.LENGTH_SHORT).show();
                    }
                }
                t1 = new TextToSpeech(this, this);
                c = 0;
                System.out.println("Object is: " + t1);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (document != null) document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Toast.makeText(this,"text to speech",Toast.LENGTH_LONG).show();
//    }

        public void stopReading() {
            x = 0;
            t1.shutdown();
            l.clear();
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onInit(int status) {
            t1.setOnUtteranceCompletedListener(this);
            playNextChunk();

        }

        @Override
        public void onUtteranceCompleted(String utteranceId) {
            playNextChunk();

        }

        public void playNextChunk() {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + l.size());
            t1.speak((String) l.poll(), t1.QUEUE_FLUSH, params);
            // System.out.println(l.getFirst());
        }

        public void contstop() {
            c = 1;
            t1.shutdown();
        }

    private void readBookmarks()
    {

        try
        {
            FileInputStream fis = openFileInput(BOOKMARK_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            bookmark = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException ioe) {

            Log.d(TAG, "No file found yet. Initializing the bookmark Map");


        } catch(IOException ioe) {
            ioe.printStackTrace();

        }catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
        if(bookmark== null)
        {
            bookmark = new HashMap<String, MyArrayList>();
        }
    }

    private void writeBookmarkToFile()
    {
        if(bookmark!=null)
        {
            try
            {
                FileOutputStream fos = openFileOutput(BOOKMARK_FILE, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(bookmark);
                oos.close();
                fos.close();

            }catch(IOException ioe)
            {
                ioe.printStackTrace();
            }



        }
    }

        @Override
        public void onDestroy() {
            super.onDestroy();

            if (t1 != null) {
                t1.stop();
                t1.shutdown();
            }
        }
    @Override
    public void onStop()
    {
        super.onStop();
        writeBookmarkToFile();
    }
}
