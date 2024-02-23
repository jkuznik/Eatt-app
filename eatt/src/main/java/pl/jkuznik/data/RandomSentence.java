package pl.jkuznik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSentence {

    private List<String> sentences = new ArrayList<>();

    public RandomSentence() {
        sentences.add("Życie jak w madrycie");
        sentences.add("Smacznego!");
    }

    public String getSentences() {
        Random random = new Random();
        int scope = sentences.size();
        return sentences.get(random.nextInt(scope));
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }
}
