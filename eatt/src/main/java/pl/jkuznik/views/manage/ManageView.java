package pl.jkuznik.views.manage;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.views.MainLayout;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Composite<VerticalLayout> {

    record SampleItem(String value, String label, Boolean disabled) {
    }

    public ManageView() {
        FormLayout formLayout2Col = new FormLayout();
        Select select = new Select();
        Button chose = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        setSelectSampleData(select);
        chose.setText("Wybierz");
        chose.setWidth("min-content");
        getContent().add(formLayout2Col);
        formLayout2Col.add(select);
        formLayout2Col.add(chose);

        chose.addClickListener(e -> {
            try {
                if (select.getValue() != null ) {
                    String restaurant = select.getValue().toString();
                    int start = restaurant.indexOf("label=");
                    restaurant = restaurant.substring(start+6);
                    int end = restaurant.indexOf(", d");
                    restaurant = restaurant.substring(0, end);
                    Notification.show("Wybrano " + restaurant);
//                    TUTAJ DOPISAĆ USTAWIENIE RESTAURACJI NA TRUE W BAZIE DANYCH
                }
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException illegalStateException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

//    record SampleItem(String value, String label, Boolean disabled) {
//    }

    private void setSelectSampleData(Select select) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        select.setItems(sampleItems);
        select.setItemLabelGenerator(item -> ((SampleItem) item).label());
        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((SampleItem) item).disabled()));
    }
}
