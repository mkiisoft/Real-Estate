package com.renderas.soldty;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.renderas.soldty.adapter.ListAdapterSave;
import com.renderas.soldty.adapter.PropertyListAdapter;
import com.renderas.soldty.sql.ContentDAO;


public class Fragment_Favorites extends Fragment {


    private ContentDAO dao;
    private ListAdapterSave adapter;
    private ListView mListSave;
    private RelativeLayout whenEmpty;

    public Fragment_Favorites() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        dao = new ContentDAO(getActivity());

        whenEmpty = (RelativeLayout) rootView.findViewById(R.id.when_empty);

        if(dao.getFavorNot().size() == 0){
            whenEmpty.setVisibility(View.VISIBLE);
        }else{
            whenEmpty.setVisibility(View.GONE);
        }

        mListSave = (ListView) rootView.findViewById(R.id.list_save);
        adapter = new ListAdapterSave(getActivity(), dao.getFavorNot());
        mListSave.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ListAdapterSave(getActivity(), dao.getFavorNot());
        mListSave.setAdapter(adapter);

        if(dao.getFavorNot().size() == 0){
            whenEmpty.setVisibility(View.VISIBLE);
        }else{
            whenEmpty.setVisibility(View.GONE);
        }
    }
}
