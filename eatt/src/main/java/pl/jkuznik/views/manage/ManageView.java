package pl.jkuznik.views.manage;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.List;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Composite<VerticalLayout> {
    
    private String restaurantName;
    private final RestaurantService restaurantService;
    Select select = new Select();
    Button choseButton = new Button("Wybierz");
    record SampleItem(String value, String label, Boolean disabled) {
    }

    public ManageView(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;

        FormLayout formLayout2Col = new FormLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        select.addThemeVariants(ButtonVariant.LUMO_ERROR);
        setSelectSampleData();
        choseButton.setWidth("min-content");
        getContent().add(formLayout2Col);
        formLayout2Col.add(select);
        formLayout2Col.add(choseButton);
        clickListener(choseButton);
    }
    private void setSelectSampleData() {   // NAPISAĆ TEST
        List<Restaurant> restaurants = restaurantService.list();
        select.setItems(restaurants);
        select.setItemLabelGenerator(restaurant -> ((Restaurant) restaurant).getName());
//        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((SampleItem) item).disabled())); // TUTAJ DOPISAĆ KOD JEŚLI UŻYTOWNIK NIE KORZYSTAŁ Z RESTAURACJI TO NIE MOŻE JEJ WYBRAĆ
    }

    private void clickListener(Button button) { // NAPISAĆ TEST
        button.addClickListener(e -> {
            try {
                if (select.getValue() != null ) {
                    Restaurant restaurant = (Restaurant) select.getValue();
                    restaurantName = restaurant.getName();
                    Notification.show("Do następnego zamówienia wybrano " + restaurantName);
                    List<Restaurant> restaurants = restaurantService.list();
                    restaurants.stream()
                            .forEach(r -> {
                                if (r.getName().equals(restaurantName)) r.setActive(true);
                                else r.setActive(false);
                            });
                    restaurantService.updateAll(restaurants);
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
}
