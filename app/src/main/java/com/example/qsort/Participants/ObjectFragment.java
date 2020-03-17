package com.example.qsort.Participants;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.qsort.R;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.VerticalTabLayout;


public class ObjectFragment extends Fragment {

    public static final String RESULT_KEY = "result";
    public static final String LABEL_KEY = "label";

    String result,labels;
    String[] resultArray, labelsArray;
    Context context;
    CharSequence title;
    List<String> labelInCateList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_card, container, false);
        context = container.getContext();

        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        result = args.getString(RESULT_KEY);
        labels = args.getString(LABEL_KEY);
        title = args.getString("title");
        resultArray = result.split(",");
        labelsArray = labels.split("\n");
        labelInCateList = new ArrayList<>();

        for(int i=0; i <resultArray.length;i++){
            if(resultArray[i].equals(title.toString())){
                labelInCateList.add(labelsArray[i]);
            }
        }
//
//
        TextView labelResult = (TextView) view.findViewById(R.id.labelResult);
        labelResult.setText(transferToString(labelInCateList));

    }

    public String transferToString(List<String> list){
        String listString = "";

        for (String s : list)
        {
            listString += s + "\n\n";
        }

        return listString;
    }

}
