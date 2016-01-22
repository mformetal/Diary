package miles.diary.data.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.ui.widget.TypefaceTextView;

/**
 * Created by mbpeele on 1/22/16.
 */
public class EntryPagerAdapter extends PagerAdapter {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ArrayList<Entry> mDataList;

    public EntryPagerAdapter(Activity activity) {
        super();
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mDataList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ViewGroup viewGroup =
                (ViewGroup) mInflater.inflate(R.layout.adapter_entry, collection, false);
//        collection.addView(viewGroup);
//
//        Sketch sketch = mDataList.get(position);
//
//        AspectRatioImageView imageView =
//                (AspectRatioImageView) viewGroup.findViewById(R.id.adapter_gallery_image);
//        TypefaceTextView textView =
//                (TypefaceTextView) viewGroup.findViewById(R.id.adapter_gallery_title);
//
//        textView.setText(sketch.getTitle());
//
//        Glide.with(mActivity)
//                .fromBytes()
//                .asBitmap()
//                .animate(android.R.anim.fade_in)
//                .load(sketch.getBytes())
//                .into(imageView);

        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
