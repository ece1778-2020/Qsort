package com.example.qsort.UxResearcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qsort.Project;
import com.example.qsort.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UxRecyclerViewAdapter extends RecyclerView.Adapter<UxRecyclerViewAdapter.MyViewHolder> {
    ArrayList<Project> projects;
    private Context mContext;

    public UxRecyclerViewAdapter(Context context, ArrayList<Project> projects){
        this.mContext = context;
        this.projects = projects;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_view,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(projects.get(position).getProjectName());
        Picasso.get().load(projects.get(position).getPictureUri()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UxReportActivity.class);
                intent.putExtra("project_id", projects.get(position).getProjectId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.projectImageView);
            textView = itemView.findViewById(R.id.projectName);
        }
    }
}
