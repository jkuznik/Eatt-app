package pl.jkuznik.views.manage;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.SamplePerson;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.services.SamplePersonService;
import pl.jkuznik.views.MainLayout;
import pl.jkuznik.views.edit.EditView;

import java.util.List;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Div/* Composite<VerticalLayout>*/ {

    private String restaurantName;
    private final RestaurantService restaurantService;
    private final SamplePersonService samplePersonService;
    Select select = new Select();
    Select select2 = new Select();
    Button choseButton = new Button("Wybierz");
    Button setButton = new Button("Ustaw");
    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "collaborative-master-detail/%s/edit";
    private TextField firstName;
    private TextField lastName;
    private Button order = new Button("Złóż zamówienie");

    record SampleItem(String value, String label, Boolean disabled) {
    }

    public ManageView(RestaurantService restaurantService, SamplePersonService samplePersonService) {
        this.restaurantService = restaurantService;
        this.samplePersonService = samplePersonService;

        SplitLayout splitLayout = new SplitLayout();

        TextField textField = new TextField();
        textField.setLabel("Ustaw informację dnia");
        textField.setHelperText("Brak informacji - ustaw to pole puste");
        textField.setValue("");
        textField.setClearButtonVisible(true);
        textField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        GridContextMenu<SamplePerson> menu = grid.addContextMenu();
        menu.addItem("View", event -> {
        });
        menu.addItem("Edit", event -> {
        });
        menu.addItem("Delete", event -> {
        });
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
//                clearForm();
                UI.getCurrent().navigate(EditView.class);
            }
        });

        FormLayout formLayout2Col = new FormLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        select.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setSelectSampleData();
        choseButton.setWidth("min-content");
        getContent().add(formLayout2Col);
        formLayout2Col.add(select);
        formLayout2Col.add(choseButton);
        clickListener(choseButton);
        formLayout2Col.add(textField);
        formLayout2Col.add(setButton);
        formLayout2Col.add(menu);
    }
    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");

        formLayout.add(firstName, lastName);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(order);
        editorLayoutDiv.add(buttonLayout);
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
                if (select.getValue() != null) {
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
