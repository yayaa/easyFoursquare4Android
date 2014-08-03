package br.com.condesales.listeners;

import java.util.ArrayList;

import br.com.condesales.listeners.ErrorListener;
import br.com.condesales.models.PhotoItem;
import br.com.condesales.models.PhotosGroup;

/**
 * Created by dionysis_lorentzos on 2/8/14.
 * All rights reserved by the Author.
 * Use with your own responsibility.
 */

public interface VenuePhotosListener extends ErrorListener {

    public void onGotVenuePhotos(PhotosGroup photosGroup);

}
