package pl.jkuznik.views.edit;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.meal.MealService;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.views.MainLayout;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Edit")
@Route(value = "edit-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class EditView extends Div implements BeforeEnterObserver {

    private final String MEAL_ID = "mealID";
    private final String MEAL_EDIT_ROUTE_TEMPLATE = "collaborative-master-detail/%s/edit";

    private final Grid<Meal> grid = new Grid<>(Meal.class, false);
    private final Button cancel = new Button("Anuluj");
    private final Button save = new Button("Zmień");
    private final Button delete = new Button("Usuń");
    private final Button add = new Button("Dodaj");
    private final CollaborationBinder<Meal> binder;
    private final MealService mealService;
    private final RestaurantService restaurantService;
    private CollaborationAvatarGroup avatarGroup;
    private TextField restaurantName;
    private Checkbox isRestaurantActive;
    private TextField name;
    private Checkbox isMealActive;
    private TextField description;
    private TextField allergens;
    private TextField nutritions;
    private SplitLayout splitLayout = new SplitLayout();
    private FormLayout formLayout = new FormLayout();
    private Div editorDiv = new Div();
    private Div editorLayoutDiv = new Div();
    private Meal meal;
    private String currentRestaurantName;
    private String currentMealName;
    private LitRenderer<Meal> restaurantLit;
    private LitRenderer<Meal> mealLit;

    public EditView(MealService mealService, RestaurantService restaurantService) {
        this.mealService = mealService;
        this.restaurantService = restaurantService;
        addClassNames("edit-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");
        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");
        // Create UI

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
        restaurantLit = LitRenderer.<Meal>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", m -> m.isRestaurantActive() ? "check" : "minus").withProperty("color",
                        m -> m.isRestaurantActive()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");
        mealLit = LitRenderer.<Meal>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", m -> m.isMealActive() ? "check" : "minus").withProperty("color",
                        m -> m.isMealActive()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        // Configure Grid
        grid.addColumn("restaurantName").setAutoWidth(true).setHeader("Restauracja");
        grid.addColumn(restaurantLit).setHeader(" ").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true).setHeader("Danie");
        grid.addColumn(mealLit).setHeader(" ").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true).setHeader("Opis");
        grid.addColumn("allergens").setAutoWidth(true).setHeader("Alergeny");
        grid.addColumn("nutritions").setAutoWidth(true).setHeader("Wartości");

        grid.setItems(query -> mealService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream()
                .sorted(Comparator.comparing(Meal::getRestaurantName))
        );
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(EditView.class);
                refreshTextField(event.getValue());
            } else {
                clearForm();
                UI.getCurrent().navigate(EditView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Meal.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        saveClickListener(save);
        cancelCLickListener(cancel);
        addClickListener(add);
        deleteClickListener(delete);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> mealId = event.getRouteParameters().get(MEAL_ID).map(Long::parseLong);
        if (mealId.isPresent()) {
            Optional<Meal> mealFromBackend = mealService.get(mealId.get());
            if (mealFromBackend.isPresent()) {
                populateForm(mealFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested meal was not found, ID = %d", mealId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EditView.class);
            }
        }
    }
    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("editor-layout");

        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        restaurantName = new TextField("Restauracja");
        isRestaurantActive = new Checkbox("Zablokuj/odbloku restauracje");
        name = new TextField("Danie");
        isMealActive = new Checkbox("Zablokuj/odblokuj danie");
        //<theme-editor-local-classname>
        isMealActive.addClassName("edit-view-checkbox-1");
        description = new TextField("Opis");
        allergens = new TextField("Alergeny");
        nutritions = new TextField("Wartości");
        formLayout.add(restaurantName, isRestaurantActive, name, isMealActive, description, allergens, nutritions);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }
    private void refreshTextField(Meal meal) {
        formLayout.remove(restaurantName, isRestaurantActive, name, isMealActive, description, allergens, nutritions);
        editorDiv.remove(formLayout);
        restaurantName.setValue(meal.getRestaurantName());
        isRestaurantActive.setValue(meal.isRestaurantActive());
        currentRestaurantName = meal.getRestaurantName();
        name.setValue(meal.getName());
        isMealActive.setValue(meal.isMealActive());
        currentMealName = meal.getName();
        description.setValue(meal.getDescription());
        allergens.setValue(meal.getAllergens());
        nutritions.setValue(meal.getNutritions());
        formLayout.add(restaurantName, isRestaurantActive, name, isMealActive, description, allergens, nutritions);
        editorDiv.add(formLayout);
    }
    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(add, cancel, save, delete);
        editorLayoutDiv.add(buttonLayout);
    }
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }
    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
    private void clearForm() {
        populateForm(null);
    }
    private void populateForm(Meal value) {
        this.meal = value;
        String topic = null;
        if (this.meal != null && this.meal.getId() != null) {
            topic = "meal/" + this.meal.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.meal);
        avatarGroup.setTopic(topic);

    }
    private void saveClickListener(Button button) {
        button.addClickListener(e -> {
            try {
                if (this.meal == null) {
                    this.meal = new Meal();
                }

                binder.writeBean(this.meal);
                List<Meal> meals = mealService.list();
                for (Meal meal : meals) {

                    if (meal.getName().equals(currentMealName)) {
                        meal.setName(this.meal.getName());
                        meal.setMealActive(isMealActive.getValue());
                        meal.setDescription(this.meal.getDescription());
                        meal.setAllergens(this.meal.getAllergens());
                        meal.setNutritions(this.meal.getNutritions());
                    }
                    if (meal.getRestaurantName().equals(currentRestaurantName)) {
                        meal.setRestaurantName(this.meal.getRestaurantName());
                        meal.setRestaurantActive(isRestaurantActive.getValue());
                    }
                    mealService.update(meal);
                }
                List<Restaurant> restaurants = restaurantService.list();
                List<Restaurant> restaurants1 = restaurants.stream()
                        .filter(rest -> rest.getName().equals(currentRestaurantName))
                        .toList();

                restaurants1.forEach(r -> {
                    r.setName(this.meal.getRestaurantName());
                    r.setEnabled(isRestaurantActive.getValue());
                    if (!isRestaurantActive.getValue()) r.setActive(false);
                });
                restaurantService.updateAll(restaurants1);

                currentRestaurantName = "";
                currentMealName = "";
                clearForm();
                refreshGrid();
                Notification.show("Zmieniono").setPosition(Position.BOTTOM_END);
                UI.getCurrent().navigate(EditView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }
    private void cancelCLickListener(Button button) {
        button.addClickListener(e -> {
            currentRestaurantName = "";
            currentMealName = "";
            clearForm();
            refreshGrid();
        });
    }
    private void addClickListener(Button button){
        button.addClickListener(e -> {
            try {
                if (this.meal == null) {
                    this.meal = new Meal();
                }

                binder.writeBean(this.meal);
                List<Restaurant> restaurants = restaurantService.list();
                Optional<Restaurant> anyRestaurant = restaurants.stream()
                        .filter(r -> r.getName().equals(this.meal.getRestaurantName()))
                        .filter(r -> !r.getName().equals("<Wszystko>"))
                        .findAny();

                if ( anyRestaurant.isEmpty() ){
                    Notification.show("Wpisz poprawnie nazwę restauracji do której chcesz dodać nowe danie").setPosition(Position.BOTTOM_END);
                    UI.getCurrent().navigate(EditView.class);
                    return;
                }
                if (this.meal.getName().isEmpty()){
                    Notification.show("Podaj nazwę dania które chcesz dodać").setPosition(Position.BOTTOM_END);
                    UI.getCurrent().navigate(EditView.class);
                    return;
                }

                List<Meal> meals = mealService.list();
                Optional<Meal> anyMeal = meals.stream()
                        .filter(m -> m.getRestaurantName().equals(this.meal.getRestaurantName()) && m.getName().equals(this.meal.getName()))
                        .findAny();

                if (anyMeal.isPresent()) {
                    Notification.show("Istnieje już danie o tej samej nazwie dla tej restauracji").setPosition(Position.BOTTOM_END);
                    UI.getCurrent().navigate(EditView.class);
                    return;
                }

                Meal newMeal = new Meal();
                newMeal.setRestaurantName(this.meal.getRestaurantName());
                newMeal.setRestaurantActive(true);
                newMeal.setName(this.meal.getName());
                newMeal.setMealActive(true);
                newMeal.setDescription(this.meal.getDescription());
                newMeal.setAllergens(this.meal.getAllergens());
                newMeal.setNutritions(this.meal.getNutritions());

                meals.add(newMeal);
                mealService.update(newMeal);
                mealService.updateAll(meals);

                currentRestaurantName = "";
                currentMealName = "";
                clearForm();
                refreshGrid();
                Notification.show("Dodano nowe danie").setPosition(Position.BOTTOM_END);
                UI.getCurrent().navigate(EditView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }
    private void deleteClickListener(Button button){
        button.addClickListener(e -> {
            try {
                if (this.meal == null) {
                    this.meal = new Meal();
                }

                binder.writeBean(this.meal);
                List<Meal> meals = mealService.list();

                for(Meal m : meals){
                    if (m.getRestaurantName().equals(this.meal.getRestaurantName()) &&
                    m.getName().equals(this.meal.getName())) mealService.delete(m.getId());
                }

                clearForm();
                refreshGrid();
                Notification.show("Usunięto danie " + currentMealName).setPosition(Position.BOTTOM_END);
                UI.getCurrent().navigate(EditView.class);
                currentRestaurantName = "";
                currentMealName = "";
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }
}
