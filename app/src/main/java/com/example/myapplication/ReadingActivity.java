package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.text.Spannable;
import android.text.SpannableString;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ReadingActivity extends AppCompatActivity {

    private TextView text;
    private ImageView arrowLeftBtn, arrowRightBtn;
    private ImageView closeBtn, bookmarkBtn, tuneBtn;
    private Button finishedBtn;
    private ProgressBar readingProgress;
    private Handler handler = new Handler();  // Khai báo handler toàn cục
    private Runnable removeHighlightRunnable; // Khai báo runnable toàn cục
    private String[] pages;  // Mảng chứa các đoạn văn của cuốn sách
    private int currentPage = 0;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading);

        text = findViewById(R.id.text);
        arrowLeftBtn = findViewById(R.id.arrow_left_btn);
        arrowRightBtn = findViewById(R.id.arrow_right_btn);
        readingProgress = findViewById(R.id.reading_progress);
        closeBtn = findViewById(R.id.close_btn);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        tuneBtn = findViewById(R.id.tune_btn);
        finishedBtn = findViewById(R.id.finished_btn);

        readingProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));

        pages = new String[]{
                "Trang 1: Nội dung của trang đầu tiên...",
                "Trang 2: Nội dung của trang tiếp theo...",
                "Trang 3: Nội dung của trang kế tiếp...",
                "Trang 4: Nội dung của trang kế tiếp...",
                "Trang 5: Nội dung của trang cuối ...",
        };

        // Cập nhật nội dung trang và tiến trình ban đầu
        updatePage();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tuneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arrowLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    updatePage();
                }
            }
        });

        arrowRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < pages.length) {  // Allow navigation to the last page
                    currentPage++;
                    updatePage();
                }
            }
        });



        finishedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initTouchEvent();
    }

    private void initTouchEvent() {
        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        // Highlight từ tại vị trí chạm
                        highlightWordAtTouch(event);
                        // Xóa bỏ tác vụ đợi xóa highlight trước đó (nếu có)
                        handler.removeCallbacks(removeHighlightRunnable);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Bắt đầu đếm thời gian 2.5s để xóa highlight
                        scheduleRemoveHighlight();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void highlightWordAtTouch(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        String pageText = pages[currentPage];
        Spannable spannableText = new SpannableString(pageText);

        int offset = text.getOffsetForPosition(touchX, touchY);
        int start = findWordStart(pageText, offset);
        int end = findWordEnd(pageText, offset);

        // Làm mờ tất cả các từ ngoại trừ từ được chọn
        applyDimEffectExcept(spannableText, start, end);
        text.setText(spannableText);
    }

    private int findWordStart(String text, int offset) {
        while (offset > 0 && !Character.isWhitespace(text.charAt(offset - 1))) {
            offset--;
        }
        return offset;
    }

    private int findWordEnd(String text, int offset) {
        while (offset < text.length() && !Character.isWhitespace(text.charAt(offset))) {
            offset++;
        }
        return offset;
    }

    private void applyDimEffectExcept(Spannable spannableText, int start, int end) {
        String text = spannableText.toString();
        int wordStart = 0;

        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i)) || i == text.length() - 1) {
                int wordEnd = (i == text.length() - 1) ? i + 1 : i;
                if (wordStart < start || wordEnd > end) {
                    spannableText.setSpan(
                            new ForegroundColorSpan(Color.GRAY), wordStart, wordEnd,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
                wordStart = i + 1;
            }
        }
    }

    private void scheduleRemoveHighlight() {
        // Định nghĩa tác vụ xóa highlight
        removeHighlightRunnable = new Runnable() {
            @Override
            public void run() {
                // Xóa highlight và khôi phục màu sắc ban đầu
                text.setText(pages[currentPage]);
            }
        };
        // Đặt thời gian chờ 2.5 giây trước khi xóa highlight
        handler.postDelayed(removeHighlightRunnable, 2500);
    }

    private void updatePage() {
        FrameLayout completionOverlay = findViewById(R.id.completion_overlay);

        // Hiển thị các trang nội dung
        if (currentPage < pages.length - 1) {  // Trang 1 đến trang 4
            text.setVisibility(View.VISIBLE);
            text.setText(pages[currentPage]);
            completionOverlay.setVisibility(View.GONE);  // Ẩn overlay trên các trang nội dung

            // Hiển thị các nút bookmark và tune
            bookmarkBtn.setVisibility(View.VISIBLE);
            bookmarkBtn.setEnabled(true);
            tuneBtn.setVisibility(View.VISIBLE);
            tuneBtn.setEnabled(true);

            // Cập nhật độ mờ của nút arrow
            arrowLeftBtn.setAlpha(currentPage == 0 ? 0.3f : 1.0f);  // Làm mờ nút trái ở trang đầu
            arrowRightBtn.setAlpha(1.0f);  // Nút phải rõ trên các trang từ 1 đến 4

        } else if (currentPage == pages.length - 1) {  // Trang 5
            text.setVisibility(View.VISIBLE);
            text.setText(pages[currentPage]);
            completionOverlay.setVisibility(View.GONE);  // Ẩn overlay trên trang 5

            // Ẩn nút bookmark và tune trên trang cuối
            disableButtons();

            // Cập nhật độ mờ của nút arrow
            arrowLeftBtn.setAlpha(1.0f);
            arrowRightBtn.setAlpha(1.0f);  // Nút phải rõ trên trang cuối nội dung

        } else if (currentPage == pages.length) {  // FrameLayout sau trang cuối
            text.setVisibility(View.GONE);
            completionOverlay.setVisibility(View.VISIBLE);  // Hiển thị overlay ở trang FrameLayout
            disableButtons();

            // Làm mờ nút phải khi đến FrameLayout
            arrowLeftBtn.setAlpha(1.0f);
            arrowRightBtn.setAlpha(0.3f);  // Làm mờ nút phải trên trang FrameLayout
        }

        // Cập nhật thanh tiến trình đọc
        int progress = (int) (((float) currentPage / pages.length) * 100);
        readingProgress.setProgress(progress);
    }


    private void disableButtons() {
        bookmarkBtn.setVisibility(View.GONE);
        bookmarkBtn.setEnabled(false);
        tuneBtn.setVisibility(View.GONE);
        tuneBtn.setEnabled(false);
    }
}