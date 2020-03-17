package com.example.qsort.Participants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.R;
import com.example.qsort.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Result;

public class PartiReviewActivity extends FragmentActivity {

    TextView labelTextView;
    String categories, labels, result, project_id;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    String[] categoriesList,labelsArray,resultArray;
    Button submitSortButton;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_review);

        submitSortButton = findViewById(R.id.submitSortButton);

        Intent intent = getIntent();

        result = intent.getExtras().getString("Result");
        result = result.replaceAll("[\\\\[\\\\]\\\\(\\\\)]","");
        categories = intent.getExtras().getString("Categories");
        labels = intent.getExtras().getString("Labels");
        project_id = intent.getExtras().getString("project_id");

        System.out.println("id:"+project_id);
        categoriesList = categories.split("\n");
        resultArray = result.split(",");
        labelsArray = labels.split("\n");

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }



    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new ObjectFragment();
            Bundle args = new Bundle();
            args.putString(ObjectFragment.LABEL_KEY,labels);
            args.putString(ObjectFragment.RESULT_KEY,result);
            args.putCharSequence("title", getPageTitle(position));



            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return categoriesList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoriesList[position];
        }
    }


    @Override
    public void onBackPressed()
    {
        finish();
    }

    public void submitSort(View view){

        submitSortButton.setEnabled(false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("projects").document(project_id)
                .collection("labels").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String label = document.getId();
                                int index = getIndexOf(labelsArray,label);
                                System.out.println(label+index);
                                firebaseFirestore.collection("projects").document(project_id)
                                        .collection("labels").document(label)
                                        .collection("categories").document(resultArray[index])
                                        .update("value", FieldValue.increment(1));

                            }
                        }

                    }
                });
        firebaseFirestore.collection("projects").document(project_id)
                .update("Participants", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
            }
        });

        showMessage("Thank you for your participant! Bye!");
    }

    public static int getIndexOf(String[] strings, String item) {
        for (int i = 0; i < strings.length; i++) {
            if (item.equals(strings[i])) return i;
        }
        return -1;
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }


}

