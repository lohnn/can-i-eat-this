package se.lohnn.canieat.databindingutils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import se.lohnn.canieat.dataservice.DataService;

public class ImageBindingUtils {
    @BindingAdapter({"bind:imageUUID", "bind:error"})
    public static void loadImage(ImageView view, String imageUUID, Drawable error) {
        Glide.with(view.getContext())
                .using(new FirebaseImageLoader())
                .load(DataService.Companion.getImageStorageRef(imageUUID))
                .error(error)
                .into(view);
    }
}
