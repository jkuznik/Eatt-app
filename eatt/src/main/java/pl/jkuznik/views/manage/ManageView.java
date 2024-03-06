package pl.jkuznik.views.manage;

import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.information.Information;
import pl.jkuznik.data.information.InformationService;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.meal.MealService;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.myOrder.MyOrderService;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@PageTitle("Manage")
@Route(value = "manage", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ManageView extends Div/* Composite<VerticalLayout>*/ {
    private final RestaurantService restaurantService;
    private final InformationService informationService;
    private final MyOrderService myOrderService;
    private final MealService mealService;
    private final Grid<MyOrder> grid = new Grid<>(MyOrder.class, false);
    private final CollaborationBinder<MyOrder> binder;
    private final Button choseButton = new Button("Wybierz");
    private final Button setButton = new Button("Ustaw");
    private final Button order = new Button("Wyślij zamówienie");
    private final Button edit = new Button("Edytuj");
    private final Button add = new Button("Dodaj");
    private final TextField userName =  new TextField("Pracownik");
    private final TextField mealName = new TextField("Danie");
    private final TextField notes = new TextField("Uwagi");
    private final TextField email = new TextField("Kontakt");
    private TextField infoTextField = new TextField();
    private TextField newRestaurant = new TextField();
    private Select select = new Select();
    private String restaurantName;
    private SplitLayout splitLayout = new SplitLayout();
    private Div editorLayoutDiv = new Div();
    private FormLayout formLayout = new FormLayout();
    private Div editorDiv = new Div();
    private Div wrapper = new Div();
    private HorizontalLayout buttonLayout = new HorizontalLayout();
    public ManageView(RestaurantService restaurantService, InformationService informationService, MyOrderService myOrdeService, MealService mealService) {
        this.restaurantService = restaurantService;
        this.informationService = informationService;
        this.myOrderService = myOrdeService;
        this.mealService = mealService;
        FormLayout formLayout2Col = new FormLayout();
        formLayout2Col.getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("30%");

        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");
        binder = new CollaborationBinder<>(MyOrder.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        grid.addColumn("userName").setAutoWidth(true).setHeader("Pracownik");
        grid.addColumn("mealName").setAutoWidth(true).setHeader("Danie");
        grid.addColumn("userEmail").setAutoWidth(true).setHeader("Kontakt");
        grid.addColumn("notes").setAutoWidth(true).setHeader("Uwagi");

        grid.setItems(query -> myOrdeService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream().filter(MyOrder::isActive));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(ManageView.class);
                refreshTextField(event.getValue());
            } else {
                UI.getCurrent().navigate(ManageView.class);
            }
        });

        select.setLabel("Wybierz restauracje do kolejnego zamówienia");
        select.setWidth("min-content");
        select.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        setSelectSampleData();

        Information emptyInfo = new Information();
        Supplier<Information> supplier = () -> emptyInfo;
        emptyInfo.setActive(false);
        emptyInfo.setText("");
        List<Information> informations = informationService.list();
        Information information = informations.stream()
                .filter(Information::isActive)
                .findFirst()
                .orElseGet(supplier);

        String info;

        if (information.getText().isEmpty()) info ="";
        else info = information.getText();

        infoTextField.setLabel("Ustaw informację dnia");
        infoTextField.setHelperText("Brak informacji - ustaw to pole puste");
        infoTextField.setValue(info);
        infoTextField.setClearButtonVisible(true);
        infoTextField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        newRestaurant.setLabel("Dodaj nową restaurację do bazy danych");
        newRestaurant.setValue("");

        formLayout2Col.add(select);
        formLayout2Col.add(choseButton);
        formLayout2Col.add(infoTextField);
        formLayout2Col.add(setButton);
        formLayout2Col.add(newRestaurant);
        formLayout2Col.add(add);
        createGridAndEditorLayout(splitLayout);
        splitLayout.addToSecondary(formLayout2Col);
        add(splitLayout);

        clickChoseListener(choseButton);
        clickSetListener(setButton);
        clickOrderListener(order);
        clickEditListener(edit);
        clickAddListener(add);
    }

    private void createGridAndEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("editor-layout");
        editorDiv.setClassName("editor");
        wrapper.setClassName("grid-wrapper");

        editorLayoutDiv.add(editorDiv);
        formLayout.add(userName, email, mealName, notes);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToPrimary(wrapper, editorLayoutDiv);
        wrapper.add(grid);
    }
    private void refreshTextField(MyOrder myOrder) {
        formLayout.remove(userName, email, mealName, notes);
        editorDiv.remove(formLayout);
        userName.setValue(myOrder.getUserName());
        mealName.setValue(myOrder.getMealName());
        if (!myOrder.getNotes().isEmpty()) notes.setValue(myOrder.getNotes());
        email.setValue(myOrder.getUserEmail());
        formLayout.add(userName, email, mealName, notes);
        editorDiv.add(formLayout);
    }
    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
    private void createButtonLayout(Div editorLayoutDiv) {  // NAPISAĆ TEST
        buttonLayout.setClassName("button-layout");
        order.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        edit.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(order, edit);
        editorLayoutDiv.add(buttonLayout);
    }
    private void setSelectSampleData() {   // NAPISAĆ TEST
        List<Restaurant> restaurants =  restaurantService.list().stream()
                        .filter(restaurant -> !restaurant.getName().equals("<Wszystko>"))
                                .toList();
        select.setItems(restaurants);
        select.setItemLabelGenerator(restaurant -> ((Restaurant) restaurant).getName());
        select.setItemEnabledProvider(item -> Boolean.TRUE.equals(((Restaurant) item).isEnabled()));
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
            List<Restaurant> restaurants = restaurantService.list();
            for (Restaurant restaurant : restaurants){
                if (restaurant.isActive()) restaurant.setActive(false);
            }

            myOrderService.updateAll(myOrders);
            refreshGrid();
            Notification n = Notification.show("Wysłano zamówienie.");
            n.setPosition(Notification.Position.MIDDLE);
        });
    }
    private void clickEditListener(Button button) {
        button.addClickListener( e -> {
            if (mealName.getValue().isEmpty()) return;
            List<MyOrder> myOrders = myOrderService.list();

            for (MyOrder myOrder : myOrders){
                if ((myOrder.getUserName().equals(userName.getValue()) && myOrder.isActive())) {
                    myOrder.setMealName(mealName.getValue());
                    myOrder.setNotes(notes.getValue());
                }
            }

            myOrderService.updateAll(myOrders);
            refreshGrid();
            Notification.show("Zmieniono").setPosition(Notification.Position.BOTTOM_END);
            UI.getCurrent().navigate(ManageView.class);
        });

    }
    public void clickAddListener(Button button){
        button.addClickListener( e -> {
            List<String> restaurantNames = restaurantService.list().stream()
                    .map(Restaurant::getName)
                    .toList();

            if (restaurantNames.contains(newRestaurant.getValue())) {
                Notification n = Notification.show("Istnieje już taka restauracja w bazie danych");
                n.setPosition(Notification.Position.MIDDLE);
                return;
            }

            Restaurant restaurant = new Restaurant();

            restaurant.setName(newRestaurant.getValue());
            restaurant.setEnabled(true);
            restaurant.setActive(false);

            restaurantService.update(restaurant);

            Meal newMeal = new Meal();
            newMeal.setRestaurantActive(true);
            newMeal.setRestaurantName(newRestaurant.getValue());
            newMeal.setMealActive(false);
            mealService.update(newMeal);

            Notification n = Notification.show("Dodano nową restaurację");
            n.setPosition(Notification.Position.MIDDLE);
        });
    }
}
