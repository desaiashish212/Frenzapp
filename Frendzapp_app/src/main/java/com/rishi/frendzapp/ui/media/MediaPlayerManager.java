package com.rishi.frendzapp.ui.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RawRes;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp_core.utils.ErrorUtils;
import com.rishi.frendzapp_core.utils.PrefsHelper;

public class MediaPlayerManager {

    public static final String DEFAULT_RINGTONE = "defaultRingtone.ogg";

    private static final int INVALID_SOURCE = R.string.error_playing_ringtone;

    private MediaPlayer player;
    private Context context;

    private boolean isPlaying;
    private int originalVolume;
    private Vibrator vibrator;
    private VibratePattern task;
    private MyTaskParams params;
    private boolean isvaibrate = true;

    public MediaPlayerManager(Context context) {
        this.context = context;
    }

    public void playSound(String resource, boolean looping,boolean isvibrate) {
        this.isvaibrate = isvibrate;
        AssetsSoundResource assetsSoundResource = new AssetsSoundResource(resource, context);
        playResource(assetsSoundResource, true, true);
    }

    public void playDefaultRingTone() {
        UriSoundResource uriSoundResource = new UriSoundResource(Settings.System.DEFAULT_RINGTONE_URI,
                context);
        playResource(uriSoundResource, true, true);
    }
    private boolean getVibrate() {
        return !PrefsHelper.getPrefsHelper().getPref(PrefsHelper.PREF_NOTIFICATION_VIBRATE, false);
    }
    public void setMaxVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC), 0);
    }

    public void returnOriginalVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    public void stopPlaying() {
        if (isPlaying) {
            if (player != null && player.isPlaying()) {
                player.stop();
                if (!getVibrate()&& isvaibrate) {
                    task.cancel(true);
                    task = null;
                }
            }
            shutDown();
        }
        isPlaying = false;
    }

    private void shutDown() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void playDefault() {
        AssetsSoundResource assetsSoundResource = new AssetsSoundResource(DEFAULT_RINGTONE, context);

        playResource(assetsSoundResource, true, false);
    }

    private void playResource(SoundResource resource, boolean looping, boolean catchException) {
        int errorId = 0;
        stopPlaying();
        player = new MediaPlayer();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        try {
            player.setLooping(looping);
            resource.putResourceInPlayer(player);
            player.prepare();
            player.start();
            isPlaying = true;
            if (!getVibrate() && isvaibrate) {
                if (task == null) {
                    startTask();
                }
            }
        } catch (Exception e) {
            errorId = INVALID_SOURCE;
            ErrorUtils.logError(e);
        }
        if (errorId != 0) {
            if (!catchException) {
                ErrorUtils.showError(context, context.getString(errorId));
            } else {
                playDefault();
            }
        }
    }

    private static class MyTaskParams {
        int dot, dash, gap;

        MyTaskParams (int dot, int dash, int gap) {
            this.dot = dot;
            this.dash = dash;
            this.gap = gap;
        }
    }

    private void startTask() {
        params = new MyTaskParams(200,500,200);
        task = new VibratePattern();
        task.execute(params);
    }

    public Integer onVibrate (Integer dot, Integer dash, Integer gap) {
        long[] pattern = {
                0,
                dot, gap, dash, gap, dot, gap, dot
        };

        vibrator.vibrate(pattern, -1);
        int span = dot + gap + dash + gap + dot + gap + dot + gap;
        return span;
    }

    private class VibratePattern extends AsyncTask<MyTaskParams, Void, Integer> {

        @Override
        protected Integer doInBackground(MyTaskParams... params) {
            int span;
            span = onVibrate(params[0].dot,params[0].dash,params[0].gap);
            return span;
        }

        @Override
        protected void onPostExecute(Integer span) {
            final android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isCancelled()) {
                        startTask();
                    }
                }
            }, span);
        }
    }
}
