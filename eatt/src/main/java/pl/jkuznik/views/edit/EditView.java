package pl.jkuznik.views.edit;

import jakarta.annotation.security.RolesAllowed;
import pl.jkuznik.data.samplePerson.SamplePerson;
import pl.jkuznik.data.meal.Meal;
import pl.jkuznik.data.meal.MealService;
import pl.jkuznik.data.restaurant.RestaurantService;
import pl.jkuznik.data.samplePerson.SamplePersonService;
import pl.jkuznik.views.MainLayout;

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
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Edit")
@Route(value = "collaborative-master-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class EditView extends Div implements BeforeEnterObserver {

    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "collaborative-master-detail/%s/edit";

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField moRestaurantName;
    private TextField moMealName;
    private TextField meDescription;
    private TextField meAllergens;
    private TextField meNutritions;

    private final Button cancel = new Button("Anuluj");
    private final Button save = new Button("Zapisz");
    private final Button edit = new Button("Edytuj");

    private final CollaborationBinder<SamplePerson> binder;

    private SamplePerson samplePerson;

    private final SamplePersonService samplePersonService;
    private final MealService mealService;
    private final RestaurantService restaurantService;

    public EditView(SamplePersonService samplePersonService, MealService mealService, RestaurantService restaurantService) {
        this.samplePersonService = samplePersonService;
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

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        Meal meal = new Meal();

        // Configure Grid
        grid.addColumn("moRestaurantName").setAutoWidth(true).setHeader("Restauracja");
        grid.addColumn("moMealName").setAutoWidth(true).setHeader("Danie");
        grid.addColumn("meDescription").setAutoWidth(true).setHeader("Opis");
        grid.addColumn("meAllergens").setAutoWidth(true).setHeader("Alergeny");
        grid.addColumn("meNutritions").setAutoWidth(true).setHeader("Wartości");

        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream()
                .filter( sp -> !sp.getMoRestaurantName().isBlank())
        );
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EditView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(SamplePerson.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            LocalDate localDate = LocalDate.of(1000, 01, 01);
            try {
                if (this.samplePerson == null) {
                    this.samplePerson = new SamplePerson();
                }
                binder.writeBean(this.samplePerson);
                samplePerson.setUserName(" ");
                samplePerson.setUserEmail("a@a.aa");
                samplePerson.setMoComment(" ");
                samplePerson.setMoRating(6);
                samplePerson.setOrderDate(localDate);
                samplePersonService.update(this.samplePerson);
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %d", samplePersonId.get()), 3000,
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
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        moRestaurantName = new TextField("Restauracja");
        moMealName = new TextField("Danie");
        meDescription = new TextField("Opis");
        meAllergens = new TextField("Alergeny");
        meNutritions = new TextField("Wartości");
        formLayout.add(moRestaurantName, moMealName, meDescription, meAllergens, meNutritions);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        edit.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel, edit);
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

    private void populateForm(SamplePerson value) {
        this.samplePerson = value;
        String topic = null;
        if (this.samplePerson != null && this.samplePerson.getId() != null) {
            topic = "samplePerson/" + this.samplePerson.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.samplePerson);
        avatarGroup.setTopic(topic);

    }
}
