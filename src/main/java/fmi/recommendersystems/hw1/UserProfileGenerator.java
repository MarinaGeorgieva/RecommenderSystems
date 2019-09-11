package fmi.recommendersystems.hw1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserProfileGenerator {
	
	private static final int DOCS_COUNT = 15;
	private static final int CATEGORIES_COUNT = 3;
	
	private static final Random random = new Random(128);

	public static List<News> generate(List<List<News>> allNews) {
        List<Integer> categories = chooseRandomCategories();
        System.out.println("Chosen categories are: ");
        for (Integer category : categories) {
            System.out.println(category);
        }
        
        List<News> allNewsByChosenCategories = getAllNewsByChosenCategories(allNews, categories);
        Collections.shuffle(allNewsByChosenCategories, random);
        
        List<News> userNews = allNewsByChosenCategories.subList(0, DOCS_COUNT);
        for (News news : userNews) {
            news.setRead(true);
        }

        return userNews;
	}
    
    private static List<News> getAllNewsByChosenCategories(List<List<News>> allNews, List<Integer> categories) {
        List<News> allNewsByChosenCategories = new ArrayList<News>();
        for (int i = 0; i < categories.size(); i++) {

            List<News> newsByCategory = allNews.get(categories.get(i) - 1);
            for (News news : newsByCategory) {
                allNewsByChosenCategories.add(news);
            }
        }

        return allNewsByChosenCategories;
    }
    
    private static List<Integer> chooseRandomCategories() {
        List<Integer> categories = new ArrayList<Integer>();
        for (int i = 1; i <= 20; i++) {
            categories.add(i);
        }

        Collections.shuffle(categories, random);
        return categories.subList(0, CATEGORIES_COUNT);
    }
}