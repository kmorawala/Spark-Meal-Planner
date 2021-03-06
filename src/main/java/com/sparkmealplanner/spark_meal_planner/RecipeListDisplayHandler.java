package com.sparkmealplanner.spark_meal_planner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONException;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The following class handles the HTML and java elements of the recipe list
 * display from search page of the app
 *
 */
public class RecipeListDisplayHandler implements Route {

	// instance variables
	private String pageTitle = "Recipe Search List List";
	private String recipeToSearch;
	private String attributionHtml;
	private HashMap<String, String> recipeNameAndDishID;

	/**
	 * The following constructor initializes a HashMap
	 */
	public RecipeListDisplayHandler() {
		recipeToSearch = "";
		attributionHtml = "";
		recipeNameAndDishID = new HashMap<String, String>();
	}

	/**
	 * The following method handles the HTML and java elements of the recipe list
	 * display from search page of the app
	 */
	public Object handle(Request request, Response response) throws Exception {

		// every time the request is made, the HashMap is cleared
		recipeNameAndDishID.clear();

		// storing the parameter from request in a variable
		recipeToSearch = request.queryParams("recipetosearch");

		// returning HTML
		return HtmlWriter.gethtmlHead("Recipe List") + HtmlWriter.createBodyTitle(pageTitle) + searchRecipeWithAPI()
				+ addRecipeFromURLButton() + HtmlWriter.getFooter() + HtmlWriter.closeTag();
	}

	/**
	 * The following method adds a button to direct users to add recipe from an
	 * external url
	 * 
	 * @return HTML
	 */
	private String addRecipeFromURLButton() {

		StringBuilder sb = new StringBuilder();

		// creating a button in HTML
		sb.append(HtmlWriter.createButton("addnewrecipe", "I have a url to add recipe from"));

		return sb.toString();
	}

	/**
	 * The following method searches the API and displays the result
	 * 
	 * @return HTML
	 */
	public String searchRecipeWithAPI() {

		// searching Yummly using the API handler and related attributes
		try {
			YummlyAPIHandler.searchReceipe(recipeToSearch);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		attributionHtml = YummlyAPIHandler.getSearchRecipeAttributionhtml();
		recipeNameAndDishID = YummlyAPIHandler.getRecipeNameAndDishID();

		StringBuilder sb = new StringBuilder();

		// creating unordered list to add each recipe
		sb.append("<ul>");

		for (Entry<String, String> recipe : recipeNameAndDishID.entrySet()) {

			String aTag = "<a href=/recipechosen?recipeid=" + recipe.getValue() + ">";

			sb.append("<li>" + aTag + recipe.getKey() + "</a>"

			// each line has a button for adding the recipe to calendar
					+ HtmlWriter.createButton("addtocalendar", " Send to Calendar", "recipename", recipe.getKey(),
							"recipeid", recipe.getValue(), "recipesearched", recipeToSearch)
					+ "</li>");

		}

		// adding attribution HTML from yummly
		sb.append("<br><br>" + attributionHtml);

		sb.append("</ul><br>");

		return sb.toString();
	}
}
