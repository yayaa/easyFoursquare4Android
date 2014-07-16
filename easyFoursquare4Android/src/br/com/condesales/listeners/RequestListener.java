package br.com.condesales.listeners;

import br.com.condesales.models.FoursquareError;

/**
 * Cr8 Event - Compubits Solutions
 * #Class description
 * Created by Felipe Conde <felipe@compubits.com>
 * On 15/07/14.
 */
public interface RequestListener<T> {

    public void onSuccess(T response);

    public void onError(FoursquareError error);

}
