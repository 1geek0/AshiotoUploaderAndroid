package xyz.ashioto.ashioto;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by geek on 22/9/16.
 * RecyclerAdapter for bluetooth list. Handles addition and removal of devices
 */

class RecyclerAdapter_bluetooth extends RecyclerView.Adapter<RecyclerAdapter_bluetooth.ViewHolder> {
    private ArrayList<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private String mItem;
        AppCompatTextView mTextView;

        ViewHolder(AppCompatTextView v) {
            super(v);
            mTextView = v;
        }

        void setItem(String item) {
            mItem = item;
            mTextView.setText(item);
        }

        @Override
        public void onClick(View view) {
            Log.d("bt_name", "onClick " + getAdapterPosition() + " " + mItem);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RecyclerAdapter_bluetooth(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter_bluetooth.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_list_textview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder((AppCompatTextView) v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));
        holder.setItem(mDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

