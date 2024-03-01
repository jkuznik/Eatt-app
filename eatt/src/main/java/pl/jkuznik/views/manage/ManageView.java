package pl.jkuznik.views.manage;

import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.SamplePerson;
import pl.jkuznik.data.information.Information;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.services.InformationService;
import pl.jkuznik.services.MyOrderService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;
import pl.jkuznik.views.edit.EditView;

import java.util.EventListener;
import java.util.List;
import java.util.UUID;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Div/* Composite<VerticalLayout>*/ {
    private String restaurantName;
    private final RestaurantService restaurantService;
    private final InformationService informationService;
    private final MyOrderService myOrderService;
    Select select = new Select();
    Select select2 = new Select();
    Button choseButton = new Button("Wybierz");
    Button setButton = new Button("Ustaw");
    private final Grid<MyOrder> grid = new Grid<>(MyOrder.class, false);
    private final CollaborationBinder<MyOrder> binder;
    private TextField userName;
    private TextField mealName;
    private Button order = new Button("Wyślij zamówienie");
    private Button edit = new Button("Edytuj");
    private TextField infoTextField = new TextField();
    private MyOrder myOrder;
    private Div editorLayoutDiv = new Div();
    private Div editorDiv = new Div();

    record SampleItem(String value, String label, Boolean disabled) {
    }

    public ManageView(RestaurantService restaurantService, InformationService informationService, MyOrderService myOrdeService) {
        this.restaurantService = restaurantService;
        this.informationService = informationService;
        this.myOrderService = myOrdeService;

        SplitLayout splitLayout = new SplitLayout();

        infoTextField.setLabel("Ustaw informację dnia");
        infoTextField.setHelperText("Brak informacji - ustaw to pole puste");
        infoTextField.setValue("");
        infoTextField.setClearButtonVisible(true);
        infoTextField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());

//        GridContextMenu<MyOrder> menu = grid.addContextMenu();
//        menu.addItem("View", event -> {
//        });
//        menu.addItem("Edit", event -> {
//        });
//        menu.addItem("Delete", event -> {
//        });
        grid.addColumn("userName").setAutoWidth(true).setHeader("Pracownik");
        grid.addColumn("mealName").setAutoWidth(true).setHeader("Danie");
        grid.addColumn("email").setAutoWidth(true).setHeader("Kontakt");

        grid.setItems(query -> myOrdeService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream().filter(MyOrder::isActive));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(ManageView.class);
                editorLayoutDiv.remove(editorDiv);
                createEditorLayout(splitLayout, event.getValue().getUserName(), event.getValue().getMealName());
                editorLayoutDiv.add(editorDiv);
            } else {
                UI.getCurrent().navigate(ManageView.class);
            }
        });
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");
        binder = new CollaborationBinder<>(MyOrder.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        FormLayout formLayout2Col = new FormLayout();
        formLayout2Col.getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("30%");
        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        select.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setSelectSampleData();


        formLayout2Col.add(select);
        formLayout2Col.add(choseButton);
        formLayout2Col.add(infoTextField);
        formLayout2Col.add(setButton);
//        formLayout2Col.add(menu);

        createGridAndEditorLayout(splitLayout);
        splitLayout.addToSecondary(formLayout2Col);
        add(splitLayout);

        clickChoseListener(choseButton);
        clickSetListener(setButton);
        clickOrderListener(order);
        clickEditListener(edit);
    }

    private void createGridAndEditorLayout(SplitLayout splitLayout) {
//        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

//        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        userName = new TextField("Pracownik");
        mealName = new TextField("Danie");

        formLayout.add(userName, mealName);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper, editorLayoutDiv);
        wrapper.add(grid);
    }
    private void createEditorLayout(SplitLayout splitLayout, String user, String meal) {
//        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

//        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        userName = new TextField("Pracownik");
        mealName = new TextField("Danie");
        userName.setValue(user);
        mealName.setValue(meal);
        formLayout.add(userName, mealName);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(MyOrder value) {
        this.myOrder = value;
        String topic = null;
        if (this.myOrder != null && this.myOrder.getId() != null) {
            topic = "myOrder/" + this.myOrder.getId();
//            avatarGroup.getStyle().set("visibility", "visible");
        } else {
//            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.myOrder);
//        avatarGroup.setTopic(topic);

    }
    private void createButtonLayout(Div editorLayoutDiv) {  // NAPISAĆ TEST
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        edit.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(order, edit);
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
                information.setText(infoTextField.getValue());

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
                    Notification n = Notification.show("Ustawiono wyświetlaną informację na " + infoTextField.getValue());
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
    private void clickOrderListener(Button button) {
        button.addClickListener( e -> {

//            Email sendOrder = new Email(orders);
//            sendOrder.sendMail();
            List<MyOrder> myOrders = myOrderService.list();

            for (MyOrder myOrder : myOrders){
                if (myOrder.isActive()) myOrder.setActive(false);
            }
            Notification n = Notification.show("Wysłano zamówienie.");
            n.setPosition(Notification.Position.MIDDLE);

            myOrderService.updateAll(myOrders);
            refreshGrid();
        });
    }
    private void clickEditListener(Button button) {
        button.addClickListener( e -> {

            try {
                if (this.myOrder == null) {
                    this.myOrder = new MyOrder();
                }
                binder.writeBean(this.myOrder);
                myOrder.setUserName(" ");
                myOrder.setMealName(" ");
                myOrderService.update(this.myOrder);
                clearForm();
                refreshGrid();
                Notification.show("Zmieniono").setPosition(Notification.Position.BOTTOM_END);
                UI.getCurrent().navigate(EditView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }

//            Email sendOrder = new Email(orders);
//            sendOrder.sendMail();
            List<MyOrder> myOrders = myOrderService.list();

            for (MyOrder myOrder : myOrders){
                if (myOrder.isActive()) myOrder.setActive(false);
            }
            Notification n = Notification.show("Wysłano zamówienie.");
            n.setPosition(Notification.Position.MIDDLE);

            myOrderService.updateAll(myOrders);
            refreshGrid();
        });
    }
}
