package athensclub.yugiohutil;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static athensclub.yugiohutil.Main.WORKING_DIRECTORY;

public final class PDFUtils {

    /*
    * PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
       try(PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
           PDImageXObject image = JPEGFactory.createFromStream(doc, Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("images/tasa1.jpg")));
           stream.drawImage(image, 0, 0, (float)PDFUtils.mm2pt(100), (float)PDFUtils.mm2pt(100));
           stream.drawImage(image, (float)PDFUtils.mm2pt(100), 0, (float)PDFUtils.mm2pt(100), (float)PDFUtils.mm2pt(100));
       } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PDFUtils.savePdfToWorkingDirectory(doc, "test.pdf");
    * */

    /**
     * The default padding of the result file, which is the amount of space in each side of paper
     * before the content is appended (in pt unit)
     */
    public static final double DEFAULT_PADDING = mm2pt(15);

    /**
     * The default spacing of result file, which is the amount of space between each picture (in put unit).
     */
    public static final double DEFAULT_SPACING = mm2pt(5);

    /**
     * Create a pdf document containing all cards in the given cards list,
     * in the same amount as present in the said list. Each card will width and height
     * (in centimeters) equal to the given card width and card height.
     * @param cards list of card ids (repeated card id means that card x will be present n times)
     * @param cardWidth the desired card width (centimeters)
     * @param cardHeight the desired card height (centimeters)
     * @return the pdf document containing all cards in the given cards list
     */
    public static PDDocument createPDDocument(List<String> cards, double cardWidth, double cardHeight) throws IOException {
        cardWidth = mm2pt(cardWidth*10);
        cardHeight = mm2pt(cardHeight*10);

        Map<String, BufferedImage> cardImages = ImageUtils.getImages(cards);
        System.out.println(cardImages);
        PDDocument doc = new PDDocument();
        int i = 0;
        while(i < cards.size()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try(PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                //PDImageXObject image = JPEGFactory.createFromStream(doc, Objects.requireNonNull(PDFUtils.class.getClassLoader().getResourceAsStream("images/tasa1.jpg")));
                double startX = page.getCropBox().getLowerLeftX() + DEFAULT_PADDING;
                double startY = page.getCropBox().getLowerLeftY() + DEFAULT_PADDING;
                double endX = page.getCropBox().getUpperRightX() - DEFAULT_PADDING;
                double endY = page.getCropBox().getUpperRightY() - DEFAULT_PADDING;

                if(startX + cardWidth > endX || startY + cardHeight > endY)
                    throw new IllegalArgumentException("Card width or card height is too large for A4 size.");

                double x = startX;
                double y = startY;
                while(y + cardHeight < endY) {
                    if(x + cardWidth > endX){
                        x = startX;
                        y += cardHeight + DEFAULT_SPACING;
                        continue;
                    }

                    if(i >= cards.size())
                        break;
                    System.out.println("Loading #" + i + " " + cards.get(i));
                    PDImageXObject image = JPEGFactory.createFromImage(doc, cardImages.get(cards.get(i)));
                    System.out.println("Drawing image #" + i);
                    stream.drawImage(image, (float)x, (float)y, (float)cardWidth, (float)cardHeight);
                    x += cardWidth + DEFAULT_SPACING;
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return doc;
    }

    /**
     * Save the given document to the pdf working directory
     * @param doc the document to save
     * @param fileName the file name of the document to save
     */
    public static void savePdfToWorkingDirectory(PDDocument doc, String fileName){
        try {
            File folder = new File(WORKING_DIRECTORY, "pdfs");
            if(!folder.exists())
                folder.mkdir();
            File f = new File(folder, "test.pdf");
            if(!f.exists())
                f.createNewFile();
            doc.save(new FileOutputStream(f));
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert pt unit to millimeters (mm)
     * @param pt the length in pt units
     * @return the given length in millimeters
     */
    public static double pt2mm(double pt) {
        return pt * 25.4f / 72.0;
    }

    /**
     * Convert millimeters unit to pt unit
     * @param mm the length in millimeters
     * @return the given length in pt unit
     */
    public static double mm2pt(double mm) {
        return mm * 72.0 / 25.4;
    }

}
