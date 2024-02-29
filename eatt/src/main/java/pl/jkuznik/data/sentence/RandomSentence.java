package pl.jkuznik.data.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSentence {
    private List<String> sentences = new ArrayList<>();
    public RandomSentence() {
    }
    public String getSentence() {
        Random random = new Random();
        int size = sentences.size();
        return sentences.get(random.nextInt(size));
    }
    public void clearSentences(String sentence) {
        sentences.clear();
    }

    public void setSentence(String text) {
            sentences.add(text);
    }
}
