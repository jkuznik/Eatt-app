package pl.jkuznik.views.menu;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import pl.jkuznik.views.MainLayout;

@PageTitle("Menu")
@Route(value = "menu", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MenuView extends Composite<VerticalLayout> {

    public MenuView() {
        RadioButtonGroup radioGroup = new RadioButtonGroup();
        Accordion accordion = new Accordion();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        radioGroup.setLabel("RestaurantName");
        radioGroup.setWidth("min-content");
        radioGroup.setItems("Order ID", "Product Name", "Customer", "Status");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        accordion.setWidth("100%");
        setAccordionSampleData(accordion);
        getContent().add(radioGroup);
        getContent().add(accordion);
    }

    private void setAccordionSampleData(Accordion accordion) {
        Span description = new Span("opis");
//        Span name = new Span("Sophia Williams");
//        Span email = new Span("sophia.williams@company.com");
//        Span phone = new Span("(501) 555-9128");
        VerticalLayout personalInformationLayout = new VerticalLayout(description/*, email, phone*/);
        personalInformationLayout.setSpacing(false);
        personalInformationLayout.setPadding(false);
        accordion.add("Opis potrawy", personalInformationLayout);
        Span allergens = new Span("allergens");
//        Span street = new Span("4027 Amber Lake Canyon");
//        Span zipCode = new Span("72333-5884 Cozy Nook");
//        Span city = new Span("Arkansas");
        VerticalLayout billingAddressLayout = new VerticalLayout();
        billingAddressLayout.setSpacing(false);
        billingAddressLayout.setPadding(false);
        billingAddressLayout.add(allergens/*street, zipCode, city*/);
        accordion.add("Alergeny", billingAddressLayout);
        Span nutritions = new Span("nutritions");
//        Span cardBrand = new Span("Mastercard");
//        Span cardNumber = new Span("1234 5678 9012 3456");
//        Span expiryDate = new Span("Expires 06/21");
        VerticalLayout paymentLayout = new VerticalLayout();
        paymentLayout.setSpacing(false);
        paymentLayout.setPadding(false);
        paymentLayout.add(nutritions/*cardBrand, cardNumber, expiryDate*/);
        accordion.add("Wartości odżywcze", paymentLayout);
    }
}
