/**
 * Crated by ste on 17/giu/2014
 */
package non_junit;

import it_at.unibo_tuwien.tucson_rSybl.RespectEnforcementAPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import alice.tucson.api.ITucsonOperation;

/**
 * @author ste
 * 
 */
public class RespectEnforcementAPINonJunitTest {
    /**
     * @param args
     *            not supported atm
     */
    public static void main(final String[] args) {
        final RespectEnforcementAPI rApi = new RespectEnforcementAPI(
                RespectEnforcementAPI.TUCSON_PORT, RespectEnforcementAPI.AID);
        ITucsonOperation result = rApi.delegate("out(t(hi))",
                RespectEnforcementAPI.OP_TIMEOUT, null);
        if (result.isResultSuccess()) {
            Logger.getAnonymousLogger().log(Level.INFO, "Success!");
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "Failure!");
        }
        result = rApi
                .delegate(
                        "out_s( e(out(t(X))), g(operation, success), b(out(tt(X)), out(ttt(X))) )",
                        RespectEnforcementAPI.OP_TIMEOUT, null);
        if (result.isResultSuccess()) {
            Logger.getAnonymousLogger().log(Level.INFO, "Success!");
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "Failure!");
        }
        rApi.delegate("quit", RespectEnforcementAPI.OP_TIMEOUT, null);
        try {
            Thread.sleep(3000);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Logger.getAnonymousLogger().log(Level.INFO, "Hola!");
    }
}
