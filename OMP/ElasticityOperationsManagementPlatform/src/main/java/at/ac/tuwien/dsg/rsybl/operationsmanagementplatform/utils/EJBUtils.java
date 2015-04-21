package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Contains convenience methods for EJBs.
 */
public final class EJBUtils {

    static final String MODULE_NAME = "OperationsManagementPlatform";

    private EJBUtils() {
    }

    /**
     * Performs a type safe JNDI lookup.
     *
     * The name consists of the module name i.e. {@code ass2-ejb}, and the
     * unqualified class name of the desired bean:
     * {@code java:global/ass2-ejb/" + clazz.getSimpleName()}
     *
     * @param clazz the type of the class to find
     * @return the object bound to name
     * @throws javax.naming.NamingException if a naming exception is encountered
     * @see javax.naming.InitialContext#lookup(String)
     */
    @SuppressWarnings("unchecked")
    public static <I, C extends I> I lookup(Context ctx, Class<C> clazz) throws NamingException {
        return (I) ctx.lookup("java:global/" + MODULE_NAME + "/" + clazz.getSimpleName());
    }
}
