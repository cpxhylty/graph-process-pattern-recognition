import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ImageBasicProcess {
    public static void main(String[] args) throws IOException {
        //read image
        File source = new File("/Users/xuhaoyu/大三下/图像处理与模式识别/lenna.jpg");
        BufferedImage image = ImageIO.read(source);

        //histogramEqualization
        BufferedImage equalizedImage = histogramEqualization(image);
//        template of getting pixel
/*        int pixel = image.getRGB(128, 128);
        Color color = new Color(pixel);
        System.out.println(color.getRed());
        System.out.println(color.getGreen());
        System.out.println(color.getBlue());*/


        //output
        ImageIO.write(equalizedImage, "jpg", new File("/Users/xuhaoyu/大三下/图像处理与模式识别/processed.jpg"));
    }

    private static BufferedImage histogramEqualization(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixel = width * height;

//        count numbers of pixels for each intensity
        int[] numberOfEachIntensity = new int[256];
        for (int i = 0; i < 256; i++) {
            numberOfEachIntensity[i] = 0;
        }
        int wNow;
        int hNow;
        Color colorNow;
        int intensityNow;
//        count for each pixel
        for (wNow = 0; wNow < width; wNow++) {
            for (hNow = 0; hNow < height; hNow++) {
                colorNow = new Color(image.getRGB(wNow, hNow));
                intensityNow = colorNow.getRed();
                numberOfEachIntensity[intensityNow] = numberOfEachIntensity[intensityNow] + 1;
            }
        }
//        draw bar chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Integer i = 0; i < 256; i++) {
            dataset.addValue(numberOfEachIntensity[i], "1", i);
        }
        JFreeChart jFreeChart = ChartFactory.createBarChart("original bar chart", "intensity", "n", dataset);
        FileOutputStream out = new FileOutputStream("/Users/xuhaoyu/大三下/图像处理与模式识别/chart.png");
        ChartUtils.writeChartAsPNG(out, jFreeChart, 900, 600);
//        count cumulated frequencies
        float[] sumFrequencyOfIntensity = new float[256];
        sumFrequencyOfIntensity[0] = (float)numberOfEachIntensity[0] / totalPixel;
        for (int i = 1; i < 256; i++) {
            sumFrequencyOfIntensity[i] = sumFrequencyOfIntensity[i - 1] + (float)numberOfEachIntensity[i] / totalPixel;
        }
//        find nearest new values
        int[] newValuesOfIntensity = new int[256];
        for (int i = 0; i < 256; i++) {
            newValuesOfIntensity[i] = Math.round(sumFrequencyOfIntensity[i] * 255);
        }
//        draw new bar chart
        int[] newNumberOfEachIntensity = new int[256];
        for (int i = 0; i < 256; i++) {
            newNumberOfEachIntensity[i] = 0;
        }
        for (int i = 0; i < 256; i++) {
            newNumberOfEachIntensity[newValuesOfIntensity[i]] += numberOfEachIntensity[i];
        }
        DefaultCategoryDataset newDataset = new DefaultCategoryDataset();
        for (Integer i = 0; i < 256; i++) {
            newDataset.addValue(newNumberOfEachIntensity[i], "1", i);
        }
        JFreeChart newChart = ChartFactory.createBarChart("original bar chart", "intensity", "n", newDataset);
        FileOutputStream newOut = new FileOutputStream("/Users/xuhaoyu/大三下/图像处理与模式识别/new chart.png");
        ChartUtils.writeChartAsPNG(newOut, newChart, 900, 600);
//        create new image
        BufferedImage imageProcessed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = imageProcessed.getRaster();
        int[] onePixel = new int[1];
        int newIntensity;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                colorNow = new Color(image.getRGB(i, j));
                intensityNow = colorNow.getRed();
                newIntensity = newValuesOfIntensity[intensityNow];
                onePixel[0] = newIntensity | newIntensity << 8 | newIntensity << 16;
                raster.setDataElements(i, j, 1, 1, onePixel);
            }
        }
        return  imageProcessed;
    }
}
