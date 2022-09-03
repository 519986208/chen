package com.ahhf.chen.code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeMax {

    // 文字显示
    private static final int QRCOLOR = 0x201f1f; // 二维码颜色:黑色
    private static final int BGWHITE = 0xFFFFFF; // 二维码背景颜色:白色

    // 设置QR二维码参数信息
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;

        {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);// 设置QR二维码的纠错级别(H为最高级别)
            put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码方式
            put(EncodeHintType.MARGIN, 0);// 白边
        }
    };

    /**
     * 生成二维码图片+背景+文字描述
     *
     * @param codeFile    生成图地址
     * @param bgImgFile   背景图地址
     * @param widthHeight 二维码宽度高度
     * @param qrUrl       二维码识别地址
     * @param fontSize    文字大小
     * @param imagesX     二维码x轴方向
     * @param imagesY     二维码y轴方向
     * @param codeTexts   文字
     */
    public static void CreatQrCode(File codeFile, File bgImgFile, Integer widthHeight, String qrUrl, Integer fontSize, Integer imagesX, Integer imagesY, QrCodeText... codeTexts) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为: 编码内容,编码类型,生成图片宽度,生成图片高度,设置参数
            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, widthHeight, widthHeight, hints);
            BufferedImage image = new BufferedImage(widthHeight, widthHeight, BufferedImage.TYPE_INT_RGB);

            // 开始利用二维码数据创建Bitmap图片，分别设为黑(0xFFFFFFFF) 白(0xFF000000)两色
            for (int x = 0; x < widthHeight; x++) {
                for (int y = 0; y < widthHeight; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
                }
            }

            // 添加背景图片
            BufferedImage backgroundImage = ImageIO.read(bgImgFile);
            // 距离背景图片x边的距离，居中显示
            Graphics2D rng = backgroundImage.createGraphics();
            rng.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
            rng.drawImage(image, imagesX, imagesY, widthHeight, widthHeight, null);

            // 文字描述参数设置
            Color textColor = Color.white;
            rng.setColor(textColor);
            rng.drawImage(backgroundImage, 0, 0, null);
            // 设置字体类型和大小(BOLD加粗/ PLAIN平常)
            rng.setFont(new Font("微软雅黑,Arial", Font.BOLD, fontSize));
            // 设置字体颜色
            rng.setColor(Color.black);

            // 背景图片的宽度-文字内容的宽度-文字的x坐标
            if (codeTexts != null && codeTexts.length > 0) {
                for (QrCodeText qrCodeText : codeTexts) {
                    String content = qrCodeText.getContent();
                    int x = qrCodeText.getX();
                    int y = qrCodeText.getY();
                    int strWidth = rng.getFontMetrics().stringWidth(content) / 2;
                    rng.drawString(content, x - strWidth, y);
                }
            }

            rng.dispose();
            image = backgroundImage;
            image.flush();
            ImageIO.write(image, "png", codeFile);
            // ImageIO.write(im, formatName, output)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        File bgImgFile = new File("e://1.jpg");// 背景图片
        File QrCodeFile = new File("e://2.jpg");// 生成图片位置
        String url = "https://baidu.com";// 二维码链接

        int widthHeight = 150;// 二维码宽度、高度
        int fontSize = 36;// 文字大小
        int imagesX = 20;// 图片x轴方向
        int imagesY = 150;// 图片y轴方向

        List<QrCodeText> list = new ArrayList<>();
        list.add(new QrCodeText("abc", 250, 250));
        list.add(new QrCodeText("ddd", 250, 150));
        list.add(new QrCodeText("bba", 250, 350));

        CreatQrCode(QrCodeFile, bgImgFile, widthHeight, url, fontSize, imagesX, imagesY, list.toArray(new QrCodeText[]{}));
        System.out.println("创建二维码成功！");
    }

}
