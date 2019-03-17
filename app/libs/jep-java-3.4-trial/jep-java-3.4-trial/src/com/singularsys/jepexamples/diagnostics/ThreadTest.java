/*
Created 2 Aug 2006 - Richard Morris
 */
package com.singularsys.jepexamples.diagnostics;

import com.singularsys.jep.ComponentSet;
import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.Variable;
import com.singularsys.jep.misc.LightWeightComponentSet;
import com.singularsys.jep.parser.Node;
import com.singularsys.jep.walkers.SerializableExpression;

/**
 * Testing thread safety, using Taylor approximation to ln(x).
 * @author Richard Morris
 *
 */
public class ThreadTest {

    static class EvaluationThread extends Thread {
        Jep myJep=null;
        Node myExpression;
        double min,max;
        int itts;
        Variable myVar;
        double results[];
        int n;
        public EvaluationThread(Jep localJep,SerializableExpression se,
                double minValue,double maxValue,int numItts,int threadNum) 
        throws JepException
        {
            min = minValue;
            max = maxValue;
            itts = numItts;
            n=threadNum;
            myJep = localJep; //(Jep) ois.readObject();
            //SerializableExpression se = (SerializableExpression) ois.readObject(); 
            myExpression = se.toNode(myJep);
            myVar = myJep.addVariable("x");
            results = new double[numItts];
        }

        @Override
        public void run() {
            System.out.println("Thread "+n+" started");
            try {
                for(int i=0;i<itts;++i)
                {
                    double value = min+(max-min)*(i)/(itts-1);
                    myVar.setValue(value);
                    Object resObj = myJep.evaluate(myExpression);
                    results[i] = ((Double) resObj);
                }
            } catch (EvaluationException e) {
                e.printStackTrace();
            }
            System.out.println("Thread "+n+" finished");
        }

        public double[] getResults() {
            return results;
        }
    }

    Jep baseJep;

    void printMem() {
        Runtime rt = Runtime.getRuntime();
        System.out.println("Used mem "+ (rt.totalMemory()-rt.freeMemory()));
    }

    public ThreadTest() {
        baseJep = new Jep();
    }

    public String getExpression(int nDeriv) {
        StringBuilder sb = new StringBuilder();
        for(int i=1;i<=nDeriv;++i)
        {
            if(i%2==0)
                sb.append("-");
            else
                sb.append("+");
            sb.append("x^"+i+"/"+i);
        }
        return sb.toString();
    }

    public void go(String expression,
            int nThreads,int nItts,
            double minValue,double maxValue) throws JepException
            {
        Node base = baseJep.parse(expression);
        SerializableExpression se = new SerializableExpression(base);
        EvaluationThread et[] = new EvaluationThread[nThreads];
        printMem();
        Jep threadJeps[] = new Jep[nThreads];
        for(int i=0;i<nThreads;++i)
        {
            ComponentSet cs = new LightWeightComponentSet(baseJep);
            threadJeps[i] = new Jep(cs);
        }
        printMem();

        for(int i=0;i<nThreads;++i)
        {
            et[i] = new EvaluationThread(threadJeps[i],se,minValue,maxValue,nItts,i);
        }
        printMem();

        for(int i=0;i<nThreads;++i)
            et[i].start();

        // now wait for threads to finish
        for(int i=0;i<nThreads;++i) {
            try {
                et[i].join();
            } catch (InterruptedException e) {
                System.out.println("thread "+i+" interrupted "+e.getMessage());
            }
        }

        // check same results from each thread
        for(int i=1;i<nThreads;++i)
            for(int j=0;j<nItts;++j) 
                if(et[0].results[j] != et[i].results[j])
                    throw new ParseException("Error values do not match ");
            }
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        ThreadTest tt = new ThreadTest();
        String s = tt.getExpression(20);
        try {
            tt.go(s,25,10000,-1.0,1.0);
        } catch (JepException e) {
            System.out.println(e.getMessage());
        }
    }

}
