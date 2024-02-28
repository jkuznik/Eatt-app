package pl.jkuznik.views.edit;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.services.MealService;
import pl.jkuznik.views.MainLayout;
import pl.jkuznik.views.myorder.MyOrderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Edit")
@Route(value = "edit/:dishesID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class EditView extends Div implements BeforeEnterObserver {

    private final String MEAL_ID = "mealID";
    private final String MEAL_EDIT_ROUTE_TEMPLATE = "edit/%s/edit";

    private final Grid<Meal> grid = new Grid<>(Meal.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField name;
    private TextField description;
    private TextField allergens;
    private TextField nutritions;
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Meal> binder;

    private Meal meal;

    private final MealService mealService;

    public EditView(MealService mealService) {
        this.mealService = mealService;
        addClassNames("edit-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
        // Configure Grid
        grid.addColumn("name").setAutoWidth(true).setHeader("Potrawa");
        grid.addColumn("description").setAutoWidth(true).setHeader("Opis");
        grid.addColumn("allergens").setAutoWidth(true).setHeader("Alergeny");
        grid.addColumn("nutritions").setAutoWidth(true).setHeader("Wartości");

        grid.setItems(query -> mealService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MEAL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EditView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Meal.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.meal == null) {
                    this.meal = new Meal();
                }
                binder.writeBean(this.meal);
                mealService.update(this.meal);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> mealId = event.getRouteParameters().get(MEAL_ID).map(Long::parseLong);
        if (mealId.isPresent()) {
            Optional<Meal> mealFromBackend = mealService.get(mealId.get());
            if (mealFromBackend.isPresent()) {
                populateForm(mealFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested dishes was not found, ID = %d", mealId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EditView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {

        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        Select select = new Select();
        Button chose = new Button("Wybierz");
        select.setLabel("Wybierz restaurację");
        select.setWidth("min-width");
        setSelectSampleData(select);

        editorDiv.setClassName("editor");
        editorLayoutDiv.add(select);
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Potrawa");
        description = new TextField("Opis");
        allergens = new TextField("Alergeny");
        nutritions = new TextField("Wartości");
        formLayout.add(name, description, allergens, nutritions);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
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
    private void setSelectSampleData(Select select) {
        List<MyOrderView.SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new MyOrderView.SampleItem("first", "First", null));
        sampleItems.add(new MyOrderView.SampleItem("second", "Second", null));
        sampleItems.add(new MyOrderView.SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new MyOrderView.SampleItem("fourth", "Fourth", null));
        select.setItems(sampleItems);
        select.setItemLabelGenerator(item -> ((MyOrderView.SampleItem) item).label());
        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((MyOrderView.SampleItem) item).disabled()));
    }
}
