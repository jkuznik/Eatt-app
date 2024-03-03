package pl.jkuznik.data.sentence;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SentencesServiceTest {

    @Mock
    private SentenceRepository sentenceRepository;

    @InjectMocks
    private SentencesService sentencesService;

    @Test
    void count() {
        BDDMockito.given(sentenceRepository.count()).willReturn(10L);
        Assertions.assertThat(sentencesService.count()).isEqualTo(10);
    }
}