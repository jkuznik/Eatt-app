package pl.jkuznik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSentence {

    private List<String> sentences = new ArrayList<>();

    public RandomSentence() {
        sentences.add("Śniadanie to najważniejszy posiłek dnia, więc warto poświęcić mu czas.");
        sentences.add("Smacznego!");
        sentences.add("Najlepszym sposobem na rozpoczęcie dnia jest pyszne śniadanie.");
        sentences.add("Wielu ludzi uważa, że wspólne posiłki rodzinne są fundamentem więzi.");
        sentences.add("Miłego dnia :)");
    }

    public String getSentences() {
        Random random = new Random();
        int scope = sentences.size();
        return sentences.get(random.nextInt(scope));
    }
    public String getSentences(int index) {
        return sentences.get(index);
    }

    public void addSentences(String sentence) {
        sentences.add(sentence);
    }
}
