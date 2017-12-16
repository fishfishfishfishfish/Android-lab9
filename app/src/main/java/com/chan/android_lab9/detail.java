package com.chan.android_lab9;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chan.android_lab9.Adapter.CommonAdapter;
import com.chan.android_lab9.Adapter.ViewHolder;
import com.chan.android_lab9.model.Detail;
import com.chan.android_lab9.model.Github;
import com.chan.android_lab9.service.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class detail extends AppCompatActivity {

    String loginName = "";
    private GithubService githubService;
    ProgressBar detailPrograss;
    RecyclerView detailList;
    List<Map<String, Object>> DetailList = new ArrayList<>();
    CommonAdapter DetailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailPrograss = findViewById(R.id.detailProgress);
        detailList = findViewById(R.id.detailList);
        Retrofit GithubRetrofit = retrofitFactory.createRetrofit("https://api.github.com/");
        githubService = GithubRetrofit.create(GithubService.class);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            loginName = extras.getString("name");
        }

        detailList.setLayoutManager(new LinearLayoutManager(this));
        DetailAdapter = new CommonAdapter(this, R.layout.detail_item_layout, DetailList) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView name = holder.getView(R.id.repo_name);
                name.setText(s.get("name").toString());
                TextView language = holder.getView(R.id.repo_lang);
                language.setText(s.get("language").toString());
                TextView description = holder.getView(R.id.repo_describe);
                description.setText(s.get("description").toString());
            }
        };
        detailList.setAdapter(DetailAdapter);
        DetailAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(DetailAdapter.getData(position, "html_url"));
                intent.setData(content_url);
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });


        githubService.getUserRepos(loginName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Detail>>() {
                    @Override
                    public void onCompleted() {
                        detailPrograss.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(detail.this, e.getMessage()+"确认你搜索的用户存在", Toast.LENGTH_LONG).show();
                        detailPrograss.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(List<Detail> details) {
                        for(int i = 0; i < details.size(); i++)
                        {
                            DetailAdapter.addData(details.get(i));
                        }
                    }
                });


    }//end onCreate
}
