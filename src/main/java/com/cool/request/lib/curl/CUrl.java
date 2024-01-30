package com.cool.request.lib.curl;


import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"rawtypes", "unchecked", "serial", "UnusedReturnValue", "WeakerAccess", "unused", "JavaDoc"})
public final class CUrl {
    private static final String VERSION = "1.2.2";
    private static final String DEFAULT_USER_AGENT = "Java-CURL version " + VERSION + " by Rocks Wang(https://github.com/rockswang)";
    private static final Pattern ptnOptionName = Pattern.compile("-{1,2}[a-zA-Z][a-zA-Z0-9\\-.]*");
    private static final CookieStore cookieStore = new CookieStore();
    private static HostnameVerifier insecureVerifier = null;
    private static SSLSocketFactory insecureFactory = null;

    private String method;
    private static boolean verbose = false;

    static {
        try {
            // Try to enable the setting to restricted headers like "Origin", this is expected to be executed before HttpURLConnection class-loading
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

            // Modify the system-wide Cookie manager to ThreadLocal-based instance
            CookieManager.setDefault(new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL));

            // For insecure HTTPS
            insecureVerifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            insecureFactory = getSocketFactory(null, null);
        } catch (Exception ignored) {
        }
    }

    private static final Map<String, Integer> optMap = Util.mapPut(new LinkedHashMap<String, Integer>(),
            "-E", 32,
            "--cert", 32,                    // <certificate[:password]> Client certificate file and password
            "--compressed", 1,                // Request compressed response (using deflate or gzip)
            "--connect-timeout", 2,        // SECONDS  Maximum time allowed for connection
            "-b", 3,
            "--cookie", 3,                    // STRING/FILE  Read cookies from STRING/FILE (H)
            "-c", 4,
            "--cookie-jar", 4,                // FILE  Write cookies to FILE after operation (H)
            "-d", 5,
            "--data", 5,                    // DATA	 HTTP POST data (H)
            "--data-ascii", 5,                // DATA	 HTTP POST ASCII data (H)
            "--data-raw", 51,                // DATA	 HTTP POST raw data (H)
            "--data-binary", 52,            // DATA	 HTTP POST binary data (H)
            "--data-urlencode", 53,        // DATA	 HTTP POST data url encoded (H)
            "-D", 6,
            "--dump-header", 6,            // FILE  Write the headers to FILE
            "-F", 7,
            "--form", 7,                    // CONTENT  Specify HTTP multipart POST data (H)
            "--form-string", 71,            // STRING  Specify HTTP multipart POST data (H)
            "-G", 8,
            "--get", 8,                    // Send the -d data with a HTTP GET (H)
            "-H", 10,
            "--header", 10,                // LINE   Pass custom header LINE to server (H)
            "-I", 11,
            "--head", 11,                    // Show document info only
//			"--ignore-content-length", 12, // Ignore the HTTP Content-Length header
            "-k", 31,
            "--insecure", 31,                // Allow insecure server connections when using SSL
            "-L", 13,
            "--location", 13,                // Follow redirects (H)
            "-m", 14,
            "--max-time", 14,                // SECONDS  Maximum time allowed for the transfer
//			"--no-keepalive", 15, 			// Disable keepalive use on the connection
            "-o", 16,
            "--output", 16,                // FILE   Write to FILE instead of stdout
            "-x", 17,
            "--proxy", 17,                    // [PROTOCOL://]HOST[:PORT]  Use proxy on given port
            "-U", 18,
            "--proxy-user", 18,            // USER[:PASSWORD]  Proxy user and password
            "-e", 19,
            "--referer", 19,                // Referer URL (H)
            "--retry", 20,                    // NUM   Retry request NUM times if transient problems occur
            "--retry-delay", 21,            // SECONDS  Wait SECONDS between retries
            "--retry-max-time", 22,        // SECONDS  Retry only within this period
            "-s", 23,
            "--silent", 23,                // Silent mode (don't output anything)
            "--stderr", 24,                // FILE   Where to redirect stderr (use "-" for stdout)
            "-u", 28,
            "--user", 28,                    // USER[:PASSWORD]  Server user and password
            "--url", 25,                    // URL	   URL to work with
            "-A", 26,
            "--user-agent", 26,            // STRING  Send User-Agent STRING to server (H)
            "-X", 27,
            "--request", 27,                // COMMAND  Specify request command to use
            "--x-max-download", 29,            // BYTES Maximum bytes allowed for the download
            "--x-tags", 30,                    // DATA extra key-value pairs, storage only
            "--verbose", 33,                // Verbose
            "-v", 33,                        // Verbose
            "", 0 // placeholder
    );

    private static final String BOUNDARY = "------------aia113jBkadk7289";
    private static final byte[] NEWLINE = "\r\n".getBytes();

    private final List<String> options = new ArrayList<String>();
    private final Map<String, IO> iomap = new HashMap<String, IO>();
    private final Map<String, String> tags = new LinkedHashMap<String, String>();
    private final Map<String, String> headers = new LinkedHashMap<String, String>();
    private final List<List<String[]>> responseHeaders = new ArrayList<List<String[]>>(4);
    private final List<URL> locations = new ArrayList<URL>(4);
    private long startTime;
    private long execTime;
    private int httpCode;
    private byte[] rawStdout;

    public CUrl() {
    }

    public CUrl(String url) {
        this.url(url);
    }

    public final CUrl method(String method) {
        this.method = method;
        return this;
    }

    public final CUrl opt(String... options) {
        for (String o : options) {
            this.options.add(o);
        }
        return this;
    }

    public final CUrl url(String url) {
        return opt("--url", url);
    }

    public final CUrl location() {
        return opt("-L");
    }

    public final CUrl proxy(String host, int port) {
        return opt("-x", host + ":" + port);
    }


    public final CUrl timeout(float connectTimeoutSeconds, float readTimeoutSeconds) {
        return opt("--connect-timeout", Float.toString(connectTimeoutSeconds),
                "--max-time", Float.toString(readTimeoutSeconds));
    }

    public final CUrl header(String headerLine) {
        return opt("-H", headerLine);
    }

    public final CUrl headers(Map<String, ?> headers) {
        for (Map.Entry<String, ?> kv : headers.entrySet()) {
            Object k = kv.getKey(), v = kv.getValue();
            opt("-H", v == null ? k + ":" : v.toString().length() == 0 ? k + ";" : k + ": " + v);
        }
        return this;
    }

    public final CUrl data(String data) {
        return data(data, false);
    }


    public final CUrl data(String data, boolean raw) {
        return opt(raw ? "--data-raw" : "-d", data);
    }


    public final CUrl data(String data, String charset) {
        return opt("-d" + (charset != null ? "-" + charset : ""), data);
    }


    public final CUrl form(String name, String content, boolean isFile) {
        return opt("-F", name + "=" + (isFile ? "@" : "") + content);
    }

    public final CUrl form(String name, IO input) {
        String key;
        iomap.put(key = "IO#" + iomap.size(), input);
        return opt("-F", name + "=@" + key);
    }


    public final CUrl form(String formString) {
        return opt("--form-string", formString);
    }


    /**
     * Load cookies from the specified IO object to the cookie-store binding with current thread
     *
     * @param input
     */
    public static void loadCookies(IO input) {
        if (input instanceof CookieIO) {
            CookieIO cs = (CookieIO) input;
            synchronized (cs) {
                for (HttpCookie c : cs.getCookies()) cookieStore.add(null, c);
            }
        } else {
            List<HttpCookie> cookies = parseCookies(Util.b2s(readInput(input), null, null));
            for (HttpCookie c : cookies) cookieStore.add(null, c);
        }
    }

    public static List<HttpCookie> parseCookies(String input) {
        BufferedReader br = new BufferedReader(new StringReader(input));
        ArrayList<HttpCookie> result = new ArrayList<HttpCookie>();
        try {
            for (String line = br.readLine(), l[]; line != null; line = br.readLine()) {
                if (line.trim().length() == 0 || line.startsWith("# ") || (l = line.split("\t")).length < 7) continue;
                HttpCookie cookie = new HttpCookie(l[5], l[6]);
                cookie.setDomain(l[0]);
                cookie.setPath(l[2]);
                cookie.setSecure("TRUE".equals(l[3]));
                long expire = Long.parseLong(l[4]);
                cookie.setMaxAge(expire >= Integer.MAX_VALUE ? Integer.MAX_VALUE : expire * 1000L - System.currentTimeMillis());
                if (!cookie.hasExpired()) cookieStore.add(null, cookie);
            }
        } catch (Exception ignored) {
        } // should not happen
        return result;
    }

    /**
     * Get all options as CURL command-line
     */
    public final String toString() {
        StringBuilder sb = new StringBuilder("curl -X " + this.method);
        for (String s : options) {
            if (s.startsWith("'") && s.endsWith("'")) {
                sb.append(' ').append(s);
            } else {
                sb.append(' ').append(ptnOptionName.matcher(s).matches() ? s : '"' + s + '"');

            }

        }
        return sb.toString();
    }


    public final List<String> getOptions() {
        return options;
    }

    public final Map<String, String> getTags() {
        return tags;
    }

    private static String unquote(String s) {
        return s.startsWith("'") && s.endsWith("'") || s.startsWith("\"") && s.endsWith("\"") ?
                s.substring(1, s.length() - 1) : s;
    }

    private static void fillResponseHeaders(HttpURLConnection con, List<String[]> headers) {
        headers.clear();
        Object responses = Util.getField(con, null, "responses", null, true); // sun.net.www.MessageHeader
        if (responses == null) { // con is sun.net.www.protocol.https.HttpsURLConnectionImpl
            Object delegate = Util.getField(con, null, "delegate", null, true);
            if (delegate != null) responses = Util.getField(delegate, null, "responses", null, true);
        }
        String[] keys, values;
        Integer nkeys;
        if (responses != null && (nkeys = (Integer) Util.getField(responses, null, "nkeys", null, true)) != null
                && (keys = (String[]) Util.getField(responses, null, "keys", null, true)) != null
                && (values = (String[]) Util.getField(responses, null, "values", null, true)) != null) {
            for (int i = 0; i < nkeys; i++) headers.add(new String[]{keys[i], values[i]});
        } else {
            try {
                headers.add(new String[]{null, con.getResponseMessage()});
            } catch (Exception ignored) {
            }
            for (int i = 0; ; i++) {
                String k = con.getHeaderFieldKey(i), v = con.getHeaderField(i);
                if (k == null && v == null) break;
                headers.add(new String[]{k, v});
            }
        }
    }

    private static void dumpHeader(List<String[]> headers, IO dumpHeader) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (String[] kv : headers)
            bos.write(((kv[0] != null ? kv[0] + ": " : "") + (kv[1] != null ? kv[1] : "") + "\r\n").getBytes());
        bos.write(NEWLINE);
        writeOutput(dumpHeader, bos.toByteArray(), false);
    }

    private static byte[] readInput(IO in) {
        InputStream is = in.getInputStream();
        byte[] bb;
        if (is == null || (bb = Util.readStream(is, false)) == null) bb = new byte[0];
        in.close();
        return bb;
    }

    private static void writeOutput(IO out, byte[] bb, boolean append) {
        out.setAppend(append);
        OutputStream os = out.getOutputStream();
        if (os == null) return;
        try {
            os.write(bb);
            os.flush();
        } catch (Exception e) {
            Util.logStderr("CUrl.writeOutput: out=%s,bb=%s,append=%s,ex=%s", out, bb, append, Util.dumpStackTrace(e, true));
        }
        out.close();
    }

    private static final HashSet<Class> RECOVERABLES = Util.listAdd(
            new HashSet<Class>(),
            (Class) Recoverable.class,
            ConnectException.class,
            HttpRetryException.class,
            SocketException.class,
            SocketTimeoutException.class,
            NoRouteToHostException.class);

    private static boolean isRecoverable(Class<? extends Throwable> errCls) {
        if (RECOVERABLES.contains(errCls)) return true;
        for (Class re : RECOVERABLES) if (re.isAssignableFrom(errCls)) return true;
        return false;
    }

    private static SSLSocketFactory getSocketFactory(IO cert, String password) throws Exception {
        TrustManager[] t_managers = null;
        KeyManager[] k_managers = null;
        Util.logStderr("Load default trust manager");
        t_managers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }

            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }
        }};
        if (cert != null) {
            Util.logStderr("Load key store");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // JKS for java, BKS for android
            keyStore.load(cert.getInputStream(), password.toCharArray());
            cert.close();
            KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore, password.toCharArray());
            k_managers = factory.getKeyManagers();
        }
        if (verbose) {
            Util.logStderr("Create socket factory");
        }
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(k_managers, t_managers, new SecureRandom());
        return sc.getSocketFactory();
    }

    ///////////////////////////// Inner Classes & static instances ///////////////////////////////////////

    public interface Resolver<T> {
        T resolve(int httpCode, byte[] responseBody) throws Throwable;
    }

    public static class ToStringResolver implements Resolver<String> {
        final private String charset;

        public ToStringResolver(String charset) {
            this.charset = charset;
        }

        @Override
        public String resolve(int httpCode, byte[] raw) throws Throwable {
            return new String(raw, charset);
        }
    }

    public static final Resolver<byte[]> RAW = new Resolver<byte[]>() {
        @Override
        public byte[] resolve(int httpCode, byte[] raw) {
            return raw;
        }
    };

    public static final ToStringResolver UTF8 = new ToStringResolver("UTF-8");
    public static final ToStringResolver GBK = new ToStringResolver("GBK");
    public static final ToStringResolver ISO_8859_1 = new ToStringResolver("ISO-8859-1");

    public interface IO {
        InputStream getInputStream();

        OutputStream getOutputStream();

        void setAppend(boolean append);

        void close();
    }

    public static final class WrappedIO implements IO {
        final InputStream is;
        final OutputStream os;

        public WrappedIO(String s, String charset) {
            this(Util.s2b(s, charset));
        }

        public WrappedIO(byte[] byteArray) {
            this(new ByteArrayInputStream(byteArray));
        }

        public WrappedIO(InputStream is) {
            this.is = is;
            this.os = null;
        }

        public WrappedIO(OutputStream os) {
            this.is = null;
            this.os = os;
        }

        public InputStream getInputStream() {
            return is;
        }

        public OutputStream getOutputStream() {
            return os;
        }

        public void setAppend(boolean append) {
        } // not supported

        public void close() {
        } // wrapper is not responsible for closing

        public String toString() {
            return "WrappedIO<" + is + "," + os + ">";
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static final class FileIO implements IO {
        private File f;
        private transient InputStream is;
        private transient OutputStream os;
        boolean append = false;

        public FileIO(File f) {
            this.f = f.getAbsoluteFile();
        }

        public FileIO(String path) {
            this(new File(path));
        }

        public InputStream getInputStream() {
            if (f.exists() && f.isFile() && f.canRead()) {
                try {
                    return is = new FileInputStream(f);
                } catch (Exception ignored) {
                }
            }
            return null;
        }

        public OutputStream getOutputStream() {
            Util.mkdirs(f.getParentFile());
            try {
                f.createNewFile();
                f.setReadable(true, false);
                f.setWritable(true, false);
                os = new FileOutputStream(f, append);
            } catch (Exception ignored) {
            }
            return os;
        }

        public void setAppend(boolean append) {
            this.append = append;
        }

        public void close() {
            try {
                if (is != null) is.close();
            } catch (Exception ignored) {
            }
            try {
                if (os != null) os.close();
            } catch (Exception ignored) {
            }
        }

        public String toString() {
            return "FileIO<" + f + ">";
        }
    }

    public static final class MemIO extends ByteArrayOutputStream implements IO {
        public MemIO() {
            super(0);
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(buf, 0, count);
        }

        public OutputStream getOutputStream() {
            return this;
        }

        public void setAppend(boolean append) {
            if (!append) this.reset();
        }

        public void close() {
        } // not needed

        public String toString() {
            return "MemIO<" + this.hashCode() + ">";
        }

        /**
         * This is useful when the MemIO was used as the target of --dump-header
         *
         * @return
         */
        public Map<String, String> parseDumpedHeader() {
            Map<String, String> result = new LinkedHashMap<String, String>();
            String s = Util.b2s(this.toByteArray(), null, null);
            for (String l : s.split("[\r\n]+")) {
                if (l.trim().length() == 0) continue;
                String[] kv = l.split(":", 2);
                result.put(kv[0], kv.length > 1 ? kv[1].trim() : "");
            }
            return result;
        }

        public List<HttpCookie> parseCookieJar() {
            return parseCookies(Util.b2s(this.toByteArray(), null, null));
        }

    }

    public static class CookieIO implements IO, java.net.CookieStore {

        public InputStream getInputStream() {
            throw new RuntimeException();
        }

        public OutputStream getOutputStream() {
            throw new RuntimeException();
        }

        public void setAppend(boolean append) {
        }

        public void close() {
        }

        protected final Map<String, List<HttpCookie>> cookiesMap;

        public CookieIO() {
            cookiesMap = new HashMap<String, List<HttpCookie>>();
        }

        protected Map<String, List<HttpCookie>> getCookiesMap() {
            return cookiesMap;
        }

        public void add(String uri, String key, String value) {
            try {
                this.add(new URI(uri), new HttpCookie(key, value));
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public void add(URI uri, HttpCookie cookie) {
            normalize(uri, cookie);
            Map<String, List<HttpCookie>> map = Util.mapListAdd(getCookiesMap(), ArrayList.class, cookie.getDomain());
            List<HttpCookie> cc = map.get(cookie.getDomain());
            cc.remove(cookie);
            if (cookie.getMaxAge() != 0) cc.add(cookie);
        }

        @Override
        public List<HttpCookie> get(URI uri) {
            List<HttpCookie> result = getCookies();
            String host = uri.getHost();
            for (ListIterator<HttpCookie> it = result.listIterator(); it.hasNext(); ) {
                String domain = it.next().getDomain();
                if (!domainMatches(domain, host)) it.remove();
            }
            return result;
        }

        @Override
        public List<HttpCookie> getCookies() {
            List<HttpCookie> result = new ArrayList<HttpCookie>();
            for (List<HttpCookie> cc : getCookiesMap().values()) {
                for (ListIterator<HttpCookie> it = cc.listIterator(); it.hasNext(); )
                    if (it.next().hasExpired()) it.remove();
                result.addAll(cc);
            }
            return result;
        }

        @Override
        public List<URI> getURIs() {
            Set<URI> result = new HashSet<URI>();
            for (HttpCookie cookie : getCookies()) {
                String scheme = cookie.getSecure() ? "https" : "http";
                String domain = cookie.getDomain();
                if (domain.startsWith(".")) domain = domain.substring(1);
                try {
                    result.add(new URI(scheme, domain, cookie.getPath(), null));
                } catch (URISyntaxException ignored) {
                }
            }
            return new ArrayList<URI>(result);
        }

        @Override
        public boolean remove(URI uri, HttpCookie cookie) {
            normalize(uri, cookie);
            List<HttpCookie> cc = getCookiesMap().get(cookie.getDomain());
            return cc != null && cc.remove(cookie);
        }

        @Override
        public boolean removeAll() {
            getCookiesMap().clear();
            return true;
        }

        private static void normalize(URI uri, HttpCookie cookie) {
            if (cookie.getDomain() == null && uri != null) cookie.setDomain(uri.getHost());
            if (cookie.getPath() == null && uri != null) cookie.setPath(uri.getPath());
            if (Util.empty(cookie.getDomain()))
                throw new IllegalArgumentException("illegal cookie domain: " + cookie.getDomain());
            if (Util.empty(cookie.getPath())) cookie.setPath("/");
            cookie.setVersion(0);
        }

        /**
         * Check a string domain-matches a given domain string or not. Refer to section 5.1.3 RFC6265
         */
        private static boolean domainMatches(String domain, String host) {
            if (domain == null || host == null) return false;
            if (domain.startsWith(".")) { // it's a suffix
                return host.toLowerCase().endsWith(domain.toLowerCase());
            } else {
                return host.equalsIgnoreCase(domain);
            }
        }

    }

    public static final class CookieStore extends CookieIO {

        private final ThreadLocal<Map<String, List<HttpCookie>>> cookies = new ThreadLocal<Map<String, List<HttpCookie>>>() {
            @Override
            protected synchronized Map<String, List<HttpCookie>> initialValue() {
                return new HashMap<String, List<HttpCookie>>();
            }
        };

        @Override
        protected Map<String, List<HttpCookie>> getCookiesMap() {
            return cookies.get();
        }
    }

    public static final class Recoverable extends Exception {
        private final int httpCode;

        public Recoverable() {
            this(null, -1);
        }

        public Recoverable(Throwable cause, int httpCode) {
            super(cause);
            this.httpCode = httpCode;
        }
    }

    @SuppressWarnings({"WeakerAccess", "JavaDoc", "ConstantConditions", "ResultOfMethodCallIgnored", "StatementWithEmptyBody", "UnusedReturnValue", "SuspiciousMethodCalls"})
    final static class Util {

        public static boolean empty(String s) {
            return s == null || s.length() == 0;
        }

        public static <T> List<T> asList(Object o) {
            if (o == null) return new ArrayList<T>(0);
            if (o instanceof Collection) {
                return new ArrayList<T>((Collection<T>) o);
            } else if (o.getClass().isArray()) {
                ArrayList<T> list = new ArrayList<T>();
                for (int i = 0, n = Array.getLength(o); i < n; i++) list.add((T) Array.get(o, i));
                return list;
            } else {
                return listAdd(new ArrayList<T>(1), (T) o);
            }
        }

        public static String qt(Object o) {
            return o == null || o instanceof Boolean || o instanceof Number ?
                    "" + o : o instanceof Character ? "'" + o + "'" : "\"" + o + "\"";
        }

        public static String dumpStackTrace(Throwable e, boolean singleLine) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String s = sw.toString();
            return singleLine ? s.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t") : s;
        }

        public static void logStderr(String msg, Object... args) {
            if (args.length > 0) msg = String.format(msg, args);
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            System.err.println("[ERR] [" + fmt.format(new Date()) + "] " + msg);
            //System.err.println("[ERR] [" + new Date() + "] " + msg);
        }

        public static <K, V> V mapGet(Map<K, V> map, K key, V fallback) {
            V v;
            return map != null && (v = map.get(key)) != null ? v : fallback;
        }

        public static <K, V> Map<K, List<V>> mapListAdd(Map<K, List<V>> map, K key, V... val) {
            return mapListAdd(map, ArrayList.class, key, val);
        }

        public static <K, V, L extends Collection<V>> Map<K, L> mapListAdd(Map<K, L> map, Class<? extends Collection> collectionClass, K key, V... val) {
            L l;
            if ((l = map.get(key)) == null) try {
                map.put(key, l = (L) collectionClass.newInstance());
            } catch (Exception ignored) {
            }
            Collections.addAll(l, val);
            return map;
        }

        public static <K, S, V, M extends Map<S, V>> V mapMapGet(Map<K, M> map, K key, S subkey, V fallback) {
            M m;
            V ret;
            return (m = map.get(key)) != null && (ret = m.get(subkey)) != null ? ret : fallback;
        }

        public static <T> Iterable<T> safeIter(Iterable<T> iter) {
            return iter != null ? iter : new ArrayList<T>(0);
        }

        public static <T> T[] safeArray(T[] array, Class<T> componentType) {
            return array != null ? array : (T[]) Array.newInstance(componentType, 0);
        }

        public static Map<String, Object> newMap(Object... keyValuePairs) {
            return mapPut(new LinkedHashMap<String, Object>(), keyValuePairs);
        }

        public static <K, V, M extends Map<K, V>> M mapPut(M map, Object... keyValuePairs) {
            if ((keyValuePairs.length & 1) != 0)
                throw new IllegalArgumentException("the number of keyValuePairs arguments must be odd");
            for (int i = 0, n = keyValuePairs.length; i < n; i += 2) {
                map.put((K) keyValuePairs[i], (V) keyValuePairs[i + 1]);
            }
            return map;
        }

        public static <T, L extends Collection<T>> L listAdd(L list, T... values) {
            list.addAll(Arrays.asList(values));
            return list;
        }

        public static class Ref<T> {
            public int i;
            public T v;

            public Ref(T v) {
                this(0, v);
            }

            public Ref(int i, T v) {
                setInt(i);
                set(v);
            }

            public T get() {
                return v;
            }

            public void set(T v) {
                this.v = v;
            }

            public int getInt() {
                return i;
            }

            public void setInt(int i) {
                this.i = i;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Ref)) return false;
                Ref<T> o;
                return (o = (Ref<T>) obj) != null && i == o.i && (v == null ? o.v == null : v.equals(o.v));
            }

            @Override
            public int hashCode() {
                return i + (v == null ? 0 : v.hashCode());
            }

            @Override
            public String toString() {
                return String.format("Ref{%s, %s}", i, qt(v));
            }

        }

        public static String urlDecode(String s, String enc) {
            if (!empty(s)) try {
                return URLDecoder.decode(s, enc);
            } catch (Exception ignored) {
            }
            return s;
        }

        public static String b2s(byte[] bb, String charset, String fallback) {
            return b2s(bb, 0, bb.length, charset, fallback);
        }

        public static String b2s(byte[] bb, int offset, int count, String charset, String fallback) {
            try {
                int start = bb.length - offset >= 3 && bb[offset] == 0xEF && bb[offset + 1] == 0xBB && bb[offset + 2] == 0xBF ? 3 : 0; // deal BOM
                return new String(bb, offset + start, count - start, charset == null ? "UTF-8" : charset);
            } catch (Exception e) {
                return fallback;
            }
        }

        public static byte[] s2b(String s, String charset) {
            try {
                return s.getBytes(charset == null ? "UTF-8" : charset);
            } catch (Exception e) {
                return null;
            }
        }

        public static String[][] split(String s, String delim1, String delim2) {
            String[] ss = s.split(delim1);
            String[][] result = new String[ss.length][];
            for (int i = ss.length; --i >= 0; result[i] = ss[i].split(delim2)) ;
            return result;
        }

        public static Map<String, String> split(String s, String entryDelim, String kvDelim, Map<String, String> toMap) {
            String[] ss = s.split(entryDelim);
            if (toMap == null) toMap = new HashMap<String, String>(ss.length);
            for (String l : ss) {
                String[] sub = l.split(kvDelim);
                toMap.put(sub[0].trim(), sub.length > 1 ? sub[1].trim() : "");
            }
            return toMap;
        }

        public static String join(Object mapOrColl, String delim, String subDelim) {
            List<List<Object>> all = new ArrayList<List<Object>>();
            if (mapOrColl == null) { // do nothing
            } else if (mapOrColl instanceof Map) {
                for (Map.Entry<?, ?> kv : ((Map<?, ?>) mapOrColl).entrySet()) {
                    all.add(listAdd(new ArrayList<Object>(2), kv.getKey(), kv.getValue()));
                }
            } else if (mapOrColl instanceof Collection) {
                for (Object o : (Collection<?>) mapOrColl) all.add(asList(o));
            } else if (mapOrColl.getClass().isArray()) {
                for (int i = 0, n = Array.getLength(mapOrColl); i < n; all.add(asList(Array.get(mapOrColl, i++))))
                    ;
            } else { // plain object
                all.add(asList(mapOrColl));
            }
            StringBuilder sb = new StringBuilder();
            int i = 0, j;
            for (List<Object> sub : all) {
                if (i++ > 0) sb.append(delim);
                j = 0;
                for (Object o : sub) sb.append(j++ > 0 ? subDelim : "").append(o);
            }
            return sb.toString();
        }

        public static String base64Encode(byte[] bb) {
            Class<?> clz = getClass("java.util.Base64", null);
            if (clz != null) {
                Object encoder = invokeSilent(null, clz, "getEncoder", false, null);
                return (String) invokeSilent(encoder, null, "encodeToString", false, "[B", (Object) bb);
            }
            clz = getClass("sun.misc.BASE64Encoder", null);
            if (clz != null) {
                Object encoder = createInstance(clz, "", true);
                return ((String) invokeSilent(encoder, null, "encode", true, "[B", (Object) bb)).replaceAll("[\r\n]+", "");
            }
            clz = getClass("org.apache.commons.codec.binary.Base64", null);
            if (clz != null) {
                return (String) invokeSilent(null, clz, "encodeBase64String", false, "[B", (Object) bb);
            }
            clz = getClass("android.util.Base64", null);
            if (clz != null) {
                return (String) invokeSilent(null, clz, "encodeToString", false, "[BI", bb, 2); // NO_WRAP
            }
            throw new RuntimeException(new NoSuchMethodException("base64Encode"));
        }

        public static byte[] readStream(InputStream is, boolean close) {
            return readStream(is, 0, close);
        }

        public static byte[] readStream(InputStream is, int interruptOnSize, boolean close) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int count = 0, c;
            while ((c = pipeStream(is, bos)) > 0 && (interruptOnSize <= 0 || count < interruptOnSize)) count += c;
            if (c < 0) count += (c & PIPE_COUNT_MASK);
            byte[] result = c < 0 && count == 0 ? null : bos.toByteArray();
            if (close) try {
                is.close();
            } catch (Exception ignored) {
            }
            return result;
        }

        public static final int PIPE_COUNT_MASK = 0x7FFFFFFF;

        private static final int BUFFER_SIZE = 10000;

        public static int pipeStream(InputStream source, OutputStream destination) {
            byte[] bb = new byte[BUFFER_SIZE];
            int len, count = 0;
            do {
                len = 0;
                try {
                    len = source.read(bb);
                } catch (SocketTimeoutException e) { // no data, but the socket connection is still alive
                } catch (SocketException e) { // EOF or socket disconnected
                    len = -1;
                } catch (IOException e) { // unexpected exceptions
                    throw new RuntimeException(e);
                }
                if (len > 0) {
                    try {
                        destination.write(bb, 0, len);
                    } catch (IOException e) { // unexpected exceptions while writing
                        throw new RuntimeException(e);
                    }
                    count += len;
                }
            } while (len == BUFFER_SIZE);
            return len < 0 ? (0x80000000 | count) : count; // len < 0 -> EOF reached
        }

        public static void mkdirs(File dir) {
            File parent = dir.getAbsoluteFile();
            List<File> mkdir = new ArrayList<File>();
            for (; !parent.exists() || !parent.isDirectory(); parent = parent.getParentFile()) {
                mkdir.add(parent);
            }
            for (int i = mkdir.size(); --i >= 0; ) {
                File d = mkdir.get(i);
                d.mkdir();
                d.setReadable(true, false);
                d.setWritable(true, false);
            }
        }

        public static Class<?> getClass(String className, ClassLoader cl) {
            try {
                return (cl != null ? cl : CUrl.class.getClassLoader()).loadClass(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        public static <T> T createInstance(Class<T> cls, String signature, boolean ignoreAccess, Object... args) {
            if (signature == null && args.length == 0) {
                try {
                    return cls.newInstance();
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
            return (T) invoke(null, cls, "<init>", ignoreAccess, signature, args);
        }

        public static Object getField(Object thiz, Class<?> cls, String fieldName, Object fallback, boolean ignoreAccess) {
            if (thiz == null && cls == null || fieldName == null)
                throw new NullPointerException("inst=" + thiz + ",class=" + cls + ",field=" + fieldName);
            try {
                for (MemberInfo mi : safeIter(getMembers(thiz != null ? thiz.getClass() : cls, fieldName))) {
                    if (-1 == mi.numArgs && (ignoreAccess || (mi.member.getModifiers() & Modifier.PUBLIC) != 0)) {
                        AccessibleObject acc;
                        if (ignoreAccess && !(acc = (AccessibleObject) mi.member).isAccessible())
                            acc.setAccessible(true);
                        return ((Field) mi.member).get(thiz);
                    }
                }
            } catch (Exception ignored) {
            }
            return fallback;
        }

        public static Object invokeSilent(Object thiz, Class<?> cls, String methodName, boolean ignoreAccess, String signature, Object... args) {
            try {
                return invoke(thiz, cls, methodName, ignoreAccess, signature, args);
            } catch (Exception ignored) {
            }
            return null;
        }

        public static Object invoke(Object thiz, Class<?> cls, String methodName, boolean ignoreAccess, String signature, Object... args) {
            if (thiz == null && cls == null || methodName == null)
                throw new NullPointerException("inst=" + thiz + ",class=" + cls + ",method=" + methodName);
            List<MemberInfo> found = getMembers(thiz != null ? thiz.getClass() : cls, methodName);
            try {
                Member m = null;
                if (found == null) { // do nothing
                } else if (signature == null) {
                    int len = args.length;
                    for (MemberInfo mi : found) {
                        if (len == mi.numArgs && (ignoreAccess || (mi.member.getModifiers() & Modifier.PUBLIC) != 0)) {
                            m = mi.member;
                            break;
                        }
                    }
                } else {
                    signature = signature.replace('/', '.');
                    for (MemberInfo mi : found) {
                        if (signature.equals(mi.signature) && (ignoreAccess || (mi.member.getModifiers() & Modifier.PUBLIC) != 0)) {
                            m = mi.member;
                            break;
                        }
                    }
                }
                if (m == null) {
                    StringBuilder msg = new StringBuilder().append('"').append(methodName).append('"');
                    if (signature == null) {
                        msg.append(" with ").append(args.length).append(" parameter(s)");
                    } else {
                        msg.append(" with signature \"").append(signature).append("\"");
                    }
                    throw new NoSuchMethodException(msg.toString());
                }
                AccessibleObject acc;
                if (ignoreAccess && !(acc = (AccessibleObject) m).isAccessible()) acc.setAccessible(true);
                return m instanceof Method ? ((Method) m).invoke(thiz, args) : ((Constructor<?>) m).newInstance(args);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        private static final Map<String, Object> primaryTypes = newMap(
                byte.class, 'B',
                char.class, 'C',
                double.class, 'D',
                float.class, 'F',
                int.class, 'I',
                long.class, 'J',
                short.class, 'S',
                void.class, 'V',
                boolean.class, 'Z');

        public static String getSignature(Class<?>... types) {
            StringBuilder sb = new StringBuilder();
            for (Class<?> t : types) {
                while (t.isArray()) {
                    sb.append('[');
                    t = t.getComponentType();
                }
                Character c;
                if ((c = (Character) primaryTypes.get(t)) != null) {
                    sb.append(c);
                } else {
                    sb.append('L').append(t.getName()).append(';');
                }
            }
            return sb.toString();
        }

        private static final Map<Class<?>, Map<String, List<MemberInfo>>> mapClassMembers = new HashMap<Class<?>, Map<String, List<MemberInfo>>>();

        private static synchronized List<MemberInfo> getMembers(Class<?> cls, String name) {
            if (!mapClassMembers.containsKey(cls)) {
                Map<String, List<MemberInfo>> map;
                mapClassMembers.put(cls, map = new LinkedHashMap<String, List<MemberInfo>>());
                Class<?> clss = cls;
                while (clss != null && !Object.class.equals(clss)) {
                    for (Constructor<?> c : safeArray(clss.getDeclaredConstructors(), Constructor.class)) {
                        Class<?>[] ptypes = c.getParameterTypes();
                        mapListAdd(map, "<init>", new MemberInfo(getSignature(ptypes), ptypes.length, c));
                    }
                    for (Method m : safeArray(clss.getDeclaredMethods(), Method.class)) {
                        Class<?>[] ptypes = m.getParameterTypes();
                        mapListAdd(map, m.getName(), new MemberInfo(getSignature(ptypes), ptypes.length, m));
                    }
                    for (Field f : safeArray(clss.getDeclaredFields(), Field.class)) {
                        mapListAdd(map, f.getName(), new MemberInfo(null, -1, f));
                    }
                    clss = clss.getSuperclass();
                }
            }
            return mapMapGet(mapClassMembers, cls, name, null);
        }

        private static class MemberInfo {
            String signature; // null for field
            int numArgs; // -1 for field
            Member member;

            MemberInfo(String sign, int num, Member member) {
                signature = sign;
                numArgs = num;
                this.member = member;
            }

            public final String toString() {
                return member.toString();
            }
        }

    }


}

