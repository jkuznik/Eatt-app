package pl.jkuznik.views.easyaccesstotaste;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import pl.jkuznik.data.information.Information;
import pl.jkuznik.data.sentence.RandomSentence;
import pl.jkuznik.data.sentence.Sentence;
import pl.jkuznik.data.information.InformationService;
import pl.jkuznik.data.sentence.SentencesService;
import pl.jkuznik.views.MainLayout;

import java.util.List;

@PageTitle("Easy Access to Taste")
@Route(value = "index", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class EasyAccesstoTasteView extends Composite<VerticalLayout> {
    private final SentencesService sentencesService;
    private final InformationService informationService;
    public EasyAccesstoTasteView(SentencesService sentencesService, InformationService informationService) {
        this.sentencesService = sentencesService;
        this.informationService = informationService;

        HorizontalLayout layoutRow = new HorizontalLayout();
        Paragraph textLarge = new Paragraph();
        Hr hr = new Hr();
        Anchor link = new Anchor();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        textLarge.setText(getText());
        textLarge.setWidth("100%");
        textLarge.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        link.setText("https://github.com/jkuznik/jkuznik");
        link.setHref("https://github.com/jkuznik/jkuznik");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, link);
        link.setWidth("min-content");
        getContent().add(layoutRow);
        layoutRow.add(textLarge);
        getContent().add(layoutRow);
//        layoutRow.add("");
        getContent().add(hr);
        getContent().add(link);
    }

    private String getText(){
        List<Information> list = informationService.list();
        Information information = list.stream()
                .filter(Information::isActive)
                .findFirst()
                .orElse(null);
        if(information!=null) return information.getText();

        RandomSentence randomSentence = new RandomSentence();
        List<Sentence> sentences = sentencesService.list();

        for (Sentence s : sentences) {
            randomSentence.addSentence(s.getText());
        }

        return randomSentence.getSentence();

//        if (information.getText().isEmpty()) return randomSentence.getSentence();
//        else return information.getText();
    }
}
