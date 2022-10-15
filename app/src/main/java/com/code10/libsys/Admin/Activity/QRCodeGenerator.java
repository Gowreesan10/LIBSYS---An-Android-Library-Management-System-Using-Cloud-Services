package com.code10.libsys.Admin.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.print.PrintHelper;

import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QRCodeGenerator extends AppCompatActivity {

    int COPIES = 0;
    String LibName = "";
    String BookName = "";
    int CopyNo = 0;
    ArrayList<String> qrArrayList = new ArrayList<>();
    ArrayList<Integer> copyCount = new ArrayList<>();

    ProgressDialog progressDialog;
    AutoCompleteTextView selectCopyNo;
    Bitmap QR = null;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generater);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        AtomicBoolean hasData = new AtomicBoolean(false);
        if (getIntent().getStringExtra("LIB NAME") != null) {
            LibName = getIntent().getStringExtra("LIB NAME");
            BookName = getIntent().getStringExtra("BOOK NAME");
            COPIES = Integer.parseInt(getIntent().getStringExtra("COPIES"));
            setVIEW();
        } else {
            LibName = firebaseUser.getDisplayName().replace(".Librarian", "");
            firebaseFirestore.collection("IssueDetails").document(LibName).get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> x0 = documentSnapshot.getData();
                if (x0 != null) {
                    hasData.set(true);
                    for (Map.Entry<String, Object> book : x0.entrySet()) {
                        qrArrayList.add(book.getKey());
                        Map<String, Object> data = (Map<String, Object>) book.getValue();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().equals("Total Copies")) {
                                int totalCopies = Integer.parseInt(String.valueOf(entry.getValue()));
                                copyCount.add(totalCopies);
                            }
                        }
                    }
//                    if (hasData.get()) {
                        setVIEW();
//                    }else {
//                        progressDialog.dismiss();
//                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(this, "No books", Toast.LENGTH_SHORT).show();
                }
            });

        }

        AppCompatImageView appCompatImageView = findViewById(R.id.qr);
        AppCompatButton generate = findViewById(R.id.generateButton);

        generate.setOnClickListener(v -> {

            if(BookName != "" && LibName!= "" && CopyNo != 0) {
                QR = generateQR();
                appCompatImageView.setImageBitmap(QR);
            }else {
                Toast.makeText(this, "Select Book And Copy No", Toast.LENGTH_SHORT).show();
            }
        });

        Button button = findViewById(R.id.printButton);
        button.setOnClickListener(v -> {
            PrintHelper imagePrinter = new PrintHelper(getBaseContext());
            imagePrinter.setScaleMode(PrintHelper.SCALE_MODE_FILL);
            imagePrinter.printBitmap(BookName + "  " + CopyNo, QR);
        });

        ImageView imageView = findViewById(R.id.imageGroup2);
        imageView.setOnClickListener(v -> finish());
    }

    public void setVIEW() {
        AutoCompleteTextView selectBook = findViewById(R.id.selectCBookauto);
        selectCopyNo = findViewById(R.id.selectCopy);

        if (qrArrayList.isEmpty() && BookName != "") {
            qrArrayList.add(BookName);
            copyCount.add(COPIES);
            selectBook.setClickable(false);
        }
        ArrayAdapter<String> book = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, qrArrayList);
        selectBook.setAdapter(book);

        selectBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BookName = adapterView.getItemAtPosition(position).toString();
                COPIES = copyCount.get(position);
                setAdapter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(QRCodeGenerator.this, "Select Copy Number", Toast.LENGTH_SHORT).show();
            }
        });

        if (qrArrayList.isEmpty() && BookName != "") {
            selectBook.setSelection(0);
        }

        selectCopyNo.setOnClickListener(v -> {
            if (COPIES == 0) {
                Toast.makeText(QRCodeGenerator.this, "Select Book", Toast.LENGTH_SHORT).show();
            }
        });

        selectCopyNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setSelectedCopyNo(Integer.parseInt(adapterView.getItemAtPosition(position).toString()));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(QRCodeGenerator.this, "Select Copy Number", Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.dismiss();
    }

    public void setAdapter() {
        List<String> noOfCopies = IntStream.rangeClosed(1, COPIES).mapToObj(i -> i + "").collect(Collectors.toList());
        ArrayAdapter<String> copyNumber = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noOfCopies);
        selectCopyNo.setAdapter(copyNumber);
    }

    public void setSelectedCopyNo(int No) {
        CopyNo = No;
    }

    public Bitmap generateQR() {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 20);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LibName", LibName);
            jsonObject.put("BookName", BookName);
            jsonObject.put("CopyNo", CopyNo);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 300, 300, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
//
//            Bitmap myLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground);
//            return mergeBitmaps(myLogo, bitmap);

        } catch (WriterException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {
//
//        int height = bitmap.getHeight();
//        int width = bitmap.getWidth();
//
//        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
//        Canvas canvas = new Canvas(combined);
//        int canvasWidth = canvas.getWidth();
//        int canvasHeight = canvas.getHeight();
//
//        canvas.drawBitmap(bitmap, new Matrix(), null);
//
//        int centreX = (canvasWidth - overlay.getWidth()) / 2;
//        int centreY = (canvasHeight - overlay.getHeight()) / 2;
//        canvas.drawBitmap(overlay, centreX, centreY, null);
//        return combined;
//    }
}
//            if (CopyNo != 0) {
//                for (int i = 1; i <= COPIES; i++) {
//                    CopyNo = i;
//                    generateQR();
//                }
//
//                int chunkWidth = 700;
//                int chunkHeight = 700;
//
//                Bitmap bitmap = Bitmap.createBitmap(chunkWidth * 3, chunkHeight * 5, Bitmap.Config.ARGB_4444);
//
//                Canvas canvas = new Canvas(bitmap);
//                int count = 0;
//                for (int rows = 0; rows < 5; rows++) {
//                    if (count == qrArrayList.size()) {
//                        break;
//                    }
//                    for (int cols = 0; cols < 3; cols++) {
//                        canvas.drawBitmap(qrArrayList.get(count), chunkWidth * cols, chunkHeight * rows,
//                                null);
//                        count++;
//                        if (count == qrArrayList.size()) {
//                            break;
//                        }
//                    }
//                }
//
//                appCompatImageView.setImageBitmap(bitmap);
//            } else {
//                Toast.makeText(QRCodeGenerator.this, "Select Copy Number", Toast.LENGTH_SHORT).show();
//            }

//            firebaseFirestore.collection("Copies").orderBy(LibName).get().addOnSuccessListener(queryDocumentSnapshots -> {
//                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
//                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
//                    Map<String, Object> copyData = documentSnapshot.getData();
//                    if (copyData.containsKey(LibName)) {
//                        qrArrayList.add(documentSnapshot.getId());
//                        HashMap<String, String> x = (HashMap<String, String>) copyData.get(LibName);
//                        for (Map.Entry<String, String> entry : x.entrySet()) {
//                            if (entry.getKey().equals("No of Copies")) {
//                                copyCount.add(Integer.parseInt(String.valueOf(entry.getValue())));
//                            }
//                        }
//                    }
//                }
//                Log.v("Array List Size", qrArrayList.size() + "");
//                setVIEW();
//            });0