package me.thuanle.Astronomers;

import java.util.Random;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by thuanle on 4/12/14.
 */
public class PlanetFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static final String ARG_PLANET_NUMBER = "planet_number";
    private WebView wv;
    private SeekBar sb;
    private int id;
    private TextView tv;
    private ImageButton btnPlay;
    private boolean playing = false;
    private long sleepTime = 10;

    @Override
    public void onClick(View v) {
        if (v == btnPlay) {
            playing = !playing;
            if (playing) {
                RunningAsync runningAsync = new RunningAsync();
                runningAsync.execute();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    }

    public class RunningAsync extends AsyncTask<Void, Void, Void> {

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

    private long getSleepTime() {
        return sleepTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        id = getArguments().getInt(ARG_PLANET_NUMBER);
        String planet = getResources().getStringArray(R.array.map_array)[id];

        tv = (TextView) rootView.findViewById(R.id.textViewDateTime);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        sb = (SeekBar) rootView.findViewById(R.id.seekbar);
        sb.setOnSeekBarChangeListener(this);
        sb.setMax(1000 * id);
        sb.setProgress(new Random().nextInt(1000 * id));

        wv = (WebView) rootView.findViewById(R.id.webview);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("http://google.com");

        getActivity().setTitle(planet);

        return rootView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv.setText(lookup(progress));
    }

    private String lookup(int progress) {
        return String.valueOf(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
