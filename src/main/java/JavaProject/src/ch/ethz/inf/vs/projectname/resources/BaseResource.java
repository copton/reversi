package ch.ethz.inf.vs.projectname.resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.ws.rs.core.UriInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.json.JSONArray;
import org.json.JSONObject;

import ch.ethz.inf.vs.projectname.commons.JerseyConstants;

/**
 * 
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 *
 */

public abstract class BaseResource {

	/* Parameters defining the current resource - inherited by all resources */

	// Current serverName and port
	protected static String hostIdentifier;

	// Number of children and values
	protected Integer childrenCount = 0;
	protected boolean getterExists = false;
	protected boolean posterExists = false;
	protected boolean putterExists = false;
	protected boolean deleterExists = false;

	// Structure Information
	protected String resourceName = null;
	protected String resourceDescription = null;
	protected String resourceURI = null;
	protected String parentURI = null;
	protected String parentName = null;

	protected List<String> childrenNames = null;
	protected List<String> childrenURIs = null;
	protected List<String> childrenDescriptions = null;
	protected List<String> childrenMethods = null;

	protected String getterName = null;
	protected String getterValue = null;
	protected String getterDescription = null;
	
	protected String posterName = null;
	protected String posterDescription = null;
	protected String posterType = null;
	protected String posterArgumentType = null;
	protected String posterPresentationType = null;
	
	protected String putterName = null;
	protected String putterDescription = null;
	protected String putterType = null;
	protected String putterArgumentType = null;
	protected String putterPresentationType = null;
	
	protected String deleterName = null;
	protected String deleterDescription = null;

	protected String infoString = null;
	
	protected ServletRequest request = null;
	protected UriInfo uri = null;
	
	// Allowed method flags
	protected boolean allowGet = true;
	protected boolean allowPost = false;
	protected boolean allowPut = false;
	protected boolean allowDelete = false;

	// Paths to external resources
	public static String CONFIGFILE_PATH = null;

	// Logging instance
	protected static Logger log = Logger.getLogger("BaseResource");

	public BaseResource() {
		// log.setLevel(Level.INFO);
		log.setLevel(JerseyConstants.LOGLEVEL);
		
		resetResourceInformation();
	}

	protected void resetResourceInformation() {
		// Structure Information
		resourceName = null;
		resourceURI = null;
		parentURI = null;
		parentName = null;

		// Number of children, getters and posters
		childrenCount = 0;
		getterExists = false;
		posterExists = false;
		putterExists = false;
		deleterExists = false;

		childrenNames = null;
		childrenURIs = null;
		childrenDescriptions = null;
		childrenMethods = null;

		getterName = null;
		getterValue = null;
		getterDescription = null;

		posterName = null;
		posterDescription = null;
		posterPresentationType = null;
		posterArgumentType = null;
	}

	// Re-loads the getters (be on the safe side when displaying them)
	public abstract void handleGetters();

