/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagetest;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Beento
 */
public class ImageTest {
 
	private static final int IMG_WIDTH = 98;//si può intendere x%
	private static final int IMG_HEIGHT = 98;//si può intendere y%
        //usa una riduzione percentuale nel metodo multi-bilineare.
        // IMG_WIDTH e IMG_HEIGTH non possono superare 100.
        private static final boolean PERCENTUALE = true; 
 
	public static void main(String [] args){
 
	try{
            BufferedImage originalImage = createTestImage(2560, 1920);
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            ImageIO.write(originalImage, "jpg", new File("assets//ORIGINAL.jpg"));

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            System.out.println("Original dimmesion: " + width + " x " + height +".");

            BufferedImage resizeImageJpg = resizeImage(originalImage, type);
            ImageIO.write(resizeImageJpg, "jpg", new File("assets//A_resized.jpg")); 


            BufferedImage resizeImagePng = resizeImage(originalImage, type);
            ImageIO.write(resizeImagePng, "png", new File("assets//B_resized.png")); 

            BufferedImage resizeImageHintJpg = resizeImageWithHint(originalImage, type);
            ImageIO.write(resizeImageHintJpg, "jpg", new File("assets//C_resized.jpg")); 

            BufferedImage resizeImageHintPng = resizeImageWithHint(originalImage, type);
            ImageIO.write(resizeImageHintPng, "png", new File("assets//D_resized_hint.png")); 

            BufferedImage resizeImageMultiBilinear = getScaledInstance( originalImage,
                                                                        IMG_WIDTH, IMG_HEIGHT,
                                                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                                                                        true);
            ImageIO.write(resizeImageMultiBilinear, "jpg", new File("assets//_resized_Multi_Bin_.jpg"));
 
	}catch(IOException e){
		System.out.println(e.getMessage());
	}
 
    }
 
    private static BufferedImage resizeImage(BufferedImage originalImage, int type){
	BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
	g.dispose();
 
	return resizedImage;
    }
 
    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type){
 
	BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
	g.dispose();	
	g.setComposite(AlphaComposite.Src);
 
	g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g.setRenderingHint(RenderingHints.KEY_RENDERING,
	RenderingHints.VALUE_RENDER_QUALITY);
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	RenderingHints.VALUE_ANTIALIAS_ON);
 
	return resizedImage;
    }
    
        /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in down-scaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@codey BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img,
                                                  int targetWidth,
                                                  int targetHeight,
                                                  Object hint,
                                                  boolean higherQuality)
    {
        //codice per fare riduzioni percentuali
        if(PERCENTUALE){
            int imgW = img.getWidth();
            int imgH = img.getHeight();
            int wReduction = (int)((imgW * targetWidth)/100);
            int hReduction = (int)((imgH * targetHeight)/100);
            targetWidth = imgW - wReduction;
            targetHeight = imgH - hReduction;
        }
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    } 
    
    private static BufferedImage createTestImage(int w, int h) {
        int w2 = w/2;
        int h2 = h/2;
        
        BufferedImage img =
            new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w2, h2);
        g2.fillRect(w2, h2, w2, h2);
        for (int i = 8; i < w; i += 16) {
            g2.setColor(Color.BLUE);
            g2.fillRect(0, i, w, 1);
            g2.setColor(Color.GREEN);
            g2.fillRect(i, 0, 1, h);
        }
        g2.setColor(Color.RED);
        g2.drawLine(w2, 0, w, h2);
        g2.drawLine(w, h2, w2, h);
        g2.drawLine(w2, h, 0, h2);
        g2.drawLine(0, h2, w2, 0);
        g2.drawOval(0, 0, w-1, h-1);
        try {
            
            BufferedImage photo =
                ImageIO.read(new File("C:\\Users\\Beento\\Documents\\NetBeansProjects\\ScaleImage-src\\src\\scaleimage\\bw.jpg"));
            g2.setClip(new Ellipse2D.Float(w2/2, h2/2, w2, h2));
            g2.drawImage(photo, w2/2, h2/2, null);
        } catch (Exception e) {
        }
        g2.dispose();
        
        return img;
    }
    
    
}

