package com.example.nemo.data.remote;

import com.example.nemo.data.model.Grammar;
import com.example.nemo.data.model.Topic;
import com.example.nemo.data.model.User;
import com.example.nemo.data.model.Vocabulary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Call<List<User>> login(@Body User user);

    @POST("register")
    Call<List<User>> register(@Body User user);

    @GET("topics")
    Call<List<Topic>> getTopics();

    @GET("levels")
    Call<List<String>> getLevels();

    @GET("vocabulary")
    Call<List<Vocabulary>> getVocabulary(@Query("topic_id") Integer topicId, @Query("level") String level);

    @GET("vocabulary")
    Call<List<Vocabulary>> getVocabularyByTopic(@Query("topic_id") int topicId);

    @GET("vocabulary")
    Call<List<Vocabulary>> getVocabularyByLevel(@Query("level") String level);

    @GET("grammar")
    Call<List<Grammar>> getGrammarLessons();

    @GET("collection")
    Call<List<Vocabulary>> getMyCollection(@Header("Authorization") String token);

    @POST("collection/add")
    Call<Void> addToCollection(@Header("Authorization") String token, @Body Vocabulary vocabulary);
}
