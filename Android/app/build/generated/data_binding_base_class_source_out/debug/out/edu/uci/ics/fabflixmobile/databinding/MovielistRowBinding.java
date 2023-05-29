// Generated by view binder compiler. Do not edit!
package edu.uci.ics.fabflixmobile.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import edu.uci.ics.fabflixmobile.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MovielistRowBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView genres;

  @NonNull
  public final TextView rating;

  @NonNull
  public final TextView stars;

  @NonNull
  public final TextView title;

  private MovielistRowBinding(@NonNull LinearLayout rootView, @NonNull TextView genres,
      @NonNull TextView rating, @NonNull TextView stars, @NonNull TextView title) {
    this.rootView = rootView;
    this.genres = genres;
    this.rating = rating;
    this.stars = stars;
    this.title = title;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MovielistRowBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MovielistRowBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.movielist_row, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MovielistRowBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.genres;
      TextView genres = ViewBindings.findChildViewById(rootView, id);
      if (genres == null) {
        break missingId;
      }

      id = R.id.rating;
      TextView rating = ViewBindings.findChildViewById(rootView, id);
      if (rating == null) {
        break missingId;
      }

      id = R.id.stars;
      TextView stars = ViewBindings.findChildViewById(rootView, id);
      if (stars == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      return new MovielistRowBinding((LinearLayout) rootView, genres, rating, stars, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}