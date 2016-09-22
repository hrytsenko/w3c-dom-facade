/*
 * #%L
 * w3c-dom-facade
 * %%
 * Copyright (C) 2015 Anton Hrytsenko
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hrytsenko.w3c.dom.facade;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class XmlElementTests {

    private XmlElement newsletters;

    @Before
    public void loadNewsletters() throws Exception {
        byte[] xml = Resources.toByteArray(Resources.getResource("newsletters.xml"));
        newsletters = XmlElement.rootOf(new ByteArrayInputStream(xml));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rootOf_emptyStream_illegalArgument() {
        XmlElement.rootOf(new ByteArrayInputStream("".getBytes()));
    }

    @Test
    public void parent_elementExists_parentElement() {
        XmlElement recipient = newsletters.find("//newsletter/recipient[@email='mark@any.org']");

        XmlElement newsletter = recipient.parent();

        Assert.assertNotNull(newsletter);
    }

    @Test(expected = NoSuchElementException.class)
    public void parent_elementNotExists_noSuchElement() {
        newsletters.parent();
    }

    @Test
    public void findAll_severalElements_listOfElements() {
        List<XmlElement> recipients = newsletters.findAll("//newsletter[@subject='Technology']/recipient");

        Assert.assertEquals(2, recipients.size());
    }

    @Test
    public void findAll_noElements_emptyList() {
        List<XmlElement> recipients = newsletters.findAll("//newsletter[@subject='Finance']/recipient");

        Assert.assertTrue(recipients.isEmpty());
    }

    @Test
    public void find_elementExists_foundElement() {
        XmlElement newsletter = newsletters.find("//newsletter[@subject='Technology']");

        Assert.assertNotNull(newsletter);
    }

    @Test(expected = NoSuchElementException.class)
    public void find_elementNotExists_noSuchElement() {
        newsletters.find("//newsletter[@subject='Finance']");
    }

    @Test(expected = IllegalArgumentException.class)
    public void find_invalidXPath_illegalArgument() {
        newsletters.find("//");
    }

    @Test(expected = IllegalArgumentException.class)
    public void find_emptyXPath_illegalArgument() {
        newsletters.find("");
    }

    @Test
    public void attrs_severalAttribtes_listOfAttributes() {
        XmlElement recipient = newsletters.find("//newsletter[@subject='Technology']/recipient[1]");

        List<String> attributes = recipient.attrs();

        Assert.assertEquals(Arrays.asList("email", "name"), attributes);
    }

    @Test
    public void attrs_noAttribtes_emptyList() {
        List<String> attributes = newsletters.attrs();

        Assert.assertTrue(attributes.isEmpty());
    }

    @Test
    public void attr_attributeExists_foundAttribute() {
        XmlElement recipient = newsletters.find("//newsletter[@subject='Technology']/recipient[1]");

        String name = recipient.attr("name");

        Assert.assertEquals("John", name);
    }

    @Test(expected = NoSuchElementException.class)
    public void attr_attributeNotExists_noSuchElement() {
        newsletters.attr("site");
    }

    @Test(expected = IllegalArgumentException.class)
    public void attr_emptyName_illegalArgument() {
        newsletters.attr("");
    }

    @Test
    public void text_elementWithoutText_emptyString() {
        XmlElement recipient = newsletters.find("//newsletter[@subject='Technology']/recipient[1]");

        String text = recipient.text();

        Assert.assertEquals("", text);
    }

}
