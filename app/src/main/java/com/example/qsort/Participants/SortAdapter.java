package com.example.qsort.Participants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qsort.Project;
import com.example.qsort.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortAdapter extends RecyclerView.Adapter<com.example.qsort.Participants.SortAdapter.MyViewHolder> {
    String[] labelList;
    String[] categoryList;
    String project_id;
    ArrayAdapter<String> adapter;
    Map<Integer, Integer> mSpinnerSelectedItem = new HashMap<Integer, Integer>();

    List<String> list;
    private Context mContext;


    public SortAdapter(Context context, String[] labelList, String[] categoryList, List<String> list, String project_id){
        this.mContext = context;
        this.labelList = labelList;
        this.categoryList = categoryList;
        this.list = list;
        this.project_id = project_id;

    }

    @NonNull
    @Override
    public com.example.qsort.Participants.SortAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_card_view,parent,false);
        com.example.qsort.Participants.SortAdapter.MyViewHolder myViewHolder = new com.example.qsort.Participants.SortAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final com.example.qsort.Participants.SortAdapter.MyViewHolder holder, final int position) {

        holder.labelToSort.setText(labelList[position]);

        adapter = new ArrayAdapter<String>
                (mContext, android.R.layout.simple_spinner_item,
                        categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.categorySpinner.setAdapter(adapter);



        if (mSpinnerSelectedItem.containsKey(position)) {
            holder.categorySpinner.setSelection(mSpinnerSelectedItem.get(position));
        }

        holder.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                list.set(position,parent.getItemAtPosition(index).toString());
                mSpinnerSelectedItem.put(position, index);

                System.out.println(position+":"+parent.getItemAtPosition(index).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(mContext,"Please make all selections adapter",Toast.LENGTH_SHORT).show();
                return;
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);

                intent.putExtra("Project_id",project_id);
                intent.putExtra("label",labelList[position]);
                // start the activity
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return labelList.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView labelToSort;
        Spinner categorySpinner;
        LinearLayout commentButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            labelToSort = itemView.findViewById(R.id.labelToSort);
            categorySpinner = itemView.findViewById(R.id.categorySpinner);
            commentButton = itemView.findViewById(R.id.commentLinearLayout);
        }
    }



}
