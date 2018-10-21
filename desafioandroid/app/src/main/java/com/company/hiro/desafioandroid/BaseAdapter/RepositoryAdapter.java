package com.company.hiro.desafioandroid.BaseAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.hiro.desafioandroid.Class.RepositoryClass;
import com.company.hiro.desafioandroid.GlobalVariable;
import com.company.hiro.desafioandroid.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedVignetteBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RepositoryAdapter extends BaseAdapter {

    Context context;
    List<RepositoryClass> repositoryList;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;


    public RepositoryAdapter(List<RepositoryClass> repositoryClasses, Context context1){
        this.repositoryList = repositoryClasses;
        this.context = context1;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_person_black_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.GRAY, 3))
                .build();
        animateFirstListener = new AnimateFirstDisplayListener();
    }

    @Override
    public int getCount() {
        return repositoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return repositoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return repositoryList.get(i).index;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;

        if( convertView == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_repository, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        RepositoryClass repository = (RepositoryClass) getItem(i);
        holder.repositoryName.setText(repository.nameRepository);
        holder.description.setText(repository.description);
        holder.authorName.setText(repository.nameAuthor);
        ImageLoader.getInstance().displayImage(repository.linkPhoto, holder.authorPhoto, options,animateFirstListener);
        holder.starNumber.setText(String.valueOf(repository.starNumber));
        holder.forkNumber.setText(String.valueOf(repository.forkNumber));
        return view;
    }

    public class ViewHolder {

        final TextView repositoryName;
        final TextView description;
        final TextView authorName;
        final ImageView authorPhoto;
        final TextView starNumber;
        final TextView forkNumber;

        public ViewHolder(View view) {

            repositoryName = (TextView) view.findViewById(R.id.item_repo_name_repo);
            description = (TextView) view.findViewById(R.id.item_repo_description_repo);
            authorName = (TextView) view.findViewById(R.id.item_repo_author_name);
            authorPhoto = (ImageView) view.findViewById(R.id.item_repo_photo);
            starNumber = (TextView) view.findViewById(R.id.item_repo_star);
            forkNumber = (TextView) view.findViewById(R.id.item_repo_fork);

        }
    }
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
