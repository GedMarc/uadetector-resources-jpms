/*******************************************************************************
 * Copyright 2012 André Rouél
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

import net.sf.uadetector.internal.data.domain.Robot;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.junit.Test;

import java.util.Formatter;
import java.util.List;

import static org.fest.assertions.Assertions.*;

public class OnlineUserAgentStringParserIntegrationTest
{

	/**
	 * Default log
	 */
	private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(OnlineUserAgentStringParserIntegrationTest.class.toString());
	private static final List<OperatingSystemExample> OS_EXAMPLES = OperatingSystemExamplesReader.read();
	private static final List<UserAgentExample> UA_EXAMPLES = UserAgentExamplesReader.read();
	private static final UserAgentStringParser PARSER = UADetectorServiceFactory.getResourceModuleParser();

	@Test
	public void testOperatingSystemExamples() throws Exception
	{
		Output out = new Output();
		int i = 0;
		for (OperatingSystemExample example : OS_EXAMPLES)
		{
			ReadableUserAgent agent = PARSER.parse(example.getUserAgentString());

			// comparing the name
			assertThat(agent.getOperatingSystem()
			                .getName()).isEqualTo(example.getName());

			// check for unknown family
			if (OperatingSystemFamily.UNKNOWN == agent.getOperatingSystem()
			                                          .getFamily())
			{
				LOG.info("Unknown operating system family found. Please update the enum 'OperatingSystemFamily' for '"
				         + agent.getOperatingSystem()
				                .getName() + "'.");
			}

			// abort if family is unknown
			String msg = "Unknown operating system for: " + example.getUserAgentString();
			assertThat(agent.getOperatingSystem()
			                .getFamily()).as(msg)
			                             .isNotEqualTo(OperatingSystemFamily.UNKNOWN);

			// save read OS for printing out
			out.print(agent.getOperatingSystem()
			               .getName(), agent.getOperatingSystem()
			                                .getVersionNumber(), example.getUserAgentString());

			i++;
		}
		LOG.info(Output.NEWLINE + out.toString());
		LOG.info(i + " operating system examples validated");
	}

	@Test
	public void testUserAgentExamples() throws Exception
	{
		Output out = new Output("%-40.40s %-30.30s %s");
		int i = 0;
		for (UserAgentExample example : UA_EXAMPLES)
		{
			ReadableUserAgent agent = PARSER.parse(example.getUserAgentString());

			// comparing the name
			UserAgentFamily family = UserAgentFamily.evaluate(example.getName());
			if (family != agent.getFamily())
			{
				LOG.info("Unexpected user agent family found. Please check the user agent string '" + example.getUserAgentString() + "'.");
			}
			String msgForFamilyDiff = "'" + family + "' != '" + agent.getFamily() + "' : " + example.getUserAgentString();
			assertThat(agent.getFamily()).as(msgForFamilyDiff)
			                             .isEqualTo(family);

			String type = "robot".equals(example.getType()) ? Robot.TYPENAME : example.getType();
			if (Robot.TYPENAME.equals(type))
			{
				// save read robot for printing out
				out.print(agent.getName(), agent.getVersionNumber(), example.getUserAgentString());
			}

			// abort if the type is not the expected one
			String msgForTypeDiff = "'" + type + "' != '" + agent.getTypeName() + "' : " + example.getUserAgentString();
			assertThat(agent.getTypeName()).as(msgForTypeDiff)
			                               .isEqualTo(type);

			i++;
		}
		LOG.info(Output.NEWLINE + out.toString());
		LOG.info(i + " User-Agent examples validated");
	}

	/**
	 * Output buffer to store informations from the detection process of the examples.
	 */
	public static class Output
	{

		public static final String DEFAULT_FORMAT = "%-30.30s %-20.20s %s";

		public static final char NEWLINE = '\n';

		private final String format;

		private final StringBuilder buffer = new StringBuilder();

		public Output()
		{
			this(DEFAULT_FORMAT);
		}

		public Output(String format)
		{
			this.format = format;
		}

		public void print(String name, VersionNumber version, String userAgent)
		{
			Formatter formatter = new Formatter(buffer);
			formatter.format(format, name, version.toVersionString(), userAgent);
			buffer.append(NEWLINE);
		}

		@Override
		public String toString()
		{
			return buffer.toString();
		}

	}

}
