package com.lvmo.tocatef2.ui.Raking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.widget.TextView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.ui.Transision.TransisionActivity;

import java.util.Random;


public class RankingActivity extends AppCompatActivity {
    private TextView etRanking;
    private  InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        etRanking=findViewById(R.id.textView1_RA);
         etRanking.setText(R.string.SetRanking);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new UserRakingFragment())
                .commit();
    }
    @Override
    public void onBackPressed() {
        interticalAuncio();
        Intent i=new Intent(RankingActivity.this, TransisionActivity.class);
        startActivity(i);
    }

    private void interticalAuncio() {
        int n = new Random().nextInt(3);
        if(n==2) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } /*else {
                    Toast.makeText(TransisionActivity.this, "nose cargo el anuncio", Toast.LENGTH_LONG);
                }*/
                }
            });
        }
    }
}
