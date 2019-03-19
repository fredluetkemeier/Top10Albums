package com.fred.topalbums;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    public static final String EXTRA_ALBUM = "com.fred.topalbums.ALBUM";
    private ArrayList<Album> albums;
    CircularProgressDrawable loadingSpinner;

    public AlbumsAdapter(ArrayList<Album> albums){
        this.albums = albums;
    }

    @Override
    public AlbumsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View albumView = inflater.inflate(R.layout.item_album, parent, false);
        ViewHolder viewHolder = new ViewHolder(albumView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Album album = albums.get(position);

        ImageView albumImageView = viewHolder.albumImageView;
        TextView albumNameTextView = viewHolder.albumNameTextView;
        TextView artistNameTextView = viewHolder.artistNameTextView;

        loadingSpinner = new CircularProgressDrawable(viewHolder.context);
        loadingSpinner.setStrokeWidth(5);
        loadingSpinner.setCenterRadius(50);
        loadingSpinner.start();
        Glide.with(viewHolder.context).load(album.getArtworkURL(200)).placeholder(loadingSpinner).into(albumImageView);

        albumNameTextView.setText(album.getAlbumName());
        artistNameTextView.setText(album.getArtistName());

        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(viewHolder.context, DetailActivity.class);
            intent.putExtra(EXTRA_ALBUM, album);
            viewHolder.context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        public ImageView albumImageView;
        public TextView albumNameTextView;
        public TextView artistNameTextView;

        public ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();

            albumImageView = itemView.findViewById(R.id.albumImageView);
            albumNameTextView = itemView.findViewById(R.id.albumDetailNameTextView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
        }
    }
}