	/**
	 * Return the representation of the current resource as HTML. This has to be
	 * customized for new resources
	 * 
	 * @return representation of resource as HTML
	 */
	protected String getRepresentationHTML() {
		String representationBuildUp = "";
		try {
			representationBuildUp = readFileAsString(JerseyConstants.RESOURCES_HTML_PATH + "/" + this.getClass().getSimpleName() + ".html");
		} catch (IOException e) {
			e.printStackTrace();
		}

		representationBuildUp = representationBuildUp.replace("{{ResourceName}}", resourceName);

		if (parentURI != null && parentURI.endsWith("/"))
			parentURI = parentURI.substring(0, parentURI.length() - 1);
		
		representationBuildUp = representationBuildUp.replace("{{HomeUrl}}", "http://" + request.getServerName() + ":" + request.getServerPort());
		
		if (parentName != null && !parentName.isEmpty()) {
			representationBuildUp = representationBuildUp.replace("{{ParentUrl}}", parentURI);
		} else {
			representationBuildUp = representationBuildUp.replace("{{HomeUrl}}", "");
			representationBuildUp = representationBuildUp.replace("{{ParentUrl}}", "");
		}

		if (childrenCount > 0) {
			String childBuildUp = "<ul class = \"child\">\n";
			for (int i = 0; i < childrenCount; i++)
				childBuildUp += childListItem(i);
			childBuildUp += "</ul>\n";
			representationBuildUp = representationBuildUp.replace("{{ChildList}}", childBuildUp);
		} else {
			representationBuildUp = representationBuildUp.replace("{{ChildList}}", "");
		}

		if (getterExists) {
			String getterBuildUp = "<ul class = \"getter\">\n";
			getterBuildUp += getterListItem();
			getterBuildUp += "</ul>\n";
			representationBuildUp = representationBuildUp.replace("{{GetterList}}", getterBuildUp);
		} else {
			representationBuildUp = representationBuildUp.replace("{{GetterList}}", "");
		}

		if (posterExists) {
			String posterBuildUp = "<ul class = \"poster\">\n";
			posterBuildUp += posterListItem();
			posterBuildUp += "</ul>\n";
			representationBuildUp = representationBuildUp.replace("{{PosterList}}", posterBuildUp);
		} else {
			representationBuildUp = representationBuildUp.replace("{{PosterList}}", "");
		}
		
		representationBuildUp = representationBuildUp.replace("{{InfoList}}", listPathInfo());
		
		return representationBuildUp;
	}

	protected String childListItem(Integer childIndex) {
		String returnString = "<li class = \"child\">";
		returnString += "<a href = \"" + childrenURIs.get(childIndex) + "\">" + childrenNames.get(childIndex);

		if (!childrenDescriptions.get(childIndex).equalsIgnoreCase("")) {
			returnString += " <span class = \"descriptor\">(" + childrenDescriptions.get(childIndex) + ")</span>";
		}

		returnString += "</a></li>\n";

		return returnString;
	}

	protected String getterListItem() {
		this.handleGetters();

		log.info(getterName);
		log.info(getterValue);

		String returnString = "<li class = \"getter\">" /*+ getterName */ + ": <span class=\"getterValue\">" + getterValue + "</span>";

		if (!(getterDescription == null) && !(getterDescription.equalsIgnoreCase(""))) {
			returnString += " <span class = \"descriptor\">(" + getterDescription + ")</span>";
		}

		returnString += "</li>\n";

		return returnString;
	}

	protected String posterListItem() {
		String returnBuilder = "<li class = \"poster\">";
		returnBuilder += "<form name=\"input\" action=\"" + resourceURI + "\" method=\"post\">\n";
		returnBuilder += "<input class=\"posterSubmit\" value=\"" + posterName + "\" type=\"submit\" value=\"\"/> ";

		if (posterPresentationType.startsWith("None")) {
			// Submit button is enough...
			returnBuilder += "<input type=\"hidden\" value = \"dummy\" name = \"" + posterName + "\"/>";
		} else if (posterPresentationType.startsWith("Text")) {
			returnBuilder += "<input class=\"posterTextInput\" size = \"10\" type = \"text\" value = \"\" name = \"value\"/>\n";
		} else if (posterPresentationType.startsWith("Binary")) {
			returnBuilder += "<input class=\"posterBinaryInput\" id=\"r1\" type=\"radio\" name=\"" + posterName
					+ "\" value=\"on\"><label for=\"r1\">on</label><input class=\"posterBinaryInput\" id=\"r2\" type=\"radio\" name=\"" + posterName
					+ "\" value=\"off\"><label for=\"r2\">off</label>\n";
		}

		if (!posterDescription.equalsIgnoreCase("")) {
			returnBuilder += "<span class = \"descriptor\">(" + posterDescription + ")</span>";
		}

		returnBuilder += "</form></li>\n";

		return returnBuilder;
	}
	
