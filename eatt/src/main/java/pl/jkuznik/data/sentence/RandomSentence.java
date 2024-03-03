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
        if (size==0) return "Brak sentencji w bazie danych";
        return sentences.get(random.nextInt(size));
    }
    public void clearSentences() {
        sentences.clear();
    }
    public void addSentence(String text) {
            sentences.add(text);
    }
    public int size(){
        return sentences.size();
    }

}
