package com.example.qsort.UxResearcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qsort.R;
import com.example.qsort.TextComment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UxTextCommentAdapter extends RecyclerView.Adapter<UxTextCommentAdapter.MyViewHolder> {
    ArrayList<TextComment> textComments;
    private Context mContext;

    public UxTextCommentAdapter(Context context, ArrayList<TextComment> textCommentsList) {
        this.mContext = context;
        this.textComments = textCommentsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_comment,parent,false);
        UxTextCommentAdapter.MyViewHolder myViewHolder = new UxTextCommentAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Long timestampLong = Long.parseLong(String.valueOf(textComments.get(position).getTimestamp()));
        if (timestampLong < 10000000000L) {
            timestampLong = timestampLong * 1000;
        }

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(Long.parseLong(String.valueOf(timestampLong)));
        String sd = sf.format(new Date(Long.parseLong(String.valueOf(timestampLong))));
        holder.timestamp.setText(sd);
        holder.comment.setText(textComments.get(position).getTextComment());
    }

    @Override
    public int getItemCount() {
        return textComments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView timestamp;
        TextView comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.textComment);
            timestamp = itemView.findViewById(R.id.textCommentTime);
        }
    }
}
