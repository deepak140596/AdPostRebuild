package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sapicons.deepak.k2psap.Objects.CategoryItem;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 08-10-2018.
 */

public class CategoryAdapter extends ArrayAdapter<CategoryItem> {


    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<CategoryItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView ==null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_single_category, parent, false);

        String categoryName = getItem(position).getName();

        TextView categoryNameTv = convertView.findViewById(R.id.item_single_category_tv);

        categoryNameTv.setText(categoryName);

        return convertView;
    }
}
