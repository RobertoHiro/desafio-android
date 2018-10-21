package com.company.hiro.desafioandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.company.hiro.desafioandroid.BaseAdapter.DetailAdapter;
import com.company.hiro.desafioandroid.Class.PullRepositoryClass;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView lv_Detalis;
    DetailAdapter detailAdapter;
    List<PullRepositoryClass> pullRepositoryClasses;
    ProgressDialog progressDialog;
    JSONArray root;
    String pullUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        lv_Detalis = v.findViewById(R.id.details_lv);
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        detailAdapter = new DetailAdapter(getActivity(), pullRepositoryClasses);

        pullRepositoryClasses = new ArrayList<PullRepositoryClass>();

        pullUrl = GlobalVariable.pullRepoList +
                GlobalVariable.repositoryList.get(GlobalVariable.repository_index).nameAuthor +
                "/" +
                GlobalVariable.repositoryList.get(GlobalVariable.repository_index).nameRepository +
                "/pulls";
        Log.i("urlPulls", pullUrl);

        new DetailsFragment.Task().execute(pullUrl);
        if (GlobalVariable.orientation == Configuration.ORIENTATION_PORTRAIT)
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GlobalVariable.repository_index = position;
        try {
            String url = pullRepositoryClasses.get(position).link;
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }catch (Exception e){
            Log.e("Error transation", e.toString());
        }
    }

    protected class Task extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Carregando...");
            progressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... urlParam)
        {
            Log.i("urlPullRepository", urlParam[0]);
            try{
                return new JSONArray(GlobalVariable.getUrlContents(urlParam[0]));
            }
            catch(Exception e)
            {
                Log.e("Error Site", "Erro ao acessar o site", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray response)
        {
            Log.i("Json request", (response.length() != 0) + "");
            if(response.length() != 0)
            {
                root = response;
                SetRepositoryList(root);
            }else{
                Log.e("Error Response", "Response Vazia");
            }
            progressDialog.cancel();
        }
    }
    public void SetRepositoryList(JSONArray jsonArray){
        for(int i = 0; i < jsonArray.length(); i++){
            PullRepositoryClass pullRepositoryClass = new PullRepositoryClass();
            try {
                pullRepositoryClass.index = jsonArray.getJSONObject(i).getInt("id");
                pullRepositoryClass.name = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
                pullRepositoryClass.linkPhoto = jsonArray.getJSONObject(i).getJSONObject("user").getString("avatar_url");
//                pullRepositoryClass.date = jsonArray.getJSONObject(i).getJSONObject("owner").getString("login");
                pullRepositoryClass.title = jsonArray.getJSONObject(i).getString("title");
                pullRepositoryClass.body = jsonArray.getJSONObject(i).getString("body");
                pullRepositoryClass.link = jsonArray.getJSONObject(i).getString("html_url");
            }
            catch (Exception e){
                Log.e("Error ItemJson", "Error ao ler o item:" + i + " descrição do erro:" + e.toString());
            }
            pullRepositoryClasses.add(pullRepositoryClass);
        }

        detailAdapter = new DetailAdapter(getActivity(), pullRepositoryClasses);
        lv_Detalis.setAdapter(detailAdapter);
        lv_Detalis.setOnItemClickListener(this);

    }

}
