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
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Facade for work with elements of the XML document.
 * 
 * <p>
 * This class is not intended for modification of DOM.
 * 
 * @author hrytsenko.anton
 */
public final class XmlElement {

    private final Node node;

    /**
     * Create the element for {@link Node}.
     * 
     * @param node
     *            the node of XML document.
     */
    private XmlElement(Node node) {
        Preconditions.checkArgument(isElement(node), "Node is not an element.");

        this.node = node;
    }

    /**
     * Get the root element of the document.
     * 
     * @param data
     *            the content of XML document.
     * 
     * @return the root element.
     * 
     * @throws IllegalArgumentException
     *             if XML document is invalid.
     */
    public static XmlElement rootOf(byte[] data) {
        return rootOf(new ByteArrayInputStream(data));
    }

    /**
     * Get the root element of the document.
     * 
     * @param stream
     *            the stream to read the XML document.
     * 
     * @return the root element.
     * 
     * @throws IllegalArgumentException
     *             if XML document is invalid.
     */
    public static XmlElement rootOf(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            return new XmlElement(document.getDocumentElement());
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid XML document.", exception);
        }
    }

    /**
     * Try get the parent of this element.
     * 
     * @return the parent element.
     */
    public Optional<XmlElement> tryGetParent() {
        return tryFind("parent::node()");
    }

    /**
     * Get the parent of this element.
     * 
     * @return the parent element.
     * 
     * @throws NoSuchElementException
     *             if this element has no parent (for example, it is root element).
     */
    public XmlElement getParent() {
        return tryGetParent().orElseThrow(NoSuchElementException::new);
    }

    /**
     * Short form of {@link #getParent()}.
     */
    public XmlElement parent() {
        return getParent();
    }

    /**
     * Try find the first matching element inside this element.
     * 
     * @param xpathExpression
     *            the XPath expression.
     * 
     * @return the found element.
     * 
     * @throws IllegalArgumentException
     *             if XPath expression is invalid.
     */
    public Optional<XmlElement> tryFind(String xpathExpression) {
        return findAll(xpathExpression).stream().findFirst();
    }

    /**
     * Find the first matching element inside this element.
     * 
     * @param xpathExpression
     *            the XPath expression.
     * 
     * @return the found element.
     * 
     * @throws IllegalArgumentException
     *             if XPath expression is invalid.
     * @throws NoSuchElementException
     *             if element not found.
     */
    public XmlElement find(String xpathExpression) {
        return tryFind(xpathExpression).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Find all matching elements inside this element.
     * 
     * @param xpathExpression
     *            the XPath expression.
     * 
     * @return the list of elements.
     * 
     * @throws IllegalArgumentException
     *             if XPath expression is invalid.
     */
    public List<XmlElement> findAll(String xpathExpression) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(xpathExpression), "XPath expression is null or empty.");

        return findByXPath(node, xpathExpression);
    }

    /**
     * Get the text content of this element and all elements inside it.
     * 
     * @return the text content.
     */
    public String text() {
        return textOf(node);
    }

    /**
     * Try get the value of attribute by its name.
     * 
     * @param name
     *            the name of attribute.
     * 
     * @return the value of attribute.
     * 
     * @throws IllegalArgumentException
     *             if name is <code>null</code> or empty.
     * @throws NoSuchElementException
     *             if attribute not found.
     */
    public Optional<String> tryGetAttribute(String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name of attribute is null or empty.");

        Node attribute = node.getAttributes().getNamedItem(name);
        return Optional.ofNullable(attribute).map(XmlElement::textOf);
    }

    /**
     * Get the value of attribute by its name.
     * 
     * @param name
     *            the name of attribute.
     * 
     * @return the value of attribute.
     * 
     * @throws IllegalArgumentException
     *             if name is <code>null</code> or empty.
     * @throws NoSuchElementException
     *             if attribute not found.
     */
    public String getAttribute(String name) {
        return tryGetAttribute(name).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Short form for {@link #getAttribute(String)}.
     */
    public String attr(String name) {
        return getAttribute(name);
    }

    /**
     * Get names of all attributes.
     * 
     * @return the unordered set of attributes.
     */
    public Set<String> getAttributes() {
        NamedNodeMap nodes = node.getAttributes();
        return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item).map(Node::getNodeName)
                .collect(Collectors.toSet());
    }

    /**
     * Short form for {@link #getAttributes()}.
     */
    public Set<String> attrs() {
        return getAttributes();
    }

    private static String textOf(Node node) {
        return Strings.nullToEmpty(node.getTextContent());
    }

    private static List<XmlElement> findByXPath(Node node, String xpathExpression) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xpath.evaluate(xpathExpression, node, XPathConstants.NODESET);
            return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item).filter(XmlElement::isElement)
                    .map(XmlElement::new).collect(Collectors.toList());
        } catch (XPathExpressionException exception) {
            throw new IllegalArgumentException("Invalid XPath expression.", exception);
        }
    }

    private static boolean isElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

}
