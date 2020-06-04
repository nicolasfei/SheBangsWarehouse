package com.shebangs.warehouse.ui.shipment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.shebangs.warehouse.R;

public class StatisticsUnShipmentFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics_unshipment, container, false);
        this.recyclerView = root.findViewById(R.id.recyclerView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
