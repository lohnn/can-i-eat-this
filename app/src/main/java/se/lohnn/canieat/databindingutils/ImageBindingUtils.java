package se.lohnn.canieat.databindingutils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageBindingUtils {
    @BindingAdapter({"bind:imageUUID", "bind:error"})
    public static void loadImage(ImageView view, String imageUUID, Drawable error) {
        Glide.with(view.getContext())
                .using(new FirebaseImageLoader())
                .load(getImageStorageRef(imageUUID))
                .error(error)
                .into(view);
    }

    private static StorageReference getImageStorageRef(String imageUUID) {
        if (imageUUID == null) return null;
        return FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://can-i-eat-this-ca957.appspot.com")
                .child("canieatthis")
                .child("images")
                .child(imageUUID + ".jpg");
    }
}
