package name.marcelomorales.cryptocheck;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;

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

/**
 * @author Marcelo Morales
 *         Date: 8/23/12
 */
public class KeyStorePanel extends Panel {

    private final IModel<String> algorithmModel;
    private final IModel<String> providerModel;
    private final Model<String> keyStoreModel;
    private final Model<String> passwordModel;

    private final Component fb;

    public KeyStorePanel(String id, IModel<String> algorithmModel, IModel<String> providerModel) {
        super(id);

        add(fb = new FeedbackPanel("feedback").setOutputMarkupId(true));

        this.algorithmModel = algorithmModel;
        this.providerModel = providerModel;

        Form<Void> form;
        add(form = new StatelessForm<Void>("form"));

        keyStoreModel = new Model<String>();
        form.add(new TextField<String>("keystore", keyStoreModel, String.class).setOutputMarkupId(true));

        passwordModel = new Model<String>();
        form.add(new PasswordTextField("password", passwordModel));

        form.add(new AjaxButton("cargar") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    KeyStore keyStore = newKeyStore();

                    Enumeration<String> aliases = keyStore.aliases();
                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();

                        boolean keyEntry = keyStore.isKeyEntry(alias);
                        System.out.println(alias + keyEntry);
                        boolean certificateEntry = keyStore.isCertificateEntry(alias);
                        System.out.println(alias + certificateEntry);
                    }

                } catch (KeyStoreException e) {
                    getSession().error(e.getMessage());
                } catch (NoSuchProviderException e) {
                    throw new IllegalStateException(e);
                } catch (FileNotFoundException e) {
                    getSession().error(e.getMessage());
                } catch (CertificateException e) {
                    getSession().error(e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException(e);
                } catch (IOException e) {
                    getSession().error(e.getMessage());
                }

                target.addComponent(fb);
            }
        });
    }

    private KeyStore newKeyStore() throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException,
            CertificateException {
        InputStream inputStream = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(this.algorithmModel.getObject(),
                    this.providerModel.getObject());

            String keyStoreFile = keyStoreModel.getObject();
            if (keyStoreFile == null || Strings.isEmpty(keyStoreFile)) {
                inputStream = null;
            } else {
                inputStream = new FileInputStream(keyStoreFile);
            }

            final char[] password;
            if (passwordModel.getObject() == null) {
                password = null;
            } else {
                password = passwordModel.getObject().toCharArray();
            }

            keyStore.load(inputStream, password);
            return keyStore;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
