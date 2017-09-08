package com.java.no_36;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;

/**
 * Created by admin on 2017/9/7.
 */

public class GlideImageView extends AppCompatImageView
{
    public GlideImageView(Context context)
    {
        super(context);
    }

    public GlideImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;

        Glide.with(this).load(image_url).apply(placeholderOf(R.drawable.user_placeholder))
                .apply(errorOf(R.drawable.user_placeholder_error)).into(this);
    }

    public String getImage_url() {
        return image_url;
    }

    private String image_url;
}
