/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UriComponentsBuilder.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils.url;

import com.cool.request.utils.*;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UriComponentsBuilder implements UriBuilder, Cloneable {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    private static final String SCHEME_PATTERN = "([^:/?#]+):";

    private static final String HTTP_PATTERN = "(?i)(http|https):";

    private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";

    private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";

    private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}:.]*[%\\p{Alnum}]*]";

    private static final String HOST_PATTERN = "(" + HOST_IPV6_PATTERN + "|" + HOST_IPV4_PATTERN + ")";

    private static final String PORT_PATTERN = "(\\{[^}]+\\}?|[^/?#]*)";

    private static final String PATH_PATTERN = "([^?#]*)";

    private static final String QUERY_PATTERN = "([^#]*)";

    private static final String LAST_PATTERN = "(.*)";

    // Regex patterns that matches URIs. See RFC 3986, appendix B
    private static final Pattern URI_PATTERN = Pattern.compile(
            "^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
                    ")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

    private static final Pattern HTTP_URL_PATTERN = Pattern.compile(
            "^" + HTTP_PATTERN + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" +
                    PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");


    private static final Object[] EMPTY_VALUES = new Object[0];


    private String scheme;


    private String ssp;


    private String userInfo;


    private String host;


    private String port;

    private CompositePathComponentBuilder pathBuilder;

    private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


    private String fragment;

    private final Map<String, Object> uriVariables = new HashMap<>(4);

    private boolean encodeTemplate;

    private Charset charset = StandardCharsets.UTF_8;

    protected UriComponentsBuilder() {
        this.pathBuilder = new CompositePathComponentBuilder();
    }

    /**
     * Create a deep copy of the given UriComponentsBuilder.
     *
     * @param other the other builder to copy from
     * @since 4.1.3
     */
    protected UriComponentsBuilder(UriComponentsBuilder other) {
        this.scheme = other.scheme;
        this.ssp = other.ssp;
        this.userInfo = other.userInfo;
        this.host = other.host;
        this.port = other.port;
        this.pathBuilder = other.pathBuilder.cloneBuilder();
        this.uriVariables.putAll(other.uriVariables);
        this.queryParams.addAll(other.queryParams);
        this.fragment = other.fragment;
        this.encodeTemplate = other.encodeTemplate;
        this.charset = other.charset;
    }
    public static UriComponentsBuilder fromHttpUrl(String httpUrl) {
        Assert.notNull(httpUrl, "HTTP URL must not be null");
        Matcher matcher = HTTP_URL_PATTERN.matcher(httpUrl);
        if (matcher.matches()) {
            UriComponentsBuilder builder = new UriComponentsBuilder();
            String scheme = matcher.group(1);
            builder.scheme(scheme != null ? scheme.toLowerCase() : null);
            builder.userInfo(matcher.group(4));
            String host = matcher.group(5);
            if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
                throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
            }
            builder.host(host);
            String port = matcher.group(7);
            if (StringUtils.hasLength(port)) {
                builder.port(port);
            }
            builder.path(matcher.group(8));
            builder.query(matcher.group(10));
            String fragment = matcher.group(12);
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        } else {
            throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
        }
    }


    public final UriComponentsBuilder encode() {
        return encode(StandardCharsets.UTF_8);
    }

    /**
     * A variant of {@link #encode()} with a charset other than "UTF-8".
     *
     * @param charset the charset to use for encoding
     * @since 5.0.8
     */
    public UriComponentsBuilder encode(Charset charset) {
        this.encodeTemplate = true;
        this.charset = charset;
        return this;
    }


    // Build methods

    /**
     * Build a {@code UriComponents} instance from the various components contained in this builder.
     *
     * @return the URI components
     */
    public UriComponents build() {
        return build(false);
    }

    /**
     * Variant of {@link #build()} to create a {@link UriComponents} instance
     * when components are already fully encoded. This is useful for example if
     *
     * @param encoded whether the components in this builder are already encoded
     * @return the URI components
     * @throws IllegalArgumentException if any of the components contain illegal
     *                                  characters that should have been encoded.
     */
    public UriComponents build(boolean encoded) {
        return buildInternal(encoded ? EncodingHint.FULLY_ENCODED :
                (this.encodeTemplate ? EncodingHint.ENCODE_TEMPLATE : EncodingHint.NONE));
    }

    private UriComponents buildInternal(EncodingHint hint) {
        UriComponents result;
        if (this.ssp != null) {
            result = new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        } else {
            HierarchicalUriComponents uric = new HierarchicalUriComponents(this.scheme, this.fragment,
                    this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams,
                    hint == EncodingHint.FULLY_ENCODED);
            result = (hint == EncodingHint.ENCODE_TEMPLATE ? uric.encodeTemplate(this.charset) : uric);
        }
        if (!this.uriVariables.isEmpty()) {
            result = result.expand(name -> this.uriVariables.getOrDefault(name, UriComponents.UriTemplateVariables.SKIP_VALUE));
        }
        return result;
    }

    /**
     * Build a {@code UriComponents} instance and replaces URI template variables
     * with the values from a map. This is a shortcut method which combines
     * calls to {@link #build()} and then {@link UriComponents#expand(Map)}.
     *
     * @param uriVariables the map of URI variables
     * @return the URI components with expanded values
     */
    public UriComponents buildAndExpand(Map<String, ?> uriVariables) {
        return build().expand(uriVariables);
    }

    @Override
    public URI build(Object... uriVariables) {
        return buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
    }

    @Override
    public URI build(Map<String, ?> uriVariables) {
        return buildInternal(EncodingHint.ENCODE_TEMPLATE).expand(uriVariables).toUri();
    }


    // Instance methods

    /**
     * Initialize components of this builder from components of the given URI.
     *
     * @param uri the URI
     * @return this UriComponentsBuilder
     */
    public UriComponentsBuilder uri(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.scheme = uri.getScheme();
        if (uri.isOpaque()) {
            this.ssp = uri.getRawSchemeSpecificPart();
            resetHierarchicalComponents();
        } else {
            if (uri.getRawUserInfo() != null) {
                this.userInfo = uri.getRawUserInfo();
            }
            if (uri.getHost() != null) {
                this.host = uri.getHost();
            }
            if (uri.getPort() != -1) {
                this.port = String.valueOf(uri.getPort());
            }
            if (StringUtils.hasLength(uri.getRawPath())) {
                this.pathBuilder = new CompositePathComponentBuilder();
                this.pathBuilder.addPath(uri.getRawPath());
            }
            if (StringUtils.hasLength(uri.getRawQuery())) {
                this.queryParams.clear();
                query(uri.getRawQuery());
            }
            resetSchemeSpecificPart();
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        return this;
    }


    @Override
    public UriComponentsBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    @Override
    public UriComponentsBuilder userInfo(String userInfo) {
        this.userInfo = userInfo;
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder host(String host) {
        this.host = host;
        if (host != null) {
            resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder port(int port) {
        Assert.isTrue(port >= -1, "Port must be >= -1");
        this.port = String.valueOf(port);
        if (port > -1) {
            resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder port(String port) {
        this.port = port;
        if (port != null) {
            resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder path(String path) {
        this.pathBuilder.addPath(path);
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
        this.pathBuilder.addPathSegments(pathSegments);
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder replacePath(String path) {
        this.pathBuilder = new CompositePathComponentBuilder();
        if (path != null) {
            this.pathBuilder.addPath(path);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder query(String query) {
        if (query != null) {
            Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = matcher.group(1);
                String eq = matcher.group(2);
                String value = matcher.group(3);
                queryParam(name, (value != null ? value : (StringUtils.hasLength(eq) ? "" : null)));
            }
            resetSchemeSpecificPart();
        } else {
            this.queryParams.clear();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQuery(String query) {
        this.queryParams.clear();
        if (query != null) {
            query(query);
            resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder queryParam(String name, Object... values) {
        Assert.notNull(name, "Name must not be null");
        if (!ObjectUtils.isEmpty(values)) {
            for (Object value : values) {
                String valueAsString = getQueryParamValue(value);
                this.queryParams.add(name, valueAsString);
            }
        } else {
            this.queryParams.add(name, null);
        }
        resetSchemeSpecificPart();
        return this;
    }


    private String getQueryParamValue(Object value) {
        if (value != null) {
            return (value instanceof Optional ?
                    ((Optional<?>) value).map(Object::toString).orElse(null) :
                    value.toString());
        }
        return null;
    }

    @Override
    public UriComponentsBuilder queryParam(String name, Collection<?> values) {
        return queryParam(name, (CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray()));
    }

    @Override
    public UriComponentsBuilder queryParamIfPresent(String name, Optional<?> value) {
        value.ifPresent(o -> {
            if (o instanceof Collection) {
                queryParam(name, (Collection<?>) o);
            } else {
                queryParam(name, o);
            }
        });
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 4.0
     */
    @Override
    public UriComponentsBuilder queryParams(MultiValueMap<String, String> params) {
        if (params != null) {
            this.queryParams.addAll(params);
            resetSchemeSpecificPart();
        }
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQueryParam(String name, Object... values) {
        Assert.notNull(name, "Name must not be null");
        this.queryParams.remove(name);
        if (!ObjectUtils.isEmpty(values)) {
            queryParam(name, values);
        }
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public UriComponentsBuilder replaceQueryParam(String name, Collection<?> values) {
        return replaceQueryParam(name, (CollectionUtils.isEmpty(values) ? EMPTY_VALUES : values.toArray()));
    }

    /**
     * {@inheritDoc}
     *
     * @since 4.2
     */
    @Override
    public UriComponentsBuilder replaceQueryParams(MultiValueMap<String, String> params) {
        this.queryParams.clear();
        if (params != null) {
            this.queryParams.putAll(params);
        }
        return this;
    }

    @Override
    public UriComponentsBuilder fragment(String fragment) {
        if (fragment != null) {
            Assert.hasLength(fragment, "Fragment must not be empty");
            this.fragment = fragment;
        } else {
            this.fragment = null;
        }
        return this;
    }


    private void resetHierarchicalComponents() {
        this.userInfo = null;
        this.host = null;
        this.port = null;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams.clear();
    }

    private void resetSchemeSpecificPart() {
        this.ssp = null;
    }


    /**
     * Public declaration of Object's {@code clone()} method.
     * Delegates to {@link #cloneBuilder()}.
     */
    @Override
    public Object clone() {
        return cloneBuilder();
    }

    /**
     * Clone this {@code UriComponentsBuilder}.
     *
     * @return the cloned {@code UriComponentsBuilder} object
     * @since 4.2.7
     */
    public UriComponentsBuilder cloneBuilder() {
        return new UriComponentsBuilder(this);
    }


    private interface PathComponentBuilder {

        HierarchicalUriComponents.PathComponent build();

        PathComponentBuilder cloneBuilder();
    }


    private static class CompositePathComponentBuilder implements PathComponentBuilder {

        private final Deque<PathComponentBuilder> builders = new ArrayDeque<>();

        public void addPathSegments(String... pathSegments) {
            if (!ObjectUtils.isEmpty(pathSegments)) {
                PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder == null) {
                    psBuilder = new PathSegmentComponentBuilder();
                    this.builders.add(psBuilder);
                    if (fpBuilder != null) {
                        fpBuilder.removeTrailingSlash();
                    }
                }
                psBuilder.append(pathSegments);
            }
        }

        public void addPath(String path) {
            if (StringUtils.hasText(path)) {
                PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder != null) {
                    path = (path.startsWith("/") ? path : "/" + path);
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.builders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }

        @SuppressWarnings("unchecked")

        private <T> T getLastBuilder(Class<T> builderClass) {
            if (!this.builders.isEmpty()) {
                PathComponentBuilder last = this.builders.getLast();
                if (builderClass.isInstance(last)) {
                    return (T) last;
                }
            }
            return null;
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            int size = this.builders.size();
            List<HierarchicalUriComponents.PathComponent> components = new ArrayList<>(size);
            for (PathComponentBuilder componentBuilder : this.builders) {
                HierarchicalUriComponents.PathComponent pathComponent = componentBuilder.build();
                if (pathComponent != null) {
                    components.add(pathComponent);
                }
            }
            if (components.isEmpty()) {
                return HierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return components.get(0);
            }
            return new HierarchicalUriComponents.PathComponentComposite(components);
        }

        @Override
        public CompositePathComponentBuilder cloneBuilder() {
            CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
            for (PathComponentBuilder builder : this.builders) {
                compositeBuilder.builders.add(builder.cloneBuilder());
            }
            return compositeBuilder;
        }
    }


    private static class FullPathComponentBuilder implements PathComponentBuilder {

        private final StringBuilder path = new StringBuilder();

        public void append(String path) {
            this.path.append(path);
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            String sanitized = getSanitizedPath(this.path);
            return new HierarchicalUriComponents.FullPathComponent(sanitized);
        }

        private static String getSanitizedPath(final StringBuilder path) {
            int index = path.indexOf("//");
            if (index >= 0) {
                StringBuilder sanitized = new StringBuilder(path);
                while (index != -1) {
                    sanitized.deleteCharAt(index);
                    index = sanitized.indexOf("//", index);
                }
                return sanitized.toString();
            }
            return path.toString();
        }

        public void removeTrailingSlash() {
            int index = this.path.length() - 1;
            if (this.path.charAt(index) == '/') {
                this.path.deleteCharAt(index);
            }
        }

        @Override
        public FullPathComponentBuilder cloneBuilder() {
            FullPathComponentBuilder builder = new FullPathComponentBuilder();
            builder.append(this.path.toString());
            return builder;
        }
    }


    private static class PathSegmentComponentBuilder implements PathComponentBuilder {

        private final List<String> pathSegments = new ArrayList<>();

        public void append(String... pathSegments) {
            for (String pathSegment : pathSegments) {
                if (StringUtils.hasText(pathSegment)) {
                    this.pathSegments.add(pathSegment);
                }
            }
        }

        @Override
        public HierarchicalUriComponents.PathComponent build() {
            return (this.pathSegments.isEmpty() ? null :
                    new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments));
        }

        @Override
        public PathSegmentComponentBuilder cloneBuilder() {
            PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
            builder.pathSegments.addAll(this.pathSegments);
            return builder;
        }
    }


    private enum EncodingHint {ENCODE_TEMPLATE, FULLY_ENCODED, NONE}

}
