package com.example.qsort.UxResearcher;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qsort.R;

import java.util.ArrayList;
import java.util.List;


public class ObjectFragment extends Fragment {

    public static final String RESULT_KEY = "result";
    public static final String LABEL_KEY = "label";

    String result,labels;
    String[] resultArray, labelsArray;
    Context context;
    CharSequence title;
    List<String> labelInCateList;
    int index;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.ux_fragment_card, container, false);
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
        for(int i=0; i<labelsArray.length; i++){
            if(title.toString().equals(labelsArray[i])){
                index =i;
            }
        }

        TextView labelResult = (TextView) view.findViewById(R.id.UXlabelResult);
        labelResult.setText(resultArray[index]);

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