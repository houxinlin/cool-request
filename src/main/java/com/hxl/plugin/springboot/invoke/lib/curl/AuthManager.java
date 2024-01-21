package com.hxl.plugin.springboot.invoke.lib.curl;

public class AuthManager {
    public enum Mechanism {
        /**
         * @deprecated (use {@link Mechanism#BASIC})
         */
        @Deprecated
        BASIC_DIGEST,
        /**
         * Basic Auth
         */
        BASIC,
        /**
         * Digest Auth
         */
        DIGEST,
        /**
         * Kerberos Auth
         */
        KERBEROS
    }
}
