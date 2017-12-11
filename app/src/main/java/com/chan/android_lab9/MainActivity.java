package com.chan.android_lab9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText searchEdit;
    Button clearBtn;
    Button fetchBtn;
    RecyclerView contentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEdit = findViewById(R.id.searchEdit);
        clearBtn = findViewById(R.id.clearBtn);
        fetchBtn = findViewById(R.id.fetchBtn);
        contentList = findViewById(R.id.recycler_view);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText("");
            }
        });
    }//end onCreate
}
