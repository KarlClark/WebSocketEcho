package com.clarkgarrett.websocketecho;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.ProgressBar;
import com.clarkgarrett.websocketecho.MessagesViewModel.EventType;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessagesViewModel viewModel;
    private RvAdapter rvAdapter;
    private ProgressBar pbWaiting;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbWaiting = findViewById(R.id.pbWaitng);

        etMessage = findViewById(R.id.etMessage);
        etMessage.setText("");
        etMessage.setOnEditorActionListener((textView, actionId, event) -> {
            viewModel.send(etMessage.getText().toString());
            etMessage.setText("");
            return true;
        });

        viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        checkOpen();
        viewModel.getEventTypeMutableLiveData().observe(this, eventType -> processEvent(eventType));

        rvAdapter = new RvAdapter(viewModel.getData());
        recyclerView = findViewById(R.id.rvEchoed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> recyclerView.scrollToPosition(rvAdapter.getItemCount() - 1));
        recyclerView.setAdapter(rvAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.openSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( ! isChangingConfigurations()){
            viewModel.close();
        }
    }

    private void processEvent(EventType eventType){
        switch (eventType){
            case OPENED:
                checkOpen();
                break;
            case MESSAGE:
                updateRecyclerView();
                break;
            case ERROR:
                displayDialog();
                break;
        }
    }

    private void checkOpen(){
        if (viewModel.isOpened()) {
            etMessage.setEnabled(true);
            pbWaiting.setVisibility(View.GONE);
        } else {
            etMessage.setEnabled(false);
            pbWaiting.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecyclerView() {
        rvAdapter.notifyItemInserted(viewModel.getData().size() - 1);
        recyclerView.scrollToPosition(rvAdapter.getItemCount() -1);
    }

    private void displayDialog () {

        String msg = "Socket Error:\n" + viewModel.getError() + "\n\nTry Again?";
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) ->{
                    checkOpen();
                    viewModel.openSocket();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> finish())
                .show();
    }
}
