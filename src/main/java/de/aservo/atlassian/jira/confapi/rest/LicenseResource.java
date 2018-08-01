package de.aservo.atlassian.jira.confapi.rest;

import com.atlassian.jira.license.LicenseDetails;
import com.atlassian.jira.rest.v2.issue.RESTException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import de.aservo.atlassian.jira.confapi.JiraApplicationHelper;
import de.aservo.atlassian.jira.confapi.JiraWebAuthenticationHelper;
import de.aservo.atlassian.jira.confapi.bean.LicenseBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * License resource to set a license.
 */
@Path("/license")
@AnonymousAllowed
@Produces(MediaType.APPLICATION_JSON)
@Component
public class LicenseResource {

    private final JiraApplicationHelper applicationHelper;

    private final JiraWebAuthenticationHelper webAuthenticationHelper;

    /**
     * Constructor.
     *
     * @param applicationHelper       the injected {@link JiraApplicationHelper}
     * @param webAuthenticationHelper the injected {@link JiraWebAuthenticationHelper}
     */
    @Inject
    public LicenseResource(
            final JiraApplicationHelper applicationHelper,
            final JiraWebAuthenticationHelper webAuthenticationHelper) {

        this.applicationHelper = applicationHelper;
        this.webAuthenticationHelper = webAuthenticationHelper;
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setLicense(
            final String licenseKey) {

        webAuthenticationHelper.mustBeSysAdmin();

        if (licenseKey == null) {
            throw new RESTException(Response.Status.BAD_REQUEST, "No key given");
        }

        final LicenseDetails licenseDetail = applicationHelper.setLicense(licenseKey);
        return Response.ok(LicenseBean.from(licenseDetail)).build();
    }

}
