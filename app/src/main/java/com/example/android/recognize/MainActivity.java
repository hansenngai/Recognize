package com.example.android.recognize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import java.util.Locale;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnProcess;
    TextView txtResult;
    Bitmap bm;
    String readText;
    String speechText;

    Uri filePath;
    Button btnChoose;
    final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image_view);
        btnProcess = (Button)findViewById(R.id.button_process);
        txtResult = (TextView)findViewById(R.id.textview_result);

        btnChoose = (Button)findViewById(R.id.btnChoose);

        final Bitmap bitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.text_recognition2
        );

        imageView.setImageBitmap(bitmap);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bm == null){
                    Log.e("ERROR", "No image selected");
                    return;
                }
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational()){
                    Log.e("ERROR", "Detector dependencies are not yet available");
                    return;
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bm).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i= 0;i<items.size(); ++i){
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    String rT = stringBuilder.toString();
                    txtResult.setText(rT);
                    readText = rT;
                    
                    switch(readText) 
                    {
                        case Biohazardous:
	                    speechText = "Biohazardous infection materials.";
                            break;
                        case Corrosion:
	                    speechText = "Serious eye damage, skin corrosion, or corrosive to metals.";
                            break;
                        case Exclamation mark:
	                    speechText = "Irritation (skin or eyes), skin sensitization, (harmful) acute toxicity specific target organ toxicity (drowsiness or dizziness, or respirator irritation), or hazardous to the ozone layer.";
                            break;
                        case Exploding bomb:
	                    speechText = "Explosive, (extremely) self-reactive, or (extremely) organic peroxide.";
                            break;
                        case Flame:
	                    speechText = "Flammable, self-reactive, pyrophoric, self-heating, in contact with water this material will emit flammable gases, or organic peroxide.";
                            break;
                        case Flame over circle:
	                    speechText ="Oxidizer.";
                            break;
                        case Gas Cylinder 
                            speechText = "Gas under pressure.";
                            break;
                        case Health hazard:
	                    speechText = "Carcinogenicity, respiratory sensitization, specific target organ toxicity, germ cell mutagenicity, or aspiration hazard.";
                            break;                      
                        case Skull and crossbones:
                            speechText = "(Fatal or toxic) acute toxicity.";
                            break;
                        default:
                            speechText = "Symbol is not identifed, please take another picture";
                }
            }
        });
    }
				       
public class SpeakingAndroid extends Activity implements OnClickListener, OnInitListener {
     
        //TTS object
    private TextToSpeech myTTS;
        //status check code
    private int MY_DATA_CHECK_CODE = 0;
     
        //create the Activity
    public void onCreate(Bundle savedInstanceState) {
     
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
             
                //get a reference to the button element listed in the XML layout
            Button speakButton = (Button)findViewById(R.id.speak);
                //listen for clicks
            speakButton.setOnClickListener(this);
 
            //check for TTS data
            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }
     
        //respond to button clicks
    public void onClick(View v) {
 
            //get the text entered
            EditText enteredText = (EditText)findViewById(R.id.enter);
            String words = enteredText.getText().toString();
            speakWords(words);
    }
     
        //speak the user text
    private void speakWords(String speech) {
 
            //speak straight away
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
     
        //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
            myTTS = new TextToSpeech(this, this);
            }
            else {
                    //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
 
        //setup TTS
    public void onInit(int initStatus) {
     
            //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }
}

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bm = bitmap;
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
