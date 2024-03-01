package pl.jkuznik.views.myorder;

import pl.jkuznik.data.samplePerson.SamplePerson;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.myOrder.MyOrder;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.user.User;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.data.meal.MealService;
import pl.jkuznik.data.myOrder.MyOrderService;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.data.samplePerson.SamplePersonService;
import pl.jkuznik.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;


@PageTitle("My Order")
@Route(value = "myorder", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MyOrderView extends Composite<VerticalLayout>  {
    private final RestaurantService restaurantService;
    private final MyOrderService myOrderService;
    private final MealService mealService;
    private final AuthenticatedUser authenticatedUser;

    public MyOrderView(RestaurantService restaurantService, MyOrderService myOrderService, MealService mealService, AuthenticatedUser authenticatedUser) {
        this.restaurantService = restaurantService;
        this.myOrderService = myOrderService;
        this.mealService = mealService;
        this.authenticatedUser = authenticatedUser;

        User loggedUser = getLoggedUser();
        String currentMyOrder;

        H1 h1 = new H1();
        H2 h2 = new H2();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Select select = new Select();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Button buttonSecondary = new Button();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Grid basicGrid = new Grid(SamplePerson.class);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h1.setText(getRestaurant().getName());
        h1.setWidth("max-content");

        if (getMyOrder(loggedUser) != null) currentMyOrder = getMyOrder(loggedUser).getMealName();
        else currentMyOrder = "Nie wybrałeś jeszcze potrawy";

        h2.setText(currentMyOrder);
        h2.setWidth("max-content");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.setAlignItems(Alignment.START);
        layoutRow.setJustifyContentMode(JustifyContentMode.START);
        select.setLabel("Historia zamówień");
        select.setWidth("min-content");
        setSelectSampleData(select);
        layoutColumn2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("80%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn2.setAlignItems(Alignment.START);
        buttonSecondary.setText("Wybierz");
        buttonSecondary.setWidth("min-content");
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        basicGrid.setWidth("80%");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid);
        getContent().add(h1);
        getContent().add(h2);
        getContent().add(layoutRow);
        layoutRow.add(select);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(buttonSecondary);
        getContent().add(layoutRow2);
        layoutRow2.add(basicGrid);
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

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

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private Restaurant getRestaurant() { // NAPISAĆ TEST
        List<Restaurant> restaurants = restaurantService.list();
        Optional<Restaurant> restaurantOptional = restaurants.stream()
                .filter(Restaurant::isActive)
                .findFirst();
        return restaurantOptional.orElse(null);
    }
    private List<String> getMeals(Restaurant restaurant) {
        List<Meal> meals = mealService.list();
        List<String> mealsNames = new ArrayList<>();
        if (restaurant == null) return mealsNames;

        List<Meal> mealList = meals.stream()
                .filter(d -> d.getRestaurantId() == restaurant.getId())
                .toList();
        for (Meal d : mealList) {
            mealsNames.add(d.getName());
        }
        return mealsNames;
    }
    private List<MyOrder> getActualMyOrders(){
        return myOrderService.list();
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

    @Autowired()
    private SamplePersonService samplePersonService;
}
