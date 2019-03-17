package com.singularsys.jeptests.unit.functions;

import org.junit.Test;
import com.singularsys.jep.functions.*;


public class RIntTest {
    
    @Test
    public void testRounding() {
        RInt rintPFMC = new RInt();
        Utilities.testUnary(rintPFMC, 2.5, 2.0);
        Utilities.testUnary(rintPFMC, 1.5, 2.0);
        Utilities.testUnary(rintPFMC, 0.1, 0.0);
    }

}
