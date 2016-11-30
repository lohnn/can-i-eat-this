package se.lohnn.canieat.databindingutils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import se.lohnn.canieat.dataservice.DataService;

public class ImageBindingUtils {
    @BindingAdapter({"bind:imagePath", "bind:error"})
    public static void loadImage(ImageView view, String imagePath, Drawable error) {
        if (imagePath == null) {
            return;
        }
        if (imagePath.startsWith("file://")) {
            String substring = imagePath.substring(7);
            Glide.with(view.getContext())
                    .load(substring)
                    .error(error)
                    .into(view);
        } else {
            Glide.with(view.getContext())
                    .using(new FirebaseImageLoader())
                    .load(DataService.Companion.getImageStorageRef(imagePath))
                    .error(error)
                    .into(view);
        }
    }
}
