package com.company.hiro.desafioandroid.BaseAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.hiro.desafioandroid.Class.PullRepositoryClass;
import com.company.hiro.desafioandroid.Class.RepositoryClass;
import com.company.hiro.desafioandroid.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DetailAdapter extends BaseAdapter {

    Context context;
    List<PullRepositoryClass> pullRepositoryClasses;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new DetailAdapter.AnimateFirstDisplayListener();

    public DetailAdapter(Context context, List<PullRepositoryClass> pullRepositoryClasses) {
        this.context = context;
        this.pullRepositoryClasses = pullRepositoryClasses;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_person_black_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.GRAY, 3))
                .build();
    }

    @Override
    public int getCount() {
        return pullRepositoryClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return pullRepositoryClasses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pullRepositoryClasses.get(position).index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        DetailAdapter.ViewHolder holder;

        if( convertView == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_pull, parent, false);
            holder = new DetailAdapter.ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (DetailAdapter.ViewHolder) view.getTag();
        }

        PullRepositoryClass pull = (PullRepositoryClass) getItem(position);
        holder.title.setText(pull.title);
        holder.body.setText(pull.body);
        holder.authorName.setText(pull.name);
        ImageLoader.getInstance().displayImage(pull.linkPhoto, holder.authorPhoto, options,animateFirstListener);

        return view;
    }
    public class ViewHolder {

        final TextView title;
        final TextView body;
        final TextView authorName;
        final ImageView authorPhoto;

        public ViewHolder(View view) {

            title = (TextView) view.findViewById(R.id.item_pull_title);
            body = (TextView) view.findViewById(R.id.item_pull_body);
            authorPhoto = (ImageView) view.findViewById(R.id.item_pull_photo);
            authorName = (TextView) view.findViewById(R.id.item_pull_authorName);

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
