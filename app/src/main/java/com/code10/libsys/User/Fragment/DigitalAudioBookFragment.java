package com.code10.libsys.User.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.code10.libsys.General.Model.AudioBook;
import com.code10.libsys.General.Utility;
import com.code10.libsys.R;
import com.code10.libsys.User.Activity.AudioPlayerActivity;
import com.code10.libsys.User.LibrivoxRetriever;

import java.util.ArrayList;


public class DigitalAudioBookFragment extends Fragment {

    ArrayList<AudioBook> audioBooks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_digital_audio_book, container, false);
        LibrivoxRetriever retriever = new LibrivoxRetriever(getContext());
        retriever.addOnCompleted((Utility.QueryFinished<ArrayList<AudioBook>>) o -> {
            Log.v("AUDIO", o.size() + "");
            Toast.makeText(getContext(), "OnQuery Finished", Toast.LENGTH_SHORT).show();
            audioBooks = o;
        });
        //retriever.execute("https://librivox.org/api/feed/audiobooks/?title=^pride%20and%20prejudice&format=json&extended=1");
        retriever.execute("https://librivox.org/api/feed/audiobooks/?title=^java&format=json");


        Button btn = view.findViewById(R.id.button);
        btn.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AudioPlayerActivity.class);
            // i.putExtra("Bookurl", audioBooks.get(0).getChaptersURL());
            startActivity(i);
        });
        return view;
    }
}