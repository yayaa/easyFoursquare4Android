package br.com.condesales.listeners;

import java.util.ArrayList;

import br.com.condesales.models.Venue;

public interface FoursquareTrendingVenuesRequestListener extends ErrorListener {

    public void onTrendedVenuesFetched(ArrayList<Venue> venues);

}
