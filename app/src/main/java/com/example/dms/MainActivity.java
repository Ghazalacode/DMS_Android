package com.example.dms;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dms.Adapters.RepoListAdapter;
import com.example.dms.Models.Repo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.dms.Utils.Network.isConnectingToInternet;

public class MainActivity extends AppCompatActivity  implements
        RepoListAdapter.OnLoadMoreItemsListener, SwipeRefreshLayout.OnRefreshListener{

    @Override
    public void onLoadMoreItems() {


      String newUrl= url1 +String.valueOf(++page)+url2;

        Log.e("onLoadMoreItems: url1 ", url1);
        Log.e("onLoadMoreItems: url2 ", url2);

        try {
            getRepos(newUrl,true,forceNetwork, 0);
        } catch (IOException e) {

        }


    }
    @Override
    public void onRefresh() {
        try {
            if (!isConnectingToInternet(MainActivity.this)){
                Log.e("onRefresh: ", " called");
                Toast.makeText(MainActivity.this,"Please Check Your Internet Connection" ,Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }else {
                Log.e("onRefresh: ", " called");
               if (repos.size()>0)
               { repos.clear();
                mAdapter.notifyDataSetChanged();}
                forceNetwork= true;

                getRepos(baseurl,false,forceNetwork,1);
                swipeRefreshLayout.setRefreshing(false);
            }

        } catch (IOException e) {
            Log.e("onRefresh: ", e.getMessage());
        }
    }




    private ArrayList<Repo> mPaginatedRepos;

    private int mResults;

    String baseurl = "https://api.github.com/users/square/repos?page=1&per_page=10";
   String url1=      "https://api.github.com/users/square/repos?page=" ;
    String url2="&per_page=10";
    String stringResponse = "";
    String lastResponse = "";
    OkHttpClient client;
    ArrayList<Repo> repos = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private RepoListAdapter mAdapter;
    ListView mListView;
    Integer page= 1;
    Integer response= 0;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean forceNetwork= false;
    Cache cache;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

     setupListView();

        try {  int cacheSize = 10 * 1024 * 1024; // 10 MiB
            cache=new Cache(MainActivity.this.getCacheDir(), cacheSize);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new ForceCacheInterceptor());
                client = builder.build();

                    getRepos(baseurl,false,forceNetwork,0);
     }
   catch (IOException e) {
            Log.e("onCrea:cacheIOExcep", e.getMessage());
        }
    }





    public void setupListView(){

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
                ListView   dialogListView = (ListView) mView.findViewById(R.id.DialoglistView);

                String[] listItems = { repos.get(pos).getHtml_url(),repos.get(pos).getOwner_html_url()};
                dialogListView.setAdapter(new ArrayAdapter(MainActivity.this,  android.R.layout.simple_list_item_1, listItems));

                mBuilder.setView(mView);


                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            if (!isConnectingToInternet(MainActivity.this)){
                                Toast.makeText(MainActivity.this,"Please Check Your Internet Connection" ,Toast.LENGTH_LONG).show();
                            }else {
                                dialog.dismiss();
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(repos.get(pos).getHtml_url())));
                            }

                        }else {
                            if (!isConnectingToInternet(MainActivity.this)){
                                Toast.makeText(MainActivity.this,"Please Check Your Internet Connection" ,Toast.LENGTH_LONG).show();
                            }else {
                                dialog.dismiss();
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(repos.get(pos).getOwner_html_url())));
                            }

                        }
                    }
                });

                Log.v("long clicked","pos: " + repos.get(pos).getOwner_html_url());
                Log.v("long clicked","pos: " + repos.get(pos).getHtml_url());
                return true;
            }
        });



    }


    public void getRepos(final String url,final Boolean displayMore ,final Boolean forceNetwork ,final int evict) throws IOException {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
              //setup new url
                Request request;
                try { if (forceNetwork){
                    if (evict==1){
                        cache.evictAll();
                    }

                    request = new Request.Builder()
                            .url(url)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                            .build();

                }else {
               request = new Request.Builder()
                            .url(url)
                            .build();
                }
                    Response response = client.newCall(request).execute();
                   /* response.cacheResponse();*/////////////////////////
                    stringResponse= response.body().string();

                    System.out.println("Response 1 response:          " + stringResponse);
                    System.out.println("Response 1 cache response:    " + response.cacheResponse());
                    System.out.println("Response 1 network response:  " + response.networkResponse());
                    System.out.println("Response 1 network response:  " + response.headers().toString());
                    response.body().close();
                    Log.e("doInBack:stringResponse", stringResponse);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }  catch (IOException e) {
                    Log.d("IO", e.getMessage().toString());
                    e.printStackTrace();

                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    if (response==0){
                        lastResponse= stringResponse ;
                        response++;
                    }else {
                        if (stringResponse.equals(lastResponse)){

                            return;
                        }else {   lastResponse= stringResponse ;
                            response++;}
                    }


                    JSONArray Jarray = new JSONArray(lastResponse);

                    Log.e("onCreate: Jarray", Jarray.toString());


                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object     = Jarray.getJSONObject(i);

                      Repo repo =   new Repo();
                        repo.setName(object.getString("name"));
                        repo.setDescription(object.getString("description"));
                        repo.setHtml_url(object.getString("html_url"));
                        repo.setFork_state(Boolean.valueOf(object.getString("fork")));
                        repo.setOwner_html_url(object.getJSONObject("owner").getString("html_url"));
                        repo.setOwner_login(object.getJSONObject("owner").getString("login"));
                        repos.add(repo);
                                if (displayMore){
                                displayMoreRepos();
                                }else {
                                    displayRepos();
                                }



                    //    Log.e("onCreate: name", repo.toString());

                    }

                    Log.e("onPostExecute: ", String.valueOf(repos.size()) );
                } catch (JSONException e) {
                    Log.e("onCreate:JSONException ",e.getMessage());
                }
            }
        }.execute();



    }


    private void displayRepos(){
        mPaginatedRepos = new ArrayList<>();
        if(repos != null){
            try{


                int iterations = repos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedRepos.add(repos.get(i));
                }

                mAdapter = new RepoListAdapter(MainActivity.this, R.layout.item_repo, mPaginatedRepos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayRepos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayRepos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMoreRepos(){
        Log.d(TAG, "displayMoreRepos: displaying more Repos");

        try{

            if(repos.size() > mResults && repos.size() > 0){

                int iterations;
                if(repos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMoreRepos: there are greater than 10 more Repos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMoreRepos: there is less than 10 more Repos");
                    iterations = repos.size() - mResults;
                }


                //add the new repos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedRepos.add(repos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayRepos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayRepos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    public class ForceCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            Response response = chain.proceed(chain.request());

            if (!isConnectingToInternet(MainActivity.this)) {

                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            if (response.cacheResponse() != null) {
                // from cache
                Log.e("intercept: ","from cache" );
            } else if (response.networkResponse() != null) {
                // from network
                Log.e("intercept: ","from network" );
            }

            return response;
        }
    }

}

