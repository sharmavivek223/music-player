package com.example.user.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import  android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Adapter.SongAdapter;
import Model.SongInfo;

public class MainActivity extends AppCompatActivity {
    private   MediaPlayer mediaPlayer;
    private   SeekBar seekBar;
    private  final int REQ_CODE=123;
    private int prevPosition=-1;
    private Button prevPlayButton,prevStopButton;
    private AudioManager mAudioManager;
    private int trial=0;
    //trial variable checks for raising sound back after notification sound



    final ArrayList<SongInfo> songInfos=new ArrayList<SongInfo>();


    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback because your Audio Focus was
                        // temporarily stolen, but will be back soon.
                        // i.e. for a phone call
                        mediaPlayer.pause();
                        prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Stop playback, because you lost the Audio Focus.
                        // i.e. the user started some other playback app
                        // Remember to unregister your controls/buttons here.
                        // And release the kra — Audio Focus!
                        // You’re done.
                        releaseMediaPlayer();
                        changeButton(prevStopButton,prevPlayButton);
                    } else if (focusChange ==
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, because something else is also
                        // playing audio over you.
                        // i.e. for notifications or navigation directions
                        // Depending on your audio playback, you may prefer to
                        // pause playback here instead. You do you.
                        setVolumeControlStream(AudioManager.STREAM_MUSIC);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);

                        trial=1;

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                            focusChange==AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                            focusChange==AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE) {
                        // Resume playback, because you hold the Audio Focus
                        // again!
                        // i.e. the phone call ended or the nav directions
                        // are finished
                        // If you implement ducking and lower the volume, be
                        // sure to return it to normal here, as well.
                        prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                        mediaPlayer.start();
                        if(trial==1)
                        {
                            mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
                            mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
                            mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);

                            trial=0;
                        }
                    }

                }
            };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);



        CheckPermission();

        SongAdapter adapter=new SongAdapter(this,songInfos);
        ListView listView=(ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SongInfo songInfo=songInfos.get(position);
                int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                 if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                   return;
                releaseMediaPlayer();
                mediaPlayer=MediaPlayer.create(MainActivity.this, Uri.parse(songInfo.getSongUrl()));
                mediaPlayer.start();
            }
        });



/*



       // mediaPlayer = new MediaPlayer();
        songAdapter.setOnItemClickListner(new SongAdapter.OnItemClickListner() {

            @Override
            public void onStopClick(Button b, Button sb, View v, SongInfo obj, int position) {

                releaseMediaPlayer();
                //mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(obj.getSongUrl()));
                sb.setVisibility(View.GONE);
                b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));

            }

            @Override
            public void onPlayClick(Button b, Button sb, View v, final SongInfo obj, int position) {
                //code for play buttons goes here

                int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    return;


                if (prevPosition == position) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else {
                        mediaPlayer.start();
                        b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    }
                } else {
                    releaseMediaPlayer();
                    mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(obj.getSongUrl()));
                    b.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    if (prevPosition != -1) {
                        prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                        prevStopButton.setVisibility(View.GONE);
                    }
                    mediaPlayer.start();
                    sb.setVisibility(View.VISIBLE);

                }
                //im setting it visible here cause after a stop button press it will not be visible again on play button click
                sb.setVisibility(View.VISIBLE);

                prevPosition = position;
                prevPlayButton = b;
                prevStopButton = sb;
            }
        });
*/

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
                songInfos.add(sinfo);
               }while(cursor.moveToNext());
            }//we have to get rid of cursor after use since it will be heavy on memory
            cursor.close();
            //time to set all info in an adapter to display it

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
            mAudioManager.abandonAudioFocus(afChangeListener);

        }
    }

    private void changeButton(Button sp, Button playPause)
    {
        sp.setVisibility(View.GONE);
        playPause.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
    }
}



