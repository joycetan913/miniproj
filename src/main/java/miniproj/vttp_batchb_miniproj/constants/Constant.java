package miniproj.vttp_batchb_miniproj.constants;

import jakarta.servlet.http.HttpSession;
import miniproj.vttp_batchb_miniproj.models.User;

public class Constant {
    public static final String ATTR_USER = "user";

    public static User getSessionUserAttribute(HttpSession sess) {
        try {
            User attrValue = (User) sess.getAttribute(ATTR_USER);
            return attrValue;
        } catch (Exception e) {
            return null;
        }

    }

    public static void setSessionUserAttribute(HttpSession sess, User attrValue) {

        sess.setAttribute(ATTR_USER, attrValue);
    }
}
