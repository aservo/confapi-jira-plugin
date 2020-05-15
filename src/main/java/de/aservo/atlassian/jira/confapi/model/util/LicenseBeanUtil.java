package de.aservo.atlassian.jira.confapi.model.util;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.atlassian.confapi.model.LicenseBean;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LicenseBeanUtil {

    /**
     * Instantiates a new License bean.
     *
     * @param productLicense the product license
     */
    @Nonnull
    public static LicenseBean toLicenseBean(
            @Nonnull final SingleProductLicenseDetailsView productLicense) {

        final LicenseBean licenseBean = new LicenseBean();
        licenseBean.setProducts(Collections.singletonList(productLicense.getProductDisplayName()));
        licenseBean.setLicenseType(productLicense.getLicenseTypeName());
        licenseBean.setOrganization(productLicense.getOrganisationName());
        licenseBean.setDescription(productLicense.getDescription());
        licenseBean.setExpiryDate(productLicense.getMaintenanceExpiryDate());
        licenseBean.setNumUsers(productLicense.getNumberOfUsers());
        return licenseBean;
    }

    private LicenseBeanUtil() {
    }

}
