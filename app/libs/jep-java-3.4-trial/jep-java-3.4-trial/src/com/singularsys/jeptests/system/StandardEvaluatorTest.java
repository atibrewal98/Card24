package com.singularsys.jeptests.system;

import org.junit.Before;

import com.singularsys.jep.Jep;
import com.singularsys.jep.standard.StandardEvaluator;

/**
 * Performs the regular JepTests using the StandardEvaluator
 */
public class StandardEvaluatorTest extends JepTest {

    @Override
    @Before
    public void setUp() {
        this.jep = new Jep(new StandardEvaluator());
    }

}
