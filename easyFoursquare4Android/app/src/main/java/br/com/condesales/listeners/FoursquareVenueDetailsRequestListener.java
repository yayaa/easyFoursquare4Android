package br.com.condesales.listeners;

import br.com.condesales.models.Venue;

public interface FoursquareVenueDetailsRequestListener extends ErrorListener {

    public void onVenueDetailFetched(Venue venues);

}
