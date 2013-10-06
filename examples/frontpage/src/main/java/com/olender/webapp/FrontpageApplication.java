/**
 *
 */
package com.olender.webapp;

import com.olender.webapp.admin.auth.AuthConfig;
import com.olender.webapp.admin.auth.SignInPage;
import com.olender.webapp.admin.pages.BackupPage;
import com.olender.webapp.admin.pages.ImagesPage;
import com.olender.webapp.admin.pages.MainPage;
import com.olender.webapp.admin.pages.SectionsPage;
import com.olender.webapp.admin.pages.sections.ContactSectionDetailsPage;
import com.olender.webapp.admin.pages.sections.CustomersSectionDetailsPage;
import com.olender.webapp.admin.pages.sections.GallerySectionDetailsPage;
import com.olender.webapp.admin.pages.sections.TextSectionDetailsPage;
import com.olender.webapp.decorators.ContactSectionDecorator;
import com.olender.webapp.decorators.CustomersSectionDecorator;
import com.olender.webapp.decorators.GallerySectionDecorator;
import com.olender.webapp.decorators.TextSectionDecorator;
import com.olender.webapp.pages.FrontPage;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import pl.doa.entity.IEntity;
import pl.doa.wicket.WicketDOAApplication;
import pl.doa.wicket.auth.IAuthConfig;
import pl.doa.wicket.ui.page.EntityPage;

/**
 * @author activey
 */
public class FrontpageApplication extends WicketDOAApplication {

    public static FrontpageApplication get() {
        WebApplication application = WebApplication.get();
        if (application instanceof FrontpageApplication == false) {
            throw new WicketRuntimeException(
                    "The application attached to the current thread is not a "
                            + FrontpageApplication.class.getSimpleName());
        }
        return (FrontpageApplication) application;
    }

    /* (non-Javadoc)
     * @see pl.doa.wicket.WicketDOAApplication#getStartingPage()
     */
    @Override
    protected Class<? extends EntityPage<? extends IEntity>> getStartingPage() {
        return FrontPage.class;
    }

    @Override
    protected void initDOAApplication() throws Exception {
        mountPage("/admin/login", SignInPage.class);

        mountPage("/admin/main", MainPage.class);
        mountPage("/admin/sections", SectionsPage.class);
        mountContainer("/admin/images", ImagesPage.class, "/images/application");
        mountPage("/admin/backup", BackupPage.class);

        mountContainer("/admin/sections/text", TextSectionDetailsPage.class,
                "/documents/application/sections/text");
        mountContainer("/admin/sections/gallery",
                GallerySectionDetailsPage.class,
                "/documents/application/sections/gallery");
        mountContainer("/admin/sections/customers",
                CustomersSectionDetailsPage.class,
                "/documents/application/sections/customers");
        mountContainer("/admin/sections/contact", ContactSectionDetailsPage.class,
                "/documents/application/sections/contact");

        registerDecorator("/definitions/sections/application/section_definition_text", TextSectionDecorator.class);
        registerDecorator("/definitions/sections/application/section_definition_gallery", GallerySectionDecorator.class);
        registerDecorator("/definitions/sections/application/section_definition_customer", CustomersSectionDecorator.class);
        registerDecorator("/definitions/sections/application/section_definition_contact", ContactSectionDecorator.class);

        mountResourcesContainer("/images/gallery", "/images/application");
        mountResourcesContainer("/admin/resources", "/admin");
        mountResourcesContainer("/common/resources", "/common");

        getDebugSettings().setAjaxDebugModeEnabled(false);
    }

    @Override
    public IAuthConfig getAuthConfig() {
        return new AuthConfig();
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }
}
