package pl.jkuznik.data.randomSentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSentence {

    private List<String> sentences = new ArrayList<>();

    public RandomSentence() {
        sentences.add("Śniadanie to najważniejszy posiłek dnia, więc warto poświęcić mu czas.");
        sentences.add("Smacznego!");
        sentences.add("Najlepszym sposobem na rozpoczęcie dnia jest pyszne śniadanie.");
        sentences.add("Wielu ludzi uważa, że wspólne posiłki są podstawą dobrych relacji.");
        sentences.add("Miłego dnia :)");
        sentences.add("Żona programisty- Idź do sklepu i kup chleb, a jak będą jajka to weź 10. " +
                "W sklepie. " +
                "- Są jajka? " +
                "- Tak. " +
                "- To poproszę 10 chlebów.");
    }

    public String getSentences() {
        Random random = new Random();
        int scope = sentences.size();
        return sentences.get(random.nextInt(scope));
    }
    public String getSentences(int index) {
        return sentences.get(index);
    }

    public void clearSentences(String sentence) {
        sentences.clear();
    }
}
