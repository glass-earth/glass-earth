//package me.thuanle.astronomers;
//
//import java.util.LinkedList;
//import java.util.Queue;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import me.thuanle.astronomers.connector.ASTConnector;
//import me.thuanle.astronomers.connector.ASTRequest;
//import me.thuanle.astronomers.connector.ASTResponse;
//
//public class AsyncPost extends AsyncTask<ASTRequest, AsyncProtocolPair, Void> {
//
//
//    private static final String TAG = "thuanle";
//
//    private final IAsyncPostCallback callback;
//        private final Queue<ASTRequest> queue;
//
//        private AsyncPost(IAsyncPostCallback callback) {
//            this.callback = callback;
//            queue = new LinkedList<ASTRequest>();
//        }
//
//        @Override
//        synchronized protected Void doInBackground(ASTRequest... requests) {
//
//            ASTConnector con = ASTConnector.getDefaultConnector();
//            while (!con.isInitialized()) {
//                try {
//                    Thread.sleep(ASTConnector.SLEEP_TIMEOUT);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            for (int i = 0; i < requests.length; i++) {
//                queue.add(requests[i]);
//            }
//            while (!queue.isEmpty()) {
//                ASTRequest req = queue.poll();
//                ASTResponse res = ASTConnector.getDefaultConnector().post(req);
//                publishProgress(new AsyncProtocolPair(req, res));
//                try {
//                    Thread.sleep(ASTConnector.SLEEP_TIMEOUT);
//                } catch (InterruptedException e) {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(AsyncProtocolPair... values) {
//            boolean result = callback.onReceiveResponse(values[0].request, values[0].response);
//            if (!result) {
//                queue.add(values[0].request);
//            }
//        }
//    }
