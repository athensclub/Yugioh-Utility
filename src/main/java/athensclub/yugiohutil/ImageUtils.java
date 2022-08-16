package athensclub.yugiohutil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.util.*;

public final class ImageUtils {

    public static final Map<String, BufferedImage> CACHED_IMAGE = new HashMap<>();

    /**
     * Return image of cards with given ids.
     * This will create a new image file and pull the image data from
     * YGOProDeck api if the image file for the given card id does not exist in working directory.
     *
     * @param ids the card ids
     * @return the file object pointing to the image of card with given id
     */
    public static Map<String, BufferedImage> getImages(List<String> ids) throws IOException {
        Map<String, BufferedImage> result = new HashMap<>();
        List<String> notFound = new ArrayList<>();
        Set<String> uniqueIds = new HashSet<>(ids);
        for (String id : uniqueIds) {
            if(CACHED_IMAGE.containsKey(id)){
                result.put(id, CACHED_IMAGE.get(id));
                continue;
            }

            File file = new File(Main.WORKING_DIRECTORY, "images/" + id + ".jpg");
            if (!file.exists()) {
                notFound.add(id);
            } else {
                result.put(id, ImageIO.read(file));
            }
        }
        if(!notFound.isEmpty())
            result.putAll(YgoprodeckAPI.loadImagesAndSave(notFound));

        CACHED_IMAGE.putAll(result);
        return result;
    }

}
