package me.thuanle.astronomers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import me.thuanle.astronomers.connector.ASTRequest;
import me.thuanle.astronomers.connector.ASTResponse;

/**
 * Created by thuanle on 4/12/14.
 */
public class PlanetFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public class SeekbarRunningAsync extends AsyncTask<Void, Void, Void> {

        private boolean running = true;

        @Override
        protected Void doInBackground(Void... params) {
            while (running) {
                try {
                    Thread.sleep(getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            int progress = sb.getProgress() + 1;
            progress = progress > sb.getMax() ? 0 : progress;
            sb.setProgress(progress);
            running = playing;
        }
    }
    public static final String ARG_PLANET_ID = "arg_planet_id";
    public static final String ARG_PLANET_LABEL = "arg_planet_label";
    public static final String ARG_PLANET_URL = "arg_planet_url";
    private static ArrayList<String> listDate;
    private WebView wv;
    private SeekBar sb;
    private TextView tv;
    private ImageButton btnPlay;
    private boolean playing = false;
    private long sleepTime = 50;
    private String id;
    private String label;
    private String url;

    private long getSleepTime() {
        return sleepTime;
    }

    private String lookup(int progress) {
        if (listDate != null && 0 <= progress && progress < listDate.size()) {
            return listDate.get(progress);
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnPlay) {
            playing = !playing;
            if (playing) {
                SeekbarRunningAsync runningAsync = new SeekbarRunningAsync();
                runningAsync.execute();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        id = getArguments().getString(ARG_PLANET_ID);
        label = getArguments().getString(ARG_PLANET_LABEL);
        url = getArguments().getString(ARG_PLANET_URL);
//        String planet = getResources().getStringArray(R.array.map_array)[id];

        tv = (TextView) rootView.findViewById(R.id.textViewDateTime);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        sb = (SeekBar) rootView.findViewById(R.id.seekbar);
        sb.setOnSeekBarChangeListener(this);
        sb.setMax(listDate.size());

        wv = (WebView) rootView.findViewById(R.id.webview);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(url);

        getActivity().setTitle(label);


        return rootView;
    }
    static {
        Calendar start = new GregorianCalendar(2012, 4, 8);
        Calendar end = new GregorianCalendar(2014, 3, 10);

        listDate = new ArrayList<String>();
        while (start.compareTo(end) == -1) {
            String s = (start.get(Calendar.DATE)+1) + "-" + (start.get(Calendar.MONTH)+1) + "-" + start.get(Calendar.YEAR);
            listDate.add(s);
            Log.i("thuanle", "import date = " + s);
            start.add(Calendar.DATE, 5);
        }
        String s = (start.get(Calendar.DATE)+1) + "-" + (start.get(Calendar.MONTH)+1) + "-" + start.get(Calendar.YEAR);
        listDate.add(s);
        Log.i("thuanle", "import date = " + s);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv.setText(lookup(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
