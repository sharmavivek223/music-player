package com.example.user.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import  android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Adapter.SongAdapter;
import Model.SongInfo;

public class MainActivity extends AppCompatActivity {
    private   MediaPlayer mediaPlayer;
    private   SeekBar seekBar;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    private ArrayList<SongInfo> songsArray;
    private  final int REQ_CODE=123;
    int prevPosition=-1;
    Button prevButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songsArray = new ArrayList<SongInfo>();
        recyclerView = findViewById(R.id.recyclerView);
        seekBar = findViewById(R.id.seekBar);
songAdapter=new SongAdapter(this,songsArray);
recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
        CheckPermission();

        mediaPlayer = new MediaPlayer();
       songAdapter.setOnItemClickListner(new SongAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Button b, View v, final SongInfo obj, int position){
               //code for play buttons goes here

                if(prevPosition==position)
                    {   if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else {
                    mediaPlayer.start();
                        b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    }
                     }


                  else
                {
                    releaseMediaPlayer();
                    mediaPlayer=MediaPlayer.create(MainActivity.this, Uri.parse(obj.getSongUrl()));
                    b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    if(prevPosition!=-1)
                    prevButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    mediaPlayer.start();

                }




/*

                if(mediaPlayer.isPlaying()){//stoping
                   mediaPlayer.reset();

                   b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));



                }else{//playing
                    try {
                        mediaPlayer.setDataSource(obj.getSongUrl());
                        mediaPlayer.prepare();
                        b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                              mp.start();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
*/
                prevPosition=position;
                prevButton=b;

            }
        });


    }

    private void CheckPermission() {
        //since only in sdk greater than 23 the permissions are granted after installing app
        //so we hsve to only ask for permission when the android version is greater or equal to marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){


                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
                return;
            }LoadSongs();

        }else{//if everything went great we are happy to load songs
            LoadSongs();
        }
    }

    private void LoadSongs() {//Time to fetch songs data
        /*
        uri stands for uniform resourse indicator,a lot lot like an url
        every url is uri but not every uri is url since url and urn(UR Names) are both
        types of uri*/
        Uri songUri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //selection will help to select the music files
        String selection=MediaStore.Audio.Media.IS_MUSIC+"!=0";
        //Curson is just providing us a reference to a 2D table containing info of all files that passes the selection
        Cursor cursor=getContentResolver().query(songUri,null,selection,null,null);
        if(cursor!=null){
            //only enters if we have files
            if(cursor.moveToFirst()){
                //only enter if it atleast has an element to move to first
                //now that the cursor is at an element in this case a songs info/uri/address
                //we will get its info
                //getting all songs data in songs array till the cursor cant go to next element
                //i.e. end of elements
               do{ String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                //passing the info to song info object
                SongInfo sinfo=new SongInfo(name,artist,url);
                songsArray.add(sinfo);
               }while(cursor.moveToNext());
            }//we have to get rid of cursor after use since it will be heavy on memory
            cursor.close();
            //time to set all info in an adapter to display it
            songAdapter=new SongAdapter(this,songsArray);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(songAdapter);
            recyclerView.addItemDecoration(dividerItemDecoration);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode){
          case REQ_CODE:if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
              LoadSongs();
          }else {
              Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG);
              CheckPermission();
          }break;
          default:super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }

    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer= null;
           // mAudioManager.abandonAudioFocus(afChangeListener);

        }
    }
}