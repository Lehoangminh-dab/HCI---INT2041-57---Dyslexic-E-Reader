package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.text.Spannable;
import android.text.SpannableString;

import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.myapplication.model.ColorRule;
import com.example.myapplication.utils.TextColorUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView arrowLeftBtn, arrowRightBtn, import_contacts_ic;
    private ImageView closeBtn, bookmarkBtn, tuneBtn;
    private Button finishedBtn;
    private ProgressBar readingProgress;
    private Handler handler = new Handler();  // Khai báo handler toàn cục
    private Runnable removeHighlightRunnable; // Khai báo runnable toàn cục
    private String[] pages;  // Mảng chứa các đoạn văn của cuốn sách
    private int currentPage = 0;
    private SharedPreferences sharedPreferences;
    private String fontName;
    private int size;
    private float lineSpace;
    private int wordSpace;
    private List<ColorRule> colorRuleList;

    private String content = "My school is my favorite place. I have" +
            " many friends in my school who always help me. My teachers" +
            " are very friendly and take care of my parents. Our school" +
            " is very beautiful. It has many classrooms, a playground, a" +
            " garden, and canteen. Our school is very big and famous. People" +
            " living in our city send their children to study here. Our" +
            " school also provides free education to poor children. Every" +
            " student studying here is supports and plays with us. Our seniors" +
            " are very friendly as well. Our school also does social services" +
            " like planting trees every month. I am proud of my school, and" +
            " love it very much. Engage your kid into diverse thoughts and" +
            " motivate them to improve their English with our Essay for Class" +
            " 1 and avail the Simple Essays suitable for them.";
    SpannableString spannableString;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading);

        textView = findViewById(R.id.text);
        arrowLeftBtn = findViewById(R.id.arrow_left_btn);
        arrowRightBtn = findViewById(R.id.arrow_right_btn);
        import_contacts_ic = findViewById(R.id.import_contacts);
        readingProgress = findViewById(R.id.reading_progress);
        closeBtn = findViewById(R.id.close_btn);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        tuneBtn = findViewById(R.id.tune_btn);
        finishedBtn = findViewById(R.id.finished_btn);

        readingProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));

        findViewById(R.id.import_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadingActivity.this, HighlightActivity.class);
                startActivity(intent);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        tuneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ReadingActivity.this, MainActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(ReadingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Sử dụng ViewTreeObserver để đợi cho đến khi TextView có kích thước chính xác
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Sau khi TextView đã có kích thước, thực hiện chia nội dung thành các trang
                pages = splitContentToPages(content);

                // Cập nhật trang đầu tiên
                updatePage();
            }
        });

        // Khởi tạo SharedPreferences để lưu chế độ highlight
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String highlightMode = sharedPreferences.getString("highlight_mode", "WORD");

        // Áp dụng chế độ highlight đã lưu
        applyHighlightMode(highlightMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fontName = sharedPreferences.getString("font", "dyslexic");
        size = sharedPreferences.getInt("size", 42);
        lineSpace = sharedPreferences.getFloat("lineSpace", 1);
        wordSpace= sharedPreferences.getInt("wordSpace", 1);
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("colorRules", null);
        Type type = new TypeToken<List<ColorRule>>() {}.getType();
        colorRuleList = gson.fromJson(jsonRetrieved, type);

//        updatePage();
    }

    // Phương thức chia nhỏ nội dung thành các trang dựa trên kích thước TextView
    private String[] splitContentToPages(String content) {
        List<String> pageList = new ArrayList<>();
        int start = 0;
        TextPaint textPaint = textView.getPaint();
        int width = textView.getWidth();
        int height = textView.getHeight();
        int linesPerPage = height / textView.getLineHeight();

        while (start < content.length()) {
            StaticLayout layout = new StaticLayout(
                    content.substring(start),
                    textPaint,
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
            );

            int end = start;
            int lines = 0;

            for (int i = 0; i < layout.getLineCount() && lines < linesPerPage; i++) {
                end = start + layout.getLineEnd(i);
                lines++;
            }

            pageList.add(content.substring(start, end).trim());
            start = end;
        }

        return pageList.toArray(new String[0]);
    }

    private void updatePage() {
        FrameLayout completionOverlay = findViewById(R.id.completion_overlay);

        // Hiển thị các trang nội dung
        if (currentPage < pages.length - 1) {
            textView.setVisibility(View.VISIBLE);
            String tempString = pages[currentPage];
            int fontId = getResources().getIdentifier(fontName, "font", this.getPackageName());
            Typeface typeface = ResourcesCompat.getFont(this, fontId);
            textView.setTypeface(typeface);
            textView.setTextSize(size);
            textView.setLineSpacing(0, lineSpace);
            if (wordSpace == 1) {
                tempString = tempString.replaceAll("\\s+", "  ");
            } else if (wordSpace == 2) {
                tempString = tempString.replaceAll("\\s+", "    ");
            } else {
                tempString = tempString.replaceAll("\\s+", "      ");
            }
            spannableString = TextColorUtils.applyColorToText(this, tempString, colorRuleList);
            textView.setText(spannableString);
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
            textView.setVisibility(View.VISIBLE);
            String tempString = pages[currentPage];
            int fontId = getResources().getIdentifier(fontName, "font", this.getPackageName());
            Typeface typeface = ResourcesCompat.getFont(this, fontId);
            textView.setTypeface(typeface);
            textView.setTextSize(size);
            textView.setLineSpacing(0, lineSpace);
            if (wordSpace == 1) {
                tempString = tempString.replaceAll("\\s+", "  ");
            } else if (wordSpace == 2) {
                tempString = tempString.replaceAll("\\s+", "    ");
            } else {
                tempString = tempString.replaceAll("\\s+", "      ");
            }
            spannableString = TextColorUtils.applyColorToText(this, tempString, colorRuleList);
            textView.setText(spannableString);
            completionOverlay.setVisibility(View.GONE);  // Ẩn overlay trên trang 5

            // Giữ các nút bookmark và tune hiện trên trang cuối
            bookmarkBtn.setVisibility(View.VISIBLE);
            bookmarkBtn.setEnabled(true);
            tuneBtn.setVisibility(View.VISIBLE);
            tuneBtn.setEnabled(true);

            // Cập nhật độ mờ của nút arrow
            arrowLeftBtn.setAlpha(1.0f);
            arrowRightBtn.setAlpha(1.0f);  // Nút phải rõ trên trang cuối nội dung

        } else if (currentPage == pages.length) {  // FrameLayout sau trang cuối
            textView.setVisibility(View.GONE);
            completionOverlay.setVisibility(View.VISIBLE);  // Hiển thị overlay ở trang FrameLayout
            disableButtons();  // Ẩn bookmark và tune ở FrameLayout cuối

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

    private void initTouchEventWord() {
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        highlightWordAtTouch(event);
                        handler.removeCallbacks(removeHighlightRunnable);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        scheduleRemoveHighlight();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initTouchEventRuler() {
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        highlightLineAtTouch(event);
                        handler.removeCallbacks(removeHighlightRunnable);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
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

        int offset = textView.getOffsetForPosition(touchX, touchY);
        int start = findWordStart(pageText, offset);
        int end = findWordEnd(pageText, offset);

        applyDimEffectExcept(spannableText, start, end);
        textView.setText(spannableText);
    }

    private void highlightLineAtTouch(MotionEvent event) {
        int touchY = (int) event.getY();
        String pageText = pages[currentPage];
        Spannable spannableText = new SpannableString(pageText);

        Layout layout = textView.getLayout();
        if (layout != null) {
            int line = layout.getLineForVertical(touchY);
            int lineStart = layout.getLineStart(line);
            int lineEnd = layout.getLineEnd(line);

            applyDimEffectExcept(spannableText, lineStart, lineEnd);
            textView.setText(spannableText);
        }
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

    private void initTouchEventDark() {
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        // Highlight dark tại vị trí chạm
                        highlightDarkAtTouch(event);
                        // Xóa bỏ tác vụ đợi xóa highlight trước đó (nếu có)
                        handler.removeCallbacks(removeHighlightRunnable);
                        return true; // Đảm bảo trả về true
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Bắt đầu đếm thời gian 2.5s để xóa highlight
                        scheduleRemoveHighlight();
                        return true; // Đảm bảo trả về true
                    }
                }
                return false; // Trả về false nếu không xử lý được
            }
        });
    }

    private void highlightDarkAtTouch(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        String pageText = pages[currentPage];
        Spannable spannableText = new SpannableString(pageText);

        int offset = textView.getOffsetForPosition(touchX, touchY);
        int start = findWordStart(pageText, offset);
        int end = findWordEnd(pageText, offset);

        // Áp dụng hiệu ứng dark highlight cho từ được chọn
        applyDarkHighlight(spannableText, start, end);
        textView.setText(spannableText);
    }

    private void applyDarkHighlight(Spannable spannableText, int start, int end) {

        // Tô sáng từ được chọn với khung chữ nhật bao quanh
        spannableText.setSpan(new BackgroundColorSpan(Color.parseColor("#FCECCB")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Màu nền cho từ được chọn
        spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Màu chữ của từ được chọn
    }

    private void scheduleRemoveHighlight() {
        removeHighlightRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage < pages.length) {
                    textView.setText(pages[currentPage]);
                }
            }
        };
        handler.postDelayed(removeHighlightRunnable, 2500);
    }

    private void applyHighlightMode(String mode) {
        ConstraintLayout readingLayout = findViewById(R.id.reading_layout);

        if (mode.equals("WORD")) {
            initTouchEventWord();
        } else if (mode.equals("RULER")) {
            initTouchEventRuler();
        } else if (mode.equals("DARK")) {
            initTouchEventDark();
            readingLayout.setBackgroundColor(Color.parseColor("#BF000000"));

            // Set icons to white color
            closeBtn.setColorFilter(ContextCompat.getColor(this, R.color.white));
            bookmarkBtn.setColorFilter(ContextCompat.getColor(this, R.color.white));
            tuneBtn.setColorFilter(ContextCompat.getColor(this, R.color.white));
            arrowLeftBtn.setColorFilter(ContextCompat.getColor(this, R.color.white));
            arrowRightBtn.setColorFilter(ContextCompat.getColor(this, R.color.white));
            import_contacts_ic.setColorFilter(ContextCompat.getColor(this, R.color.white));
        } else if (mode.equals("OFF")) {
            // Nếu chế độ là OFF, hủy sự kiện chạm để không thể highlight
            textView.setOnTouchListener(null);

            closeBtn.clearColorFilter();
            bookmarkBtn.clearColorFilter();
            tuneBtn.clearColorFilter();
            arrowLeftBtn.clearColorFilter();
            arrowRightBtn.clearColorFilter();
            import_contacts_ic.clearColorFilter();
        }
    }
}