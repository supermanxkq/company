package com.ccservice.b2b2c.atom.test;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 鎴睆
 *
 */
public class S { 
    /** 
    * @param args 
    */ 
    public static void captureScreen(String fileName) throws Exception { 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
        Rectangle screenRectangle = new Rectangle(screenSize); 
        Robot robot = new Robot(); 
        BufferedImage image = robot.createScreenCapture(screenRectangle); 
        ImageIO.write(image, "jpg", new File(fileName)); 
    } 

    public static void main(String[] args) { 
        String str = "D:\\aa.jpg"; 
        try { 
            captureScreen(str); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        System.out.println("----------鎴睆鎴愬姛----------------"); 
    } 
} 
