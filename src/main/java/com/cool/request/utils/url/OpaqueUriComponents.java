package com.cool.request.utils.url;


import com.cool.request.utils.LinkedMultiValueMap;
import com.cool.request.utils.ObjectUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

@SuppressWarnings("serial")
final class OpaqueUriComponents extends UriComponents {

    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap<>();


    private final String ssp;


    OpaqueUriComponents(String scheme, String schemeSpecificPart, String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }


    @Override

    public String getSchemeSpecificPart() {
        return this.ssp;
    }

    @Override

    public String getUserInfo() {
        return null;
    }

    @Override

    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return -1;
    }

    @Override

    public String getPath() {
        return null;
    }

    @Override

    public String getQuery() {
        return null;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return QUERY_PARAMS_NONE;
    }

    @Override
    public UriComponents encode(Charset charset) {
        return this;
    }

    @Override
    protected UriComponents expandInternal(UriTemplateVariables uriVariables) {
        String expandedScheme = expandUriComponent(getScheme(), uriVariables);
        String expandedSsp = expandUriComponent(getSchemeSpecificPart(), uriVariables);
        String expandedFragment = expandUriComponent(getFragment(), uriVariables);
        return new OpaqueUriComponents(expandedScheme, expandedSsp, expandedFragment);
    }


    @Override
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();

        if (getScheme() != null) {
            uriBuilder.append(getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(getFragment());
        }

        return uriBuilder.toString();
    }

    @Override
    public URI toUri() {
        try {
            return new URI(getScheme(), this.ssp, getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OpaqueUriComponents)) {
            return false;
        }
        OpaqueUriComponents otherComp = (OpaqueUriComponents) other;
        return (ObjectUtils.nullSafeEquals(getScheme(), otherComp.getScheme()) &&
                ObjectUtils.nullSafeEquals(this.ssp, otherComp.ssp) &&
                ObjectUtils.nullSafeEquals(getFragment(), otherComp.getFragment()));
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.ssp);
        result = 31 * result + ObjectUtils.nullSafeHashCode(getFragment());
        return result;
    }

}
