package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.qsort.UxResearcher.ObjectFragment;
import com.example.qsort.UxResearcher.UxTabReviewActivity;
import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class UxTabReviewActivity extends AppCompatActivity {
    ViewPager viewPager;
    UxTabReviewActivity.PagerAdapter pagerAdapter;
    String labels, categories, project_id;
    String[] labelList, categoriesList, categoriesTitleList;
    String categoryTitle;
    StringBuilder result;
    FirebaseFirestore firebaseFirestore;

    String[] temp_cate_lst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_tab_review);

        Intent intent = getIntent();
        labels = intent.getExtras().getString("Labels");
        categories = intent.getExtras().getString("Category");
        project_id = intent.getExtras().getString("project_id");

        labels = labels.replaceAll("[\\[\\]\\(\\)]","");
        categories = categories.replaceAll("[\\[\\]\\(\\)]","");
        System.out.println("----LABELS"+labels);

        labelList = labels.split(", ");
        categoriesList = categories.split(", ");
        System.out.println("Length:"+categoriesList.length);
        for(int i=0; i<categoriesList.length; i++){
            System.out.println(categoriesList[i]);
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("projects").document(project_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            categoryTitle = document.getData().get("Categories").toString();
                            categoriesTitleList = categoryTitle.split("\n");
                        }
                        result = new StringBuilder();
                        for(int i=0; i<categoriesTitleList.length; i++){
                            for(int j=0; j<categoriesList.length; j++){
                                if(categoriesTitleList[i].equals(categoriesList[j])){
                                    result.append(labelList[j]+"\n");
//                                    System.out.println("Find equal:::"+labelList[j]+","+categoriesTitleList[i]);
                                }
                            }
                            result.append(",");
                        }

                        viewPager = (ViewPager) findViewById(R.id.ux_pager);
                        pagerAdapter = new UxTabReviewActivity.PagerAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(pagerAdapter);
                    }
                });

    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new ObjectFragment();
            Bundle args = new Bundle();
            args.putString(ObjectFragment.LABEL_KEY,categoryTitle);
            args.putString(ObjectFragment.RESULT_KEY,result.toString());
            args.putCharSequence("title", getPageTitle(position));

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return categoriesTitleList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoriesTitleList[position];
        }
    }


}
