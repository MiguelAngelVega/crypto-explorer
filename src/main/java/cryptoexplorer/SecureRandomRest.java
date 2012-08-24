package cryptoexplorer;

import com.google.gson.Gson;

import javax.ws.rs.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static java.security.SecureRandom.getInstance;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

/**
 * @author Marcelo Morales
 *         Date: 8/24/12
 */
@Path("SecureRandom")
@Produces(APPLICATION_JSON)
public class SecureRandomRest {

    @POST
    @Path("/{algorithm}/{provider}")
    public String secureRandom(@PathParam("algorithm") String algorithm, @PathParam("provider") String provider) {
        try {
            SecureRandom secureRandom = getInstance(algorithm, provider);
            ArrayList<Integer> objects = newArrayList();
            for (int i = 0; i < 119; i++) {
                objects.add(secureRandom.nextInt(1));
            }
            return new Gson().toJson(objects);
        } catch (NoSuchAlgorithmException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity(e.getMessage()).build());
        } catch (NoSuchProviderException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity(e.getMessage()).build());
        }
    }
}
