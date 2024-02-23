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
import pl.jkuznik.data.RandomSentence;
import pl.jkuznik.views.MainLayout;

@PageTitle("Easy Access to Taste")
@Route(value = "index", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class EasyAccesstoTasteView extends Composite<VerticalLayout> {

    public EasyAccesstoTasteView() {
        RandomSentence randomSentence = new RandomSentence();

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
        textLarge.setText(randomSentence.getSentences());
        textLarge.setWidth("100%");
        textLarge.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        link.setText("https://github.com/jkuznik/jkuznik");
        link.setHref("https://github.com/jkuznik/jkuznik");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, link);
        link.setWidth("min-content");
        getContent().add(layoutRow);
        layoutRow.add(textLarge);
        getContent().add(hr);
        getContent().add(link);
    }
}
