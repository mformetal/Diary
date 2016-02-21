package miles.diary.data.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import miles.diary.util.Logg;

public class AutoCompleteAdapter
        extends ArrayAdapter<AutoCompleteAdapter.AutoCompleteAdapterItem> implements Filterable {

    private ArrayList<AutoCompleteAdapterItem> mResultList;

    final GoogleApiClient mGoogleApiClient;

    private LatLngBounds mBounds;

    private Context mContext;

    public AutoCompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                               LatLngBounds bounds) {
        super(context, resource);
        mContext = context;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public AutoCompleteAdapterItem getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        results.values = mResultList;
                        results.count = mResultList.size();
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

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as PlaceAutocomplete
     * objects to store the Place ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
     */
    private ArrayList<AutoCompleteAdapterItem> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, null);

            AutocompletePredictionBuffer autocompletePredictions = results.await(15, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Logg.log("Error contacting API: " + status.toString());
                Logg.log("STATUS CODE: " + status.getStatusCode());
                autocompletePredictions.release();
                return null;
            }

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList<AutoCompleteAdapterItem> resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(
                        new AutoCompleteAdapterItem(prediction.getPlaceId(), prediction.getFullText(null)));
            }

            autocompletePredictions.release();

            return resultList;
        }
        return null;
    }

    public class AutoCompleteAdapterItem {

        public CharSequence placeId;
        public CharSequence description;

        AutoCompleteAdapterItem(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}
