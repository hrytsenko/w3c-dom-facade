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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

public class XmlElementTest {

    private XmlElement newsletters;

    @Before
    public void loadFonts() throws Exception {
        byte[] newslettersXml = Resources.toByteArray(Resources.getResource("newsletters.xml"));
        newsletters = XmlElement.rootOf(newslettersXml);
    }

    @Test
    public void testParent() {
        XmlElement mark = newsletters.find("//newsletter/recipient[@email='mark@any.org']");

        Assert.assertEquals("Healthcare", mark.parent().attr("subject"));
    }

    @Test(expected = NoSuchElementException.class)
    public void testParentIfRoot() {
        newsletters.parent();
    }

    @Test
    public void testTryFind() {
        Optional<XmlElement> technology = newsletters.tryFind("//newsletter[@subject='Technology']");
        Assert.assertTrue(technology.isPresent());
    }

    @Test
    public void testTryFindIfNotExists() {
        Optional<XmlElement> finance = newsletters.tryFind("//newsletter[@subject='Finance']");
        Assert.assertFalse(finance.isPresent());
    }

    @Test
    public void testFind() {
        newsletters.find("//newsletter[@subject='Technology']");
    }

    @Test(expected = NoSuchElementException.class)
    public void testFindIfNotExists() {
        newsletters.find("//newsletter[@subject='Finance']");
    }

    @Test
    public void testFindAll() {
        List<XmlElement> technologyRecipients = newsletters.findAll("//newsletter[@subject='Technology']/recipient");
        Assert.assertEquals(2, technologyRecipients.size());
    }

    @Test
    public void testAttrs() {
        XmlElement john = newsletters.find("//newsletter[@subject='Technology']/recipient[1]");
        Set<String> attributes = john.attrs();

        String[] expected = new String[] { "email", "name" };
        Assert.assertEquals(expected.length, attributes.size());
        Assert.assertThat(attributes, CoreMatchers.hasItems(expected));
    }

    @Test
    public void testAttr() {
        XmlElement john = newsletters.find("//newsletter[@subject='Technology']/recipient[1]");
        Assert.assertEquals("John", john.attr("name"));
    }

    @Test
    public void testUsage() {
        List<String> emails = newsletters.findAll("//newsletter[@subject='Healthcare']/recipient").stream()
                .map(r -> r.attr("email")).collect(Collectors.toList());

        Assert.assertEquals(Arrays.asList("john@any.org", "mark@any.org", "robert@any.org"), emails);
    }

}
