package com.chan.android_lab9.service;

import com.chan.android_lab9.model.Github;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 61915 on 17/12/14.
 */

public interface GithubService {
    @GET("/users/{user}")
    Observable<Github> getUser(@Path("user") String user);
}
