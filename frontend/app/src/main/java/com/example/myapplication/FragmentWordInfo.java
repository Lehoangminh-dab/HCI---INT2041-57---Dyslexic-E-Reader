package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Type;

import android.media.MediaPlayer;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.utils.TextColorUtils;
import com.example.myapplication.model.ColorRule;

public class FragmentWordInfo extends Fragment {
    private static final String LOG_TAG = "FragmentWordInfo";
    private ImageButton closeButton;
    private ImageButton pronounceButton;
    private ImageButton understoodButton;
    private static final String ARG_WORD = "word";
    private ImageView wordImage;
    private TextView loadingText;
    private TextView wordTextView;
    private String word;
    private SharedPreferences sharedPreferences;

    public FragmentWordInfo() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getString(ARG_WORD);
        }
    }

    public static FragmentWordInfo newInstance(String word) {
        FragmentWordInfo fragment = new FragmentWordInfo();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_info, container, false);

        // Display image
        loadingText = view.findViewById(R.id.loading_text);
        wordImage = view.findViewById(R.id.word_image);
        displayImage(word);

        // Display word
        wordTextView = view.findViewById(R.id.word);
        displayWordTextView(word);

        // Initialize buttons
        closeButton = view.findViewById(R.id.close_button);
        pronounceButton = view.findViewById(R.id.pronounce_button);
        understoodButton = view.findViewById(R.id.understood_button);
        if (closeButton != null && pronounceButton != null && understoodButton != null) {
            setOnClickListeners();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setOnClickListeners() {
        setCloseButtonOnClickListener();
        setPronounceButtonOnClickListener();
        setUnderstoodButtonOnClickListener();
    }
    private void setCloseButtonOnClickListener() {
        closeButton.setOnClickListener(new View.OnClickListener() {
            // Close fragment
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(FragmentWordInfo.this).commit();
            }
        });
    }
    private void setPronounceButtonOnClickListener() {
        pronounceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Establish request URL
                String apiUrl = "http://192.168.1.42:5000/api/pronunciation?word=";
                String requestUrl = apiUrl + word;

                // Send request to receive pronunciation audio file
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        // Handle the error
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Save the audio file to a temporary location
                            String audioFilePath = getContext().getExternalCacheDir() +
                                    "/pronunciation.mp3";
                            FileOutputStream fos = new FileOutputStream(audioFilePath);
                            fos.write(response.body().bytes());
                            fos.close();

                            // Play the pronunciation audio file
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    try {
                                        mediaPlayer.setDataSource(audioFilePath);
                                        mediaPlayer.prepare();
                                        mediaPlayer.start();
                                        Log.d(LOG_TAG, "Audio file played successfully");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private void setUnderstoodButtonOnClickListener() {
        understoodButton.setOnClickListener(new View.OnClickListener() {
            // Close fragment
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(FragmentWordInfo.this).commit();
            }
        });
    }
    private void displayImage(String word) {
        // Show loading text, hide image
        loadingText.setVisibility(View.VISIBLE);
        wordImage.setVisibility(View.GONE);

        String requestUrl = "http://192.168.1.42:5000/api/image?word=" + word;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Save the image file to a temporary location
                    String imageFilePath = getContext().getExternalCacheDir() + "/image.png";
                    FileOutputStream fos = new FileOutputStream(imageFilePath);
                    fos.write(response.body().bytes());
                    fos.close();

                    // Display the image in the image container
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                            wordImage.setImageBitmap(bitmap);
                            // Hide loading text, show image
                            loadingText.setVisibility(View.GONE);
                            wordImage.setVisibility(View.VISIBLE);
                            Log.d(LOG_TAG, "Image displayed successfully");
                        }
                    });
                }
            }
        });
    }

    private void displayWordTextView(String word) {
        // Init shared preferences
        sharedPreferences = getContext().getSharedPreferences("MySharedPref",
                getContext().MODE_PRIVATE);

        // Set font
        String fontName = sharedPreferences.getString("font", "dyslexic");
        int fontId = getContext().getResources().getIdentifier(fontName, "font",
                getContext().getPackageName());
        Typeface typeface = ResourcesCompat.getFont(getContext(), fontId);
        wordTextView.setTypeface(typeface);

        // Set color
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("colorRules", null);
        Type type = new TypeToken<List<ColorRule>>() {}.getType();
        List<ColorRule> colorRuleList = gson.fromJson(jsonRetrieved, type);
        SpannableString spannableString = TextColorUtils.applyColorToText(getContext(), word,
                colorRuleList);

        // Display
        wordTextView.setText(spannableString);
    }
}
