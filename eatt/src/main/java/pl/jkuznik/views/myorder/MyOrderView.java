package pl.jkuznik.views.myorder;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.myOrder.MyOrderService;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.data.user.User;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.views.MainLayout;

import java.util.List;
import java.util.Optional;


@PageTitle("My Order")
@Route(value = "myorder", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MyOrderView extends Composite<VerticalLayout>  {
    private final RestaurantService restaurantService;
    private final MyOrderService myOrderService;
    private final AuthenticatedUser authenticatedUser;
    private HorizontalLayout layoutRow = new HorizontalLayout();
    private VerticalLayout layoutColumn2 = new VerticalLayout();
    private HorizontalLayout layoutRow2 = new HorizontalLayout();
    private VerticalLayout horizontalLayout = new VerticalLayout();
    private HorizontalLayout commentLayout = new HorizontalLayout();
    private HorizontalLayout rateLayout = new HorizontalLayout();
    private TextField comment = new TextField();
    private Button addComment = new Button("Dodaj");
    private Grid<MyOrder> grid = new Grid(MyOrder.class, false);
    private User loggedUser = new User();
    private Select rateSelect = new Select();
    private Button addRate = new Button("Oceń");

    private Select select = new Select();
    private MyOrder myOrder = new MyOrder();

    public MyOrderView(RestaurantService restaurantService, MyOrderService myOrderService, AuthenticatedUser authenticatedUser) {
        this.restaurantService = restaurantService;
        this.myOrderService = myOrderService;
        this.authenticatedUser = authenticatedUser;
        String currentMyOrder;
        loggedUser = getLoggedUser();

        H1 h1 = new H1();
        H3 h3 = new H3();
        H4 h4 = new H4();

        h1.setWidth("max-content");
        h1.addClassName("my-order-view-h1-1");
        h3.setWidth("max-content");

        if (getRestaurant() == null) h1.setText("Wybierz coś dla siebie");
        else h1.setText(getRestaurant().getName());

        if (getMyOrder(loggedUser) != null) currentMyOrder = "Czekasz na zamówienie -  " + getMyOrder(loggedUser).getMealName() +
                                                             ",         uwagi : " + getMyOrder(loggedUser).getNotes();
        else currentMyOrder = "Nie wybrano potrawy. Nie zastanawiaj się długo, kto się spóźni - ten nie je.";

        h3.setText(currentMyOrder);
        h4.setText("Historia zrealizowanych zamówień");

        grid.setWidth("80%");
        grid.getStyle().set("flex-grow", "0");
        setGridSampleData(grid);

        layoutRow.setWidthFull();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.setAlignItems(Alignment.START);
        layoutRow.setJustifyContentMode(JustifyContentMode.START);
        layoutRow.setFlexGrow(1.0, layoutColumn2);

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setFlexGrow(1.0, layoutRow);

        layoutColumn2.setHeightFull();
        layoutColumn2.setWidth("80%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn2.setAlignItems(Alignment.START);
        layoutRow2.setWidthFull();
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        getContent().setFlexGrow(1.0, layoutRow2);

//        select.setLabel("Historia zamówień");
//        select.setWidth("min-content");
//        setSelectSampleData(select);
/** Tutaj było dodawanie selectu z której restauracji historię chcesz wyświetlić*/
//        Button buttonSecondary = new Button();
//        buttonSecondary.setText("Wybierz");
//        buttonSecondary.setWidth("min-content");

        getContent().add(h1);
        getContent().add(h3);
        getContent().add(layoutRow);
//        layoutRow.add(select);
//        layoutRow.add(layoutColumn2);
//        layoutColumn2.add(buttonSecondary);
        getContent().add(h4, layoutRow2);
        comment.setHelperText("Dodaj komentarz");
        rateSelect.setHelperText("Oceń");
        setRateSelectData(rateSelect);

        commentLayout.add(comment ,addComment);
        rateLayout.add(rateSelect, addRate);
        horizontalLayout.add(commentLayout, rateLayout);
        horizontalLayout.setWidth("300px");
        layoutRow2.add(grid, horizontalLayout);

        addCommentClickListener(addComment);    // dopisać obsługę beforeClick bo leci nullpointer
        addRateClickListener(addRate);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                setOrderToComment(event.getValue());
            }
        });

    }
    private void setOrderToComment(MyOrder event){
        myOrder = event;
    }
    private void setSelectSampleData(Select select) {
        List<Restaurant> restaurants = restaurantService.list();

        select.setItems(restaurants);
        select.setItemLabelGenerator(item -> ((Restaurant) item).getName());
//        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((SampleItem) item).disabled()));
    }
    private void setRateSelectData(Select rateSelect){
        List<Integer> rates = List.of(1,2,3,4,5);
        rateSelect.setItems(rates);
        rateSelect.setItemLabelGenerator(Object::toString);

    }
    private void setGridSampleData(Grid grid) {
        grid.addColumn("restaurantName").setAutoWidth(true).setHeader("Restauracja");
        grid.addColumn("mealName").setAutoWidth(true).setHeader("Danie");
        grid.addColumn("comment").setAutoWidth(true).setHeader("Komentarz");
        grid.addColumn("rating").setAutoWidth(true).setHeader("Ocena");

        grid.setItems(query -> myOrderService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream()
                        .filter( order -> order.getUserName().equals(loggedUser.getName()))
                        .filter( order -> !order.isActive())
                /*.filter(myOrder -> myOrder.getRestaurantName().equals(select.getLabel()))*/);
    }
    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
    private void addCommentClickListener(Button button){
        button.addClickListener(e -> {
            try {
                if (myOrder == null){   // tu zmienić bo nie spełnia funkcji
                    return;
                }
                myOrder.setComment(comment.getValue());
                myOrderService.update(myOrder);
                refreshGrid();
                Notification n = Notification.show("Dodano komentarz");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "ObjectOptimisticLockingFailureException podczas dodawania komentarza w my_order do rekodu " + myOrder.getId());
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException illegalStateException) {
                Notification.show("IllegalStateException podczas dodawania komentarza w my_order do rekodu " + myOrder.getId());
            }
        });
    }
    private void addRateClickListener(Button button){
        button.addClickListener(e -> {
            try {
                if (myOrder == null){
                    return;
                }
                Object value = rateSelect.getValue();
                myOrder.setRating( (Integer) value);
                myOrderService.update(myOrder);
                refreshGrid();
                Notification n = Notification.show("Dodano ocene " + value);
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "ObjectOptimisticLockingFailureException podczas dodawania komentarza w my_order do rekodu " + myOrder.getId());
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException illegalStateException) {
                Notification.show("IllegalStateException podczas dodawania komentarza w my_order do rekodu " + myOrder.getId());
            }
        });
    }
    private Restaurant getRestaurant() { // NAPISAĆ TEST
        List<Restaurant> restaurants = restaurantService.list();
        Optional<Restaurant> restaurantOptional = restaurants.stream()
                .filter(Restaurant::isActive)
                .findFirst();
        return restaurantOptional.orElse(null);
    }
    private MyOrder getMyOrder(User user) {
        List<MyOrder> myOrders = myOrderService.list();
        Optional<MyOrder> activeOrder = myOrders.stream()
                .filter(order -> order.getUserName().equals(user.getName()) && order.isActive())
                .findFirst();
        return activeOrder.orElse(null);
    }
    private User getLoggedUser(){
        Optional<User> loggedUser = authenticatedUser.get();
        return loggedUser.get();
    }

}
