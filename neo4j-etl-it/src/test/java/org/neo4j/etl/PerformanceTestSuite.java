package org.neo4j.etl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses({BigPerformanceTest.class, MusicBrainzPerformanceTest.class})
public class PerformanceTestSuite
{
}
