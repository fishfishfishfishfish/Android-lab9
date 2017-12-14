package com.chan.android_lab9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chan.android_lab9.Adapter.CommonAdapter;
import com.chan.android_lab9.Adapter.ViewHolder;
import com.chan.android_lab9.model.Github;
import com.chan.android_lab9.service.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    EditText searchEdit;
    Button clearBtn;
    Button fetchBtn;
    RecyclerView contentList;
    List<Map<String, Object>> GithubList = new ArrayList<>();
    CommonAdapter GithubAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEdit = findViewById(R.id.searchEdit);
        clearBtn = findViewById(R.id.clearBtn);
        fetchBtn = findViewById(R.id.fetchBtn);
        contentList = findViewById(R.id.recycler_view);
        Retrofit GithubRetrofit = createRetrofit("https://api.github.com/");
        final GithubService githubservice = GithubRetrofit.create(GithubService.class);

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

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText("");
            }
        });
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = searchEdit.getText().toString();
                githubservice.getUser(User)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Github>(){
                            @Override
                            public void onCompleted() {
                                removeWait();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Github github) {

                            }
                        });
            }
        });
    }//end onCreate

    private static Retrofit createRetrofit(String baseUrl){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(createOkHttp())
                .build();
    }

    private static OkHttpClient createOkHttp(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }
}
