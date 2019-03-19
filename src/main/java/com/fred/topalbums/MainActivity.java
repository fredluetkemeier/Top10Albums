package com.fred.topalbums;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Album> albums;
    private String itunesRSSFeedURL = "https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/10/explicit.json";
    private String itunesRSSFeedURLNonExplicit = "https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/10/non-explicit.json";
    private String currentFeed = itunesRSSFeedURL;
    private Boolean isFeedExplicit = true;
    private Context context = this;
    private DividerItemDecoration decoration;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        loadFeed(currentFeed);

        final FloatingActionButton refreshFab = findViewById(R.id.refreshFab);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        refreshFab.setOnClickListener(v -> loadFeed(currentFeed));
        refreshFab.setOnLongClickListener(v -> {
            if(isFeedExplicit){
                currentFeed = itunesRSSFeedURLNonExplicit;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Switched to non-explicit feed", duration);
                toast.show();
                isFeedExplicit = false;
                loadFeed(currentFeed);
            } else {
                currentFeed = itunesRSSFeedURL;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Switched to explicit feed", duration);
                toast.show();
                isFeedExplicit = true;
                loadFeed(currentFeed);
            }

            return true;
        });
    }

    private void loadFeed(String feedURL){
        albums = new ArrayList<>();
        if(isConnectionAvailable()){
            final FloatingActionButton refreshFab = findViewById(R.id.refreshFab);
            refreshFab.hide();

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            final Request request = new Request.Builder().url(feedURL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, "Could not load feed", duration);
                        toast.show();
                        refreshFab.show();
                        progressBar.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject feed = null;
                    try {
                        JSONObject top = new JSONObject(response.body().string());
                        feed = top.getJSONObject("feed");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(feed != null){
                        try {
                            JSONArray results = feed.getJSONArray("results");
                            for(int i = 0; i < results.length(); i++){
                                JSONObject current = results.getJSONObject(i);
                                String artistName = current.getString("artistName");
                                String releaseDate = current.getString("releaseDate");
                                String albumName = current.getString("name");
                                String copyright = current.getString("copyright");
                                String artworkURL = current.getString("artworkUrl100");

                                JSONArray albumGenresArray = current.getJSONArray("genres");
                                ArrayList<String> albumGenres = new ArrayList<>();
                                for(int j = 0; j < albumGenresArray.length(); j++){
                                    JSONObject currentGenre = albumGenresArray.getJSONObject(j);
                                    albumGenres.add(currentGenre.getString("name"));
                                }

                                albums.add(new Album(artistName, releaseDate, albumName, copyright, artworkURL, albumGenres));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(() -> {
                            RecyclerView albumRView = findViewById(R.id.albumRView);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                            albumRView.removeItemDecoration(decoration);

                            AlbumsAdapter adapter = new AlbumsAdapter(albums);
                            albumRView.setAdapter(adapter);
                            albumRView.setLayoutManager(layoutManager);
                            albumRView.setHasFixedSize(true);

                            decoration = new DividerItemDecoration(albumRView.getContext(), layoutManager.getOrientation());
                            albumRView.addItemDecoration(decoration);

                            refreshFab.show();
                            progressBar.setVisibility(View.GONE);

                            if(isFeedExplicit){
                                setTitle("Top 10 iTunes Albums: Explicit");
                            } else {
                                setTitle("Top 10 iTunes Albums: Non-Explicit");
                            }
                        });
                    }
                }
            });
        }
    }

    private boolean isConnectionAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
