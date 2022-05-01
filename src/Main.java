import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Main {
	
	public static void main(String[] args) {
		
		if(args.length == 0) {
			System.out.println("java -jar ascii.jar inputFile outputFile compression charSize light toImage");
			System.out.println("\nMandatory parameters :");
			System.out.println(" -inputFile : full path to the image");
			System.out.println(" -outputFile : full path to the destination text file. /!\\ if the file already exists, it will be overwritten");
			System.out.println("\nOptionnal parameters : ");
			System.out.println(" -compression : amount of shrinking of the image. Default : 1");
			System.out.println(" -charSize : number of times each character repeats itself, can be usefull to fix weird aspect ratio. /!\\ if toImage is set to 1, the default value Default : 1");
			System.out.println(" -light : percentage of light maximum, smaller image can look saturated, in this case you can try lowering this value. Default : 100");
			System.out.println(" -toImage : set this value to 1 to have an image output, or leave it to 0 to have a text output. Default : 0");
			System.out.println("\nExamples :");
			System.out.println("java -jar ascii.jar /home/User/Desktop/Image.jpg /home/User/Desktop/outputText.txt 3 4 50 0");
			System.out.println("java -jar ascii.jar /home/User/Desktop/Image.jpg /home/User/Desktop/outputImage.jpg 2 1 100 1");
			System.exit(0);
		}
		
		/*
		 * save program args as variables
		 */
		String inputFile = args[0];
		String outputFile = args[1];
		int compression = args.length > 2
				? Integer.parseInt(args[2])
				: 1;
		int charSize = args.length > 3
				? Integer.parseInt(args[3])
				: 1;
		int light = args.length > 4
				? Integer.parseInt(args[4])
				: 100;
		boolean toImage = args.length > 5
				? (args[5].equals("1") ? true : false)
				: false;
		
		String grayScale = " .'`^\",:;Il!i><~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@";
		
		/*
		 * read the files
		 */
		BufferedImage bfImg = null;
		
		try {
			bfImg = ImageIO.read(new File(inputFile));
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(0);
		}
		
		/*
		 * for each pixel of the image, calculate the corresponding grayscale character
		 */
		
		ArrayList<String> txt = new ArrayList<>();
		
		System.out.println("generating text");
		
		for(int i = 0; i < bfImg.getHeight()/compression; i++) {
			
			String line = "";
			for(int j = 0; j < bfImg.getWidth()/compression; j++) {
				
				Color pixelColor = new Color(bfImg.getRGB(j*compression, i*compression));
				float lightLevel = (float)(pixelColor.getRed()*pixelColor.getGreen()*pixelColor.getRed())/(255*255*256);
				char px = grayScale.charAt((int)(lightLevel*(float)grayScale.length()*(float)light/100));
				
				line+= String.valueOf(px).repeat(charSize);
			}
			txt.add(line);
			System.out.print("\r" + (String.valueOf((float)100*(i+1)/(bfImg.getHeight()/compression)) + "----").substring(0, 5) + "%");
		}
		
		if(toImage) {
			File outputImg = new File(outputFile);
			try {
				outputImg.createNewFile();
				ImageIO.write(txtToImg(txt), outputImg.getName().split("\\.")[outputImg.getName().split("\\.").length-1], outputImg);
				System.out.println("Printed to image");
			} catch(Exception e){
				System.err.println(e);
				System.exit(0);
			}
			
		} else {
			
			PrintWriter txtWrt = null;
			try {
				txtWrt = new PrintWriter(outputFile, "UTF-8");
			} catch(Exception e) {
				System.err.println(e);
				System.exit(0);
			}
			
			System.out.println("\nPrinting to text file");
			for(int i = 0; i < txt.size(); i++) {
				txtWrt.write(txt.get(i) + "\n");
				System.out.print("\r" + (String.valueOf((float)100*(i+1)/(txt.size())) + "----").substring(0, 5) + "%");
			}
			txtWrt.close();
			
			System.out.println("Printed to text file");
		}
		
	}
	
	public static BufferedImage txtToImg(ArrayList<String> txt) {
		
		int fontSize = 15;
		BufferedImage img = new BufferedImage(fontSize*txt.get(0).length(), fontSize*txt.size(), BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics g = img.getGraphics();
		g.setFont(new Font("ROMAN_BASELINE", Font.PLAIN, fontSize));
		
		System.out.println("\nPrinting to image");
		
		for(int i = 0; i < txt.size(); i++) {
			for(int j = 0; j < txt.get(0).length(); j++) {
				g.drawString(String.valueOf(txt.get(i).charAt(j)), j*fontSize, i*fontSize);
			}
			System.out.print("\r" + (String.valueOf((float)100*(i+1)/(txt.size())) + "----").substring(0, 5) + "%");
		}
		g.dispose();
		
		System.out.print("\n");
		
		return(img);
	}

}