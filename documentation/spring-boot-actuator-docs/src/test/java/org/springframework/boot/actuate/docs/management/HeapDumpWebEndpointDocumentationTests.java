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

package org.springframework.boot.actuate.docs.management;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.actuate.docs.MockMvcEndpointDocumentationTests;
import org.springframework.boot.actuate.management.HeapDumpWebEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.cli.CurlRequestSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

/**
 * Tests for generating documentation describing the {@link HeapDumpWebEndpoint}.
 *
 * @author Andy Wilkinson
 */
@TestPropertySource(properties = "management.endpoint.heapdump.access=unrestricted")
class HeapDumpWebEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {

	@Test
	void heapDump() {
		assertThat(this.mvc.get().uri("/actuator/heapdump")).hasStatusOk()
			.apply(document("heapdump", new CurlRequestSnippet(CliDocumentation.multiLineFormat()) {

				@Override
				protected Map<String, Object> createModel(Operation operation) {
					Map<String, Object> model = super.createModel(operation);
					model.put("options", "-O");
					return model;
				}

			}));
	}

	@Configuration(proxyBeanMethods = false)
	static class TestConfiguration {

		@Bean
		HeapDumpWebEndpoint endpoint() {
			return new HeapDumpWebEndpoint() {

				@Override
				protected HeapDumper createHeapDumper() {
					return (live) -> {
						File file = Files.createTempFile("heap-", ".hprof").toFile();
						FileCopyUtils.copy("<<binary content>>", new FileWriter(file));
						return file;
					};
				}

			};
		}

	}

}
