package com.singularsys.jeptests.unit.functions;

import org.junit.Test;
import com.singularsys.jep.functions.*;


public class RoundTest {
    
    @Test
    public void testRounding() {
        Round roundPFMC = new Round();
        Utilities.testUnary(roundPFMC, 2.5, 3.0);
        Utilities.testUnary(roundPFMC, 1.5, 2.0);
        Utilities.testUnary(roundPFMC, 0.1, 0.0);
    }
    

}
