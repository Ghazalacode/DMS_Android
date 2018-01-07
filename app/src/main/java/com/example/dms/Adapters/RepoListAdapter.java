package com.example.dms.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dms.Models.Repo;
import com.example.dms.R;

import java.util.List;

public class RepoListAdapter extends ArrayAdapter<Repo> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;



        private static final String TAG = "RepoListAdapter";


        private LayoutInflater mInflater;
        private List<Repo> mRepos = null;
        private int layoutResource;
        private Context mContext;

        public RepoListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Repo> objects) {
            super(context, resource, objects);
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutResource = resource;
            this.mRepos = objects;
        }

        private static class ViewHolder{
            TextView repoName , description ,owner ;
            CardView repoCard ;

        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            final ViewHolder holder;
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.repoName = (TextView) convertView.findViewById(R.id.repo_name);
            holder.description = (TextView) convertView.findViewById(R.id.Description);
            holder.owner = (TextView) convertView.findViewById(R.id.owner);
            holder.repoCard = (CardView) convertView.findViewById(R.id.repoCard);


            holder.repoName.setText(getItem(position).getName());
            holder.description.setText(getItem(position).getDescription());
            holder.owner.setText(getItem(position).getOwner_login());
            if (  ! getItem(position).isFork_state() ){
            holder.repoCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGreen));
            }

            if(reachedEndOfList(position)){
                loadMoreData();

            return convertView;
        }



        return convertView;
        }


    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: NullPointerException: " +e.getMessage() );
        }
    }
    }
