package com.example.qsort.Participants;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.R;
import com.example.qsort.UxResearcher.UxMainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartiMainActivity extends AppCompatActivity {

    RecyclerView sortRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    String categories, labels, project_id, project_name;
    String[] labelsArray,categoriesArray;
    Button reviewSortButton;
    SortAdapter myAdapter;
    TextView projectLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_main);

        reviewSortButton = findViewById(R.id.reviewSortButton);
        sortRecyclerView = findViewById(R.id.sortRecyclerView);
        projectLabel = findViewById(R.id.cardSortingLabel);

        Intent intent = getIntent();

        categories = intent.getExtras().getString("Categories");
        labels = intent.getExtras().getString("Labels");
        project_name = intent.getExtras().getString("project_name");
        project_id = intent.getExtras().getString("project_id");

        projectLabel.setText(project_name);
        createRecyclerView();

    }

    public void createRecyclerView(){
        labelsArray = labels.split("\n");
        categoriesArray = categories.split("\n");
        List<String> list = new ArrayList<>(Collections.nCopies(labelsArray.length, ""));


        String[] newArray = new String[categoriesArray.length+1];
        newArray[0]="";
        for(int i=1; i<newArray.length;i++){
            newArray[i]=categoriesArray[i-1];
        }


        myAdapter = new SortAdapter(PartiMainActivity.this,labelsArray,newArray,list,project_id);
        layoutManager = new GridLayoutManager(PartiMainActivity.this, 1);
        sortRecyclerView.setLayoutManager(layoutManager);
        sortRecyclerView.setAdapter(myAdapter);

        reviewSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myAdapter.list.contains("")){
                    Toast.makeText(getApplicationContext(),"Please make all selections",Toast.LENGTH_SHORT).show();
                }
                else{

                    Intent intent = new Intent(getApplicationContext(), PartiReviewActivity.class);

                    intent.putExtra("Result",transferToString(myAdapter.list));
                    intent.putExtra("Labels",labels);
                    intent.putExtra("Categories", categories);
                    intent.putExtra("project_id",project_id);

                    startActivity(intent);
                }

            }
        });
    }

    public String transferToString(List<String> list){
        String listString = "";

        for (String s : list)
        {
            listString += s + ",";
        }

        return listString;
    }



}
