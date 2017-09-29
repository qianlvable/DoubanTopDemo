package com.lvable.ningjiaqi.doubanrx.utils;

import com.lvable.ningjiaqi.doubanrx.data.Actor;
import com.lvable.ningjiaqi.doubanrx.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningjiaqi on 16/4/26.
 */
public class JsonParser {
    public static Movie parseSingleMovie(JSONObject jsonObj) throws JSONException {
        Movie movie = new Movie();
        movie.id = jsonObj.getString("id");
        movie.year = jsonObj.getInt("year");
        movie.title = jsonObj.getString("title");
        movie.orignTitle = jsonObj.getString("original_title");
        movie.directors = jsonObj.getJSONArray("directors").getJSONObject(0).getString("name");

        JSONObject images = jsonObj.getJSONObject("images");
//        movie.imgUrl[0] = images.getString("small");
        movie.imgUrl = images.getString("large");
//        movie.imgUrl[2] = images.getString("medium");

        JSONObject ratings = jsonObj.getJSONObject("rating");
        movie.rating = Float.parseFloat(ratings.getString("average"));
        return movie;
    }

    public static List<Movie> parseMovieList(String jsonStr){
        List<Movie> result = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray items = jsonObject.getJSONArray("subjects");
            for (int i = 0;i < items.length();i++){
                result.add(JsonParser.parseSingleMovie(items.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }



}
