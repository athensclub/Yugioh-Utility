package athensclub.yugiohutil;

import athensclub.yugiohutil.data.CardsData;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class YgoprodeckAPI {

    public static final int RATE_LIMIT = 10;

    /**
     * Load all the card images with the given ids and save to working directory,
     * returning a mapping from card id to the card with that id.
     * @param ids the card ids
     * @return a mapping from card id to the card with that id.
     */
    public static Map<String, BufferedImage> loadImagesAndSave(List<String> ids){
        try {
            URL url = new URL("https://db.ygoprodeck.com/api/v7/cardinfo.php?id=" + String.join(",", ids));
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            CardsData data = gson.fromJson(new InputStreamReader(connection.getInputStream()), CardsData.class);

            Map<String, BufferedImage> result = new HashMap<>();
            Map<String, String> urls = data.getIdToImageUrl();
            int i = 1;
            for(var e : urls.entrySet()){
                if(i % RATE_LIMIT == 0)
                    Thread.sleep(1000);

                BufferedImage img = ImageIO.read(new URL(e.getValue()));

                File folder = new File(Main.WORKING_DIRECTORY, "images");
                if(!folder.exists())
                    folder.mkdirs();
                File file = new File(folder, e.getKey() + ".jpg");
                if(!file.exists())
                    file.createNewFile();

                ImageIO.write(img, "jpg", file);
                result.put(e.getKey(), img);
                i++;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
