package com.company.hiro.desafioandroid;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.company.hiro.desafioandroid.BaseAdapter.RepositoryAdapter;
import com.company.hiro.desafioandroid.Class.RepositoryClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RepositoryListFragment extends Fragment implements AdapterView.OnItemClickListener{

    Context context;
    ListView lv_repositorys;
    JSONObject root;
    JSONArray jsonRepositoryList;
    RepositoryAdapter repoAdapter;
    public ProgressDialog progressDialog;
    View v;
    int tentativasRestantes = 3;

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_repository_list, container, false);
        context = container.getContext();
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        lv_repositorys = v.findViewById(R.id.repository_list_lv);
        repoAdapter = new RepositoryAdapter(GlobalVariable.repositoryList, context);
        if(GlobalVariable.repositoryList == null){
            GlobalVariable.repositoryList = new ArrayList<RepositoryClass>();
            new Task().execute("https://api.github.com/search/repositories?q=language:Java&sort=stars&page=" + GlobalVariable.repository_page);
        }else{
            lv_repositorys.setAdapter(repoAdapter);
            lv_repositorys.setOnItemClickListener(this);
        }
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GlobalVariable.repository_index = position;
        try {
            SetUrlAndReplaceFragment(position);
        }catch (Exception e){
            Log.e("Error transation", e.toString());
        }
    }

    protected class Task extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Carregando...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urlParam)
        {
            Log.i("urlRepository", urlParam[0]);
            try{
                return new JSONObject(GlobalVariable.getUrlContents(urlParam[0]));
            }
            catch(Exception e)
            {
                Log.e("Error Site", "Erro ao acessar o site", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            Log.i("Json request", (response.length() != 0) + "");
            if(response.length() != 0)
            {
                try {
                    ///O objeto Json em si
                    Log.e("App", "Success: " + response.getString("total_count") );
                    root = response;
                    jsonRepositoryList = root.getJSONArray("items");
                    SetRepositoryList(jsonRepositoryList);
                } catch (JSONException ex) {
                    Log.e("Json Error", "Erro ao ler Json", ex);
                }
            }else{
                Log.e("Error Response", "Response Vazia");
            }
            progressDialog.cancel();
        }
    }
    public void SetRepositoryList(JSONArray jsonArray){
        GlobalVariable.repository_count_on_Page += jsonArray.length();
        for(int i = 0; i < jsonArray.length(); i++){
            RepositoryClass repoClass = new RepositoryClass();
            try {
                repoClass.index = jsonArray.getJSONObject(i).getInt("id");
                repoClass.nameRepository = jsonArray.getJSONObject(i).getString("name");
                repoClass.description = jsonArray.getJSONObject(i).getString("description");
                repoClass.nameAuthor = jsonArray.getJSONObject(i)
                        .getJSONObject("owner").getString("login");
                repoClass.linkPhoto = jsonArray.getJSONObject(i)
                        .getJSONObject("owner").getString("avatar_url");
                repoClass.starNumber = jsonArray.getJSONObject(i).getInt("stargazers_count");
                repoClass.forkNumber = jsonArray.getJSONObject(i).getInt("forks_count");
            }
            catch (Exception e){
                Log.e("Error ItemJson", "Error ao ler o item:" + i + " descrição do erro:" + e.toString());
            }
            GlobalVariable.repositoryList.add(repoClass);
        }

        if(GlobalVariable.repository_page == 1){
            repoAdapter = new RepositoryAdapter(GlobalVariable.repositoryList, context);
            lv_repositorys.setAdapter(repoAdapter);
            lv_repositorys.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if(!progressDialog.isShowing() && firstVisibleItem > (GlobalVariable.repository_count_on_Page - 10)){
                        GlobalVariable.repository_page ++;
                        String url = "https://api.github.com/search/repositories?q=language:Java&sort=stars&page=" + GlobalVariable.repository_page;
                        Log.i("Scroll", url);
                        new Task().execute(url);
                    }
                }
            });
        }else{
            repoAdapter.notifyDataSetChanged();
        }
        lv_repositorys.setOnItemClickListener(this);

        GlobalVariable.orientation = getResources().getConfiguration().orientation;
        if (GlobalVariable.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SetUrlAndReplaceFragmentLandscape();
        }
    }

    public void SetUrlAndReplaceFragment(int position){

        Log.i("isLandScape", (GlobalVariable.orientation == Configuration.ORIENTATION_LANDSCAPE) + "");
        if(GlobalVariable.orientation == Configuration.ORIENTATION_PORTRAIT)
            GlobalVariable.replaceFragment(getActivity(), R.id.main_content, new DetailsFragment(), null, true);
        else
            GlobalVariable.replaceFragment(getActivity(), R.id.main_detail, new DetailsFragment(), null, false);
    }

    public void waitOneSecondToLoadConfigurations(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                SetUrlAndReplaceFragmentLandscape();
            }
        }, 1000);
    }

    public void SetUrlAndReplaceFragmentLandscape(){
        if(progressDialog.isShowing()){
            waitOneSecondToLoadConfigurations();
        }else{
            ///Time to ActivityGet FragmentManager

            try {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_detail, new DetailsFragment(), null);
                fragmentTransaction.replace(R.id.main_content, new RepositoryListFragment(), null);
                fragmentTransaction.commit();
                tentativasRestantes = 3;
            }catch (NullPointerException ne){
                tentativasRestantes --;
                if(tentativasRestantes > 0)
                    waitOneSecondToLoadConfigurations();
            }
        }
    }
}

