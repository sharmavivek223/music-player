package com.example.user.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 2/28/2017.
 */

public class Tab2Fragment extends Tab1Fragment {
    private static final String TAG = "Tab2Fragment";

    private Button btnTEST;
    private TextView t1;
    private  Button b1,b2,b3,stopButton;
    int index=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        t1=(TextView)view.findViewById(R.id.textTab2);
        b1=(Button)view.findViewById(R.id.button);
        b2=(Button)view.findViewById(R.id.button2);
        b3=(Button)view.findViewById(R.id.button3);
        stopButton=(Button)view.findViewById(R.id.btnTEST2);


        try{
            //LoadSongs();
            //add some better way instead of loading songs again.
            //taken care by making the array static,it wont destroy now.

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index=0;
                    playSong();
                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index=1;
                    playSong();
                }
            });

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index=2;
                    playSong();
                }
            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.stop();
                    mediaPlayer=null;
                }
            });

        }catch (Exception e){
            t1.setText(e.getMessage());
        }

        return view;
    }
    void playSong(){
        mediaPlayer= MediaPlayer.create(getContext(), Uri.parse(songsArray.get(index).getSongUrl()));
        mediaPlayer.start();
    }

}
