package pl.jkuznik.views.manage;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import pl.jkuznik.data.information.Information;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.services.InformationService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.services.SamplePersonService;
import pl.jkuznik.views.MainLayout;
import pl.jkuznik.views.edit.EditView;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Div/* Composite<VerticalLayout>*/ {

    private String restaurantName;
    private final RestaurantService restaurantService;
    private final InformationService informationService;
    private final SamplePersonService samplePersonService;
    Select select = new Select();
    Select select2 = new Select();
    Button choseButton = new Button("Wybierz");
    Button setButton = new Button("Ustaw");
    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "collaborative-master-detail/%s/edit";
    private TextField firstName;
    private TextField lastName;
    private Button order = new Button("Wyślij zamówienie");
    private Button change = new Button("Edytuj");
    private TextField textField = new TextField();

    record SampleItem(String value, String label, Boolean disabled) {
    }

    public ManageView(RestaurantService restaurantService, InformationService informationService, SamplePersonService samplePersonService) {
        this.restaurantService = restaurantService;
        this.informationService = informationService;
        this.samplePersonService = samplePersonService;

        SplitLayout splitLayout = new SplitLayout();

        textField.setLabel("Ustaw informację dnia");
        textField.setHelperText("Brak informacji - ustaw to pole puste");
        textField.setValue("");
        textField.setClearButtonVisible(true);
        textField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());

        GridContextMenu<SamplePerson> menu = grid.addContextMenu();
        menu.addItem("View", event -> {
        });
        menu.addItem("Edit", event -> {
        });
        menu.addItem("Delete", event -> {
        });
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
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
        formLayout2Col.getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("30%");
        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        select.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setSelectSampleData();


        formLayout2Col.add(select);
        formLayout2Col.add(choseButton);
        formLayout2Col.add(textField);
        formLayout2Col.add(setButton);
        formLayout2Col.add(menu);

        createGridAndEditorLayout(splitLayout);
        splitLayout.addToSecondary(formLayout2Col);
        add(splitLayout);

        clickChoseListener(choseButton);
        clickSetListener(setButton);
    }

    private void createGridAndEditorLayout(SplitLayout splitLayout) {
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

        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper, editorLayoutDiv);
        wrapper.add(grid);
    }
    private void createGridLayout(SplitLayout splitLayout) {

        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
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
        change.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(order, change);
        editorLayoutDiv.add(buttonLayout);
    }

    private void setSelectSampleData() {   // NAPISAĆ TEST
        List<Restaurant> restaurants = restaurantService.list();
        select.setItems(restaurants);
        select.setItemLabelGenerator(restaurant -> ((Restaurant) restaurant).getName());
//        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((SampleItem) item).disabled())); // TUTAJ DOPISAĆ KOD JEŚLI UŻYTOWNIK NIE KORZYSTAŁ Z RESTAURACJI TO NIE MOŻE JEJ WYBRAĆ
    }

    private void clickChoseListener(Button button) { // NAPISAĆ TEST
        button.addClickListener(e -> {
            try {
                if (select.getValue() != null) {
                    Restaurant restaurant = (Restaurant) select.getValue();
                    restaurantName = restaurant.getName();
                    Notification n = Notification.show("Do następnego zamówienia wybrano " + restaurantName);
                    n.setPosition(Notification.Position.BOTTOM_END);
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
    private void clickSetListener(Button button) { // NAPISAĆ TEST
        button.addClickListener(e -> {
            try {
                Information information = new Information();
                information.setText(textField.getValue());

                if (!information.getText().isBlank()) {
                    information.setActive(true);

                    List<Information> informations = informationService.list();
                    if (!informations.isEmpty()) {
                        for (Information info : informations) {
                            info.setActive(false);
                        }
                        informations.add(information);
                        informationService.updateAll(informations);
                    } else {
                        informations.add(information);
                        informationService.updateAll(informations);
                    }
                    Notification n = Notification.show("Ustawiono wyświetlaną informację na " + textField.getValue());
                    n.setPosition(Notification.Position.BOTTOM_END);
                }
                else {
                    List<Information> informations = informationService.list();

                    for (Information info : informations){
                        info.setActive(false);
                    }
                    informationService.updateAll(informations);

                    Notification n = Notification.show("Ustawiono wyświetlanie losowej sentencji");
                    n.setPosition(Notification.Position.BOTTOM_END);
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
