package io.github.andyradionov.udacitybakingapp.data.utils;

import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @author Andrey Radionov
 */

public class ImageHelper {
    private ImageHelper() {
    }

    public static void loadImageIntoView(ImageView imageView, String imgUrl) {
        Picasso.get()
                .load(imgUrl)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        imageView.setVisibility(View.GONE);
                    }
                });
    }
}
