package miles.diary.data.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import miles.diary.data.model.google.AutoCompleteItem;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 3/2/16.
 */
public class AutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private List<AutocompletePrediction> data;
    private final GoogleApiClient client;
    private LatLngBounds bounds;
    private AutocompleteFilter filter;

    public AutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                               LatLngBounds latLngBounds, AutocompleteFilter autocompleteFilter) {
        super(context, resource);
        client = googleApiClient;
        bounds = latLngBounds;
        filter = autocompleteFilter;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return data.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    data = getAutocomplete(constraint);
                    if (data != null) {
                        results.values = data;
                        results.count = data.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        PendingResult<AutocompletePredictionBuffer> results =
                Places.GeoDataApi
                        .getAutocompletePredictions(client, constraint.toString(), bounds, null);

        AutocompletePredictionBuffer autocompletePredictions = results.await(10, TimeUnit.SECONDS);

        final Status status = autocompletePredictions.getStatus();
        if (!status.isSuccess()) {
            Logg.log("Error contacting API: " + status.toString());
            Logg.log("STATUS CODE: " + status.getStatusCode());
            autocompletePredictions.release();
            return null;
        }

        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
        List<AutocompletePrediction> resultList = new ArrayList<>(autocompletePredictions.getCount());
        while (iterator.hasNext()) {
            AutocompletePrediction prediction = iterator.next();
            prediction.freeze();
            resultList.add(prediction);
        }

        autocompletePredictions.release();

        return resultList;
    }
}
