package cpen221.mp3.wikimediator;

import java.util.*;

import cpen221.mp3.cache.Cache;
import fastily.jwiki.core.Wiki;
import jdk.jfr.StackTrace;


public class WikiMediator {
    //wiki to access English version of Wikipedia.
    public Wiki wiki = new Wiki("en.wikipedia.org");
    //cache to store touched objects. Contains 256 objects and time out at 12 hours (43200s)
    public Cache cache = new Cache(256, 43200);

    /**
     * default constructor to create a WikiMediator.
     */
    public WikiMediator() {
        wiki.enableLogging(false);
    }

    /**
     * searches Wikipedia for pages whose titles contains the query.
     * @param query
     * @param limit
     * @return list of Wikipedia page titles that contains the query. If the limit is less than 1 or no page
     * titles contains the que\ry return an empty list.
     */
    public List<String> simpleSearch(String query, int limit) {
        if(limit < 1) return new ArrayList<>();
        return wiki.search(query, limit);
    }

    /**
     *get the text associated with the page title on Wikipedia.
     * @param pageTitle
     * @return
     */
    public String getPage(String pageTitle) {
        String page = wiki.getPageText(pageTitle);
        //add page to cache.
        return page;
    }

    public List<String> getConnectedPages(String pageTitle, int hops) {
        if(hops < 1) {
            List<String> no_hops = new ArrayList<>();
            no_hops.add(pageTitle);
            return no_hops;
        }

        List<String> all_links = new ArrayList<>();

        Stack<String> stack = new Stack<>();

        stack.push(pageTitle);
        int depth = 1;
        int bottom = 0;

        while(!stack.isEmpty()) {
            String s = stack.pop();
            if(!all_links.contains(s)) {
                all_links.add(s);
            }
            //if about to reach the hop limit add all children of s to the list and decrement depth.
            if (depth % hops == 0) {
                List<String> neighbors = new ArrayList<>(wiki.getLinksOnPage(s));
                for(int i = 0; i < neighbors.size(); i++) {
                    if(!all_links.contains(neighbors.get(i))) all_links.add(neighbors.get(i));
                }
                if(stack.size() == bottom) depth--;
            }
            //else continue to grow the stack and hop deeper.
            else {
                bottom = stack.size();
                List<String> neighbors = new ArrayList<>(wiki.getLinksOnPage(s));
                for (int i = 0; i < neighbors.size(); i++) {
                    //need to only allow main body links (figure out NS field).
                    stack.push(neighbors.get(i));
                }
                depth++;
                }
            }
        return all_links;
    }


    /**
     *
     * @param limit
     * @return
     */
    public List<String> zeitgeist(int limit) {
        return null;
    }

    /**
     *
     * @param limit
     * @return
     */
    public List<String> trending(int limit) {
        return null;
    }

    /**
     *
     * @return
     */
    public int peakLoad30s() {
        return 0;
    }
}

