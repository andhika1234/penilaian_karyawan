package id.co.lua.pbj.penilaian_karyawan.utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class MySanitizeString {
    private MySanitizeString() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String strictSanitize(String untrustedString) {
        if (untrustedString != null) {
            PolicyFactory policy = new HtmlPolicyBuilder()
                    .allowStandardUrlProtocols()
                    .toFactory();
            return policy.sanitize(untrustedString);
        }else{
            return null;
        }
    }
    public static String sanitizeWYSIWYG(String untrustedString) {

        if (untrustedString != null) {
            PolicyFactory policy = new HtmlPolicyBuilder()
                    .allowCommonInlineFormattingElements()
                    .allowCommonBlockElements()
                    .allowElements("a").allowStandardUrlProtocols()
                    .allowAttributes("href", "target").onElements("a")
                    .allowAttributes("size").onElements("font")
                    .allowAttributes("class", "style").globally()
                    .toFactory();
            return  policy.sanitize(untrustedString);
        }else{
            return null;
        }
    }
}
