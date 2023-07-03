package antonio.david.selling4usandroid.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    private static final int MAX_IMAGE_SIZE = 1024;

    public static byte[] compressImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap == null) {
            Log.e("ImageUtils", "Error decoding image");
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleFactor = Math.min((float) MAX_IMAGE_SIZE / width, (float) MAX_IMAGE_SIZE / height);
        int newWidth = Math.round(scaleFactor * width);
        int newHeight = Math.round(scaleFactor * height);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        int rotation = getRotation(imagePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] compressedBytes = outputStream.toByteArray();

        bitmap.recycle();
        resizedBitmap.recycle();
        rotatedBitmap.recycle();

        return compressedBytes;
    }

    private static int getRotation(String imagePath) {
        return 0;
    }
}
