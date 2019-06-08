package app.zerobugz.fcms.ims.timetable;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zerobugz.fcms.ims.R;

public class ThuFragment extends BaseFragment {
    private ProgressBar progressBar;
    private View parent_view;
    RecyclerView recyclerView;
    List<TimetableFeeds> feedsList = new ArrayList<TimetableFeeds>();
    MyTimetableAdapter adapter;
    TextView timetable_text;
    ImageView timetable_image;

    public ThuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_thu, container, false);
        parent_view = getActivity().findViewById(android.R.id.content);

        timetable_text = (TextView) v.findViewById(R.id.timetable_txt_thu);
        timetable_image = (ImageView) v.findViewById(R.id.timetable_icn_thu);

        timetable_text.setVisibility(View.INVISIBLE);
        timetable_image.setVisibility(View.INVISIBLE);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) v.findViewById(R.id.tuerecyclerview);
        adapter = new MyTimetableAdapter(getActivity(), feedsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        //progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_cyc);
        String day = "Thursday";
        loadtimetabledetails(recyclerView, feedsList, adapter, progressBar, parent_view, timetable_text, timetable_image, day);
        return v;
    }

}
