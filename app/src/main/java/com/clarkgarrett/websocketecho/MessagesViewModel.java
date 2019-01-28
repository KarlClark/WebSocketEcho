package com.clarkgarrett.websocketecho;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import static com.clarkgarrett.websocketecho.MessagesViewModel.EventType.*;

public class MessagesViewModel extends AndroidViewModel {

    public enum EventType {OPENED, ERROR, MESSAGE}
    private final MutableLiveData<EventType> eventTypeMutableLiveData = new MutableLiveData<>();
    private List<String> data = new ArrayList<>();
    private String error = "";
    private boolean opened = false;
    private boolean openSocketCalled = false;
    private WebSocket webSocket;
    private Handler mainHandler;
    private static final int NORMAL_CLOSURE = 1000;
    private static final String URL = "ws://echo.websocket.org";

    private final class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            opened = true;
            mainHandler.post(() -> eventTypeMutableLiveData.setValue(OPENED));
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {

            data.add(text);
            mainHandler.post(() -> eventTypeMutableLiveData.setValue(MESSAGE));
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            opened = false;
            openSocketCalled = false;
            error = t.getMessage();
            mainHandler.post(() -> eventTypeMutableLiveData.setValue(ERROR));
        }
    }

    public MessagesViewModel (Application application){

        super(application);

        mainHandler = new Handler(Looper.getMainLooper());
        openSocket();
    }

    void openSocket(){
        if ( ! openSocketCalled) {
            openSocketCalled = true;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();
            webSocket = client.newWebSocket(request, new SocketListener());
        }
    }

    void send(String message){
        webSocket.send(message);
    }

    MutableLiveData<EventType> getEventTypeMutableLiveData() {
        return eventTypeMutableLiveData;
    }

    List<String> getData() {
        return data;
    }

    String getError() {
        return error;
    }

    boolean isOpened() {
        return opened;
    }

    void close(){
        webSocket.close(NORMAL_CLOSURE, null);
        opened = false;
        openSocketCalled = false;
    }
}
