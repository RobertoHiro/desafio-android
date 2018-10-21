package com.company.hiro.desafioandroid;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.company.hiro.desafioandroid.Class.RepositoryClass;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class GlobalVariable {
    public static int repository_index = 0;
    public static int repository_page = 1;
    public static int repository_count_on_Page = 0;
    public static List<RepositoryClass> repositoryList;
    public static String pullRepoList = "https://api.github.com/repos/";
    public static ImageLoaderConfiguration imageLoaderConfig;
    public static int orientation;


    public static String getUrlContents(String url) {

        StringBuilder content = new StringBuilder();

        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(OpenHttpConnection(url)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }

            bufferedReader.close();
        } catch(Exception e) {
            Log.e("Error: getUrlContents", e.toString());
        }

        return content.toString();
    }
    private static InputStream OpenHttpConnection(String strURL) throws IOException{
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try{
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        }
        catch (Exception ex){
        }

        return inputStream;
    }
    public static void replaceFragment(FragmentActivity fragmentActivity, int resourceID, Fragment frag, String tag, boolean doAddToBackStack) {
        if (doAddToBackStack)
            fragmentActivity.getSupportFragmentManager().beginTransaction().replace(resourceID,frag, null).addToBackStack(null).commit();
        else
            fragmentActivity.getSupportFragmentManager().beginTransaction().replace(resourceID,frag, null).commit();
    }
}
