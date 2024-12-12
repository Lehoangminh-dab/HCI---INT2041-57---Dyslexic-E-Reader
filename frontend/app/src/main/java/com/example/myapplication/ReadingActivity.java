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
import android.util.Log;
import android.view.GestureDetector;
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
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.model.Book;
import com.example.myapplication.model.ColorRule;
import com.example.myapplication.model.Font;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.TextColorUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ReadingActivity";

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
    private User user;
    private Font font;
    private String fontName;
    private int size;
    private float lineSpace;
    private int wordSpace;
    private List<ColorRule> ruleList;
    private String highlightMode;

    private String content;
    private SpannableString spannableString;
    private GestureDetector doubleTapGestureDetector;
    private Book currentBook;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        textView = findViewById(R.id.text);
        arrowLeftBtn = findViewById(R.id.arrow_left_btn);
        arrowRightBtn = findViewById(R.id.arrow_right_btn);
        import_contacts_ic = findViewById(R.id.import_contacts);
        readingProgress = findViewById(R.id.reading_progress);
        closeBtn = findViewById(R.id.close_btn);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        tuneBtn = findViewById(R.id.tune_btn);
        finishedBtn = findViewById(R.id.finished_btn);

        Intent receivedIntent = getIntent();
        currentBook = (Book) receivedIntent.getSerializableExtra("book");
        content = currentBook.getContent();

        readingProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadingActivity.this, MainMenuActivity.class);
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

        finishedBtn.setOnClickListener(v -> {
            currentBook.setIsComplete("true");
            Intent intent = new Intent(ReadingActivity.this, MainMenuActivity.class);
            startActivity(intent);
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

        // Áp dụng chế độ highlight đã lưu
        applyHighlightMode(highlightMode);

        // Xu ly tra tu
        doubleTapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handleDoubleClick(e);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);

        font = user.getFont();
        fontName = font.getName();
        size = font.getSize();
        lineSpace = font.getLineSpace();
        wordSpace= font.getWordSpace();
        ruleList = user.getRuleList();
        highlightMode = user.getHighLight();

        applyHighlightMode(highlightMode);
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
            spannableString = TextColorUtils.applyColorToText(this, tempString, ruleList);
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
            spannableString = TextColorUtils.applyColorToText(this, tempString, ruleList);
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
                // Nếu là double click, tra thông tin về từ đấy.
                if (doubleTapGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                // Nếu chỉ là single click, highlight từ đấy.
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

        // Xóa tất cả hiệu ứng dim cũ trước khi áp dụng highlight mới
        clearDimEffect(spannableString);

        int offset = textView.getOffsetForPosition(touchX, touchY);
        int start = findWordStart(spannableString.toString(), offset);
        int end = findWordEnd(spannableString.toString(), offset);

        // Áp dụng dim cho tất cả các từ khác, giữ nguyên hiệu ứng màu gốc cho từ được chọn
        applyDimEffectExcept(spannableString, start, end);
        textView.setText(spannableString);

//        String pageText = pages[currentPage];
//        Spannable spannableText = new SpannableString(pageText);
//
//        int offset = textView.getOffsetForPosition(touchX, touchY);
//        int start = findWordStart(pageText, offset);
//        int end = findWordEnd(pageText, offset);
//
//        applyDimEffectExcept(spannableText, start, end);
//        textView.setText(spannableText);
    }

    private void clearDimEffect(Spannable spannableText) {
        // Loại bỏ tất cả các ForegroundColorSpan có màu dim (xám)
        ForegroundColorSpan[] spans = spannableText.getSpans(0, spannableText.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) {
            if (span.getForegroundColor() == Color.GRAY) {
                spannableText.removeSpan(span);
            }
        }
    }


    private void highlightLineAtTouch(MotionEvent event) {
        int touchY = (int) event.getY();
        String pageText = pages[currentPage];
        Spannable spannableText = new SpannableString(pageText);

        // Xóa hiệu ứng dim cũ
        clearDimEffect(spannableString);

        // Lấy text layout từ textView
        Layout layout = textView.getLayout();
        if (layout != null) {
            int line = layout.getLineForVertical(touchY);
            int lineStart = layout.getLineStart(line);
            int lineEnd = layout.getLineEnd(line);

            // Áp dụng highlight cho dòng hiện tại
            applyDimEffectExcept(spannableString, lineStart, lineEnd);
            textView.setText(spannableString);

//            applyDimEffectExcept(spannableText, lineStart, lineEnd);
//            textView.setText(spannableText);
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

        // Áp dụng dim cho tất cả các từ ngoại trừ từ được chọn
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

        // Xóa hiệu ứng highlight dark cũ
        clearDarkHighlight(spannableString);
        String pageText = pages[currentPage];
//        Spannable spannableText = new SpannableString(pageText);

        int offset = textView.getOffsetForPosition(touchX, touchY);
        int start = findWordStart(spannableString.toString(), offset);
        int end = findWordEnd(spannableString.toString(), offset);

        // Áp dụng highlight dark cho từ hiện tại
        applyDarkHighlight(spannableString, start, end);
        textView.setText(spannableString);
    }

    private void clearDarkHighlight(Spannable spannableText) {
        BackgroundColorSpan[] spans = spannableText.getSpans(0, spannableText.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            if (span.getBackgroundColor() == Color.parseColor("#FCECCB")) { // Màu của highlight dark
                spannableText.removeSpan(span);
            }
        }




        // Áp dụng hiệu ứng dark highlight cho từ được chọn
//        applyDarkHighlight(spannableText, start, end);
//        textView.setText(spannableText);
    }

    private void applyDarkHighlight(Spannable spannableText, int start, int end) {

        // Tô sáng từ được chọn với khung chữ nhật bao quanh
        spannableText.setSpan(new BackgroundColorSpan(Color.parseColor("#FCECCB")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Màu nền cho từ được chọn
    }

    private void scheduleRemoveHighlight() {
        removeHighlightRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage < pages.length) {
                    textView.setText(pages[currentPage]);
                    updatePage();
                }
            }
        };
        handler.postDelayed(removeHighlightRunnable, 2000);
    }

    private void applyHighlightMode(String mode) {
        ConstraintLayout readingLayout = findViewById(R.id.reading_layout);

        if (mode == null) {
            Log.w(LOG_TAG, "Highlight mode is null. Exiting applyHighlightMode.");
            return;
        }

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

    private void handleDoubleClick(MotionEvent e) {
        // Get the double clicked word.
        int x = (int) e.getX();
        int y = (int) e.getY();
        int offset = textView.getOffsetForPosition(x, y);
        String text = spannableString.toString();
        int start = findWordStart(text, offset);
        int end = findWordEnd(text, offset);
        String word = text.substring(start, end);
        Log.d(LOG_TAG, "Double clicked word: " + word);

        // Start the fragment with the word passed in.
        FragmentWordInfo fragment = FragmentWordInfo.newInstance(word);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}