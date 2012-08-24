package cryptoexplorer;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import com.google.gson.Gson;

import javax.ws.rs.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

/**
 * @author Marcelo Morales
 *         Date: 8/24/12
 */
@Path("/KeyStore")
@Produces(APPLICATION_JSON)
public class KeyStoreRest {

    @POST
    @Path("/{algorithm}/{provider}/list")
    @Consumes(APPLICATION_JSON)
    public String list(@PathParam("algorithm") String algorithm, @PathParam("provider") String provider,
                       String keystoreAndPassword) {

        InputStream inputStream = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(algorithm, provider);

            Gson gson = new Gson();
            PathAndPassword pathAndPassword = gson.fromJson(keystoreAndPassword, PathAndPassword.class);

            if (pathAndPassword.path == null) {
                inputStream = null;
            } else {
                inputStream = new FileInputStream(pathAndPassword.path);
            }

            char[] password;
            if (pathAndPassword.password == null) {
                password = null;
            } else {
                password = pathAndPassword.password.toCharArray();
            }

            keyStore.load(inputStream, password);

            List<Map<String, String>> aliases = newLinkedList();

            Enumeration<String> aliasesEnumeration = keyStore.aliases();
            while (aliasesEnumeration.hasMoreElements()) {
                String s = aliasesEnumeration.nextElement();

                aliases.add(ImmutableMap.of("alias", s,
                        "isKey", Boolean.toString(keyStore.isKeyEntry(s)),
                        "isCert", Boolean.toString(keyStore.isCertificateEntry(s))));
            }

            return gson.toJson(aliases);
        } catch (KeyStoreException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity(e.getMessage()).build());
        } catch (NoSuchProviderException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity(e.getMessage()).build());
        } catch (FileNotFoundException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity("KeyStore not found").build());
        } catch (CertificateException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity("Certificate not readable").build());
        } catch (NoSuchAlgorithmException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity(e.getMessage()).build());
        } catch (IOException e) {
            throw new WebApplicationException(status(NOT_FOUND).entity("KeyStore not readable or bad password").build());
        } finally {
            Closeables.closeQuietly(inputStream);
        }
    }

    public static final class PathAndPassword {
        String path;
        String password;
    }
}
