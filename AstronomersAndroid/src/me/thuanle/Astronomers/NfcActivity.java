package me.thuanle.astronomers;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by thuanle on 4/12/14.
 */
public class NfcActivity extends Activity {

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                onNfcReceive(result);
            }
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
    }

    private void onNfcReceive(String result) {
        Toast.makeText(getApplicationContext(), "NFC: " + result, Toast.LENGTH_LONG).show();

        try {
            Intent intent = this.getIntent();
            intent.setClass(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            MainActivity.index = Integer.valueOf(result);
            startActivity(intent);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private static final String NFC_MIME = "text/plain";
    private static final String TAG = MainActivity.class.getName();
    private NfcAdapter mNfcAdapter;

    /**
     * Handle intent and NFC intent
     *
     * @param intent
     */
    private void handleIntend(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (NFC_MIME.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is turn off or disabled", Toast.LENGTH_LONG).show();
        }

        handleIntend(getIntent());

    }
}
