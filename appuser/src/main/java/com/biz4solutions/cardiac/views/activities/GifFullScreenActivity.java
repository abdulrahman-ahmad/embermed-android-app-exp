package com.biz4solutions.cardiac.views.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.biz4solutions.R;
import com.biz4solutions.databinding.ActivityGifFullScreenBinding;
import com.biz4solutions.utilities.CommonFunctions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class GifFullScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private int gifId;
    private ActivityGifFullScreenBinding binding;
    private boolean isGifPlaying;
    private List<Integer> gifList;
    private int gifPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_full_screen);
        initListeners();
        handleIntent(getIntent());
    }

    private void initListeners() {
        binding.btnCloseFull.setOnClickListener(this);
        binding.btnPlayCprFull.setOnClickListener(this);
        binding.btnNextCprFull.setOnClickListener(this);
        binding.btnPreviousCprFull.setOnClickListener(this);
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra("gifIdArray")) {
            gifList = (List<Integer>) intent.getSerializableExtra("gifIdArray");
            if (gifList != null && gifList.size() > 0) {
                gifId = gifList.get(0);
            }
            if (gifId > 0) {
                playGif();
            }
        }
    }

    private void playGif() {
        try {
            CommonFunctions.getInstance().loadProgressDialog(this);
            Glide.with(this)
                    .asGif()
                    .load(gifList.get(gifPosition))
                    .addListener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            CommonFunctions.getInstance().dismissProgressDialog();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            CommonFunctions.getInstance().dismissProgressDialog();
                            return false;
                        }
                    })

                    .into(binding.gifImageFull);
            isGifPlaying = true;
            binding.btnPlayFull.setVisibility(View.GONE);
            binding.btnPauseFull.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_cpr_full:

                if (gifPosition == gifList.size() - 1) {
                    gifPosition = 0;
                } else {
                    gifPosition++;
                }
                playGif();
                break;
            case R.id.btn_close_full:
                if ((binding.gifImageFull.getDrawable()) != null) {
                    ((GifDrawable) binding.gifImageFull.getDrawable()).stop();
                }
                finish();
                break;
            case R.id.btn_previous_cpr_full:
                if (gifPosition == 0) {
                    gifPosition = gifList.size() - 1;
                } else {
                    gifPosition--;
                }
                playGif();
                break;
            case R.id.btn_play_cpr_full:
                if ((binding.gifImageFull.getDrawable()) != null) {
                    if (((GifDrawable) binding.gifImageFull.getDrawable()).isRunning()) {
                        binding.btnPlayFull.setVisibility(View.VISIBLE);
                        binding.btnPauseFull.setVisibility(View.GONE);
                        ((GifDrawable) binding.gifImageFull.getDrawable()).stop();

                    } else {
                        binding.btnPlayFull.setVisibility(View.GONE);
                        binding.btnPauseFull.setVisibility(View.VISIBLE);
                        ((GifDrawable) binding.gifImageFull.getDrawable()).start();
                    }
                    isGifPlaying = !isGifPlaying;
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}