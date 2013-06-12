/**
 * 
 */
package com.krobothsoftware.commons.parse;

/**
 * Filter with {@link HandlerSAX#buildCharacters(String)} applied to
 * {@link HandlerSAX} for {@link Expression} evaluation.
 * 
 * @author Kyle Kroboth
 * @since COMMONS 1.0.1
 */
public interface ExpressionBuilderFilter extends ExpressionFilter {

	/**
	 * Called when the expression's path is reached. If there is more than on
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param content
	 * @see HandlerSAX#buildCharacters(String)
	 * @since COMMONS 1.0.1
	 */
	public void buildCharacters(int expr, String content);

}
