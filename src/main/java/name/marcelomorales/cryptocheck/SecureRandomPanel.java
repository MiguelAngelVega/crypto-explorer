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

import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;

/**
 *
 * @author Marcelo Morales
 */
public class SecureRandomPanel extends Panel {

    private static final long serialVersionUID = 928374928374L;

    private static final int SIZE = 10;

    private final IModel<String> provider;

    public final IModel<String> algorithm;

    public SecureRandomPanel(String id, IModel<String> provider, IModel<String> algorithm) {
        super(id);
        this.provider = provider;
        this.algorithm = algorithm;
        List<IColumn<RandomNumber>> columns = new LinkedList<IColumn<RandomNumber>>();
        columns.add(new PropertyColumn<RandomNumber>(new Model<String>(""), "column1"));
        columns.add(new PropertyColumn<RandomNumber>(new Model<String>(""), "column2"));
        columns.add(new PropertyColumn<RandomNumber>(new Model<String>(""), "column3"));
        add(new DefaultDataTable<RandomNumber>("randomnumbers", columns, new RandomNumbersDataProvider(), SIZE));
    }

    private class RandomNumbersDataProvider extends SortableDataProvider<RandomNumber> {

        private static final long serialVersionUID = 928389234L;

        @Override
        public Iterator<? extends RandomNumber> iterator(int first, int count) {
            try {
                SecureRandom secureRandom = SecureRandom.getInstance(algorithm.getObject(), provider.getObject());
                List<RandomNumber> randomNumbers = new ArrayList<RandomNumber>(30);
                for (int i = 0; i < SIZE; i++) {
                    randomNumbers.add(new RandomNumber(
                            Integer.toString(secureRandom.nextInt()),
                            Integer.toString(secureRandom.nextInt()),
                            Integer.toString(secureRandom.nextInt())));
                }
                return randomNumbers.iterator();
            } catch (NoSuchAlgorithmException ex) {
                return Collections.<RandomNumber>emptyList().iterator();
            } catch (NoSuchProviderException e) {
                return Collections.<RandomNumber>emptyList().iterator();
            }
        }

        @Override
        public int size() {
            return SIZE;
        }

        @Override
        public IModel<RandomNumber> model(final RandomNumber object) {
            return new AbstractReadOnlyModel<RandomNumber>() {

                private static final long serialVersionUID = 928342374L;

                @Override
                public RandomNumber getObject() {
                    return object;
                }
            };
        }
    }

    protected class RandomNumber implements Serializable {

        private static final long serialVersionUID = 239872437L;

        private String column1;

        private String column2;

        private String column3;

        public RandomNumber(String column1, String column2, String column3) {
            this.column1 = column1;
            this.column2 = column2;
            this.column3 = column3;
        }

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }
    }
}
