package pl.jkuznik.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import pl.jkuznik.data.user.User;
import pl.jkuznik.security.AuthenticatedUser;
import pl.jkuznik.views.easyAccessToTaste.EasyAccessToTasteView;
//import pl.jkuznik.views.edit.EditView;
import pl.jkuznik.views.edit.*;
import pl.jkuznik.views.manage.ManageView;
import pl.jkuznik.views.menu.MenuView;
import pl.jkuznik.views.myorder.MyOrderView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Eatt");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        //<theme-editor-local-classname>
        header.addClassName("main-layout-header-1");

        Image image = new Image();
        try {
            String filePath = "src/main/resources/img1.png";

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);

            StreamResource resource = new StreamResource("img1.png", () -> fis);

            image = new Image(resource, "alt text");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, image, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(EasyAccessToTasteView.class)) {
            nav.addItem(new SideNavItem("Easy Access to Taste", EasyAccessToTasteView.class,
                    LineAwesomeIcon.APPLE_ALT_SOLID.create()));

        }
        if (accessChecker.hasAccess(MenuView.class)) {
            nav.addItem(new SideNavItem("Menu", MenuView.class, LineAwesomeIcon.ALIGN_LEFT_SOLID.create()));

        }
        if (accessChecker.hasAccess(MyOrderView.class)) {
            nav.addItem(new SideNavItem("My Order", MyOrderView.class, LineAwesomeIcon.MORTAR_PESTLE_SOLID.create()));

        }
        if (accessChecker.hasAccess(ManageView.class)) {
            nav.addItem(new SideNavItem("Manage", ManageView.class, LineAwesomeIcon.TOOLS_SOLID.create()));

        }
        if (accessChecker.hasAccess(EditView.class)) {
            nav.addItem(new SideNavItem("Edit", EditView.class, LineAwesomeIcon.TOOLS_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Wyloguj", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Zaloguj");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
