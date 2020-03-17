package com.example.qsort.UxResearcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qsort.Project;
import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UxReportButtonAdapter extends RecyclerView.Adapter<UxReportButtonAdapter.MyViewHolder> {
    ArrayList<String> labelList;
    private Context mContext;
    String project_id;
    int index = -1;
    Boolean clicked = true;

    public UxReportButtonAdapter(Context context, ArrayList<String> labelList, String project_id){
        this.mContext = context;
        this.labelList = labelList;
        this.project_id = project_id;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_button_view,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.labelButton.setText(labelList.get(position));
        clicked = true;
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        Button labelButton;
        LinearLayout linearlayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            labelButton = itemView.findViewById(R.id.labelReportButton);
//            labelButton.setBackgroundColor(Color.GRAY);
            linearlayout = itemView.findViewById(R.id.linear);
        }
    }

}
