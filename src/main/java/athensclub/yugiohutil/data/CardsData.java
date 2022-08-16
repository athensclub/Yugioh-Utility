package athensclub.yugiohutil.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardsData {

    public List<CardData> data;

    /**
     *
     * @return a mapping from card id to the url of that card image for all cards in this card data.
     */
    public Map<String, String> getIdToImageUrl(){
        Map<String, String> result = new HashMap<>();
        for(CardData d : data)
            result.put(Long.toString(d.card_images.get(0).id), d.card_images.get(0).image_url);
        return result;
    }

}
