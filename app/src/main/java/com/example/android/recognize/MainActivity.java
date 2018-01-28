package com.example.android.recognize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View.OnClickListener;

import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CHECK_DATA_CODE = 0;

    ImageView imageView;
    Button btnProcess;
    TextView txtResult;
    Bitmap bm;
    String readText;
    String speechText;

    Uri filePath;
    Button btnChoose;
    final int PICK_IMAGE_REQUEST = 71;


    TextToSpeech t1;
    Button speakButton;
    private OnInitListener onInitListener;


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

        speakButton=(Button)findViewById(R.id.speakButton);

        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CHECK_DATA_CODE);

        onInitListener = new OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        };
//        t1=new TextToSpeech(getApplicationContext(), onInitListener);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = speechText;
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });




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
                        case "Biohazardous\n":
                            speechText = "Biohazardous infection materials.";
                            break;
                        case "Corrosion\n":
                            speechText = "Serious eye damage, skin corrosion, or corrosive to metals.";
                            break;
                        case "Exclamationmark\n":
                            speechText = "Irritation (skin or eyes), skin sensitization, (harmful) acute toxicity specific target organ toxicity (drowsiness or dizziness, or respirator irritation), or hazardous to the ozone layer.";
                            break;
                        case "Explodingbomb\n":
                            speechText = "Explosive, (extremely) self-reactive, or (extremely) organic peroxide.";
                            break;
                        case "Flame\n":
                            speechText = "Flammable, self-reactive, pyrophoric, self-heating, in contact with water this material will emit flammable gases, or organic peroxide.";
                            break;
                        case "Flameovercircle\n":
                            speechText ="Oxidizer.";
                            break;
                        case "GasCylinder\n":
                            speechText = "Gas under pressure.";
                            break;
                        case "Healthhazard\n":
                            speechText = "Carcinogenicity, respiratory sensitization, specific target organ toxicity, germ cell mutagenicity, or aspiration hazard.";
                            break;
                        case "Skullandcrossbones\n":
                            speechText = "Fatal or toxic acute toxicity.";
                            break;
                        default:
                            speechText = "Symbol is not identified, please take another picture";
                    }
                }
            }
        });
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
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bm = bitmap;
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        } else if (requestCode == CHECK_DATA_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                t1 = new TextToSpeech(this, onInitListener);
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }
    }
}
