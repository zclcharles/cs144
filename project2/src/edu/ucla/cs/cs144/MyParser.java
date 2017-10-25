/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {

    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    static final String[] typeName = {
            "none",
            "Element",
            "Attr",
            "Text",
            "CDATA",
            "EntityRef",
            "Entity",
            "ProcInstr",
            "Comment",
            "Document",
            "DocType",
            "DocFragment",
            "Notation",
    };
    static Map<String, String[]> usersMap = new HashMap<>();

    static class MyErrorHandler implements ErrorHandler {

        public void warning(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void error(SAXParseException exception)
                throws SAXException {
            fatalError(exception);
        }

        public void fatalError(SAXParseException exception)
                throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                    "in the supplied XML files.");
            System.exit(3);
        }

    }

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector<Element> elements = new Vector<Element>();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName)) {
                elements.add((Element) child);
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }

    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }

    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        } else
            return "";
    }

    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }

    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try {
                am = nf.parse(money).doubleValue();
            } catch (ParseException e) {
                System.out.println("This method should work for all " +
                        "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        } catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */

        Element root = doc.getDocumentElement();
        Element[] items = getElementsByTagNameNR(root, "Item");

        // Iterate over all items and construct dat files.
        StringBuffer itemSB = new StringBuffer();
        StringBuffer bidsSB = new StringBuffer();
        StringBuffer itemCtgrySB = new StringBuffer();

        SimpleDateFormat dateReadFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        SimpleDateFormat dateWriteFormat = new SimpleDateFormat("yyyyMMddHHmmss");

//        char quo = '|';

        for (int i = 0; i < items.length; i++) {
            /*
             Items(id, name, currently, buy_price, first_bid, n_bids, location, latitude, longitude, country, started,
             ends, seller_id, description)
             */
            String itemsAtt[] = new String[14];
            String itemID = items[i].getAttribute("ItemID");
            itemsAtt[0] = itemID;
            itemsAtt[1] = getElementTextByTagNameNR(items[i], "Name");
            itemsAtt[2] = strip(getElementTextByTagNameNR(items[i], "Currently"));
            itemsAtt[3] = strip(getElementTextByTagNameNR(items[i], "Buy_Price"));
            if (itemsAtt[3].isEmpty()) itemsAtt[3] = "\\N";
            itemsAtt[4] = strip(getElementTextByTagNameNR(items[i], "First_Bid"));
            itemsAtt[5] = getElementTextByTagNameNR(items[i], "Number_of_Bids");
            // There may be commas in the location names
            itemsAtt[6] = getElementTextByTagNameNR(items[i], "Location");
            itemsAtt[7] = getElementByTagNameNR(items[i], "Location").getAttribute("Latitude");
            itemsAtt[8] = getElementByTagNameNR(items[i], "Location").getAttribute("Longitude");
            if (itemsAtt[7].isEmpty()) itemsAtt[7] = "\\N";
            if (itemsAtt[8].isEmpty()) itemsAtt[8] = "\\N";
            // There may be commas in the country names, e.g. "Croatia, Republic of"
            itemsAtt[9] = getElementTextByTagNameNR(items[i], "Country");
            try {
                Date started = dateReadFormat.parse(getElementTextByTagNameNR(items[i], "Started"));
                Date ends = dateReadFormat.parse(getElementTextByTagNameNR(items[i], "Ends"));
                itemsAtt[10] = dateWriteFormat.format(started);
                itemsAtt[11] = dateWriteFormat.format(ends);
            } catch (ParseException e) {
                System.out.println("Parse date failed.");
                e.printStackTrace();
            }
            itemsAtt[12] = getElementByTagNameNR(items[i], "Seller").getAttribute("UserID");
            itemsAtt[13] = getElementTextByTagNameNR(items[i], "Description");
            // truncate description to the length of 4000
            itemsAtt[13] = itemsAtt[13].substring(0, Math.min(4000, itemsAtt[13].length()));

            String delim = "";
            for (int j = 0; j < 14; j++) {
                itemSB.append(delim).append(itemsAtt[j]);
                delim = "\t";
            }
            itemSB.append('\n');

            /*
             Bids(item_id, bidder_id, time, amount)
             */
            Element[] bids = getElementsByTagNameNR(getElementByTagNameNR(items[i], "Bids"), "Bid");
            if (bids.length > 0) {
                for (Element bid : bids) {
                    String bidAtt[] = new String[4];
                    bidAtt[0] = itemID;
                    Element bidder = getElementByTagNameNR(bid, "Bidder");
                    String bidderID = bidder.getAttribute("UserID");
                    bidAtt[1] = bidderID;
                    try {
                        Date bidTime = dateReadFormat.parse(getElementTextByTagNameNR(bid, "Time"));
                        bidAtt[2] = dateWriteFormat.format(bidTime);
                    } catch (ParseException e) {
                        System.out.println("Parse date failed.");
                        e.printStackTrace();
                    }
                    bidAtt[3] = strip(getElementTextByTagNameNR(bid, "Amount"));

                    delim = "";
                    for (String ba : bidAtt) {
                        bidsSB.append(delim).append(ba);
                        delim = "\t";
                    }
                    bidsSB.append('\n');

                    /*
                     Users(user_id, bidder_rating, seller_rating, location, country)
                     */
                    if (usersMap.containsKey(bidderID)) {
                        String[] userAtt = usersMap.get(bidderID);
                        String bidderRating = userAtt[0];
//                        String sellerRating = userAtt[1];
                        if (bidderRating.equals("\\N")) {
                            userAtt[0] = bidder.getAttribute("Rating");
                            userAtt[2] = getElementTextByTagNameNR(bidder, "Location");
                            userAtt[3] = getElementTextByTagNameNR(bidder, "Country");
//                            if (!sellerRating.isEmpty()) {
//                                userAtt[1] = sellerRating;
//                            } else {
//                                userAtt[1] = "";
//                            }
//                            usersMap.put(bidderID, userAtt);
                        }
                    } else {
                        String[] userAtt = new String[4];
                        userAtt[0] = bidder.getAttribute("Rating");
                        userAtt[1] = "\\N";
                        userAtt[2] = getElementTextByTagNameNR(bidder, "Location");
                        userAtt[3] = getElementTextByTagNameNR(bidder, "Country");
                        usersMap.put(bidderID, userAtt);
                    }
                }
            }

            /*
             Users(user_id, bidder_rating, seller_rating, location, country)
             */
            Element seller = getElementByTagNameNR(items[i], "Seller");
            String sellerID = seller.getAttribute("UserID");
            if (usersMap.containsKey(sellerID)) {
                String[] userAtt = usersMap.get(sellerID);
                if (userAtt[1].equals("\\N")) {
//                    userAtt[0] = usersMap.get(sellerID)[0];
                    userAtt[1] = seller.getAttribute("Rating");
//                    userAtt[2] = usersMap.get(sellerID)[2];
//                    userAtt[3] = usersMap.get(sellerID)[3];
//                    usersMap.put(sellerID, userAtt);
                }
            } else {
                String[] userAtt = new String[4];
                userAtt[0] = "\\N";
                userAtt[1] = seller.getAttribute("Rating");
                userAtt[2] = "";
                userAtt[3] = "";
                usersMap.put(sellerID, userAtt);
            }

            /*
             ItemCategory(item_id, category)
             */
            Element[] ctgrys = getElementsByTagNameNR(items[i], "Category");
            for (Element ctgry : ctgrys) {
                itemCtgrySB.append(itemID).append("\t").append(ctgry.getTextContent()).append('\n');
            }
        }

        // Write into data files
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("items_dup.dat"),true))) {
            bwr.write(itemSB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("bids_dup.dat"),true))) {
            bwr.write(bidsSB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("categories_dup.dat"),true))) {
            bwr.write(itemCtgrySB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**************************************************************/

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        } catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        StringBuffer usersSB = new StringBuffer();

        // Fill the content of usersMap into its corresponding stringbuffer
        for (String userID : usersMap.keySet()) {
            usersSB.append(userID);
            for (String ua : usersMap.get(userID)) {
                usersSB.append("\t").append(ua);
            }
            usersSB.append('\n');
        }

        try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("users.dat")))) {
            bwr.write(usersSB.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
