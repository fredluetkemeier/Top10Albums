package com.fred.topalbums;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Album album = intent.getParcelableExtra("com.fred.topalbums.ALBUM");
        ImageView albumDetailImageView = findViewById(R.id.albumDetailImageView);
        TextView albumDetailNameTextView = findViewById(R.id.albumDetailNameTextView);
        TextView artistDetailNameTextView = findViewById(R.id.artistDetailNameTextView);
        TextView albumDetailReleaseDateTextView = findViewById(R.id.albumDetailReleaseDateTextView);
        TextView albumDetailCopyrightTextView = findViewById(R.id.albumDetailCopyrightTextView);
        TextView albumDetailGenresTextView = findViewById(R.id.albumDetailGenresTextView);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = "";
        try {
            Date temp = formatter.parse(album.getReleaseDate());
            formatter.applyPattern("MMMM d, yyyy");
            formattedDate = formatter.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String genreString = "";
        for(String genre : album.getAlbumGenres()){
            genreString += ", " + genre;
        }
        genreString = genreString.replaceFirst(", ", "");

        setTitle("Album Details");

        //Low quality album art
        RequestBuilder<Drawable> thumbnail = Glide.with(this)
                .load(album.getArtworkURL())
                .onlyRetrieveFromCache(true);

        //High quality album art
        Glide.with(this)
                .load(album.getArtworkURL(1000))
                .thumbnail(thumbnail)
                .into(albumDetailImageView);

        albumDetailNameTextView.setText(album.getAlbumName());
        artistDetailNameTextView.setText(album.getArtistName());
        albumDetailReleaseDateTextView.setText(formattedDate);
        albumDetailCopyrightTextView.setText("Copyright " + album.getCopyright());
        albumDetailGenresTextView.setText(genreString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }
}