	/**
	 * Return the representation of the current resource in JSON format. The
	 * JSON string is derived from the XML representation of the resource.
	 * 
	 * @return representation of resource as JSON
	 */
	protected String getRepresentationJSON() {
		String stringXML;
		try {
			JSONObject jsonObject = new JSONObject();

			JSONObject resourceObject = new JSONObject();

			resourceObject.put("name", resourceName);

			JSONArray supportedMethodsArray = new JSONArray();
			if (allowGet)
				supportedMethodsArray.put("GET");
			if (allowPut)
				supportedMethodsArray.put("PUT");
			if (allowPost)
				supportedMethodsArray.put("POST");
			if (allowDelete)
				supportedMethodsArray.put("DELETE");
			resourceObject.put("methods", supportedMethodsArray);

			JSONArray childrenArray = new JSONArray();
			for (int i = 0; i < childrenCount; i++) {
				JSONObject childObject = new JSONObject();
				childObject.put("description", childrenDescriptions.get(i));
				childObject.put("name", childrenNames.get(i));
				childObject.put("uri", childrenURIs.get(i));
				childrenArray.put(childObject);
			}
			resourceObject.put("children", childrenArray);

			JSONObject getterObject = new JSONObject();
			getterObject.put("name", getterName);
			getterObject.put("value", getterValue);
			getterObject.put("description", getterDescription);
			resourceObject.put("getter", getterObject);

			if (posterExists) {
				JSONObject posterObject = new JSONObject();
				posterObject.put("name", posterName);
				posterObject.put("description", posterDescription);
				posterObject.put("argtype", posterArgumentType);
				posterObject.put("showtype", posterPresentationType);
				resourceObject.put("poster", posterObject);
			}
			
			if (putterExists) {
				JSONObject putterObject = new JSONObject();
				putterObject.put("name", putterName);
				putterObject.put("description", putterDescription);
				putterObject.put("argtype", putterArgumentType);
				putterObject.put("showtype", putterPresentationType);
				resourceObject.put("putter", putterObject);
			}
			
			if (deleterExists) {
				JSONObject deleterObject = new JSONObject();
				deleterObject.put("name", deleterName);
				deleterObject.put("description", deleterDescription);
				resourceObject.put("deleter", deleterObject);
			}
			
			jsonObject.put("resource", resourceObject);

			return jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	protected String getRepresentationXML() {
		org.jdom.Document d = new org.jdom.Document();
		org.jdom.Element r = new org.jdom.Element("resource");
		
		// Attach the current resource's name
		org.jdom.Element resourceNameElement = new org.jdom.Element("name");
		resourceNameElement.addContent(resourceName);
		r.addContent(resourceNameElement);
		
		// Attach the methods allowed for the current resource
		org.jdom.Element resourceMethodElement = new org.jdom.Element("methods");
		if(allowGet) {
			org.jdom.Element methodElement = new org.jdom.Element("method");
			methodElement.addContent("GET");
			resourceMethodElement.addContent(methodElement);
		}
		if(allowPost) {
			org.jdom.Element methodElement = new org.jdom.Element("method");
			methodElement.addContent("POST");
			resourceMethodElement.addContent(methodElement);
		}
		if(allowPut) {
			org.jdom.Element methodElement = new org.jdom.Element("method");
			methodElement.addContent("PUT");
			resourceMethodElement.addContent(methodElement);
		}
		if(allowDelete) {
			org.jdom.Element methodElement = new org.jdom.Element("method");
			methodElement.addContent("DELETE");
			resourceMethodElement.addContent(methodElement);
		}
		resourceMethodElement.addContent("");
		
		r.addContent(resourceMethodElement);
		
		// Attach the current resource's values
		if(getterExists) {
			org.jdom.Element contentsElement = new org.jdom.Element("getter");
			
			org.jdom.Element contentNameElement = new org.jdom.Element("name");
			org.jdom.Element contentValueElement = new org.jdom.Element("value");
			org.jdom.Element contentDescriptionElement = new org.jdom.Element("description");
			
			contentNameElement.addContent(getterName);
			contentValueElement.addContent(getterValue);
			contentDescriptionElement.addContent(getterDescription);
			
			contentsElement.addContent(contentNameElement);
			contentsElement.addContent(contentValueElement);
			contentsElement.addContent(contentDescriptionElement);
			
			r.addContent(contentsElement);
		}
		
		// Attach the current resource's posters
		if (posterExists) {
			org.jdom.Element posterElement = new org.jdom.Element("poster");
			
			org.jdom.Element contentNameElement = new org.jdom.Element("name");
			org.jdom.Element contentDescriptionElement = new org.jdom.Element("description");
			
			contentNameElement.addContent(posterName);
			contentDescriptionElement.addContent(posterDescription);
			
			posterElement.addContent(contentNameElement);
			posterElement.addContent(contentDescriptionElement);
			
			r.addContent(posterElement);
		}
		
		// Attach the current resource's children
		if (childrenCount > 0) {
			org.jdom.Element childrenTag = new org.jdom.Element("children");

			for (int i = 0; i < childrenCount; i++) {
				org.jdom.Element childTag = new org.jdom.Element("child");

				org.jdom.Element childName = new org.jdom.Element("name");
				childName.addContent(childrenNames.get(i));
				childTag.addContent(childName);

				// If the current resource is a leaf...
				if (childrenURIs != null) {
					org.jdom.Element childUri = new org.jdom.Element("uri");
					childUri.addContent(childrenURIs.get(i));
					childTag.addContent(childUri);
				}

				org.jdom.Element childDescription = new org.jdom.Element("description");
				childDescription.addContent(childrenDescriptions.get(i));
				childTag.addContent(childDescription);

				childrenTag.addContent(childTag);
			}

			r.addContent(childrenTag);
		}
		
		d.setRootElement(r);
		org.jdom.output.XMLOutputter o = new org.jdom.output.XMLOutputter();
		return o.outputString(d);
	}
	
	public void parseResourceInformation(ServletRequest request, UriInfo uri) {
		this.request = request;
		this.uri = uri;
		hostIdentifier = "http://" + request.getServerName() + ":" + request.getServerPort();
		parseNestedResourceInformation(request);
	}

	/**
	 * Initializes the Management and Linking Structures of the current
	 * resource. This is the main customizer of a new resource to enable rapid
	 * prototyping Set the resourceName, resourceURI, resourceDescription and
	 * childrenNames for all resources. - Children are internal nodes that have
	 * a name, description and uri - Getters are leaf nodes that have a name, a
	 * description and a value - Posters are leaf nodes that have a name, a
	 * type, and a description
	 * 
	 */
	protected void parseNestedResourceInformation(ServletRequest request) {
		
		// Get the Configuration File
		documentModel = getConfigurationFromXML();
		
		if (documentModel.getElementsByTagName(this.getClass().getSimpleName()) != null) {
			
			// Get the Document Element <ClassName> ... </ClassName>
			Element docElement = (Element) (documentModel.getElementsByTagName(this.getClass().getSimpleName()).item(0));

			// Get Resource Name
			NodeList nameNodes = docElement.getElementsByTagName("resourceName");
			if (nameNodes != null) {
				Element name = (Element) nameNodes.item(0);
				resourceName = replaceSubstitutes(name.getTextContent().trim());
			} else {
				log.severe("XML parsing exception: wrong number of resourceName nodes");
			}

			// Get Resource URI
			NodeList uriNodes = docElement.getElementsByTagName("resourceURI");
			if (uriNodes != null) {
				Element uri = (Element) uriNodes.item(0);
				resourceURI = replaceSubstitutes(uri.getTextContent().trim());
			} else {
				log.severe("XML parsing exception: wrong number of resourceURI nodes");
			}

			// Get Resource Description
			NodeList descriptionNodes = docElement.getElementsByTagName("resourceDescription");
			if (descriptionNodes != null) {
				Element desc = (Element) descriptionNodes.item(0);
				resourceDescription = replaceSubstitutes(desc.getTextContent().trim());
			} else {
				log.severe("XML parsing exception: wrong number of resourceURI nodes");
			}
			
			// Get Parent Node
			Node cursor = docElement.getParentNode();
			Boolean hasFound = false;
			
			// Cursor is NULL when reaching the "parent" of the root resource
			// HasFound is true when the parent of the current node has been found
			while (cursor != null && !hasFound) {
				NodeList children = cursor.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					
					// Find URI and Name of Parent
					if (children.item(i).getNodeName().equalsIgnoreCase("resourceURI")) {
						parentURI = hostIdentifier + replaceSubstitutes(children.item(i).getTextContent().trim());
						hasFound = true;
					}
					if (children.item(i).getNodeName().equalsIgnoreCase("resourceName")) {
						parentName = replaceSubstitutes(children.item(i).getTextContent().trim());
						hasFound = true;
					}
				}
				cursor = cursor.getParentNode();
			}

			// Get children of current node
			NodeList subNodes = docElement.getChildNodes();
			NodeList childNodes = null;
			
			// Find the subnode <children>, subnodes of the node <children> are the child resources
			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("children")) {
					childNodes = subNodes.item(i).getChildNodes();
				}
			}
			
			// Fetch data of the child nodes
			childrenNames = new ArrayList<String>();
			childrenURIs = new ArrayList<String>();
			childrenDescriptions = new ArrayList<String>();

			int childNumber = 0;
			if (childNodes != null) {
				// Go through all subnodes of <children>...</children>
				for (int j = 0; j < childNodes.getLength(); j++) {
					
					// The child list has the following structure <children><child>...</child><child>...</child></children>
					if (childNodes.item(j).getNodeName().equalsIgnoreCase("child")) {
						
						// Get the type attribute of <child>
						if (childNodes.item(j).getAttributes().getNamedItem("type") != null) {
							
							// If it is a collection, load it using Java Refection
							if (childNodes.item(j).getAttributes().getNamedItem("type").getTextContent().equalsIgnoreCase("collection")) {
								
								// Get features of that child
								String originalMethod = null;
								String originalDescription = null;
								String originalURI = null;
								String originalName = null;

								NodeList childElements = childNodes.item(j).getChildNodes();

								for (int k = 0; k < childElements.getLength(); k++) {
									if (childElements.item(k).getNodeName().equalsIgnoreCase("#text"))
										continue;

									log.fine(childElements.item(k).getNodeName());

									NodeList childFeats = childElements.item(k).getChildNodes();

									for (int i = 0; i < childFeats.getLength(); i++) {
										if (childFeats.item(i).getNodeName().equalsIgnoreCase("collectionMethod")) {
											originalMethod = childFeats.item(i).getTextContent().trim();
										}

										else if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceName")) {
											originalName = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
										}

										else if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceURI")) {
											originalURI = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
										}

										else if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceDescription")) {
											originalDescription = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
										}
									}

									childNumber++;
								}

								// log.info(originalMethod);
								String className = originalMethod.substring(0, originalMethod.lastIndexOf("."));
								String methodName = originalMethod.split("\\.")[originalMethod.split("\\.").length - 1];

								log.info(originalMethod + " --> Callback at class " + className + "." + methodName);

								Method callbackMethod = null;
								Class<?> c = null;
								try {
									c = Class.forName(className);
								} catch (ClassNotFoundException e1) {
									e1.printStackTrace();
								}
								try {
									callbackMethod = c.getMethod(methodName, new Class[] { HashMap.class });
								} catch (SecurityException e) {
									System.err.println("JAVA REFLECTION HAS FAILED!");
									e.printStackTrace();
								} catch (NoSuchMethodException e) {
									System.err.println("JAVA REFLECTION HAS FAILED!");
									e.printStackTrace();
								}

								List<String> currentResources = null;
								try {
									Object ret = callbackMethod.invoke(c, new Object[] { getSource() });
									currentResources = (List<String>) ret;
								} catch (IllegalArgumentException e) {
								} catch (IllegalAccessException e) {
								} catch (InvocationTargetException e) {
									System.err.println("JAVA REFLECTION HAS FAILED!");
									e.printStackTrace();
								}

								// We have to add some resources to the child list
								int k = 0;
								for (String resourceName : currentResources) {
									childrenNames.add(originalName + " (" + resourceName + ")");
									childrenURIs.add(originalURI.replaceAll("\\{[\\w]+\\}", resourceName));
									childrenDescriptions.add(originalDescription.replaceAll("\\{[\\w]+\\}", resourceName));

									k++;
								}
							}
						} else {
							// It is not a collection
							
							NodeList childElements = childNodes.item(j).getChildNodes();

							for (int k = 0; k < childElements.getLength(); k++) {
								if (childElements.item(k).getNodeName().equalsIgnoreCase("#text"))
									continue;

								log.fine(childElements.item(k).getNodeName());

								NodeList childFeats = childElements.item(k).getChildNodes();

								for (int i = 0; i < childFeats.getLength(); i++) {
									if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceName")) {
										childrenNames.add(replaceSubstitutes(childFeats.item(i).getTextContent().trim()));
									} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceURI")) {
										childrenURIs.add(replaceSubstitutes(childFeats.item(i).getTextContent().trim()));
									}
									if (childFeats.item(i).getNodeName().equalsIgnoreCase("resourceDescription")) {
										childrenDescriptions.add(replaceSubstitutes(childFeats.item(i).getTextContent().trim()));
									}
								}

								childNumber++;
							}
						}
					}
				}
			}
			
			childrenCount = childrenNames.size();

			// Get Getter of current node
			Node getterNode = null;
			subNodes = docElement.getChildNodes();
			
			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("getter") && subNodes.item(i).hasChildNodes()) {
					getterNode = subNodes.item(i);
					getterExists = true;
					break;
				}
			}

			if (getterExists) {
				NodeList childFeats = getterNode.getChildNodes();
				for (int i = 0; i < childFeats.getLength(); i++) {
					if (childFeats.item(i).getNodeName().equalsIgnoreCase("#text"))
						continue;
					if (childFeats.item(i).getNodeName().equalsIgnoreCase("getterName")) {
						getterName = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("getterDescription")) {
						getterDescription = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					}
				}
			}

			// Get poster of current node
			Node posterNode = null;
			subNodes = docElement.getChildNodes();

			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("poster") && subNodes.item(i).hasChildNodes()) {
					posterNode = subNodes.item(i);
					posterExists = true;
					break;
				}
			}

			if (posterExists) {
				NodeList childFeats = posterNode.getChildNodes();
				for (int i = 0; i < childFeats.getLength(); i++) {
					if (childFeats.item(i).getNodeName().equalsIgnoreCase("posterName")) {
						posterName = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("posterDescription")) {
						posterDescription = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("posterType")) {
						posterType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("posterArgType")) {
						posterArgumentType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("posterShowType")) {
						posterPresentationType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					}
				}
			}
			
			
			// Get putter of current node
			Node putterNode = null;
			subNodes = docElement.getChildNodes();

			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("putter") && subNodes.item(i).hasChildNodes()) {
					putterNode = subNodes.item(i);
					putterExists = true;
					break;
				}
			}

			if (putterExists) {
				NodeList childFeats = putterNode.getChildNodes();
				for (int i = 0; i < childFeats.getLength(); i++) {
					if (childFeats.item(i).getNodeName().equalsIgnoreCase("putterName")) {
						putterName = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("putterDescription")) {
						putterDescription = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("putterType")) {
						putterType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("putterArgType")) {
						putterArgumentType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("putterShowType")) {
						putterPresentationType = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					}
				}
			}
			
			// Get deleter of current node
			Node deleterNode = null;
			subNodes = docElement.getChildNodes();

			for (int i = 0; i < subNodes.getLength(); i++) {
				if (subNodes.item(i).getNodeName().equalsIgnoreCase("deleter") && subNodes.item(i).hasChildNodes()) {
					deleterNode = subNodes.item(i);
					deleterExists = true;
					break;
				}
			}

			if (deleterExists) {
				NodeList childFeats = deleterNode.getChildNodes();
				for (int i = 0; i < childFeats.getLength(); i++) {
					if (childFeats.item(i).getNodeName().equalsIgnoreCase("deleterName")) {
						deleterName = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					} else if (childFeats.item(i).getNodeName().equalsIgnoreCase("deleterDescription")) {
						deleterDescription = replaceSubstitutes(childFeats.item(i).getTextContent().trim());
					}
				}
			}
		}
	}
	
	protected HashMap<String, Object> getSource() {
		return new HashMap(uri.getPathParameters());
	}

	/**
	 * Excavates path variables from input {@link String}
	 * 
	 * @param input
	 * @return path variables from input {@link String}
	 */
	public List<String> getPathVariables(String input) {
		List<String> result = new LinkedList<String>();
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	/**
	 * TODO: Specify how to substitute the user-defined, custom arguments
	 * 
	 * @param input
	 * @return
	 */
	protected String replaceSubstitutes(String input) {
		input = input.replace("\t", "").replace("\n", "");

		List<String> replaceables = getPathVariables(input);

		for (String replaceable : replaceables) {
			replaceable = replaceable.substring(1, replaceable.length() - 1);
			if(uri.getPathParameters().containsKey(replaceable)) {
				String substitute = uri.getPathParameters().get(replaceable).get(0);
				if (substitute != null)
					input = input.replace("{" + replaceable + "}", substitute);
			}
		}

		return input;
	}

	Document documentModel = null;

	

	/**
	 * Loads the XML configuration file and creates the Document documentModel
	 */
	protected Document getConfigurationFromXML() {
		log.info("Initializing the config!");

		if (documentModel == null) {
			// Get DocumentBuilderFactory
			DocumentBuilderFactory docbFactory = DocumentBuilderFactory.newInstance();

			// Get a DocumentBuilder Instance
			DocumentBuilder docBuilder;
			try {
				docBuilder = docbFactory.newDocumentBuilder();
				log.fine("Parsing for " + this.getClass().getSimpleName());
				documentModel = docBuilder.parse(JerseyConstants.RESOURCES_CONFIGFILE_PATH);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return documentModel;
	}
	
	/**
	 * Sends an HTTP GET request to a url
	 * 
	 * @param endpoint
	 *            - The URL of the server. (Example:
	 *            " http://www.yahoo.com/search")
	 * @param requestParameters
	 *            - all the request parameters (Example:
	 *            "param1=val1&param2=val2"). Note: This method will add the
	 *            question mark (?) to the request - DO NOT add it yourself
	 * @return - The response from the end point
	 */
	public static String sendGetRequest(String endpoint, String requestParameters) {
		String result = null;
		if (endpoint.startsWith("http://")) {
			// Send a GET request to the servlet
			try {
				// Construct data
				StringBuffer data = new StringBuffer();

				// Send data
				String urlStr = endpoint;
				if (requestParameters != null && requestParameters.length() > 0) {
					urlStr += "?" + requestParameters;
				}

				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();

				conn.setRequestProperty("Accept", "text/json");

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				result = sb.toString();
			} catch (Exception e) {
				System.out.println("Exception in GET: " + e.getMessage());

				if (!e.getMessage().equalsIgnoreCase("Connection refused")) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param filePath
	 *            the name of the file to open.
	 */
	protected static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
	protected String listPathInfo() {
		if(uri.getPathParameters().size() > 0) {
        	String infoBuilder = "<ul class=\"info\">\n";
	        for(String variableName : uri.getPathParameters().keySet()) {
	        	infoBuilder += "<li class=\"info\">" + variableName + " = " + uri.getPathParameters().get(variableName).get(0) + "</li>\n";
	        }
	        infoBuilder += "</ul>\n";
	        return infoBuilder;
        }
		
        return "";
	}
}
