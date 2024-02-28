package pl.jkuznik.views.myorder;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

import pl.jkuznik.components.avataritem.AvatarItem;
import pl.jkuznik.data.restaurant.Restaurant;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.services.MyOrderService;
import pl.jkuznik.services.RestaurantService;
import pl.jkuznik.views.MainLayout;

@PageTitle("My Order")
@Route(value = "my-order", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MyOrderView extends Composite<VerticalLayout> {

    private final AuthenticatedUser authenticatedUser;
    private final RestaurantService restaurantService;
    private final MyOrderService myOrderService;
    public MyOrderView(RestaurantService restaurantService, MyOrderService myOrderService, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.restaurantService = restaurantService;
        this.myOrderService = myOrderService;

        AvatarItem avatarItem = new AvatarItem();
        Hr hr = new Hr();
        Paragraph textSmall = new Paragraph();
        Select select = new Select();
        MultiSelectListBox avatarItems = new MultiSelectListBox();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        avatarItem.setWidth("min-content");
        setAvatarItemSampleData(avatarItem);
        textSmall.setText(
                "feedback");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        select.setLabel("Restauracja");
        select.setWidth("min-content");
        setSelectSampleData(select);
        avatarItems.setWidth("min-content");
        setAvatarItemsSampleData(avatarItems);
        getContent().add(avatarItem);
        getContent().add(hr);
        getContent().add(textSmall);
        getContent().add(select);
        getContent().add(avatarItems);
    }

    private void setAvatarItemSampleData(AvatarItem avatarItem) {
        avatarItem.setHeading("Aria Bailey");
        avatarItem.setDescription("Endocrinologist");
        avatarItem.setAvatar(new Avatar("Aria Bailey"));
    }

    public record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setSelectSampleData(Select select) {
        List<Restaurant> restaurants = restaurantService.list();
        select.setItems(restaurants);
        select.setItemLabelGenerator(restaurant -> ((Restaurant) restaurant).getName());
//        select.setItemEnabledProvider(item -> !Boolean.TRUE.equals(((SampleItem) item).disabled())); // TUTAJ DOPISAĆ KOD JEŚLI UŻYTOWNIK NIE KORZYSTAŁ Z RESTAURACJI TO NIE MOŻE JEJ WYBRAĆ
    }

    private void setAvatarItemsSampleData(MultiSelectListBox multiSelectListBox) {
        record Person(String name, String profession) {
        }
        ;
        List<Person> data = List.of(new Person("Aria Bailey", "Endocrinologist"), new Person("Aaliyah Butler", "Nephrologist"), new Person("Eleanor Price", "Ophthalmologist"), new Person("Allison Torres", "Allergist"), new Person("Madeline Lewis", "Gastroenterologist"));
        multiSelectListBox.setItems(data);
        multiSelectListBox.setRenderer(new ComponentRenderer(item -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(((Person) item).name);
            avatarItem.setDescription(((Person) item).profession);
            avatarItem.setAvatar(new Avatar(((Person) item).name));
            return avatarItem;
        }));
    }
//    private Restaurant getRestaurant
}
