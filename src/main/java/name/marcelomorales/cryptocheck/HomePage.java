/*
 *  Copyright 2009 Marcelo Morales.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package name.marcelomorales.cryptocheck;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree.LinkType;
import org.apache.wicket.extensions.markup.html.tree.table.AbstractTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.security.Provider;
import java.security.Security;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Marcelo Morales
 */
public class HomePage extends WebPage {

    private static final long serialVersionUID = 928239847L;

    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    private static final String _VERSION_ = ", version ";

    private final IModel<String> algorithmModel;

    private final IModel<String> providerModel;

    public HomePage(PageParameters parameters) {
        super(parameters);

        algorithmModel = Model.of("");
        providerModel = Model.of("");

        LOG.debug("Entrando a la pagina de inicio");
        add(new FeedbackPanel("feedback"));

        DefaultMutableTreeNode providers = new DefaultMutableTreeNode("Root", true);
        for (int i = 0; i < Security.getProviders().length; i++) {
            Provider provider = Security.getProviders()[i];
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(provider.getName(), true);
            providers.add(root);
            for (Provider.Service service : provider.getServices()) {
                Enumeration<?> enumeration = root.children();
                DefaultMutableTreeNode found = null;
                while (enumeration.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                    if (service.getType().equals(node.getUserObject())) {
                        found = node;
                        break;
                    }
                }
                if (found == null) {
                    found = new DefaultMutableTreeNode(service.getType(), true);
                    root.add(found);
                }
                found.add(new DefaultMutableTreeNode(service.getAlgorithm()));
            }
        }

        final ModalWindow secureRandomModalWindow;
        add(secureRandomModalWindow = new ModalWindow("securerandom"));
        secureRandomModalWindow.setContent(new SecureRandomPanel(secureRandomModalWindow.getContentId(), providerModel,
                algorithmModel));
        secureRandomModalWindow.setTitle("Secure Random Number Generator");

        final ModalWindow keyStoreModalWindow;
        add(keyStoreModalWindow = new ModalWindow("keystore"));
        keyStoreModalWindow.setContent(new KeyStorePanel(keyStoreModalWindow.getContentId(), algorithmModel, providerModel));
        keyStoreModalWindow.setTitle("Key Store");

        TreeTable treeTable = new TreeTable("providers", new DefaultTreeModel(providers), new IColumn[]{
                new AbstractTreeColumn(
                        new ColumnLocation(ColumnLocation.Alignment.LEFT, 600, ColumnLocation.Unit.PX), "provider") {

                    private static final long serialVersionUID = 9384723987L;

                    @Override
                    public String renderNode(TreeNode node) {
                        return node.toString();
                    }
                }
        }) {

            private static final long serialVersionUID = 9238923874L;

            @Override
            protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
                if (node.isLeaf()) {
                    if ("SecureRandom".equals(node.getParent().toString())) {
                        algorithmModel.setObject(node.toString());
                        providerModel.setObject(node.getParent().getParent().toString());
                        secureRandomModalWindow.show(target);
                    }
                    if ("KeyStore".equals(node.getParent().toString())) {
                        algorithmModel.setObject(node.toString());
                        providerModel.setObject(node.getParent().getParent().toString());
                        keyStoreModalWindow.show(target);
                    }
                }
            }
        };
        treeTable.setRootLess(true);
        treeTable.setLinkType(LinkType.AJAX_FALLBACK);
        add(treeTable);

        add(new MultiLineLabel("java", new LoadableDetachableModel<Object>() {

            @Override
            protected Object load() {
                StringBuilder stringBuilder = new StringBuilder();
                Properties properties = System.getProperties();
                for (Object o : properties.keySet()) {
                    stringBuilder.append(o).append(" = ").append(properties.get(o)).append("\r\n");
                }
                return stringBuilder;
            }
        }));
    }
}
