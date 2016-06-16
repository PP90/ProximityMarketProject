package com.example.p3.myapp.ActivitiesPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p3.myapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class SeeNearAds extends AppCompatActivity {

    static final String TAG="SeeNearAds";
    int numberOfAds;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /*** The {@link ViewPager} that will host the section contents.     */
    private ViewPager mViewPager;
    private ArrayList<String> adListRaw;
    static final int N_PARAMS_AD=8;
    private  ArrayList<ArrayList<String>> adListParsed;

    private void adListParsed(){
        adListParsed=new ArrayList<ArrayList<String>>();
        ArrayList<String> adTemp;
        int countAds=adListRaw.size()/N_PARAMS_AD;


        for(int j=0; j<countAds; j++){
                adTemp = new ArrayList<>(adListRaw.subList(j*N_PARAMS_AD, (j+1)*N_PARAMS_AD));
                Log.i(TAG,"The ad number "+(j+1)+" has the following elements: "+adTemp.toString());
                adListParsed.add(adTemp);
            }
    }


    private void setFieldsFrag(){


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_near_ads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        adListRaw=getIntent().getStringArrayListExtra("adList");
        adListParsed();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "SWIPE >> to Cancel , or CLICK ON :", Snackbar.LENGTH_LONG)
                        .setAction("SEND", new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {

                                //TODO: take email of seller and put in EXTRA_EMAIL and remove my email
                                //TODO: take title ad and put it in (EXTRA_SUBJECT, "..."+ID)
                                //TODO: take email address from the account of the user of the app (from Login?) and put inside Intent Email
                                Intent Email = new Intent(Intent.ACTION_SEND);
                                Email.setType("text/email");
                                Email.putExtra(Intent.EXTRA_EMAIL,new String[]{"fabiogreco3@gmail.com"});  //seller's email
                                Email.putExtra(Intent.EXTRA_SUBJECT,"Hi, I'm interested in your Ad"); // Email's Object
                                Email.putExtra(Intent.EXTRA_TEXT, "Dear Seller, I'm interested in your Ad, please contact me soon" + "");  //Email text

                                try {
                                    startActivity(Intent.createChooser(Email, "Hi, I'm interested"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }

                                Snackbar snackbar1 = Snackbar.make(view1, "send the Message from your preferred Email Client", Snackbar.LENGTH_LONG);
                                snackbar1.show();
                            }
                        }).show();


            }
        });


    }

/* //TODO: possibile menu delle Opzioni già previsto
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_see_near_ads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment {
        // The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() { }

        // Returns a new instance of this fragment for the given section number.
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
           View rootView = inflater.inflate(R.layout.fragment_see_near_ads, container, false);
           TextView textView = (TextView) rootView.findViewById(R.id.section_label);
           textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
           return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            Log.i(TAG, "getItem->(position): "+position);

            //qui si può aggiungere il download della foto dell'annuncio, il titolo il prezzo e tutto il resto
            //Come???

            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show numberOfAds total pages.
             Intent intent = getIntent();
             numberOfAds =intent.getIntExtra("numberOfAds", 1);
            return numberOfAds;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // fragment creation FG
            Log.i(TAG, "getPageTitle");
            int i;
            for(i=0; i< numberOfAds; i++) {
                if(position==i)  {
                    String cc=String.valueOf(i+1);
                    return cc;}

            }return null;
        }
    }



    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Integer> {

        private final String TAG="ReceiverImage";

        @Override
        protected Integer doInBackground(String... params) {


            try {
                Bitmap image= Picasso.with(getApplicationContext()).load(params[0]).get();
                Log.i(TAG,"bytes: "+image.getByteCount());
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

        }
        protected void onPostExecute(Integer a) {

        }
        //TODO: implement onPostExecuteMethod in which a message is shown to the user
    }

}
