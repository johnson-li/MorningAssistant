package com.johnson.gettingUpState;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;

import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechRecognizer;
import com.johnson.Log;
import com.johnson.utils.Preferences;
/**
 * Created by johnson on 9/23/14.
 * Judging getting up state by voice info
 */
public class RecordMonitor extends Monitor{
    RecognizerListener recognizerListener = new RecognizerListener.Stub() {
        @Override
        public void onVolumeChanged(int i) throws RemoteException {

        }

        @Override
        public void onBeginOfSpeech() throws RemoteException {

        }

        @Override
        public void onEndOfSpeech() throws RemoteException {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) throws RemoteException {
            String text = recognizerResult.getResultString();
            Log.d(text);
            for (String keyWord: Preferences.getKeyWords()) {
                if (text.contains(keyWord)) {
                    sendGettingUp(true);
                }
            }
            /*
            *   start a new speech recognition immediately
            * */
            startRecognize();
        }

        @Override
        public void onError(int i) throws RemoteException {

        }
    };

    public RecordMonitor(Handler handler, Context mContext) {
        super(handler, mContext);
    }

    /*
    *   Starting voice engine periodically until key words appear
    * */
    @Override
    void startMonitor() throws InterruptedException {
        if (!Preferences.useVoiceEngine()) {
            throw new InterruptedException();
        }
        startRecognize();
    }

    void startRecognize() {
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(mContext, null);
        speechRecognizer.startListening(recognizerListener);
        try {
            Thread.sleep(20 * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getClassName() {
        return RecordMonitor.class.getSimpleName();
    }
}
