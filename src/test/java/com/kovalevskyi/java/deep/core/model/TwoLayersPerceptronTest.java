package com.kovalevskyi.java.deep.core.model;

import com.kovalevskyi.java.deep.core.model.activation.Sigmoid;
import com.kovalevskyi.java.deep.core.model.graph.ConnectedNeuron;
import com.kovalevskyi.java.deep.core.model.graph.InputNeuron;
import com.kovalevskyi.java.deep.core.model.activation.Relu;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static junit.framework.TestCase.assertTrue;


public class TwoLayersPerceptronTest {

    private ConnectedNeuron outputNeuron;
    private ConnectedNeuron hiddenNeuron1;
    private ConnectedNeuron hiddenNeuron2;
    private InputNeuron inputFriend;
    private InputNeuron inputVodka;
    private InputNeuron inputSunny;

    @Before
    public void setUp() {
        Random random = new Random();
        double learningRate = 0.05;
        inputFriend = new InputNeuron("friend");
        inputVodka = new InputNeuron("vodka");
        inputSunny = new InputNeuron("sunny");

        hiddenNeuron1 = new ConnectedNeuron.Builder()
                .activationFunction(new Relu())
                .learningRate(learningRate)
                .name("hidden1")
                .build();
        inputFriend.connect(hiddenNeuron1, random.nextDouble());
        inputVodka.connect(hiddenNeuron1, random.nextDouble());
        inputSunny.connect(hiddenNeuron1, random.nextDouble());

        hiddenNeuron2 = new ConnectedNeuron.Builder()
                .activationFunction(new Relu())
                .learningRate(learningRate)
                .name("hidden2")
                .build();

        inputFriend.connect(hiddenNeuron2, random.nextDouble());
        inputVodka.connect(hiddenNeuron2, random.nextDouble());
        inputSunny.connect(hiddenNeuron2, random.nextDouble());

        outputNeuron = new ConnectedNeuron.Builder()
                .activationFunction(new Sigmoid())
                .learningRate(learningRate)
                .build();
        hiddenNeuron1.connect(outputNeuron, random.nextDouble());
        hiddenNeuron2.connect(outputNeuron, random.nextDouble());
    }

    @Test
    public void testTraining() throws Exception {
        double error = .0;
        for (int i = 0; i < 1000; i++) {
            error =
                    trainIteration(
                            1.,
                            1.,
                            1.,
                            1.)
                    + trainIteration(
                            1.,
                            1.,
                            0.,
                            0.)
                    + trainIteration(
                            1.,
                            0.,
                            1.,
                            1.)
                    + trainIteration(
                            0.,
                            1.,
                            1.,
                            1.)
                    + trainIteration(
                            0.,
                            0.,
                            1.,
                            0.)
                    + trainIteration(
                            0.,
                            1.,
                            0.,
                            0.)
                    + trainIteration(
                            1.,
                            0.,
                            0.,
                            0.)
                    + trainIteration(
                            0.,
                            0.,
                            0.,
                            0.);
            error = error / 8.;
            System.out.printf("ERROR: %s \n", error);
        }
        assertTrue(Math.abs(error) < 0.1);
    }

    private double trainIteration(double friendInput,
                                double vodkaInput,
                                double sunnyInput,
                                double expectedResult) throws Exception {
        inputFriend.forwardSignalReceived(null, friendInput);
        inputVodka.forwardSignalReceived(null, vodkaInput);
        inputSunny.forwardSignalReceived(null, sunnyInput);
        double actualResult = outputNeuron.getForwardResult();
        double errorDy = 2 * (expectedResult - actualResult);
        outputNeuron.backwardSignalReceived(errorDy);
        outputNeuron.forwardInvalidate();
        System.out.printf("For: F: %f V: %f S: %f Expected: %f Actual: %f\n",
                friendInput,
                vodkaInput,
                sunnyInput,
                expectedResult,
                actualResult);
        return expectedResult - actualResult;
    }
}