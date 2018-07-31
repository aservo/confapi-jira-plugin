package de.aservo.atlassian.jira.confapi.bean;

import com.atlassian.application.api.ApplicationKey;
import com.atlassian.jira.license.LicenseDetails;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Bean for {@link com.atlassian.jira.license.LicenseDetails} results in REST responses
 */
@XmlRootElement(name = "license")
public class LicenseBean {

    @XmlElement
    private final String key;

    @XmlElement
    private final Collection<String> applicationKeys;

    private LicenseBean(
            final String key,
            final Collection<String> applicationKeys) {

        this.key = key;
        this.applicationKeys = applicationKeys;
    }

    public String getKey() {
        return key;
    }

    public Collection<ApplicationKey> getApplicationKeys() {
        return applicationKeys.stream()
                .map(ApplicationKey::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * Factory method for creating a bean from {@link LicenseDetails}.
     *
     * @param licenseDetails the license details
     */
    public static LicenseBean from(
            final LicenseDetails licenseDetails) {

        final String key = licenseDetails.getLicenseString();
        final Collection<String> applicationKeys = licenseDetails.getLicensedApplications().getKeys().stream()
                .map(ApplicationKey::value)
                .collect(Collectors.toSet());

        return new LicenseBean(key, applicationKeys);
    }

}