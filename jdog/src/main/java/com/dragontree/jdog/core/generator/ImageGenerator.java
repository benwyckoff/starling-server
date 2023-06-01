package com.dragontree.jdog.core.generator;

import com.dragontree.jdog.core.ObjectRequest;
import com.dragontree.jdog.core.ObjectResponse;
import com.dragontree.jdog.core.ResponseType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public class ImageGenerator {

    private ImageGenerator() {

    }

    static Dimension2 computeImageDimension(ObjectRequest request) {
        // first check if request has explicit width and/or height
        Integer width = request.getWidth();
        Integer height = Optional.ofNullable(request.getHeight()).orElse(width);

        if(width == null) {
            width = height;
        }

        if(width == null) {
            Integer size = request.getSize();
            if(size == null) {
                return Constants.DEFAULT_IMAGE_DIMENSION;
            }
            width = height = Math.max(1, 4*(int)Math.sqrt(size));
        }
        return new Dimension2(width, height);
    }

    public static ObjectResponse generateImage(ObjectRequest request) {

        // Build an image if the requested width and height.
        // If w/h are not provided, compute approximate dimensions based on the size
        // param.

        Dimension2 dim = computeImageDimension(request);
        int width = dim.getWidth();
        int height = dim.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Color color = Color.BLUE;
        g.setColor(color);
        g.fillRect(0,0,width,height);
        int gap = 20;
        for(int steps = 0; steps < width/gap; steps += 1) {
            color = color == Color.BLUE ? Color.GREEN : Color.BLUE;
            g.setColor(color);
            int off = steps*gap/2;
            g.fillRect(off,off,width-(off*2),height-(off*2));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, Constants.DEFAULT_IMAGE_FORMAT, baos);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        byte[] bytes = baos.toByteArray();

        return new ObjectResponse(ResponseType.PNG, bytes);
    }

}
