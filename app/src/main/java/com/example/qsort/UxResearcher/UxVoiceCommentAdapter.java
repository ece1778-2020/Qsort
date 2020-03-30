package com.example.qsort.UxResearcher;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.Participants.CommentActivity;
import com.example.qsort.Project;
import com.example.qsort.R;
import com.example.qsort.VoiceComment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UxVoiceCommentAdapter extends RecyclerView.Adapter<UxVoiceCommentAdapter.MyViewHolder> {
    ArrayList<VoiceComment> voiceComments;
    private Context mContext;
    private MediaPlayer mediaPlayer;

    public UxVoiceCommentAdapter(Context context, ArrayList<VoiceComment> voiceCommentsList) {
        this.mContext = context;
        this.voiceComments = voiceCommentsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_comment,parent,false);
        UxVoiceCommentAdapter.MyViewHolder myViewHolder = new UxVoiceCommentAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Long timestampLong = Long.parseLong(String.valueOf(voiceComments.get(position).getTimestamp()));
        if (timestampLong < 10000000000L) {
            timestampLong = timestampLong * 1000;
        }

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(Long.parseLong(String.valueOf(timestampLong)));
        String sd = sf.format(new Date(Long.parseLong(String.valueOf(timestampLong))));
        holder.timestamp.setText(sd);

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.setDataSource(voiceComments.get(position).getStorageRef());
                    mediaPlayer.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(mContext, "Playing...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return voiceComments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView timestamp;
        Button btnPlay;
        Button btnStop;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnStop = itemView.findViewById(R.id.btnFinish);
            timestamp = itemView.findViewById(R.id.voiceCommentTime);
        }
    }
}
