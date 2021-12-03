package ie.ul.mobileappdevproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static CountDownTimer countDownTimer = null;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button startBtn;
    private Button resetBtn;
    private EditText editTextMins;
    private EditText editTextSecs;


    private long START_TIME_IN_MILLIS;
    private CountDownTimer cdt;
    private boolean running;
    private long TLiM;
    private boolean initialTap = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        startBtn = (Button) view.findViewById(R.id.startBtn);
        resetBtn = (Button) view.findViewById(R.id.resetBtn);
        editTextMins = (EditText) view.findViewById(R.id.editTextMins);
        editTextSecs = (EditText) view.findViewById(R.id.editTextSecs);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(running){
                    pauseTimer();
                }
                else {
                    startTimer();
                }
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        updateCDTxt();

        return view;

    }

    private void startTimer() {
        if(!initialTap){
            System.out.println("weienker");
            changeToMillis();
            TLiM = START_TIME_IN_MILLIS;
            initialTap = true;
        }
        System.out.println(TLiM);
        cdt = new CountDownTimer(TLiM,1000) {
            @Override
            public void onTick(long MUF) {
                TLiM = MUF;
                System.out.println("STEP2");

                updateCDTxt();
                System.out.println("STEP2");

            }

            @Override
            public void onFinish() {
                running =false;
                startBtn.setText("Start");
                startBtn.setVisibility(View.INVISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
            }
        }.start();
        running= true;
        startBtn.setText("Pause");
        resetBtn.setVisibility(View.INVISIBLE);
    }
    private void pauseTimer() {
        cdt.cancel();
        running = false;
        startBtn.setText("Start");
        resetBtn.setVisibility(View.VISIBLE);
    }
    private void resetTimer() {
        TLiM = START_TIME_IN_MILLIS;
        initialTap = false;

        updateCDTxt();
        resetBtn.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.VISIBLE);
    }

    private void changeToMillis(){
        String strMins = editTextMins.getText().toString();
        String strSecs = editTextSecs.getText().toString();
        int mins = Integer.parseInt(strMins);
        int secs = Integer.parseInt(strSecs);
        if((mins > 59 || secs > 59) || (mins == 0 && secs == 0)){
            Toast.makeText(getActivity(), "Invalid time given", Toast.LENGTH_SHORT).show();
            START_TIME_IN_MILLIS = 1;
            return;
        }
        START_TIME_IN_MILLIS = mins * 60000 + secs * 1000;
    }

    private void updateCDTxt(){
        int mins = (int) (TLiM /1000) / 60;
        int secs = (int) (TLiM /1000) % 60;
        String minsLeftFormatted = String.format(Locale.getDefault(), "%02d", mins);
        String secsLeftFormatted = String.format(Locale.getDefault(), "%02d", secs);
        editTextMins.setText(minsLeftFormatted);
        editTextSecs.setText(secsLeftFormatted);
    }



}
