package Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import  android.support.v7.app.AppCompatActivity;
import android.content.Context;
import java.util.ArrayList;
import com.example.user.musicplayer.R;
import Model.SongInfo;

/*
* {@link AndroidFlavorAdapter} is an {@link ArrayAdapter} that can provide the layout for each list
* based on a data source, which is a list of {@link AndroidFlavor} objects.
* */
public class SongAdapter extends ArrayAdapter<SongInfo> {

    public SongAdapter(Activity context, ArrayList<SongInfo> songInfos){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, songInfos);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    View listItemView = convertView;
        if(listItemView == null) {
        listItemView = LayoutInflater.from(getContext()).inflate(
                R.layout.list_row, parent, false);
    }

    // Get the {@link AndroidFlavor} object located at this position in the list
    SongInfo currentSongInfo = getItem(position);

    // Find the TextView in the list_item.xml layout with the ID version_name
    TextView nameTextView = (TextView) listItemView.findViewById(R.id.SongName);
    // Get the version name from the current AndroidFlavor object and
    // set this text on the name TextView
        nameTextView.setText(currentSongInfo.getSongName());

    // Find the TextView in the list_item.xml layout with the ID version_number
    TextView numberTextView = (TextView) listItemView.findViewById(R.id.ArtistName);
    // Get the version number from the current AndroidFlavor object and
    // set this text on the number TextView
        numberTextView.setText(currentSongInfo.getArtistName());

    // Find the ImageView in the list_item.xml layout with the ID list_item_icon


    // Return the whole list item layout (containing 2 TextViews and an ImageView)
    // so that it can be shown in the ListView
        return listItemView;
}

}







/*
package Adapter;

import android.content.Context;  //
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;  //
import android.view.View;  //
import android.view.ViewGroup;//
import android.widget.Button;//
import android.widget.TextView;//

import com.example.user.musicplayer.R;//

import java.io.IOException;//
import java.util.ArrayList;//

import Model.SongInfo;//

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    ArrayList<SongInfo> _songs;
    Context context;

    public SongAdapter(Context context,ArrayList<SongInfo> _songs) {
        this._songs = _songs;
        this.context = context;
        setHasStableIds(true);
    }
    //TODO add another button click listener for stop button
    OnItemClickListner onItemClickListner;
    public interface OnItemClickListner{
        void onPlayClick(Button b,Button sb,View v ,SongInfo obj,int position) ;
        void onStopClick(Button b,Button sb,View v ,SongInfo obj,int position) ;
    }
public void setOnItemClickListner(OnItemClickListner onItemClickListner){
        this.onItemClickListner=onItemClickListner;
}
    //create layout from row
    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_row,parent,false);
        return new SongHolder(view);
    }
    //to perform action on any objects in any view
    @Override
    public void onBindViewHolder(final SongHolder holder, int position) {

        final SongInfo sinfo=_songs.get(holder.getAdapterPosition());
        holder.songName.setText(sinfo.songName);
        holder.artistName.setText(sinfo.artistName);

        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListner !=null){

                        onItemClickListner.onPlayClick(holder.actionBtn,holder.stopBtn,v,sinfo, holder.getAdapterPosition());

                }
            }
        });

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListner !=null){

                    onItemClickListner.onStopClick(holder.actionBtn,holder.stopBtn,v,sinfo, holder.getAdapterPosition());

                }
            }
        });


    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    //gives you the count of item
    @Override
    public int getItemCount() {
        return _songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView songName,artistName;
        Button actionBtn;
        Button stopBtn;
        public SongHolder(View itemView) {
            super(itemView);
            songName=(TextView)itemView.findViewById(R.id.SongName);
            artistName=(TextView)itemView.findViewById(R.id.ArtistName);
            actionBtn=(Button) itemView.findViewById(R.id.ActionBtn);
            stopBtn=(Button)itemView.findViewById(R.id.StopBtn);
        }
    }
}

*/