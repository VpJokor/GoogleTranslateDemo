package com.example.test20230805;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.smartreply.SmartReply;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView text;
    TextView text_type;
    EditText input;
    Button ok;
    Translator englishGermanTranslator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);

//        testReply();

        text_type = findViewById(R.id.text_type);
        languageIdentifier = LanguageIdentification.getClient();
        text.setText("Ааароекино арлекино");
        identifyDemo();
        translateDemo();
    }

    private void testReply(){
        List<TextMessage> conversation = new ArrayList<>();
        conversation.add(TextMessage.createForLocalUser("What should I do if I catch a cold?", System.currentTimeMillis()));

        SmartReplyGenerator smartReply = SmartReply.getClient();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            Log.e(TAG, "onSuccess: 没有建议" );
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            for (int i = 0; i < result.getSuggestions().size(); i++) {
                                Log.e(TAG, "onSuccess: " + i + " , " + result.getSuggestions().get(i).getText());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e );
                        // Task failed with an exception
                        // ...
                    }
                });
    }

    LanguageIdentifier languageIdentifier;
    private void identifyDemo( ){

        languageIdentifier.identifyLanguage(text.getText().toString())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Log.i(TAG, "Can't identify language.");
                                } else {
                                    Log.i(TAG, "Language: " + languageCode);
                                    text_type.setText(languageCode);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "LanguageIdentifier onFailure: " ,e );
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });
    }

    private void translateDemo(){
        input = findViewById(R.id.input);
        ok = findViewById(R.id.ok);

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.CHINESE)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        englishGermanTranslator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions
                .Builder()
                .requireWifi()
                .build();
        englishGermanTranslator
                .downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        unused -> ok.setOnClickListener(v -> {
                            translate(input.getText().toString());
                        })
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e );
                            }
                        });
    }

    private void translate(String inputText){
        englishGermanTranslator.translate(inputText)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                text.setText(s);
                                identifyDemo();
                                Log.e(TAG, "onSuccess: " + s );
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " ,e );
                            }
                        });
    }

}