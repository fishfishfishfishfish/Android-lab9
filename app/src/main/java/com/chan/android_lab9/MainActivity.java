package com.chan.android_lab9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chan.android_lab9.Adapter.CommonAdapter;
import com.chan.android_lab9.Adapter.ViewHolder;
import com.chan.android_lab9.model.Github;
import com.chan.android_lab9.service.GithubService;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    SharedPreferences SP;
    public static int MODE = MODE_PRIVATE;
    public static String PREFERENCE_NAME = "SaveLogin";
    EditText searchEdit;
    Button clearBtn;
    Button fetchBtn;
    RecyclerView contentList;
    ProgressBar waitPrograss;
    List<Map<String, Object>> GithubList = new ArrayList<>();
    CommonAdapter GithubAdapter;
    private GithubService githubservice;
    private int cntLogin = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SP = getSharedPreferences(PREFERENCE_NAME, MODE);
        if(SP.getString("cntLogin","").equals("")){
            SharedPreferences.Editor SPEdit = SP.edit();
            SPEdit.putString("cntLogin", Integer.toString(0));
            SPEdit.commit();
        }else{
            cntLogin = Integer.parseInt(SP.getString("cntLogin", "0"));
        }

        searchEdit = findViewById(R.id.searchEdit);
        clearBtn = findViewById(R.id.clearBtn);
        fetchBtn = findViewById(R.id.fetchBtn);
        contentList = findViewById(R.id.recycler_view);
        waitPrograss = findViewById(R.id.mian_prograss);

        Retrofit GithubRetrofit = retrofitFactory.createRetrofit("https://api.github.com/");
        githubservice = GithubRetrofit.create(GithubService.class);

        contentList.setLayoutManager(new LinearLayoutManager(this));
        GithubAdapter = new CommonAdapter(this, R.layout.main_item_layout, GithubList) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.item_name);
                name.setText(s.get("name").toString());
                TextView id = holder.getView(R.id.item_id);
                id.setText(s.get("id").toString());
                TextView blog = holder.getView(R.id.item_blog);
                blog.setText(s.get("blog").toString());
            }
        };
        contentList.setAdapter(GithubAdapter);
        GithubAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("name", GithubAdapter.getData(position, "name"));
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                GithubAdapter.removeData(position);
                return true;
            }
        });

        init_List();
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GithubAdapter.clearData();
                SharedPreferences.Editor SPEdit = SP.edit();
                cntLogin = 0;
                SPEdit.clear();
                SPEdit.commit();
            }
        });
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = searchEdit.getText().toString();
                waitPrograss.setVisibility(View.VISIBLE);
                githubservice.getUser(User)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Github>() {
                            @Override
                            public void onCompleted() {
                                waitPrograss.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this, e.getMessage()+"确认你搜索的用户存在", Toast.LENGTH_LONG).show();
                                waitPrograss.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(Github github) {
                                GithubAdapter.addData(github);
                                SharedPreferences.Editor SPEdit = SP.edit();
                                SPEdit.putString(Integer.toString(cntLogin), github.getLogin());
                                cntLogin += 1;
                                SPEdit.putString("cntLogin", Integer.toString(cntLogin));
                                SPEdit.commit();
                            }
                        });
            }
        });
    }//end onCreate

    void init_List(){
        for(int i = 0; i < cntLogin; i++){
            String User = SP.getString(Integer.toString(i), "");
            waitPrograss.setVisibility(View.VISIBLE);
            githubservice.getUser(User)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Github>() {
                        @Override
                        public void onCompleted() {
                            waitPrograss.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, e.getMessage()+"确认你搜索的用户存在", Toast.LENGTH_LONG).show();
                            waitPrograss.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(Github github) {
                            GithubAdapter.addData(github);
                        }
                    });
        }
    }
}
