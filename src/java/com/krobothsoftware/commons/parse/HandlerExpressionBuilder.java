/* ===================================================
 * Copyright 2013 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.commons.parse;

/**
 * 
 * @author Kyle Kroboth
 * @since SNC 1.0.1
 */
public class HandlerExpressionBuilder extends HandlerExpression {
	private final ExpressionBuilderFilter filter;

	public HandlerExpressionBuilder(HandlerSAX delegate) {
		super(delegate);
		filter = (ExpressionBuilderFilter) delegate;
	}

	@Override
	public void buildCharacters(String content) {
		delegate.buildCharacters(content);
		if (reached) filter.buildCharacters(index, content);
	}

}
