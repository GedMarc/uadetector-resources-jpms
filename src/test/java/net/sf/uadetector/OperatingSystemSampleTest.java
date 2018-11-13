/*******************************************************************************
 * Copyright 2013 André Rouél
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector;

import net.sf.uadetector.service.UADetectorServiceFactory;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.*;

public class OperatingSystemSampleTest
{

	/**
	 * Default log
	 */
	private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(OperatingSystemSampleTest.class.toString());

	/**
	 * Parser to detect informations about an user agent
	 */
	private static final UserAgentStringParser PARSER = UADetectorServiceFactory.getResourceModuleParser();

	@Test
	public void test()
	{
		List<OperatingSystemSample> samples = OperatingSystemSampleReader.readAll();
		int i = 0;
		for (OperatingSystemSample sample : samples)
		{
			ReadableUserAgent agent = PARSER.parse(sample.getUserAgentString());

			// comparing the name
			if (!sample.getName()
			           .equals(agent.getOperatingSystem()
			                        .getName()))
			{
				LOG.info("Naming different: " + sample.getName() + " != " + agent.getOperatingSystem()
				                                                                 .getName() + " ("
				         + sample.getUserAgentString() + ")");
				assertThat(agent.getOperatingSystem()
				                .getName()).isEqualTo(sample.getName());
			}

			// check for unknown family
			if (OperatingSystemFamily.UNKNOWN == agent.getOperatingSystem()
			                                          .getFamily())
			{
				LOG.info("Unknown operating system family found. Please update the enum 'OperatingSystemFamily'.");
			}

			// comparing version number
			if (!sample.getVersion()
			           .equals(agent.getOperatingSystem()
			                        .getVersionNumber()))
			{
				LOG.info("Versioning different: " + sample.getVersion() + " != " + agent.getOperatingSystem()
				                                                                        .getVersionNumber() + " ("
				         + sample.getUserAgentString() + ")");
				assertThat(agent.getOperatingSystem()
				                .getVersionNumber()).isEqualTo(sample.getVersion());
			}

			// abort if unknown family
			assertThat(OperatingSystemFamily.UNKNOWN == agent.getOperatingSystem()
			                                                 .getFamily()).isFalse();

			// save read OS for printing out
			i++;
		}
		LOG.info(i + " operating system samples validated");
	}

}
