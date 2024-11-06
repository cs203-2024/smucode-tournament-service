package com.cs203.smucode.services.impl;

import com.cs203.smucode.dto.UserDTO;
import com.cs203.smucode.consumers.UserServiceConsumer;
import com.cs203.smucode.exceptions.PredictionFailedException;
import com.cs203.smucode.exceptions.PredictionModelNotFoundException;
import com.cs203.smucode.models.PredictionResult;
import com.cs203.smucode.services.PredictionService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.SGD;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.HashMap;
import java.util.Map;

@Service
public class PredictionServiceImpl implements PredictionService {
    private static final Logger logger = LoggerFactory.getLogger(PredictionServiceImpl.class);
    private static final double BETA = 4.166667; // TrueSkill constant
    private static final NormalDistribution NORMAL = new NormalDistribution(0, 1);
    // Paths to the trained model files in resources
    private static final String MODEL_PATH = "prediction/trueskill_model.model";
    private static final String STRUCTURE_PATH = "prediction/trueskill_model.structure";

    private final UserServiceConsumer userServiceConsumer;
    private SGD model; // Trained Weka (SGD) Model, Binary Classification
    private Instances dataset; // Structure/Metadata for the Model

    @Autowired
    public PredictionServiceImpl(UserServiceConsumer userServiceConsumer) {
        this.userServiceConsumer = userServiceConsumer;
    }

    /**
     * Initializes the prediction service by loading the trained model.
     * Called automatically after construction.
     *
     * @throws RuntimeException if model loading fails
     */
    @PostConstruct
    public void initialize() {
        try {
            loadModel();
            logger.info("Prediction model loaded successfully");
        } catch (Exception e) {
            throw new PredictionModelNotFoundException("Failed to load prediction model");
        }
    }

    /**
     * Loads the trained model and its structure from resources.
     * The model is used for supplementing TrueSkill predictions.
     *
     * @throws Exception if model files cannot be loaded
     */
    public void loadModel() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        model = (SGD) SerializationHelper.read(classLoader.getResourceAsStream(MODEL_PATH));
        dataset = (Instances) SerializationHelper.read(classLoader.getResourceAsStream(STRUCTURE_PATH));
    }

    /**
     * Predicts the outcome of a match between two players.
     *
     * @param player1Username Username of the first player
     * @param player2Username Username of the second player
     * @return PredictionResult containing win probabilities for both players
     * @throws RuntimeException if prediction fails
     */
    @Override
    public PredictionResult predictMatch(String player1Username, String player2Username) {
        try {
            UserDTO player1 = userServiceConsumer.getUserById(player1Username);
            UserDTO player2 = userServiceConsumer.getUserById(player2Username);

            double finalP1Prob = calculateBlendedProbability(player1, player2);
            double finalP2Prob = 1.0 - finalP1Prob;

            return new PredictionResult(
                    player1Username,
                    player2Username,
                    finalP1Prob,
                    finalP2Prob
            );
        } catch (Exception e) {
            throw new PredictionFailedException("Failed to predict match outcome");
        }
    }

    /**
     * Calculates the final win probability by blending TrueSkill and model predictions.
     * Uses a dynamic weighting system based on the TrueSkill probability:
     * - If TrueSkill probability < 0.6, uses only TrueSkill to avoid overfitting
     * - If TrueSkill probability >= 0.6, uses 80% TrueSkill + 20% model prediction
     *
     * @param player1 First player's data
     * @param player2 Second player's data
     * @return Final win probability for player1
     * @throws Exception if model prediction fails
     */
    public double calculateBlendedProbability(UserDTO player1, UserDTO player2) throws Exception {
        Map<String, Double> modelProbs = getModelProbabilities(player1, player2);
        double tsProb = calculateTrueSkillProbability(player1, player2);

        //Blend probabilities (80% TrueSkill, 20% model)
        //if tsProb < 0.6, don't take into account model (overfit risk )
        double finalProb = tsProb >= 0.6 ? (0.8 * tsProb) + (0.2 * modelProbs.get("player1")) : tsProb;

        //Ensure reasonable bounds (5% minimum, 95% maximum)
        return Math.max(0.05, Math.min(0.95, finalProb));
    }

    /**
     * Gets win probability predictions from the trained model.
     * Creates a feature vector from player statistics and uses the model to predict.
     *
     * @param player1 First player's data
     * @param player2 Second player's data
     * @return Map containing win probabilities for both players
     * @throws Exception if model prediction fails
     */
    public Map<String, Double> getModelProbabilities(UserDTO player1, UserDTO player2) throws Exception {
        Instance inst = new DenseInstance(3);
        inst.setDataset(dataset);

        double skillDiff = (player1.mu() - player2.mu()) /
                Math.sqrt(Math.pow(player1.sigma(), 2) + Math.pow(player2.sigma(), 2));
        double uncertaintyRatio = player1.sigma() / player2.sigma();

        inst.setValue(0, skillDiff);
        inst.setValue(1, uncertaintyRatio);
        inst.setMissing(2);

        double[] probs = model.distributionForInstance(inst);

        Map<String, Double> modelProbs = new HashMap<>();
        modelProbs.put("player1", probs[0]);
        modelProbs.put("player2", probs[1]);

        return modelProbs;
    }

    /**
     * Trains the model with a new match result
     * @param player1Username First player's username
     * @param player2Username Second player's username
     * @param player1Won Whether player1 won the match
     */
    public void trainModelWithResult(String player1Username, String player2Username, boolean player1Won) {
        try {
            UserDTO player1 = userServiceConsumer.getUserById(player1Username);
            UserDTO player2 = userServiceConsumer.getUserById(player2Username);

            Instance inst = new DenseInstance(3);
            inst.setDataset(dataset);

            double skillDiff = (player1.mu() - player2.mu()) /
                    Math.sqrt(Math.pow(player1.sigma(), 2) + Math.pow(player2.sigma(), 2));
            double uncertaintyRatio = player1.sigma() / player2.sigma();

            inst.setValue(0, skillDiff);
            inst.setValue(1, uncertaintyRatio);
            inst.setValue(2, player1Won ? 1 : 0); // Set the class value

            // Update the model
            model.updateClassifier(inst);

            logger.info("Model trained with new match result: {} vs {} (winner: {})",
                    player1Username, player2Username, player1Won ? player1Username : player2Username);
        } catch (Exception e) {
            logger.error("Failed to train model with new match result", e);
        }
    }

    /**
     * Calculates win probability for 1v1 matches using the TrueSkill formula.
     * For 1v1 matches, this simplifies to:
     * - deltaMu = player1.mu - player2.mu
     * - sumSigma = player1.sigma² + player2.sigma²
     * - size = 2 (always for 1v1)
     * - denominator = sqrt(size * BETA² + sumSigma)
     */
    private double calculateTrueSkillProbability(UserDTO player1, UserDTO player2) {
        double deltaMu = player1.mu() - player2.mu();
        double sumSigma = Math.pow(player1.sigma(), 2) + Math.pow(player2.sigma(), 2);
        int size = 2; //always 2 for 1v1
        double denom = Math.sqrt(size * (BETA * BETA) + sumSigma);

        return NORMAL.cumulativeProbability(deltaMu / denom);
    }
}