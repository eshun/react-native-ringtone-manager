package com.xmcares.dataone.bss.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RNRingtoneManagerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String TYPE_ALARM_KEY = "TYPE_ALARM";
    private static final String TYPE_ALL_KEY = "TYPE_ALL";
    private static final String TYPE_NOTIFICATION_KEY = "TYPE_NOTIFICATION";
    private static final String TYPE_RINGTONE_KEY = "TYPE_RINGTONE";

    final static class SettingsKeys {
        public static final String URI = "uri";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String SIZE = "size";
        public static final String MIME_TYPE = "mimeType";
        public static final String DURATION = "duration";
        public static final String RINGTONE_TYPE = "ringtoneType";
    }

    public RNRingtoneManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RingtoneManager";
    }

    @ReactMethod
    public void getRingtones() {
        getRingtones(RingtoneManager.TYPE_ALL);
    }

    @ReactMethod
    public void getRingtones(int ringtoneType) {

    }

    @ReactMethod
    public void play() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this.reactContext, notification);
        r.play();
    }

    @ReactMethod
    public void createRingtone(ReadableMap settings) {
        String uriStr = settings.getString(SettingsKeys.URI);
        File ringtone = new File(uriStr);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, ringtone.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, settings.getString(SettingsKeys.TITLE));
        values.put(MediaStore.MediaColumns.SIZE, settings.getInt(SettingsKeys.SIZE));
        values.put(MediaStore.MediaColumns.MIME_TYPE, settings.getString(SettingsKeys.MIME_TYPE));
        values.put(MediaStore.Audio.Media.ARTIST, settings.getString(SettingsKeys.ARTIST));
        values.put(MediaStore.Audio.Media.DURATION, settings.getInt(SettingsKeys.DURATION));
        int ringtoneType = settings.getInt(SettingsKeys.RINGTONE_TYPE);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, isRingtoneType(ringtoneType, RingtoneManager.TYPE_RINGTONE));
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, isRingtoneType(ringtoneType, RingtoneManager.TYPE_NOTIFICATION));
        values.put(MediaStore.Audio.Media.IS_ALARM, isRingtoneType(ringtoneType, RingtoneManager.TYPE_ALARM));
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        if (ringtone.exists() && getCurrentActivity() != null) {
            ContentResolver contentResolver = getCurrentActivity().getContentResolver();
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtone.getAbsolutePath());
            contentResolver.insert(uri, values);
        }
    }

    @ReactMethod
    public void setRingtone(String uri) {

    }

    @ReactMethod
    public void pickRingtone() {

    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(TYPE_ALARM_KEY, RingtoneManager.TYPE_ALARM);
        constants.put(TYPE_ALL_KEY, RingtoneManager.TYPE_ALL);
        constants.put(TYPE_NOTIFICATION_KEY, RingtoneManager.TYPE_NOTIFICATION);
        constants.put(TYPE_RINGTONE_KEY, RingtoneManager.TYPE_RINGTONE);
        return constants;
    }

    /**
     * Returns true when the given ringtone type matches the ringtone to compare.
     * Will default to true if the given ringtone type is RingtoneManager.TYPE_ALL.
     * @param ringtoneType ringtone type given
     * @param ringtoneTypeToCompare ringtone type to compare to
     * @return true if the type matches or is TYPE_ALL
     */
    private boolean isRingtoneType(int ringtoneType, int ringtoneTypeToCompare) {
        return ringtoneTypeToCompare == ringtoneType || RingtoneManager.TYPE_ALL == ringtoneType;
    }
}
