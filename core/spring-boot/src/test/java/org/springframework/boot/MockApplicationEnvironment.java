/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot;

import org.springframework.mock.env.MockEnvironment;

/**
 * {@link MockEnvironment} with the same property overrides as
 * {@link ApplicationEnvironment}.
 *
 * @author Phillip Webb
 */
public class MockApplicationEnvironment extends MockEnvironment {

	@Override
	protected String doGetActiveProfilesProperty() {
		return null;
	}

	@Override
	protected String doGetDefaultProfilesProperty() {
		return null;
	}

}
