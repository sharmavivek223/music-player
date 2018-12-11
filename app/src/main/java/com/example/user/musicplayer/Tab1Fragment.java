package com.example.user.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Adapter.SongAdapter;
import Model.SongInfo;
import Adapter.SongAdapter.SongHolder;
/**
 * Created by User on 2/28/2017.
 */

public class Tab1Fragment extends Fragment{
    private static final String TAG = "Tab1Fragment";
    protected MediaPlayer mediaPlayer;
    protected SeekBar seekBar;
    protected RecyclerView recyclerView;
    protected SongAdapter songAdapter;
    protected AudioManager mAudioManager;
    protected ArrayList<SongInfo> songsArray= new ArrayList<SongInfo>();
    protected  final int REQ_CODE=123;
    protected int prevPosition=-1,currentPosition=-1;
    protected TextView elapsedTimeTextView,durationTextView;
    //private Handler handler;
    protected ImageButton playButton,nextButton,prevButton;
    protected int trial=0;
    //trial variable controls raising sound at the end of notification sound
    double duration=0;
    int size;
    boolean isPaused=false;
    int minutes=0,seconds=0;
    protected Thread updateThread;
    Handler handler;
    Runnable runnable;







    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if(mediaPlayer.isPlaying()==false)
                        return;
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback because your Audio Focus was
                        // temporarily stolen, but will be back soon.
                        // i.e. for a phone call
                        pauseMediaPlayer();
                        //prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else if(focusChange==AudioManager.AUDIOFOCUS_GAIN_TRANSIENT){
                        pauseMediaPlayer();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Stop playback, because you lost the Audio Focus.
                        // i.e. the user started some other playback app
                        // Remember to unregister your controls/buttons here.
                        // And release the kra — Audio Focus!
                        // You’re done.
                        releaseMediaPlayer();
                        //prevStopButton.setVisibility(View.GONE);
                        //prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    } else if (focusChange ==
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, because something else is also
                        // playing audio over you.
                        // i.e. for notifications or navigation directions
                        // Depending on your audio playback, you may prefer to
                        // pause playback here instead. You do you.
                        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
                        mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);

                        trial=1;

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                            focusChange==AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE) {
                        // Resume playback, because you hold the Audio Focus
                        // again!
                        // i.e. the phone call ended or the nav directions
                        // are finished
                        // If you implement ducking and lower the volume, be
                        // sure to return it to normal here, as well.
                        if(!isPaused) {
                           mediaPlayer.start();
                            //prevStopButton.setVisibility(View.VISIBLE);
                            //prevPlayButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                        }
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        songsArray = new ArrayList<SongInfo>();
        mAudioManager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        recyclerView =(RecyclerView) view.findViewById(R.id.recyclerView);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        elapsedTimeTextView=(TextView)view.findViewById(R.id.elapsedTimeTextView);
        durationTextView=(TextView)view.findViewById(R.id.durationTextView);
        songAdapter=new SongAdapter(getContext(),songsArray);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setHasFixedSize(true);

        handler=new Handler();
        playButton=(ImageButton)view.findViewById(R.id.play_button);
        nextButton=(ImageButton)view.findViewById(R.id.next_button);
        prevButton=(ImageButton)view.findViewById(R.id.prev_button);
        CheckPermission();

playButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        pauseMediaPlayer();
    }
});
nextButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        songsArray.get(currentPosition).setImageResource(null);
        //songAdapter.notifyDataSetChanged();
        songAdapter.notifyItemChanged(currentPosition);
        prevPosition=currentPosition;
        playNextSong(currentPosition);
    }
});
prevButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(currentPosition>=1)
        {
            songsArray.get(currentPosition).setImageResource(null);
            songAdapter.notifyItemChanged(currentPosition);
            prevPosition=currentPosition;
            //changing song if its not the first element
            if(currentPosition!=1)
                playNextSong(currentPosition-2);
            else
                playNewSong(songsArray.get(0),0);



        }

        else {//play the last song if the current song is first one.
            songsArray.get(currentPosition).setImageResource(null);
            //songAdapter.notifyDataSetChanged();
            songAdapter.notifyItemChanged(currentPosition);
            playNewSong(songsArray.get(songsArray.size()-1),songsArray.size()-1);
            }
    }
});



        songAdapter.setOnItemClickListner(new SongAdapter.OnItemClickListner() {
            @Override
            public void onPlayClick( final View v, final SongInfo obj, int position, int index){


                //code for play buttons goes here
                prevPosition=currentPosition;

                if(prevPosition==index)
                {
                    pauseMediaPlayer();
                }

                else
                {

                    if(prevPosition!=-1)
                    {
                        songsArray.get(prevPosition).setImageResource(null);
                        songAdapter.notifyDataSetChanged();
                    }
                    playNewSong(obj,index);
                }


             //prevPosition=index;
                currentPosition=index;

//todo whatever
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(mediaPlayer==null)
                        return;
                    mediaPlayer.seekTo(progress);
                    elapsedTimeTextView.setText(timeConvertor(mediaPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
    private void CheckPermission() {
        //since only in sdk greater than 23 the permissions are granted after installing app
        //so we have to only ask for permission when the android version is greater or equal to marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_CODE);
                return;
            }
            LoadSongs();
            addSongsToAdapter();

        }else{//if it is less than marshmallow the permissions would be grantd at installation
            //so we will straight away load songs
            LoadSongs();
            addSongsToAdapter();
        }
    }

    private String timeConvertor(int millis){
        millis=millis/1000;
        int c= Math.round(millis);
        minutes=c/60;
        seconds=c%60;
        String converted;
        if(seconds<10)
            converted=minutes+":0"+seconds;
        else
            converted=minutes+":"+seconds;
        return converted;
    }


    protected void LoadSongs() {//Time to fetch songs data
        /*
        uri stands for uniform resourse indicator,a lot lot like an url
        every url is uri but not every uri is url since url and urn(UR Names) are both
        types of uri*/
        Uri songUri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //selection will help to select the music files
        String selection=MediaStore.Audio.Media.IS_MUSIC+"!=0";
        //Curson is just providing us a reference to a 2D table containing info of all files that passes the selection
        Cursor cursor=getActivity().getContentResolver().query(songUri,null,selection,null,null);
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
                    if(artist.equalsIgnoreCase("Your recordings"))
                        continue;
                    SongInfo sinfo=new SongInfo(name,artist,url,null);
                    songsArray.add(sinfo);

                }while(cursor.moveToNext());
            }//we have to get rid of cursor after use since it will be heavy on memory
            cursor.close();
            size=songsArray.size();
            //time to set all info in an adapter to display it



        }
    }
    private void addSongsToAdapter(){

        songAdapter=new SongAdapter(getContext(),songsArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setItemViewCacheSize(songAdapter.getItemCount());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQ_CODE:if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                LoadSongs();
            }else {
                Toast.makeText(getContext(),"Permission Denied",Toast.LENGTH_LONG);
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
            //handler.removeCallbacks(r);
            handler.removeCallbacks(runnable);

        }
    }
    private void resetSeekbar(){
        seekBar.setProgress(0);
        elapsedTimeTextView.setText("00:00");
        durationTextView.setText("00:00");
    }

    public void playCycle(){

      seekBar.setProgress(mediaPlayer.getCurrentPosition());
        String converted=timeConvertor(mediaPlayer.getCurrentPosition());
        elapsedTimeTextView.setText(converted);
        if(mediaPlayer.isPlaying())
        {
            runnable=new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable,100);
        }

    }

    public void playNextSong(final int index) {

        int newIndex=0;
        //prevPosition=index;

        songsArray.get(index).setImageResource(null);
        songAdapter.notifyItemChanged(index);

        if(prevPosition<size-2)
           newIndex=index+1;

        playNewSong(songsArray.get(newIndex),newIndex);

    }


    public void playNewSong(final SongInfo obj, final int index)
    {

        releaseMediaPlayer();
        int result=mAudioManager.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if(result!=AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            return;
        mediaPlayer=MediaPlayer.create(getContext(), Uri.parse(obj.getSongUrl()));
        duration=mediaPlayer.getDuration();
        durationTextView.setText(timeConvertor((int) duration));
        seekBar.setMax(mediaPlayer.getDuration());
        obj.setImageResource(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
        //songAdapter.notifyDataSetChanged();
        songAdapter.notifyItemChanged(index);
        prevPosition=currentPosition;
        currentPosition=index;
        mediaPlayer.start();
        playCycle();
        playButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
        isPaused=false;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
                resetSeekbar();
                playNextSong(index);

            }
        });


    }

    public void pauseMediaPlayer()
    {
        if(mediaPlayer!=null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));



            } else {

                mediaPlayer.start();
                playCycle();
                playButton.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));


            }
        }else{
            playNewSong(songsArray.get(0),0);
        }


    }






}
